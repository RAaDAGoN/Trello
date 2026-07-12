package dev.radagon.trello.service;

import dev.radagon.trello.dto.BoardColumnDTO;
import dev.radagon.trello.entity.Board;
import dev.radagon.trello.entity.BoardColumn;
import dev.radagon.trello.repository.BoardColumnRepository;
import dev.radagon.trello.repository.BoardRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BoardColumnService {
    private BoardColumnRepository columnRepository;
    private BoardRepository boardRepository;

    /**
     * Поиск доски по id доски и юзеру
     * Получение максимальной позиции в доске
     * если колонок нет, то -1
     */

    @Transactional
    public BoardColumn createColumn(BoardColumnDTO boardColumnDTO, Long boardId, Long userId) {
        Board board = boardRepository.findByIdAndUser_Id(boardId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Board Not Found"));

        int maxPosition = columnRepository.findMaxPositionByBoardId(boardId)
                .orElse(-1);

        BoardColumn boardColumn = BoardColumn.builder()
                .board(board)
                .name(boardColumnDTO.getName())
                .position(maxPosition + 1)
                .build();

        return columnRepository.save(boardColumn);
    }

    @Transactional
    public BoardColumn updateColumn(Long columnId, BoardColumnDTO columnDTO, Long userId) {
        BoardColumn column = columnRepository.findByIdWithBoardAndUser(columnId)
                .orElseThrow(() -> new EntityNotFoundException("Column Not Found"));

        // Проверка принадлежит ли колонка юзеру
        if (!column.getBoard().getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Access Denied");
        }

        column.setName(columnDTO.getName());
        return columnRepository.save(column);
    }

    /**
     * Проверяем, есть ли право у юзера смотреть доску.
     * Возвращаем список колонок в доске
     */

    public List<BoardColumn> getBoardColumnsByBoardId(Long boardId, Long userId) {
        boardRepository.findByIdAndUser_Id(boardId, userId)
                .orElseThrow(() -> new AccessDeniedException("Access denied"));

        return columnRepository.findByBoardIdOrderByPositionAsc(boardId);
    }

    @Transactional
    public Board deleteColumn(Long columnId, Long userId) {
        BoardColumn column = columnRepository.findByIdWithBoardAndUser(columnId)
                .orElseThrow(() -> new EntityNotFoundException("Колонка не найдена"));

        // Проверка прав
        if (!column.getBoard().getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Нет прав на эту колонку");
        }

        Board board = column.getBoard();
        Long boardId = board.getId();
        int deletedPosition = column.getPosition();

        // Удаление колонки (каскадно удалятся карточки и комментарии)
        columnRepository.delete(column);

        // Переиндексация оставшихся колонок
        columnRepository.decrementPositionsAfter(boardId, deletedPosition);

        return board;
    }

}

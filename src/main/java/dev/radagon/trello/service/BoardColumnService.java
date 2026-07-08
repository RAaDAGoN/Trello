package dev.radagon.trello.service;

import dev.radagon.trello.dto.BoardColumnDTO;
import dev.radagon.trello.entity.Board;
import dev.radagon.trello.entity.BoardColumn;
import dev.radagon.trello.repository.BoardColumnRepository;
import dev.radagon.trello.repository.BoardRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BoardColumnService {
    private BoardColumnRepository boardColumnRepository;
    private BoardRepository boardRepository;

    /**
     * Поиск доски по id доски и юзеру
     * Получение максимальной позиции в доске
     * если колонок нет, то -1
     */

    public BoardColumn createColumn(BoardColumnDTO boardColumnDTO, Long boardId, Long userId) {
        Board board = boardRepository.findByIdAndUser_Id(boardId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Board Not Found"));

        int maxPosition = boardColumnRepository.findMaxPositionByBoardId(boardId)
                .orElse(-1);

        BoardColumn boardColumn = BoardColumn.builder()
                .board(board)
                .name(boardColumnDTO.getName())
                .position(maxPosition + 1)
                .build();

        return boardColumnRepository.save(boardColumn);
    }

    /**
     * Проверяем, есть ли право у юзера смотреть доску.
     * Возвращаем список колонок в доске
     */

    public List<BoardColumn> getBoardColumnsByBoardId(Long boardId, Long userId) {
        boardRepository.findByIdAndUser_Id(boardId, userId)
                .orElseThrow(() -> new AccessDeniedException("Access denied"));

        return boardColumnRepository.findByBoardIdOrderByPositionAsc(boardId);
    }

}

package dev.radagon.trello.service;

import dev.radagon.trello.dto.BoardDTO;
import dev.radagon.trello.entity.Board;
import dev.radagon.trello.entity.User;
import dev.radagon.trello.repository.BoardRepository;
import dev.radagon.trello.repository.UserRepository;
import dev.radagon.trello.utils.SlugGenerator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final SlugGenerator slugGenerator;

    public Board createBoard(BoardDTO boardDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Board newBoard = Board.builder()
                .user(user)
                .name(boardDTO.getName())
                .description(boardDTO.getDescription())
                .createdAt(LocalDate.now().toString())
                .slug(slugGenerator.generateSlug(boardDTO.getName()))
                .build();

        return boardRepository.save(newBoard);
    }

    public Board getBoardBySlug(String slug, Long userId) {
        Board board = boardRepository.findBySlugAndUser_Id(slug, userId)
                .orElseThrow(() -> new AccessDeniedException("Access denied"));

        if (!board.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Access denied");
        }

        return board;
    }

    /*
    Возвращает список досок конкретного пользователя
     */
    public List<Board> getBoardByOwner(Long userId) {
        return boardRepository.findByUser_Id(userId);
    }

    public User getOwnerByPublicId(String publicId) {
        return userRepository.findByPublicId(publicId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public User getUserByEmail(String currentEmail) {
        return  userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public Board getBoardById(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found"));
    }
}

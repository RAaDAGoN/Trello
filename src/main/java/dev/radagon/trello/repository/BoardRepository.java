package dev.radagon.trello.repository;

import dev.radagon.trello.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board,Long> {
    List<Board> findByUser_id(Long id);

    Board findBySlugAndUser_Id(String slug, Long userId);
    boolean existsBySlugAndUser_Id(String slug, Long userId);

    Optional<Board> findByIdAndUser_Id(Long id, Long userId);

    Optional<Board> findBoardByUser_Id(Long userId);
}

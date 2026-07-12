package dev.radagon.trello.repository;

import dev.radagon.trello.entity.Board;
import dev.radagon.trello.entity.BoardColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardColumnRepository extends JpaRepository<BoardColumn,Long> {
    List<BoardColumn> getBoardColumnsByBoard_Id(Long id);
//    List<BoardColumn> findboardcolumn(Long id, Long userId);

    @Query("SELECT MAX(c.position) FROM BoardColumn c WHERE c.board.id = :boardId")
    Optional<Integer> findMaxPositionByBoardId(@Param("boardId") Long boardId);

    List<BoardColumn> findByBoardIdOrderByPositionAsc(Long id);

//    Optional<BoardColumn> findByIdWithBoardAndUser(Long columnId);


    @Query("""
        SELECT c FROM BoardColumn c
        JOIN FETCH c.board b
        join fetch b.user u
        where c.id = :columnId
    """)
    Optional<BoardColumn> findByIdWithBoardAndUser(@Param("columnId") Long columnID);

    @Modifying
    @Query("""
        UPDATE BoardColumn c
        SET c.position = c.position - 1
        WHERE c.board.id = :boardId AND c.position > :deletedPosition
    """)
    void decrementPositionsAfter(@Param("boardId") Long boardId, @Param("deletedPosition") int deletedPosition);
}

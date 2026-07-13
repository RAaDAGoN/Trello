package dev.radagon.trello.repository;

import dev.radagon.trello.entity.CardComment;
import dev.radagon.trello.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CardCommentRepository extends JpaRepository<CardComment,Long> {

    @Query("""
        SELECT c FROM CardComment c
        JOIN FETCH c.card card
        JOIN FETCH card.column col
        JOIN FETCH col.board b
        JOIN FETCH b.user u
        WHERE c.id = :commentId
    """)
    Optional<CardComment> findByIdWithFullHierarchy(@Param("commentId") Long commentId);

}

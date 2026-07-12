package dev.radagon.trello.repository;

import dev.radagon.trello.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card,Long> {
    @Query("""
        SELECT c FROM Card c
        JOIN FETCH c.column col
        JOIN FETCH col.board b
        JOIN FETCH b.user u
        WHERE c.id = :cardId
    """)
    Optional<Card> findByIdWithFullHierarchy(@Param("cardId") Long cardId);
}

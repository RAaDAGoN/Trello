package dev.radagon.trello.repository;

import dev.radagon.trello.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card,Long> {
}

package dev.radagon.trello.repository;

import dev.radagon.trello.entity.CardComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardCommentRepository extends JpaRepository<CardComment,Long> {
}

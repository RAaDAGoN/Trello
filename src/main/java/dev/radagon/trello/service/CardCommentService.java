package dev.radagon.trello.service;

import dev.radagon.trello.dto.CardCommentDTO;
import dev.radagon.trello.entity.Card;
import dev.radagon.trello.entity.CardComment;
import dev.radagon.trello.entity.User;
import dev.radagon.trello.repository.CardCommentRepository;
import dev.radagon.trello.repository.CardRepository;
import dev.radagon.trello.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class CardCommentService {
    private final CardCommentRepository commentRepository;
    private final CardRepository cardRepository;

    @Transactional
    public CardComment createComment(Long cardId, CardCommentDTO dto, Long userId) {
        // получаем карточку
        Card card = cardRepository.findByIdWithFullHierarchy(cardId)
                .orElseThrow(()-> new EntityNotFoundException("Card with id: " + cardId + " not found"));

        // проверка прав по цепочке
        Long boardOwnerId = card.getColumn().getBoard().getUser().getId();
        if (!boardOwnerId.equals(userId)) {
            throw new AccessDeniedException("Not access");
        }

        User user = card.getColumn().getBoard().getUser();

        CardComment comment = CardComment.builder()
                .card(card)
                .user(user)
                .comment(dto.getComment())
                .build();

        return commentRepository.save(comment);
    }

    @Transactional
    public Card deleteComment(Long commentId, Long userId) {
        CardComment comment = commentRepository.findByIdWithFullHierarchy(commentId)
                .orElseThrow(()-> new EntityNotFoundException("Card with id: " + commentId + " not found"));

        // проверка прав по цепочке
        Long boardOwnerId = comment.getCard().getColumn().getBoard().getUser().getId();
        Long commentAuthorId = comment.getUser().getId();
        if (!boardOwnerId.equals(userId) && !commentAuthorId.equals(userId)) {
            throw new AccessDeniedException("Not access");
        }

        Card card = comment.getCard();
        commentRepository.delete(comment);

        return card;
    }
}

package dev.radagon.trello.service;

import dev.radagon.trello.dto.CardDTO;
import dev.radagon.trello.entity.Board;
import dev.radagon.trello.entity.BoardColumn;
import dev.radagon.trello.entity.Card;
import dev.radagon.trello.entity.User;
import dev.radagon.trello.repository.BoardColumnRepository;
import dev.radagon.trello.repository.CardRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CardService {
    private final CardRepository cardRepository;
    private final BoardColumnRepository columnRepository;

    private final BoardColumnRepository boardColumnRepository;

    public Card createCard(Long columnId, CardDTO cardDTO, Long userId) {
        BoardColumn column = columnRepository.findByIdWithBoardAndUser(columnId)
                .orElseThrow(()-> new EntityNotFoundException("Column with id: " + columnId + " not found"));

        User boardOwner = column.getBoard().getUser();
        if (!boardOwner.getId().equals(userId)) {
            throw new AccessDeniedException("You are not allowed to perform this action");
        }

        int maxPosition = boardColumnRepository.findMaxPositionByBoardId(columnId)
                .orElse(-1);

        Card card = Card.builder()
                .column(column)
                .userId(boardOwner)
                .title(cardDTO.getTitle())
                .description(cardDTO.getDescription())
                .position(maxPosition + 1)
                .build();

        return cardRepository.save(card);
    }

    /**
     * Ищет card, далее проверяет права через цепочку
     * сохраняет изменения в бд
     */

    @Transactional
    public Card update(CardDTO cardDTO, Long cardId, Long userId) {
        Card card = cardRepository.findByIdWithFullHierarchy(cardId)
                .orElseThrow(()-> new EntityNotFoundException("Card with id: " + cardId + " not found"));

        if (!card.getColumn().getBoard().getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You are not allowed to perform this action");
        }

        card.setTitle(cardDTO.getTitle());
        card.setDescription(cardDTO.getDescription());

        return cardRepository.save(card);
    }

    @Transactional
    public Card deleteCard(Long cardId, Long userId) {
        Card card = cardRepository.findByIdWithFullHierarchy(cardId)
                .orElseThrow(()-> new EntityNotFoundException("Card with id: " + cardId + " not found"));

        if (!card.getColumn().getBoard().getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You are not allowed to perform this action");
        }


        cardRepository.delete(card);

        return card;
    }

    @Transactional
    public Card toggleCardCompleted(Long cardId, Long userId) {
        Card card = cardRepository.findByIdWithFullHierarchy(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Карточка не найдена"));

        // Проверка прав
        if (!card.getColumn().getBoard().getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Нет прав на эту карточку");
        }

        // Переключаем флаг
        card.setCompleted(!card.getCompleted());
        return cardRepository.save(card);
    }
}

package dev.radagon.trello.service;

import dev.radagon.trello.dto.CardDTO;
import dev.radagon.trello.entity.BoardColumn;
import dev.radagon.trello.entity.Card;
import dev.radagon.trello.entity.User;
import dev.radagon.trello.repository.BoardColumnRepository;
import dev.radagon.trello.repository.CardRepository;
import jakarta.persistence.EntityNotFoundException;
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
}

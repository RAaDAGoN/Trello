package dev.radagon.trello.controller;

import dev.radagon.trello.dto.CardDTO;
import dev.radagon.trello.entity.BoardColumn;
import dev.radagon.trello.entity.Card;
import dev.radagon.trello.entity.User;
import dev.radagon.trello.service.BoardColumnService;
import dev.radagon.trello.service.BoardService;
import dev.radagon.trello.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequiredArgsConstructor
@RequestMapping("/{publicId}")
public class CardController {
    private final CardService cardService;
    private final BoardService boardService;
    private final BoardColumnService columnService;

    @PostMapping("/columns/{columnId}/cards")
    public String createCard(
            @PathVariable String publicId,
            @PathVariable Long columnId,
            @ModelAttribute CardDTO dto,
            Authentication authentication) {
        String email = authentication.getName();

        User owner = boardService.getOwnerByPublicId(publicId);
        User currentUser = boardService.getUserByEmail(email);

        try {
            Card card = cardService.createCard(columnId, dto, currentUser.getId());
            BoardColumn column = card.getColumn();
            return "redirect:/" + publicId + "/" + column.getBoard().getSlug();
        } catch (AccessDeniedException e) {
            throw new AccessDeniedException(HttpStatus.FORBIDDEN.toString());
        }
    }
}

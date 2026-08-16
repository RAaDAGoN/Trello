package dev.radagon.trello.controller;

import dev.radagon.trello.config.SecurityHelper;
import dev.radagon.trello.dto.CardDTO;
import dev.radagon.trello.entity.Board;
import dev.radagon.trello.entity.BoardColumn;
import dev.radagon.trello.entity.Card;
import dev.radagon.trello.entity.User;
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
import org.springframework.web.server.ResponseStatusException;


@Controller
@RequiredArgsConstructor
@RequestMapping("/{publicId}")
public class CardController {
    private final CardService cardService;
    private final SecurityHelper securityHelper;

    @PostMapping("/columns/{columnId}/cards")
    public String createCard(
            @PathVariable String publicId,
            @PathVariable Long columnId,
            @ModelAttribute CardDTO dto,
            Authentication authentication) {

        User currentUser = securityHelper.getAuthenticatedUser(publicId, authentication);
        if (currentUser == null) {return  "redirect:/login";}

        Card card = cardService.createCard(columnId, dto, currentUser.getId());
        return "redirect:/" + publicId + "/" + card.getColumn().getBoard().getSlug();
    }

    @PostMapping("/cards/{cardId}/update")
    public String updateCard(
            @PathVariable String publicId,
            @PathVariable Long cardId,
            @ModelAttribute CardDTO dto,
            Authentication authentication) {

        User currentUser = securityHelper.getAuthenticatedUser(publicId, authentication);
        if (currentUser == null) {return  "redirect:/login";}

        Card card = cardService.update(dto, cardId, currentUser.getId());
        return "redirect:/" + publicId + "/" + card.getColumn().getBoard().getSlug();
    }

    @PostMapping("/cards/{cardId}/delete")
    public String deleteCard(
            @PathVariable String publicId,
            @PathVariable Long cardId,
            Authentication authentication) {

        User currentUser = securityHelper.getAuthenticatedUser(publicId, authentication);
        if (currentUser == null) {return  "redirect:/login";}

        Card card = cardService.deleteCard(cardId, currentUser.getId());
        return "redirect:/" + publicId + "/" + card.getColumn().getBoard().getSlug();
    }

    @PostMapping("/cards/{cardId}/toggle")
    public String toggleCardCompleted(
            @PathVariable String publicId,
            @PathVariable Long cardId,
            Authentication authentication) {

        User currentUser = securityHelper.getAuthenticatedUser(publicId, authentication);
        if (currentUser == null) {return  "redirect:/login";}

        Card card = cardService.toggleCardCompleted(cardId, currentUser.getId());
        return "redirect:/" + publicId + "/" + card.getColumn().getBoard().getSlug();
    }
}

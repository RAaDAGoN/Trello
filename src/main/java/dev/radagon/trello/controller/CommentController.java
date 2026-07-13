package dev.radagon.trello.controller;

import dev.radagon.trello.dto.CardCommentDTO;
import dev.radagon.trello.entity.Card;
import dev.radagon.trello.entity.CardComment;
import dev.radagon.trello.entity.User;
import dev.radagon.trello.service.BoardService;
import dev.radagon.trello.service.CardCommentService;
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
public class CommentController {
    private final CardCommentService commentService;

    private final BoardService boardService;

    @PostMapping("/cards/{cardId}/comments")
    public String createComment(
            @PathVariable String publicId,
            @PathVariable Long cardId,
            @ModelAttribute CardCommentDTO dto,
            Authentication authentication) {

        String currentEmail = authentication.getName();
        User user = boardService.getUserByEmail(currentEmail);

        try {
            CardComment comment = commentService.createComment(cardId, dto, user.getId());
            Card card = comment.getCard();
            return "redirect:/" + publicId + "/" + card.getColumn().getBoard().getSlug();
        } catch (AccessDeniedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/comments/{commentId}/delete")
    public String deleteComment(
            @PathVariable String publicId,
            @PathVariable Long commentId,
            Authentication authentication){

        String currentEmail = authentication.getName();
        User user = boardService.getUserByEmail(currentEmail);

        try {
            Card card = commentService.deleteComment(commentId, user.getId());
            return "redirect:/" + publicId + "/" + card.getColumn().getBoard().getSlug();
        } catch (AccessDeniedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }
}

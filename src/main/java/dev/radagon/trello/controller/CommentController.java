package dev.radagon.trello.controller;

import dev.radagon.trello.config.SecurityHelper;
import dev.radagon.trello.dto.CardCommentDTO;
import dev.radagon.trello.entity.Card;
import dev.radagon.trello.entity.CardComment;
import dev.radagon.trello.entity.User;
import dev.radagon.trello.service.CardCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/{publicId}")
public class CommentController {
    private final CardCommentService commentService;
    private final SecurityHelper securityHelper;

    @PostMapping("/cards/{cardId}/comments")
    public String createComment(
            @PathVariable String publicId,
            @PathVariable Long cardId,
            @ModelAttribute CardCommentDTO dto,
            Authentication authentication) {

        User currentUser = securityHelper.getAuthenticatedUser(publicId, authentication);
        if (currentUser == null) {return  "redirect:/login";}

        CardComment comment = commentService.createComment(cardId, dto, currentUser.getId());
        return "redirect:/" + publicId + "/" + comment.getCard().getColumn().getBoard().getSlug();
    }

    @PostMapping("/comments/{commentId}/delete")
    public String deleteComment(
            @PathVariable String publicId,
            @PathVariable Long commentId,
            Authentication authentication){

        User currentUser = securityHelper.getAuthenticatedUser(publicId, authentication);
        if (currentUser == null) {return  "redirect:/login";}

        Card card = commentService.deleteComment(commentId, currentUser.getId());
        return "redirect:/" + publicId + "/" + card.getColumn().getBoard().getSlug();
    }
}

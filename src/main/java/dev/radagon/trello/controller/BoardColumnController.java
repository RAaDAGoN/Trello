package dev.radagon.trello.controller;

import dev.radagon.trello.config.SecurityHelper;
import dev.radagon.trello.dto.BoardColumnDTO;
import dev.radagon.trello.entity.Board;
import dev.radagon.trello.entity.BoardColumn;
import dev.radagon.trello.entity.User;
import dev.radagon.trello.service.BoardColumnService;
import dev.radagon.trello.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/{publicId}")
public class BoardColumnController {
    private final BoardColumnService columnService;
    private final BoardService boardService;

    private final SecurityHelper securityHelper;

    @PostMapping("/boards/{boardId}/columns")
    public String createColumn(
            @PathVariable String publicId,
            @PathVariable Long boardId,
            @ModelAttribute BoardColumnDTO dto,
            Authentication authentication
            ) {

        User currentUser = securityHelper.getAuthenticatedUser(publicId, authentication);
        if (currentUser == null) {return  "redirect:/login";}

        columnService.createColumn(dto, boardId, currentUser.getId());

        Board board = boardService.getBoardById(boardId);
        return "redirect:/" + publicId + "/" + board.getSlug();
    }

    @PostMapping("/columns/{columnId}/update")
    public String updateColumn(
            @PathVariable String publicId,
            @PathVariable Long columnId,
            @ModelAttribute BoardColumnDTO dto,
            Authentication authentication) {

        User currentUser = securityHelper.getAuthenticatedUser(publicId, authentication);
        if (currentUser == null) {return  "redirect:/login";}

        BoardColumn column = columnService.updateColumn(columnId, dto, currentUser.getId());
        return "redirect:/" + publicId + "/" + column.getBoard().getSlug();
    }

    @PostMapping("/columns/{columnId}/delete")
    public String deleteColumn(
            @PathVariable String publicId,
            @PathVariable Long columnId,
            Authentication authentication) {

        User currentUser = securityHelper.getAuthenticatedUser(publicId, authentication);
        if (currentUser == null) {return  "redirect:/login";}

        Board board = columnService.deleteColumn(columnId,  currentUser.getId());
        return "redirect:/" + publicId + "/" + board.getSlug();
    }
}

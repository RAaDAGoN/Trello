package dev.radagon.trello.controller;

import dev.radagon.trello.dto.BoardColumnDTO;
import dev.radagon.trello.entity.Board;
import dev.radagon.trello.entity.BoardColumn;
import dev.radagon.trello.entity.User;
import dev.radagon.trello.service.BoardColumnService;
import dev.radagon.trello.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/{publicId}")
public class BoardColumnController {
    private final BoardColumnService columnService;
    private final BoardService boardService;

    @PostMapping("/boards/{boardId}/columns")
    public String createColumn(
            @PathVariable String publicId,
            @PathVariable Long boardId,
            @ModelAttribute BoardColumnDTO dto,
            Authentication auth
            ) {

        String currentEmail = auth.getName();

//        User owner = boardService.getOwnerByPublicId(publicId);
        User currentUser = boardService.getUserByEmail(currentEmail);


        try {
            columnService.createColumn(dto, boardId, currentUser.getId());
        } catch (AccessDeniedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нет прав на эту доску");
        }

        Board board = boardService.getBoardById(boardId);

        return "redirect:/" + publicId + "/" + board.getSlug();
    }

    @PostMapping("/columns/{columnId}/update")
    public String updateColumn(
            @PathVariable String publicId,
            @PathVariable Long columnId,
            @ModelAttribute BoardColumnDTO dto,
            Authentication auth) {

        String currentEmail = auth.getName();
        User currentUser = boardService.getUserByEmail(currentEmail);

        BoardColumn column = columnService.updateColumn(columnId, dto, currentUser.getId());

        Board board = column.getBoard();

        return "redirect:/" + publicId + "/" + board.getSlug();
    }

    @PostMapping("/columns/{columnId}/delete")
    public String deleteColumn(
            @PathVariable String publicId,
            @PathVariable Long columnId,
            Authentication auth) {

        String currentEmail = auth.getName();
        User currentUser = boardService.getUserByEmail(currentEmail);

        Board board = columnService.deleteColumn(columnId,  currentUser.getId());

        return "redirect:/" + publicId + "/" + board.getSlug();
    }
}

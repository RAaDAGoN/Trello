package dev.radagon.trello.controller;

import dev.radagon.trello.config.SecurityHelper;
import dev.radagon.trello.dto.BoardDTO;
import dev.radagon.trello.entity.Board;
import dev.radagon.trello.entity.User;
import dev.radagon.trello.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/{publicId}")
public class BoardController {
    private final BoardService boardService;
    private final SecurityHelper securityHelper;

    @GetMapping("/boards")
    public String boards(
            @PathVariable String publicId,
            Model model,
            Authentication authentication) {

        User currentUser = securityHelper.getAuthenticatedUser(publicId, authentication);
        if (currentUser == null) {return  "redirect:/login";}

        List<Board> boards = boardService.getBoardByOwner(currentUser.getId());

        model.addAttribute("boards", boards);
        model.addAttribute("user", currentUser);
        model.addAttribute("publicId", publicId);
        model.addAttribute("boardDto", new BoardDTO());
        model.addAttribute("updateColumnDTO", new BoardDTO());

        return "boards/boards";
    }

    @PostMapping("/boards")
    public String createBoard(
            @PathVariable String publicId,
            @ModelAttribute BoardDTO boardDTO,
            Authentication authentication) {
        User currentUser = securityHelper.getAuthenticatedUser(publicId, authentication);
        if (currentUser == null) {return  "redirect:/login";}

        Board board = boardService.createBoard(boardDTO, currentUser.getId());
        return "redirect:/"+publicId+ "/" + board.getSlug();
    }

    @GetMapping("/{boardSlug}")
    public String viewBoard(
            @PathVariable String publicId,
            @PathVariable String boardSlug,
            Model model,
            Authentication authentication) {
        User currentUser = securityHelper.getAuthenticatedUser(publicId, authentication);
        if (currentUser == null) {return  "redirect:/login";}

        Board board = boardService.getBoardBySlug(boardSlug, currentUser.getId());

        model.addAttribute("board", board);
        model.addAttribute("user", currentUser);
        model.addAttribute("publicId", publicId);

        return "boards/board";
    }

    @PostMapping("/boards/{boardId}/update")
    public String updateBoard(
            @PathVariable String publicId,
            @PathVariable Long boardId,
            @ModelAttribute BoardDTO boardDTO,
            Authentication authentication) {

        User currentUser = securityHelper.getAuthenticatedUser(publicId, authentication);
        if (currentUser == null) {return  "redirect:/login";}

        Board board = boardService.updateBoard(boardId, boardDTO, currentUser.getId());
        return "redirect:/"+publicId+ "/" + board.getSlug();
    }
}

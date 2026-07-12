package dev.radagon.trello.controller;

import dev.radagon.trello.dto.BoardDTO;
import dev.radagon.trello.entity.Board;
import dev.radagon.trello.entity.User;
import dev.radagon.trello.repository.UserRepository;
import dev.radagon.trello.service.BoardService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@AllArgsConstructor
public class BoardController {
    private final BoardService boardService;
    private final UserRepository userRepository;

    @GetMapping("/{publicId}/boards")
    public String boards(
            @PathVariable String publicId,
            Model model,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()
        || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }

        String currentEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElse(null);

        if (currentUser == null) {
            return "redirect:/login";
        }

        if (!currentUser.getPublicId().equals(publicId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No access");
        }

        List<Board> boards = boardService.getBoardByOwner(currentUser.getId());

        model.addAttribute("boards", boards);
        model.addAttribute("user", currentUser);
        model.addAttribute("publicId", publicId);
        model.addAttribute("boardDto", new BoardDTO());
        model.addAttribute("updateColumnDTO", new BoardDTO());

        return "boards/boards";
    }

    @PostMapping("/{publicId}/boards")
    public String createBoard(
            @PathVariable String publicId,
            @ModelAttribute BoardDTO boardDTO,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }

        String currentEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElse(null);

        if (currentUser == null) {
            return "redirect:/login";
        }

        if (!currentUser.getPublicId().equals(publicId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No access");
        }

        Board board = boardService.createBoard(boardDTO, currentUser.getId());

        return "redirect:/"+publicId+ "/" + board.getSlug();
    }

    @GetMapping("/{publicId}/{boardSlug}")
    public String viewBoard(
            @PathVariable String publicId,
            @PathVariable String boardSlug,
            Model model,
            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }

        String currentEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElse(null);

        if (currentUser == null) {
            return "redirect:/login";
        }

        if (!currentUser.getPublicId().equals(publicId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No access");
        }

        Board board = boardService.getBoardBySlug(boardSlug, currentUser.getId());

        model.addAttribute("board", board);
        model.addAttribute("user", currentUser);
        model.addAttribute("publicId", publicId);

        return "boards/board";
    }
}

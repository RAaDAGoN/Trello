package dev.radagon.trello.controller;

import dev.radagon.trello.dto.BoardDTO;
import dev.radagon.trello.entity.Board;
import dev.radagon.trello.entity.User;
import dev.radagon.trello.repository.UserRepository;
import dev.radagon.trello.service.BoardService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@AllArgsConstructor
public class BoardController {
    private final BoardService boardService;
    private final UserRepository userRepository;

    @GetMapping("/{publicId}/boards")
    public String boards(@PathVariable String publicId, Model model) {
        User user = userRepository.findByPublicId(publicId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Board> boards = boardService.getBoardByOwner(user.getId());

        model.addAttribute("boards", boards);
        model.addAttribute("user", user);
        model.addAttribute("publicId", publicId);
        model.addAttribute("boardDto", new BoardDTO());

        return "boards/boards";
    }

    @PostMapping("/{publicId}/boards")
    public String createBoard(@PathVariable String publicId, @ModelAttribute BoardDTO boardDTO) {
        User user = userRepository.findByPublicId(publicId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Board board = boardService.createBoard(boardDTO, user.getId());

        return "redirect:/"+publicId+ "/" + board.getSlug();
    }

    @GetMapping("/{publicId}/{boardSlug}")
    public String viewBoard(@PathVariable String publicId, @PathVariable String boardSlug, Model model) {
        User user = userRepository.findByPublicId(publicId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Board board = boardService.getBoardBySlug(boardSlug, user.getId());

        model.addAttribute("board", board);
        model.addAttribute("user", user);
        model.addAttribute("publicId", publicId);

        return "boards/board";
    }
}

package dev.radagon.trello.controller;

import dev.radagon.trello.entity.User;
import dev.radagon.trello.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public String userPage(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);

        return "user";
    }
}

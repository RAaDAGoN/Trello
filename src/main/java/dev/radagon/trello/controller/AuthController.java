package dev.radagon.trello.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;


@Controller
@AllArgsConstructor
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "Login/Login";
    }
}

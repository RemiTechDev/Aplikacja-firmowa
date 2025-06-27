package com.aplikacja.Aplikacja.firmowa.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("message", "Witaj w aplikacji firmowej!");
        return "layout";
    }

    @GetMapping("/about")
    public String about() {
        return "layout";
    }

//    @GetMapping("/login")
//    public String login() {
//        return "login";
//    }

//    @GetMapping("/register")
//    public String register() {
//        return "register";
//    }
}
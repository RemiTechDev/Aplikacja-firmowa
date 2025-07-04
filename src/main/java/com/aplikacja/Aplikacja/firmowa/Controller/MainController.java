package com.aplikacja.Aplikacja.firmowa.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String home(Model model) {
//        model.addAttribute("content", "index :: content");
        model.addAttribute("title", "Strona Główna");
        return "index";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("content", "about :: content");
        model.addAttribute("title", "O aplikacji");
        return "about";
    }
}

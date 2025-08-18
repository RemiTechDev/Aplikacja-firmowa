package com.aplikacja.Aplikacja.firmowa.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MainController {

    @GetMapping("/")
    public String home(Model model,
                       @RequestParam(value = "sent", required = false) String sent) {
        model.addAttribute("title", "Strona główna");
        if ("1".equals(sent)) {
            model.addAttribute("successMessage", "Dziękujemy! Twoja wiadomość została wysłana.");
        }
        return "index";
    }

    // prosty handler formularza kontaktowego
    @PostMapping("/contact")
    public String handleContact(@RequestParam String name,
                                @RequestParam String email,
                                @RequestParam String subject,
                                @RequestParam String message,
                                RedirectAttributes ra) {
        // Tu możesz dodać wysyłkę maila / zapis do bazy / logowanie itp.
        // Na razie tylko sygnalizujemy sukces:
        ra.addFlashAttribute("successMessage", "Dziękujemy! Twoja wiadomość została wysłana.");
        return "redirect:/?sent=1#contact";
    }

    // (opcjonalnie) /about – jeśli chcesz zachować oddzielny widok
    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "O aplikacji");
        return "index"; // landing ma sekcję #about-app – nie potrzebujemy oddzielnego widoku
    }
}
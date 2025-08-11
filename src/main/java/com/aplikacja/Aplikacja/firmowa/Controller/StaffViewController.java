package com.aplikacja.Aplikacja.firmowa.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaffViewController {

    @GetMapping("/staff/dashboard")
    public String staffDashboard(Model model) {
        model.addAttribute("title", "Panel Pracownika");
        // Dodaj inne dane, które chcesz wyświetlić
        return "staff_dashboard";
    }
}

package com.aplikacja.Aplikacja.firmowa.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ManagerViewController {

    @GetMapping("/manager/dashboard")
    public String managerDashboard(Model model) {
        model.addAttribute("title", "Panel Managera");
        // Dodaj inne dane, które chcesz wyświetlić
        return "manager_dashboard";
    }
}

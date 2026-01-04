package com.aplikacja.Aplikacja.firmowa.Controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Set;

@Controller
public class PanelRedirectController {

    // UWAGA: tylko te dwa adresy — NIE dodawaj
    @GetMapping({"/panel", "/go-to-panel"})
    public String toPanel(Authentication auth) {
        if (auth == null) return "redirect:/login";

        Set<String> roles = AuthorityUtils.authorityListToSet(auth.getAuthorities());

        if (roles.contains("ROLE_ADMIN_ROLE"))   return "redirect:/admin/dashboard/extended";
        if (roles.contains("ROLE_MANAGER_ROLE")) return "redirect:/manager/dashboard";
        if (roles.contains("ROLE_STAFF_ROLE"))   return "redirect:/staff/dashboard";

        // USER
        return "redirect:/user/dashboard";
    }
}
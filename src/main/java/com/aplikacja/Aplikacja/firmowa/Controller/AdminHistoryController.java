package com.aplikacja.Aplikacja.firmowa.Controller;

import com.aplikacja.Aplikacja.firmowa.Repositories.LoginHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/history")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN_ROLE','ROLE_MANAGER_ROLE')") // admin + manager
public class AdminHistoryController {

    private final LoginHistoryRepository loginHistoryRepository;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size,
                       Model model) {

        Page<?> events = loginHistoryRepository.findAllByOrderByLoginTimeDesc(PageRequest.of(page, size));
        model.addAttribute("events", events);
        return "admin/history"; // templates/admin/history.html
    }

    @PostMapping("/delete-selected")
    public String deleteSelected(@RequestParam(name = "ids", required = false) List<Long> ids,
                                 RedirectAttributes ra) {

        if (ids == null || ids.isEmpty()) {
            ra.addFlashAttribute("error", "Zaznacz co najmniej jeden wpis do usunięcia.");
            return "redirect:/admin/history";
        }

        // Bezpieczne i kompatybilne na każdej wersji Spring Data:
        loginHistoryRepository.deleteAll(
                loginHistoryRepository.findAllById(ids)
        );

        ra.addFlashAttribute("message", "Wybrane wpisy zostały usunięte.");
        return "redirect:/admin/history";
    }

    @PostMapping("/delete-all")
    public String deleteAll(RedirectAttributes ra) {
        long count = loginHistoryRepository.count();
        loginHistoryRepository.deleteAll();
        ra.addFlashAttribute("message", "Usunięto wszystkie wpisy (" + count + ").");
        return "redirect:/admin/history";
    }
}
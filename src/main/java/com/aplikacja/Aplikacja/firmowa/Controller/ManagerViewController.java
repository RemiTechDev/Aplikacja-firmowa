package com.aplikacja.Aplikacja.firmowa.Controller;

import com.aplikacja.Aplikacja.firmowa.Repositories.DocumentRepository;
import com.aplikacja.Aplikacja.firmowa.Repositories.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/manager")
@PreAuthorize("hasRole('MANAGER_ROLE')")
public class ManagerViewController {

    private final MeetingRepository meetingRepository;
    private final DocumentRepository documentRepository;

    @GetMapping({"/dashboard", "/dashboard/extended"})
    public String managerDashboard(Model model) {
        model.addAttribute("upcomingMeetings", meetingRepository.findTop5ByOrderByDateTimeAsc());
        model.addAttribute("latestDocuments", documentRepository.findTop2ByOrderByCreatedDesc());
        return "manager_dashboard";
    }

    @GetMapping("/documents")
    public String managerDocuments(Model model) {
        model.addAttribute("docs", documentRepository.findAllByOrderByCreatedDesc());
        return "manager_documents";
    }
}
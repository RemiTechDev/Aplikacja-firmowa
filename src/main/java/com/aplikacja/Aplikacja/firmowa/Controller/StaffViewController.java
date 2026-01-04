package com.aplikacja.Aplikacja.firmowa.Controller;

import com.aplikacja.Aplikacja.firmowa.Repositories.DocumentRepository;
import com.aplikacja.Aplikacja.firmowa.Repositories.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@RequestMapping("/staff")
@PreAuthorize("hasRole('STAFF_ROLE')")
public class StaffViewController {

    private final MeetingRepository meetingRepository;
    private final DocumentRepository documentRepository;

    @GetMapping({"/dashboard", "/dashboard/extended"})
    public String staffDashboard(Model model) {
        model.addAttribute("upcomingMeetings",
                meetingRepository.findUpcoming(LocalDateTime.now(), PageRequest.of(0, 5)));
        model.addAttribute("latestDocuments",
                documentRepository.findTop2ByOrderByCreatedDesc());
        return "staff_dashboard";
    }

    @GetMapping("/documents")
    public String staffDocuments(Model model) {
        model.addAttribute("docs", documentRepository.findAllByOrderByCreatedDesc());
        return "staff_documents";
    }
}

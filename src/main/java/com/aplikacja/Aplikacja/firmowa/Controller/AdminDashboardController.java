package com.aplikacja.Aplikacja.firmowa.Controller;

import com.aplikacja.Aplikacja.firmowa.Model.Meeting;
import com.aplikacja.Aplikacja.firmowa.Repositories.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN_ROLE')")
public class AdminDashboardController {

    private final MeetingRepository meetingRepository;



    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());

        List<Meeting> upcomingMeetings = meetingRepository.findTop5ByOrderByDateTimeAsc();
        model.addAttribute("upcomingMeetings", upcomingMeetings);

        return "admin_dashboard";
    }
}
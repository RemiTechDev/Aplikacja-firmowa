package com.aplikacja.Aplikacja.firmowa.Controller;

import com.aplikacja.Aplikacja.firmowa.Model.Document;
import com.aplikacja.Aplikacja.firmowa.Model.Meeting;
import com.aplikacja.Aplikacja.firmowa.Model.User;
import com.aplikacja.Aplikacja.firmowa.Repositories.DocumentRepository;
import com.aplikacja.Aplikacja.firmowa.Repositories.MeetingRepository;
import com.aplikacja.Aplikacja.firmowa.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
@PreAuthorize("hasRole('USER_ROLE')")
public class UserViewController {

    private final MeetingRepository meetingRepository;
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;

    @GetMapping({"/dashboard", "/dashboard/extended"})
    public String userDashboard(Model model, Principal principal) {

        String login = (principal != null) ? principal.getName() : null;

        if (login != null) {
            userRepository.findByLogin(login).ifPresent(u -> model.addAttribute("user", u));
        }

        List<Meeting> upcomingMeetings = (login != null)
                ? meetingRepository.findByUser_Login(login).stream()
                .sorted(Comparator.comparing(Meeting::getDateTime))
                .limit(5)
                .toList()
                : List.of();
        model.addAttribute("upcomingMeetings", upcomingMeetings);

        List<Document> latestDocuments = (login != null)
                ? documentRepository.findByUser_Login(login).stream()
                .sorted(Comparator.comparing(Document::getCreated, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .limit(2)
                .toList()
                : List.of();
        model.addAttribute("latestDocuments", latestDocuments);

        return "user_dashboard";
    }
}

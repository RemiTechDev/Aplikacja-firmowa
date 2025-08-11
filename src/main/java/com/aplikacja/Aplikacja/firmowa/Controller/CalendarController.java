package com.aplikacja.Aplikacja.firmowa.Controller;

import com.aplikacja.Aplikacja.firmowa.Model.ERoles;
import com.aplikacja.Aplikacja.firmowa.Model.Meeting;
import com.aplikacja.Aplikacja.firmowa.Model.User;
import com.aplikacja.Aplikacja.firmowa.Repositories.MeetingRepository;
import com.aplikacja.Aplikacja.firmowa.Service.CalendarService;
import com.aplikacja.Aplikacja.firmowa.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;
    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;

    @GetMapping("/calendar")
    public String calendar(Model model, Principal principal) {
        List<List<LocalDate>> calendarWeeks = calendarService.generateCalendarWeeks();
        model.addAttribute("calendarWeeks", calendarWeeks);

        var ym = YearMonth.now();
        var next = ym.plusMonths(1);
        var pl = new Locale("pl", "PL");
        model.addAttribute("currentMonthName", ym.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, pl));
        model.addAttribute("currentYear", ym.getYear());
        model.addAttribute("nextMonthName", next.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, pl));
        model.addAttribute("nextYear", next.getYear());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String username = principal != null ? principal.getName() : null;
        Set<String> roleNames = Set.of();
        if (username != null) {
            userRepository.findByLogin(username).ifPresent(u -> model.addAttribute("roles", u.getRoles()));
            roleNames = userRepository.findByLogin(username)
                    .map(User::getRoles).orElseGet(Set::of)
                    .stream().map(r -> r.getName().name())
                    .collect(Collectors.toSet());
        }

        List<Meeting> meetings;
        if (roleNames.contains("ADMIN_ROLE") || roleNames.contains("MANAGER_ROLE")) {
            meetings = meetingRepository.findAll();
        } else if (username != null) {
            meetings = meetingRepository.findByUser_Login(username);
        } else {
            meetings = List.of();
            model.addAttribute("notLoggedIn", true);
        }

        Map<LocalDate, List<Meeting>> meetingsByDate = meetings.stream()
                .collect(Collectors.groupingBy(m -> m.getDateTime().toLocalDate()));
        model.addAttribute("meetingsByDate", meetingsByDate);

        // lista do <select> – zgodnie z regułami
        List<User> assignable;
        if (roleNames.contains("ADMIN_ROLE")) {
            assignable = userRepository.findAll();
        } else if (roleNames.contains("MANAGER_ROLE")) {
            assignable = userRepository.findAll().stream()
                    .filter(u -> u.getRoles().stream().noneMatch(r -> r.getName() == ERoles.ADMIN_ROLE))
                    .collect(Collectors.toList());
        } else if (username != null) {
            assignable = userRepository.findByLogin(username).map(List::of).orElseGet(List::of);
        } else {
            assignable = List.of();
        }
        model.addAttribute("allUsers", assignable);

        return "calendar";
    }
}
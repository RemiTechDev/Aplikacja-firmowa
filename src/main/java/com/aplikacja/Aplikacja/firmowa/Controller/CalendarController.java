package com.aplikacja.Aplikacja.firmowa.Controller;

import com.aplikacja.Aplikacja.firmowa.Model.ERoles;
import com.aplikacja.Aplikacja.firmowa.Model.Meeting;
import com.aplikacja.Aplikacja.firmowa.Model.User;
import com.aplikacja.Aplikacja.firmowa.Repositories.MeetingRepository;
import com.aplikacja.Aplikacja.firmowa.Repositories.UserRepository;
import com.aplikacja.Aplikacja.firmowa.Service.CalendarService;
import com.aplikacja.Aplikacja.firmowa.Dto.CalendarDayDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;
    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;

    @GetMapping("/calendar")
    public String calendar(Model model,
                           Principal principal,
                           @RequestParam(value = "ym", required = false) String ymParam,
                           @RequestParam(value = "prev", required = false) String prev,
                           @RequestParam(value = "next", required = false) String next) {

        // 1) Ustal miesiąc bazowy (domyślnie: teraz)
        YearMonth ym;
        try {
            ym = (ymParam != null && !ymParam.isBlank()) ? YearMonth.parse(ymParam) : YearMonth.now();
        } catch (Exception e) {
            ym = YearMonth.now();
        }

        // 2) Obsłuż kliknięcia
        if (prev != null) ym = ym.minusMonths(1);
        if (next != null) ym = ym.plusMonths(1);

        // 3) Wystaw ym do widoku (żeby linki działały)
        model.addAttribute("ym", ym.toString()); // "YYYY-MM"

        YearMonth nextMonth = ym.plusMonths(1);
        Locale pl = new Locale("pl", "PL");

        model.addAttribute("currentMonthName", ym.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, pl));
        model.addAttribute("currentYear", ym.getYear());
        model.addAttribute("nextMonthName", nextMonth.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, pl));
        model.addAttribute("nextYear", nextMonth.getYear());
        model.addAttribute("currentMonth", ym.getMonthValue());

        String username = (principal != null) ? principal.getName() : null;
        Set<String> roleNames = Set.of();

        if (username != null) {
            userRepository.findByLogin(username).ifPresent(u -> model.addAttribute("roles", u.getRoles()));
            roleNames = userRepository.findByLogin(username)
                    .map(User::getRoles).orElseGet(Set::of)
                    .stream()
                    .map(r -> r.getName().name())
                    .collect(Collectors.toSet());
        } else {
            model.addAttribute("notLoggedIn", true);
        }

        List<Meeting> meetings;
        if (roleNames.contains("ADMIN_ROLE") || roleNames.contains("MANAGER_ROLE")) {
            meetings = meetingRepository.findAll();
        } else if (username != null) {
            meetings = meetingRepository.findByUser_Login(username);
        } else {
            meetings = List.of();
        }

        // grid na podstawie WYBRANEGO ym (nie YearMonth.now())
        List<List<CalendarDayDto>> grid = calendarService.buildCalendarWithMeetings(ym, meetings);
        model.addAttribute("grid", grid);

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

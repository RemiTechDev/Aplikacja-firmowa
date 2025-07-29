package com.aplikacja.Aplikacja.firmowa.Controller;

import com.aplikacja.Aplikacja.firmowa.Model.ERoles;
import com.aplikacja.Aplikacja.firmowa.Repositories.MeetingRepository;
import com.aplikacja.Aplikacja.firmowa.Service.CalendarService;
import com.aplikacja.Aplikacja.firmowa.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
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

        Set<String> roleNames = Set.of();
        String username = null;

        if (principal != null) {
            username = principal.getName();
            var userOptional = userRepository.findByLogin(username);

            if (userOptional.isPresent()) {
                var user = userOptional.get();
                roleNames = user.getRoles().stream()
                        .map(role -> role.getName().name())                        .collect(Collectors.toSet());
                model.addAttribute("roles", user.getRoles());
            }

            // Pobieranie spotkań:
            if (roleNames.contains("ADMIN") || roleNames.contains("MANAGER")) {
                model.addAttribute("meetings", meetingRepository.findAll());
            } else {
                model.addAttribute("meetings", meetingRepository.findByUser_Login(username));
            }

            // Statystyki
            long staffMeetings = meetingRepository.countByUserRole(ERoles.STAFF_ROLE);
            long userMeetings = meetingRepository.countByUserRole(ERoles.USER_ROLE);
            model.addAttribute("staffMeetingCount", staffMeetings);
            model.addAttribute("userMeetingCount", userMeetings);
        } else {
            model.addAttribute("meetings", meetingRepository.findAll());
            model.addAttribute("notLoggedIn", true);
        }
        return "calendar";
    }
}

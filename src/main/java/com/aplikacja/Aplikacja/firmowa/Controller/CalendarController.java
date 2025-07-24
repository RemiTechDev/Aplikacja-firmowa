package com.aplikacja.Aplikacja.firmowa.Controller;

import com.aplikacja.Aplikacja.firmowa.Repositories.MeetingRepository;
import com.aplikacja.Aplikacja.firmowa.Service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;
    private final MeetingRepository meetingRepository;



    @GetMapping("/calendar")
    public String calendar(Model model, Principal principal) {
        List<List<LocalDate>> calendarWeeks = calendarService.generateCalendarWeeks();
        model.addAttribute("calendarWeeks", calendarWeeks);

        if (principal != null) {
            model.addAttribute("meetings", meetingRepository.findByUser_Login(principal.getName()));
        } else {
            model.addAttribute("meetings", meetingRepository.findAll()); // lub null, jak wolisz
            model.addAttribute("notLoggedIn", true);
        }

        return "calendar";
    }
}

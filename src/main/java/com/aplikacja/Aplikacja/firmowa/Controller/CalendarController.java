package com.aplikacja.Aplikacja.firmowa.Controller;

import com.aplikacja.Aplikacja.firmowa.Repositories.MeetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class CalendarController {

    @Autowired private MeetingRepository meetingRepository;

    @GetMapping("/calendar")
    public String calendar(Model model, Principal principal) {
        if(principal != null) {
            model.addAttribute("meetings", meetingRepository.findByUser_Login(principal.getName()));
        }else {
            model.addAttribute("meetings", null);
            model.addAttribute("Nie zalogowany", true);
        }
        return "index";
    }
}

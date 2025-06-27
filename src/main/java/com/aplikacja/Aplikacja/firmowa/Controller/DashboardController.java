package com.aplikacja.Aplikacja.firmowa.Controller;

import com.aplikacja.Aplikacja.firmowa.Repositories.DocumentRepository;
import com.aplikacja.Aplikacja.firmowa.Repositories.MeetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class DashboardController {

    @Autowired private DocumentRepository documentRepository;
    @Autowired private MeetingRepository meetingRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        model.addAttribute("docCount", documentRepository.countByUser_Login(principal.getName()));
        model.addAttribute("meetingCount", meetingRepository.countByUser_LoginAndToday(principal.getName()));
        return "layout";
    }
}

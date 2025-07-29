package com.aplikacja.Aplikacja.firmowa.Controller;

import com.aplikacja.Aplikacja.firmowa.Model.Meeting;
import com.aplikacja.Aplikacja.firmowa.Model.User;
import com.aplikacja.Aplikacja.firmowa.Repositories.MeetingRepository;
import com.aplikacja.Aplikacja.firmowa.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;

    @PostMapping("/meetings/add")
    public String addMeeting(@RequestParam String title,
                             @RequestParam String description,
                             @RequestParam String dateTime,
                             @RequestParam String location,
                             Principal principal) {

        if (principal == null) return "redirect:/login";

        Optional<User> userOptional = userRepository.findByLogin(principal.getName());

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            Meeting meeting = new Meeting();
            meeting.setTitle(title);
            meeting.setDescription(description);
            meeting.setLocation(location);
            meeting.setDateTime(LocalDateTime.parse(dateTime));
            meeting.setUser(user);

            meetingRepository.save(meeting);
        }

        return "redirect:/calendar";
    }
}

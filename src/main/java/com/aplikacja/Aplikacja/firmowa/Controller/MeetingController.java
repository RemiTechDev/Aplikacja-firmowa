package com.aplikacja.Aplikacja.firmowa.Controller;

import com.aplikacja.Aplikacja.firmowa.Model.ERoles;
import com.aplikacja.Aplikacja.firmowa.Model.Meeting;
import com.aplikacja.Aplikacja.firmowa.Model.MeetingStatus;
import com.aplikacja.Aplikacja.firmowa.Model.User;
import com.aplikacja.Aplikacja.firmowa.Repositories.MeetingRepository;
import com.aplikacja.Aplikacja.firmowa.Repositories.UserRepository;
import com.aplikacja.Aplikacja.firmowa.security.MeetingPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final MeetingPermission perm;

    @PostMapping("/meetings/add")
    public String addMeeting(@RequestParam String title,
                             @RequestParam String description,
                             @RequestParam String location,
                             @RequestParam String dateTime,
                             @RequestParam(required = false) Long userId,
                             Principal principal) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!perm.canCreate(auth)) return "redirect:/calendar";

        String login = principal != null ? principal.getName() : null;

        // Wyznacz ownera zgodnie z regułami
        User owner = null;
        if (userId != null) {
            Optional<User> target = userRepository.findById(userId);
            if (target.isPresent() && perm.canSetOwner(auth, target.get(), login)) {
                owner = target.get();
            }
        }
        if (owner == null && login != null) {
            owner = userRepository.findByLogin(login).orElse(null);
        }

        Meeting m = new Meeting();
        m.setTitle(title);
        m.setDescription(description);
        m.setLocation(location);
        m.setDateTime(LocalDateTime.parse(dateTime));
        m.setStatus(MeetingStatus.PLANOWANE);
        m.setUser(owner);

        meetingRepository.save(m);
        return "redirect:/calendar";
    }
}
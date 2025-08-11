package com.aplikacja.Aplikacja.firmowa.Controller;

import com.aplikacja.Aplikacja.firmowa.Model.*;
import com.aplikacja.Aplikacja.firmowa.Repositories.MeetingCommentRepository;
import com.aplikacja.Aplikacja.firmowa.Repositories.MeetingRepository;
import com.aplikacja.Aplikacja.firmowa.Repositories.UserRepository;
import com.aplikacja.Aplikacja.firmowa.Service.MeetingCommentService;
import com.aplikacja.Aplikacja.firmowa.security.MeetingPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/meetings")
public class MeetingEditController {

    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;
    private final MeetingPermission perm;

    // -------------------- EDIT (GET) --------------------

    /**
     * Wersja z parametrem zapytania:
     * GET /meetings/edit?id=123
     */
    @GetMapping("/edit")
    public String editMeeting(@RequestParam Long id, Model model, Principal principal) {
        Optional<Meeting> optionalMeeting = meetingRepository.findById(id);
        if (optionalMeeting.isEmpty()) return "redirect:/calendar";

        Meeting meeting = optionalMeeting.get();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String login = principal != null ? principal.getName() : null;

        if (!perm.canEdit(auth, meeting, login)) {
            return "redirect:/calendar"; // brak uprawnień -> wracamy bez 403
        }

        model.addAttribute("meeting", meeting);
        model.addAttribute("statuses", MeetingStatus.values());
        model.addAttribute("comments", meeting.getComments());

        // lista użytkowników możliwych do przypisania (wg uprawnień)
        List<User> assignableUsers = userRepository.findAll().stream()
                .filter(u -> perm.canSetOwner(auth, u, login))
                .collect(Collectors.toList());
        model.addAttribute("allUsers", assignableUsers);

        return "edit-meeting";
    }

    /**
     * Wersja „ładna” po ścieżce:
     * GET /meetings/edit/123
     *
     * Deleguje do metody powyżej, żeby uniknąć duplikacji logiki.
     */
    @GetMapping("/edit/{id}")
    public String editMeetingPath(@PathVariable Long id, Model model, Principal principal) {
        return editMeeting(id, model, principal);
    }

    // -------------------- EDIT (POST) --------------------

    @PostMapping("/edit")
    public String updateMeeting(@RequestParam Long id,
                                @RequestParam String title,
                                @RequestParam String description,
                                @RequestParam String location,
                                @RequestParam String dateTime,
                                @RequestParam MeetingStatus status,
                                @RequestParam(required = false) Long userId,
                                Principal principal) {

        Optional<Meeting> optionalMeeting = meetingRepository.findById(id);
        if (optionalMeeting.isEmpty()) return "redirect:/calendar";

        Meeting meeting = optionalMeeting.get();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String login = principal != null ? principal.getName() : null;

        if (!perm.canEdit(auth, meeting, login)) {
            return "redirect:/calendar";
        }

        meeting.setTitle(title);
        meeting.setDescription(description);
        meeting.setLocation(location);
        meeting.setDateTime(LocalDateTime.parse(dateTime));
        meeting.setStatus(status);

        // zmiana ownera – tylko jeśli wolno na wskazanego usera
        if (userId != null) {
            userRepository.findById(userId).ifPresent(target -> {
                if (perm.canSetOwner(auth, target, login)) {
                    meeting.setUser(target);
                }
            });
        }

        meetingRepository.save(meeting);
        return "redirect:/calendar";
    }

    // -------------------- DELETE --------------------

    @GetMapping("/delete/{id}")
    public String deleteMeeting(@PathVariable Long id, Principal principal) {
        Optional<Meeting> optionalMeeting = meetingRepository.findById(id);
        if (optionalMeeting.isEmpty()) return "redirect:/calendar";

        Meeting meeting = optionalMeeting.get();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String login = principal != null ? principal.getName() : null;

        if (!perm.canDelete(auth, meeting, login)) {
            return "redirect:/calendar";
        }

        meetingRepository.delete(meeting);
        return "redirect:/calendar";
    }
}
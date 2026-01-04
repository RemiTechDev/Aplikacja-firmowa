//package com.aplikacja.Aplikacja.firmowa.Controller;
//
//import com.aplikacja.Aplikacja.firmowa.Model.Meeting;
//import com.aplikacja.Aplikacja.firmowa.Model.MeetingStatus;
//import com.aplikacja.Aplikacja.firmowa.Model.User;
//import com.aplikacja.Aplikacja.firmowa.Repositories.MeetingRepository;
//import com.aplikacja.Aplikacja.firmowa.Repositories.UserRepository;
//import com.aplikacja.Aplikacja.firmowa.Service.MeetingCommentService;
//import com.aplikacja.Aplikacja.firmowa.security.MeetingPermission;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//import java.security.Principal;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Controller
//@RequiredArgsConstructor
//@RequestMapping("/meetings")
//public class MeetingEditController {
//
//    private final MeetingRepository meetingRepository;
//    private final UserRepository userRepository;
//    private final MeetingPermission perm;
//    private final MeetingCommentService meetingCommentService;
//
//    // GET /meetings/edit?id=...
//    @GetMapping("/edit")
//    public String editMeeting(@RequestParam Long id, Model model, Principal principal) {
//        Optional<Meeting> optionalMeeting = meetingRepository.findById(id);
//        if (optionalMeeting.isEmpty()) return "redirect:/calendar";
//
//        Meeting meeting = optionalMeeting.get();
//
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String login = principal != null ? principal.getName() : null;
//
//        if (!perm.canEdit(auth, meeting, login)) {
//            return "redirect:/calendar";
//        }
//
//        model.addAttribute("meeting", meeting);
//        model.addAttribute("statuses", MeetingStatus.values());
//        // komentarze pobieramy z serwisu (posortowane)
//        model.addAttribute("comments", meetingCommentService.getComments(meeting.getId()));
//
//        List<User> assignableUsers = userRepository.findAll().stream()
//                .filter(u -> perm.canSetOwner(auth, u, login))
//                .collect(Collectors.toList());
//        model.addAttribute("allUsers", assignableUsers);
//
//        return "edit-meeting";
//    }
//
//    // GET /meetings/edit/{id}
//    @GetMapping("/edit/{id}")
//    public String editMeetingPath(@PathVariable Long id, Model model, Principal principal) {
//        return editMeeting(id, model, principal);
//    }
//
//    // POST /meetings/edit  (Zapisz -> dashboard)
//    @PostMapping("/edit")
//    public String updateMeeting(@RequestParam Long id,
//                                @RequestParam String title,
//                                @RequestParam String description,
//                                @RequestParam String location,
//                                @RequestParam String dateTime,
//                                @RequestParam MeetingStatus status,
//                                @RequestParam(required = false) Long userId,
//                                Principal principal) {
//
//        Optional<Meeting> optionalMeeting = meetingRepository.findById(id);
//        if (optionalMeeting.isEmpty()) return "redirect:/calendar";
//
//        Meeting meeting = optionalMeeting.get();
//
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String login = principal != null ? principal.getName() : null;
//
//        if (!perm.canEdit(auth, meeting, login)) {
//            return "redirect:/calendar";
//        }
//
//        meeting.setTitle(title);
//        meeting.setDescription(description);
//        meeting.setLocation(location);
//        meeting.setDateTime(LocalDateTime.parse(dateTime));
//        meeting.setStatus(status);
//
//        if (userId != null) {
//            userRepository.findById(userId).ifPresent(target -> {
//                if (perm.canSetOwner(auth, target, login)) {
//                    meeting.setUser(target);
//                }
//            });
//        }
//
//        meetingRepository.save(meeting);
//        // po edycji przenosimy na dashboard rozszerzony
//        return "redirect:/admin/dashboard/extended";
//    }
//
//    // POST /meetings/{id}/comments  (Dodaj komentarz -> wraca do edycji)
//    @PostMapping("/{id}/comments")
//    public String addComment(@PathVariable Long id,
//                             @RequestParam String content,
//                             Principal principal) {
//        Optional<Meeting> optionalMeeting = meetingRepository.findById(id);
//        if (optionalMeeting.isEmpty()) return "redirect:/calendar";
//
//        Meeting meeting = optionalMeeting.get();
//
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String login = (principal != null) ? principal.getName() : null;
//
//        if (!perm.canEdit(auth, meeting, login)) {
//            return "redirect:/calendar";
//        }
//
//        meetingCommentService.addComment(meeting, login, content);
//        // zostajemy na stronie edycji
//        return "redirect:/meetings/edit?id=" + id;
//    }
//
//    // DELETE
//    @GetMapping("/delete/{id}")
//    public String deleteMeeting(@PathVariable Long id, Principal principal) {
//        Optional<Meeting> optionalMeeting = meetingRepository.findById(id);
//        if (optionalMeeting.isEmpty()) return "redirect:/calendar";
//
//        Meeting meeting = optionalMeeting.get();
//
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String login = principal != null ? principal.getName() : null;
//
//        if (!perm.canDelete(auth, meeting, login)) {
//            return "redirect:/admin/dashboard/extended";
//        }
//
//        meetingRepository.delete(meeting);
//        return "redirect:/calendar";
//    }
//}


package com.aplikacja.Aplikacja.firmowa.Controller;

import com.aplikacja.Aplikacja.firmowa.Model.Meeting;
import com.aplikacja.Aplikacja.firmowa.Model.MeetingStatus;
import com.aplikacja.Aplikacja.firmowa.Model.User;
import com.aplikacja.Aplikacja.firmowa.Repositories.MeetingRepository;
import com.aplikacja.Aplikacja.firmowa.Repositories.UserRepository;
import com.aplikacja.Aplikacja.firmowa.Service.MeetingCommentService;
import com.aplikacja.Aplikacja.firmowa.security.MeetingPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/meetings")
public class MeetingEditController {

    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;
    private final MeetingPermission perm;
    private final MeetingCommentService meetingCommentService;

    @GetMapping("/edit")
    public String editMeeting(@RequestParam Long id, Model model, Principal principal, RedirectAttributes ra) {
        Optional<Meeting> optionalMeeting = meetingRepository.findById(id);
        if (optionalMeeting.isEmpty()) return "redirect:/calendar";

        Meeting meeting = optionalMeeting.get();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String login = principal != null ? principal.getName() : null;

        if (!perm.canEdit(auth, meeting, login)) {
            ra.addFlashAttribute("err", "Wymagane konto administratora, aby edytować spotkania utworzone przez ADMINA.");
            return "redirect:/calendar";
        }

        model.addAttribute("meeting", meeting);
        model.addAttribute("statuses", MeetingStatus.values());
        model.addAttribute("comments", meetingCommentService.getComments(meeting.getId()));

        List<User> assignableUsers = userRepository.findAll().stream()
                .filter(u -> perm.canSetOwner(auth, u, login))
                .collect(Collectors.toList());
        model.addAttribute("allUsers", assignableUsers);

        return "edit-meeting";
    }

    @GetMapping("/edit/{id}")
    public String editMeetingPath(@PathVariable Long id, Model model, Principal principal, RedirectAttributes ra) {
        return editMeeting(id, model, principal, ra);
    }

    @PostMapping("/edit")
    public String updateMeeting(@RequestParam Long id,
                                @RequestParam String title,
                                @RequestParam String description,
                                @RequestParam String location,
                                @RequestParam String dateTime,
                                @RequestParam MeetingStatus status,
                                @RequestParam(required = false) Long userId,
                                Principal principal,
                                RedirectAttributes ra) {

        Optional<Meeting> optionalMeeting = meetingRepository.findById(id);
        if (optionalMeeting.isEmpty()) return "redirect:/calendar";

        Meeting meeting = optionalMeeting.get();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String login = principal != null ? principal.getName() : null;

        if (!perm.canEdit(auth, meeting, login)) {
            ra.addFlashAttribute("err", "Wymagane konto administratora, aby edytować spotkania utworzone przez ADMINA.");
            return "redirect:/calendar";
        }

        meeting.setTitle(title);
        meeting.setDescription(description);
        meeting.setLocation(location);
        meeting.setDateTime(LocalDateTime.parse(dateTime));
        meeting.setStatus(status);

        if (userId != null) {
            userRepository.findById(userId).ifPresent(target -> {
                if (perm.canSetOwner(auth, target, login)) {
                    meeting.setUser(target);
                }
            });
        }

        meetingRepository.save(meeting);
        return "redirect:/panel";
    }

    /** KOMENTARZE – pozwalamy managerowi komentować także spotkania admina */
    @PostMapping("/{id}/comments")
    public String addComment(@PathVariable Long id,
                             @RequestParam String content,
                             Principal principal,
                             RedirectAttributes ra) {

        Optional<Meeting> optionalMeeting = meetingRepository.findById(id);
        if (optionalMeeting.isEmpty()) return "redirect:/calendar";

        Meeting meeting = optionalMeeting.get();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String login = (principal != null) ? principal.getName() : null;

        if (!perm.canComment(auth, meeting, login)) {
            ra.addFlashAttribute("err", "Brak uprawnień do komentowania tego spotkania.");
            return "redirect:/calendar";
        }

        meetingCommentService.addComment(meeting, login, content);
        return "redirect:/meetings/edit?id=" + id; // zostajemy przy edycji, jeśli ktoś ma prawo
    }

    @GetMapping("/delete/{id}")
    public String deleteMeeting(@PathVariable Long id, Principal principal, RedirectAttributes ra) {
        Optional<Meeting> optionalMeeting = meetingRepository.findById(id);
        if (optionalMeeting.isEmpty()) return "redirect:/calendar";

        Meeting meeting = optionalMeeting.get();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String login = principal != null ? principal.getName() : null;

        if (!perm.canDelete(auth, meeting, login)) {
            ra.addFlashAttribute("err", "Wymagane konto administratora, aby usuwać spotkania utworzone przez " +
                    "ADMINA.");
            return "redirect:/calendar";
        }

        meetingRepository.delete(meeting);
        return "redirect:/panel";
    }
}

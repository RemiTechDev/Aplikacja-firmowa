package com.aplikacja.Aplikacja.firmowa.Controller;

import com.aplikacja.Aplikacja.firmowa.Model.Meeting;
import com.aplikacja.Aplikacja.firmowa.Model.MeetingComment;
import com.aplikacja.Aplikacja.firmowa.Model.User;
import com.aplikacja.Aplikacja.firmowa.Repositories.MeetingCommentRepository;
import com.aplikacja.Aplikacja.firmowa.Repositories.MeetingRepository;
import com.aplikacja.Aplikacja.firmowa.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MeetingCommentController {

    private final MeetingCommentRepository commentRepository;
    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;

    @PostMapping("/calendar/comment/add")
    public String addComment(@RequestParam Long meetingId,
                             @RequestParam String content,
                             Principal principal) {
        if (principal == null) return "redirect:/login";

        Optional<Meeting> meeting = meetingRepository.findById(meetingId);
        Optional<User> user = userRepository.findByLogin(principal.getName());

        if (meeting.isPresent() && user.isPresent()) {
            MeetingComment comment = new MeetingComment();
            comment.setMeeting(meeting.get());
            comment.setAuthor(user.get());
            comment.setContent(content);
            commentRepository.save(comment);
        }

        return "redirect:/calendar";
    }
}

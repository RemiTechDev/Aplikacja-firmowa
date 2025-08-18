package com.aplikacja.Aplikacja.firmowa.Service;


import com.aplikacja.Aplikacja.firmowa.Model.Meeting;
import com.aplikacja.Aplikacja.firmowa.Model.MeetingComment;
import com.aplikacja.Aplikacja.firmowa.Model.User;
import com.aplikacja.Aplikacja.firmowa.Repositories.MeetingCommentRepository;
import com.aplikacja.Aplikacja.firmowa.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingCommentService {

    private final MeetingCommentRepository commentRepository;
    private final UserRepository userRepository;

    /** Do widoku edycji – komentarze posortowane chronologicznie. */
    public List<MeetingComment> getComments(Long meetingId) {
        return commentRepository.findByMeeting_IdOrderByCreatedAtAsc(meetingId);
    }

    /** Ogólne dodanie komentarza przez login autora. */
    public MeetingComment addComment(Meeting meeting, String authorLogin, String content) {
        User author = userRepository.findByLogin(authorLogin)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono użytkownika: " + authorLogin));

        MeetingComment comment = new MeetingComment();
        comment.setMeeting(meeting);
        comment.setAuthor(author);
        comment.setContent(content == null ? "" : content.trim());
        comment.setCreatedAt(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    /** Komentarz „systemowy” do zmiany statusu – zostawiamy na setterach. */
    public void addStatusComment(Meeting meeting, User author, String commentText) {
        MeetingComment comment = new MeetingComment();
        comment.setMeeting(meeting);
        comment.setAuthor(author);
        comment.setContent(commentText == null ? "" : commentText.trim());
        comment.setCreatedAt(LocalDateTime.now());
        commentRepository.save(comment);
    }
}
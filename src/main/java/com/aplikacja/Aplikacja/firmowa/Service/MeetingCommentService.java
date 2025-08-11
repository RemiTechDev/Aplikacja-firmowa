package com.aplikacja.Aplikacja.firmowa.Service;

import com.aplikacja.Aplikacja.firmowa.Model.Meeting;
import com.aplikacja.Aplikacja.firmowa.Model.MeetingComment;
import com.aplikacja.Aplikacja.firmowa.Model.User;
import com.aplikacja.Aplikacja.firmowa.Repositories.MeetingCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MeetingCommentService {

    private final MeetingCommentRepository commentRepository;

    @Autowired
    public MeetingCommentService(MeetingCommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    /**
     * Zwraca wszystkie komentarze przypisane do danego spotkania.
     */
    public List<MeetingComment> getCommentsForMeeting(Long meetingId) {
        return commentRepository.findByMeeting_Id(meetingId);
    }

    /**
     * Dodaje komentarz dotyczący zmiany statusu spotkania.
     */
    public void addStatusComment(Meeting meeting, User author, String commentText) {
        MeetingComment comment = new MeetingComment();
        comment.setMeeting(meeting);
        comment.setAuthor(author); // poprawione z: setUser
        comment.setContent(commentText); // poprawione z: setComment
        comment.setCreatedAt(LocalDateTime.now());

        commentRepository.save(comment);
    }
}
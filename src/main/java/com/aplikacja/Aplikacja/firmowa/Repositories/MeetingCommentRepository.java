package com.aplikacja.Aplikacja.firmowa.Repositories;

import com.aplikacja.Aplikacja.firmowa.Model.MeetingComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingCommentRepository extends JpaRepository<MeetingComment, Long> {
    List<MeetingComment> findByMeeting_IdOrderByCreatedAtAsc(Long meetingId);
}
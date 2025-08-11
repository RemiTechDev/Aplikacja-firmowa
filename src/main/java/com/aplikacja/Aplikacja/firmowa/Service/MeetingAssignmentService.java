package com.aplikacja.Aplikacja.firmowa.Service;
import com.aplikacja.Aplikacja.firmowa.Model.Meeting;
import com.aplikacja.Aplikacja.firmowa.Model.User;
import com.aplikacja.Aplikacja.firmowa.Repositories.MeetingRepository;
import com.aplikacja.Aplikacja.firmowa.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MeetingAssignmentService {

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private UserRepository userRepository;

    //  Przypisuje już istniejące spotkanie do użytkownika
    public Meeting assignMeetingToUser(Long meetingId, Long userId) {
        Optional<Meeting> meetingOpt = meetingRepository.findById(meetingId);
        Optional<User> userOpt = userRepository.findById(userId);

        if (meetingOpt.isPresent() && userOpt.isPresent()) {
            Meeting meeting = meetingOpt.get();
            meeting.setUser(userOpt.get());
            return meetingRepository.save(meeting);
        }

        throw new IllegalArgumentException("Nie znaleziono spotkania lub użytkownika");
    }

    // 🔹 Tworzy nowe spotkanie i przypisuje do użytkownika
    public Meeting createAndAssignMeeting(Meeting meeting, Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isPresent()) {
            meeting.setUser(userOpt.get());
            return meetingRepository.save(meeting);
        }

        throw new IllegalArgumentException("Nie znaleziono użytkownika o ID: " + userId);
    }

    // 🔹 Obsługuje oba przypadki zależnie od tego, czy meeting posiada ID (null = nowe)
    public Meeting assignOrCreateMeeting(Meeting meeting, Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isPresent()) {
            meeting.setUser(userOpt.get());
            return meetingRepository.save(meeting);
        }

        throw new IllegalArgumentException("Nie znaleziono użytkownika");
    }
}

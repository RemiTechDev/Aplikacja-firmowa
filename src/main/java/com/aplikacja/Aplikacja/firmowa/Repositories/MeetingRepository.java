package com.aplikacja.Aplikacja.firmowa.Repositories;

import com.aplikacja.Aplikacja.firmowa.Model.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    List<Meeting> findByUser_Login(String login);
    List<Meeting> findTop5ByOrderByDateTimeAsc();

    // Przykładowa implementacja liczby dzisiejszych spotkań
    default long countByUser_LoginAndToday(String login) {
        return findByUser_Login(login).stream()
                .filter(m -> m.getDateTime().toLocalDate().equals(LocalDate.now()))
                .count();
    }


}

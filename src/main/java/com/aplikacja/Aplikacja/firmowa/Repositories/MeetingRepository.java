package com.aplikacja.Aplikacja.firmowa.Repositories;

import com.aplikacja.Aplikacja.firmowa.Model.ERoles;
import com.aplikacja.Aplikacja.firmowa.Model.Meeting;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    List<Meeting> findByUser_Login(String login);

    @EntityGraph(attributePaths = {"user", "createdBy"})
    List<Meeting> findTop5ByOrderByDateTimeAsc();

    // Przykładowa implementacja liczby dzisiejszych spotkań
    default long countByUser_LoginAndToday(String login) {
        return findByUser_Login(login).stream()
                .filter(m -> m.getDateTime().toLocalDate().equals(LocalDate.now()))
                .count();
    }

    @Query("SELECT COUNT(m) FROM Meeting m JOIN m.user.roles r WHERE r.name = :role")
    long countByUserRole(@Param("role") ERoles role);
}

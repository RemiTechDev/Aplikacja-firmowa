package com.aplikacja.Aplikacja.firmowa.Repositories;

import com.aplikacja.Aplikacja.firmowa.Model.LoginHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {

    // do kart na dashboardzie:
    List<LoginHistory> findTop20ByOrderByLoginTimeDesc();

    // do listowania z paginacją:
    Page<LoginHistory> findAllByOrderByLoginTimeDesc(Pageable pageable);

    // do zamykania sesji przy logout:
    Optional<LoginHistory> findFirstByUser_IdAndLogoutTimeIsNullOrderByLoginTimeDesc(Long userId);
}
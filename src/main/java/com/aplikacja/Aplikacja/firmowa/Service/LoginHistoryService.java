package com.aplikacja.Aplikacja.firmowa.Service;

import com.aplikacja.Aplikacja.firmowa.Model.LoginHistory;
import com.aplikacja.Aplikacja.firmowa.Model.User;
import com.aplikacja.Aplikacja.firmowa.Repositories.LoginHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LoginHistoryService {

    private final LoginHistoryRepository loginHistoryRepository;

    public void recordLogin(User user) {
        LoginHistory h = LoginHistory.builder()
                .user(user)
                .name(user.getFirstName() + " " + user.getLastName())
                .role(user.getRoles().stream().findFirst().map(r -> r.getName().name()).orElse("N/A"))
                .loginTime(LocalDateTime.now())
                .build();
        loginHistoryRepository.save(h);
    }

    public void recordLogout(User user) {
        loginHistoryRepository
                .findFirstByUser_IdAndLogoutTimeIsNullOrderByLoginTimeDesc(user.getId())
                .ifPresent(h -> {
                    h.setLogoutTime(LocalDateTime.now());
                    loginHistoryRepository.save(h);
                });
    }
}
package com.aplikacja.Aplikacja.firmowa.Listeners;

import com.aplikacja.Aplikacja.firmowa.Repositories.UserRepository;
import com.aplikacja.Aplikacja.firmowa.Service.LoginHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginEventListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private final UserRepository userRepository;
    private final LoginHistoryService loginHistoryService;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        String login = event.getAuthentication().getName();
        userRepository.findByLogin(login).ifPresent(loginHistoryService::recordLogin);
    }
}
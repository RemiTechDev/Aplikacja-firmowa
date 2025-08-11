package com.aplikacja.Aplikacja.firmowa.Listeners;

import com.aplikacja.Aplikacja.firmowa.Repositories.UserRepository;
import com.aplikacja.Aplikacja.firmowa.Service.LoginHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LogoutEventListener implements ApplicationListener<LogoutSuccessEvent> {

    private final UserRepository userRepository;
    private final LoginHistoryService loginHistoryService;

    @Override
    public void onApplicationEvent(LogoutSuccessEvent event) {
        String login = event.getAuthentication().getName();
        userRepository.findByLogin(login).ifPresent(loginHistoryService::recordLogout);
    }
}
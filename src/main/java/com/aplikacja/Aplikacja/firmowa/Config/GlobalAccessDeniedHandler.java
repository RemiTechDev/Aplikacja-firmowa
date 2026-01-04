package com.aplikacja.Aplikacja.firmowa.Config;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalAccessDeniedHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public String onDenied(RedirectAttributes ra) {
        ra.addFlashAttribute("err", "Brak uprawnień do wykonania tej operacji.");
        return "redirect:/calendar";
    }
}
// KLASA BĘDZIE ZASTĘPOWAĆ FUNKCJE Z INNYCH KLAS W PRZYSZŁOŚCI, OBSŁUGA WYJĄTKÓW.
// BĘDZIE ROZWIJANE W PRZYSZŁOŚCI
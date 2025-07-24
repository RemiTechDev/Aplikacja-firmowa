package com.aplikacja.Aplikacja.firmowa.Service;

import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CalendarService {

    public List<List<LocalDate>> generateCalendarWeeks() {
        List<List<LocalDate>> weeks = new ArrayList<>();

        // Obliczamy pierwszy dzień miesiąca i jego dzień tygodnia
        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
        DayOfWeek startDay = firstDayOfMonth.getDayOfWeek();

        // Wyliczamy pierwszy poniedziałek (lub niedzielę – zależnie od preferencji)
        LocalDate calendarStart = firstDayOfMonth.minusDays((startDay.getValue() + 6) % 7);

        // Tworzymy 6 tygodni (42 dni – wystarcza na każdy miesiąc)
        for (int week = 0; week < 6; week++) {
            List<LocalDate> days = new ArrayList<>();
            for (int day = 0; day < 7; day++) {
                days.add(calendarStart.plusDays(week * 7L + day));
            }
            weeks.add(days);
        }

        return weeks;
    }
}

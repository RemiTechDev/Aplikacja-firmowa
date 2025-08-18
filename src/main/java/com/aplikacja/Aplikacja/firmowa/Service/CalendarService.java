package com.aplikacja.Aplikacja.firmowa.Service;

import com.aplikacja.Aplikacja.firmowa.Dto.CalendarDayDto;
import com.aplikacja.Aplikacja.firmowa.Model.Meeting;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class CalendarService {

    /**
     * Buduje siatkę 6 tygodni dla wskazanego miesiąca (pon–nd),
     * gdzie każdy dzień ma już przypięte spotkania.
     */
    public List<List<CalendarDayDto>> buildCalendarWithMeetings(YearMonth ym, List<Meeting> meetings) {
        List<List<CalendarDayDto>> weeks = new ArrayList<>();

        LocalDate firstDay = ym.atDay(1);
        // poniedziałek <= pierwszy dzień miesiąca
        LocalDate start = firstDay.minusDays((firstDay.getDayOfWeek().getValue() + 6) % 7);

        // posortuj spotkania po dacie/czasie (ładniej się wyświetlą)
        meetings.sort(Comparator.comparing(Meeting::getDateTime));

        for (int w = 0; w < 6; w++) {
            List<CalendarDayDto> row = new ArrayList<>();
            for (int d = 0; d < 7; d++) {
                LocalDate day = start.plusDays(w * 7L + d);
                CalendarDayDto cell = new CalendarDayDto(day);

                // wepnij spotkania z tego dnia
                for (Meeting m : meetings) {
                    if (m.getDateTime().toLocalDate().isEqual(day)) {
                        cell.addMeeting(m);
                    }
                }

                row.add(cell);
            }
            weeks.add(row);
        }

        return weeks;
    }

    /**
     * Stara metoda – zostawiam
     * Zwraca same daty bez meetingów.
     */
    public List<List<LocalDate>> generateCalendarWeeks() {
        List<List<LocalDate>> weeks = new ArrayList<>();

        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate start = firstDayOfMonth.minusDays((firstDayOfMonth.getDayOfWeek().getValue() + 6) % 7);

        for (int week = 0; week < 6; week++) {
            List<LocalDate> days = new ArrayList<>();
            for (int day = 0; day < 7; day++) {
                days.add(start.plusDays(week * 7L + day));
            }
            weeks.add(days);
        }
        return weeks;
    }
}
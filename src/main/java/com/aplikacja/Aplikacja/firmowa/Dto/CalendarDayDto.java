package com.aplikacja.Aplikacja.firmowa.Dto;

import com.aplikacja.Aplikacja.firmowa.Model.Meeting;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CalendarDayDto {

    private final LocalDate date;
    private final List<Meeting> meetings = new ArrayList<>();

    public CalendarDayDto(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }

    public List<Meeting> getMeetings() {
        return meetings;
    }

    public void addMeeting(Meeting m) {
        this.meetings.add(m);
    }
}



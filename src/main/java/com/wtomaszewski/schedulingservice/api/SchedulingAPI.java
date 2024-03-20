package main.java.com.wtomaszewski.schedulingservice.api;

import main.java.com.wtomaszewski.schedulingservice.model.Meeting;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public interface SchedulingAPI {

    void createPerson(final String name, final String email);

    void createMeeting(final Set<String> personEmails, final LocalDate date, final int hour, final boolean checkConflicts);

    Set<Meeting> getSchedule(final String email, final LocalDate date, final int hour);

    Set<LocalDateTime> suggestTimeSlots(final Set<String> personEmails, final LocalDate startDate, final int startHour, final LocalDate endDate, final int endHour);
}

package main.java.com.wtomaszewski.schedulingservice.service;

import main.java.com.wtomaszewski.schedulingservice.model.Meeting;
import main.java.com.wtomaszewski.schedulingservice.model.Person;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

public interface SchedulingService {

    void createPerson(final String name, final String email);

    void createMeeting(final Set<String> personEmails, final LocalDate date, final int hour);

    void createMeeting(final Set<String> personEmails, final LocalDate date, final int hour, final boolean checkConflicts);

    Set<Meeting> getSchedule(final String email, final LocalDate date, final int hour);

    Set<LocalDateTime> suggestTimeSlots(final Set<String> personEmails, final LocalDate startDate, final int hour, final LocalDate endDate, final int endHour);

    Set<Meeting> getMeetings(final LocalDate date, final int hour);

    Map<String, Person> getPersonsMap();
}
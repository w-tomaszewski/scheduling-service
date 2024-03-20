package main.java.com.wtomaszewski.schedulingservice.util;

import main.java.com.wtomaszewski.schedulingservice.exception.MeetingTimeslotConflictException;
import main.java.com.wtomaszewski.schedulingservice.exception.NotUniquePersonException;
import main.java.com.wtomaszewski.schedulingservice.exception.PersonNotExistException;
import main.java.com.wtomaszewski.schedulingservice.model.Meeting;
import main.java.com.wtomaszewski.schedulingservice.model.Person;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SchedulingUtils {

    public static void checkPersonsAndMeetingConflicts(final Map<String, Person> persons, final Map<String, Set<LocalDateTime>> reservedTimeSlots,
                                                       final Set<String> personEmails, final boolean checkConflicts, final LocalDateTime startTime) {
        for (String email : personEmails) {
            if (!persons.containsKey(email)) {
                throw new PersonNotExistException(email);
            }
            final Set<LocalDateTime> reservedSlots = reservedTimeSlots.get(email);
            if (reservedSlots != null && checkConflicts && reservedSlots.contains(startTime)) {
                throw new MeetingTimeslotConflictException(email);
            }
        }
    }

    public static void updateMeetingMap(final Map<LocalDateTime, Set<Meeting>> sortedMeetingsMap, final Set<String> personEmails, final LocalDateTime startTime) {
        sortedMeetingsMap.computeIfAbsent(startTime, k -> new HashSet<>()).add(new Meeting(personEmails, startTime));
    }

    public static void updateReservedTimeslots(final Map<String, Set<LocalDateTime>> reservedPersonTimeSlotsMap, final Set<String> personEmails, final LocalDateTime startTime) {
        for (String email : personEmails) {
            final Set<LocalDateTime> reservedTimeSlots = reservedPersonTimeSlotsMap.computeIfAbsent(email, k -> new HashSet<>());
            reservedTimeSlots.add(startTime);
        }
    }

    public static LocalDateTime getLocalDateTime(final LocalDate date, final int hour) {
        return LocalDateTime.of(date, LocalTime.of(hour, 0));
    }

    public static void validateInput(final boolean condition, final String errorMessage) {
        if (condition) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static void validatePerson(final Map<String, Person> persons, final String email) {
        if (persons.containsKey(email)) {
            throw new NotUniquePersonException(email);
        }
    }
}

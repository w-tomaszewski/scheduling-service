package main.java.com.wtomaszewski.schedulingservice.service;

import main.java.com.wtomaszewski.schedulingservice.exception.DataInitializationException;
import main.java.com.wtomaszewski.schedulingservice.model.Meeting;
import main.java.com.wtomaszewski.schedulingservice.model.Person;
import main.java.com.wtomaszewski.schedulingservice.repository.MeetingRepository;
import main.java.com.wtomaszewski.schedulingservice.repository.PersonRepository;
import org.junit.platform.commons.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import static main.java.com.wtomaszewski.schedulingservice.util.SchedulingUtils.checkPersonsAndMeetingConflicts;
import static main.java.com.wtomaszewski.schedulingservice.util.SchedulingUtils.getLocalDateTime;
import static main.java.com.wtomaszewski.schedulingservice.util.SchedulingUtils.updateMeetingMap;
import static main.java.com.wtomaszewski.schedulingservice.util.SchedulingUtils.updateReservedTimeslots;
import static main.java.com.wtomaszewski.schedulingservice.util.SchedulingUtils.validateInput;
import static main.java.com.wtomaszewski.schedulingservice.util.SchedulingUtils.validatePerson;

public class MinimalSchedulingService implements SchedulingService {

    public static final String INVALID_INPUT_ERROR = "Invalid input parameters";

    private final Map<String, Person> personsMap;
    private final TreeMap<LocalDateTime, Set<Meeting>> meetingsSortedByTimeSlotMap;
    private final Map<String, Set<LocalDateTime>> reservedPersonTimeSlotsMap;

    public MinimalSchedulingService(final PersonRepository personRepository, final MeetingRepository meetingRepository) {
        this.personsMap = new HashMap<>();
        this.meetingsSortedByTimeSlotMap = new TreeMap<>();
        this.reservedPersonTimeSlotsMap = new HashMap<>();
        try {
            initializePersons(personRepository.getPersons());
            initializeMeetings(meetingRepository.getMeetings());
        } catch (Exception e) {
            throw new DataInitializationException(e);
        }
    }

    @Override
    public void createPerson(final String name, final String email) {
        validatePerson(personsMap, email);
        personsMap.put(email, new Person(name, email));
    }

    @Override
    public void createMeeting(final Set<String> personEmails, final LocalDate date, final int hour) {
        createMeeting(personEmails, date, hour, false);
    }

    @Override
    public void createMeeting(final Set<String> personEmails, final LocalDate date, final int hour, final boolean checkConflicts) {
        validateInput(personEmails == null || date == null || hour < 0 || hour > 23, INVALID_INPUT_ERROR);
        final LocalDateTime startTime = getLocalDateTime(date, hour);
        checkPersonsAndMeetingConflicts(personsMap, reservedPersonTimeSlotsMap, personEmails, checkConflicts, startTime);
        updateReservedTimeslots(reservedPersonTimeSlotsMap, personEmails, startTime);
        updateMeetingMap(meetingsSortedByTimeSlotMap, personEmails, startTime);
    }

    @Override
    public Set<Meeting> getSchedule(final String email, final LocalDate date, final int hour) {
        validateInput(StringUtils.isBlank(email) || date == null || hour < 0 || hour > 23, INVALID_INPUT_ERROR);
        final LocalDateTime startTime = getLocalDateTime(date, hour);
        final Set<Meeting> schedule = new TreeSet<>();

        SortedMap<LocalDateTime, Set<Meeting>> subMap = meetingsSortedByTimeSlotMap.tailMap(startTime);

        for (Map.Entry<LocalDateTime, Set<Meeting>> entry : subMap.entrySet()) {
            for (Meeting meeting : entry.getValue()) {
                if (meeting.persons().contains(email)) {
                    schedule.add(meeting);
                }
            }
        }

        return schedule;
    }

    @Override
    public Set<LocalDateTime> suggestTimeSlots(final Set<String> personEmails, final LocalDate startDate, final int startHour, final LocalDate endDate, int endHour) {
        validateInput(personEmails.isEmpty() || startDate == null || endDate == null || startHour < 0 || startHour > 23 || endHour < 0 || endHour > 23, INVALID_INPUT_ERROR);
        final Set<LocalDateTime> availableTimeSlots = new TreeSet<>();
        LocalDateTime startTime = getLocalDateTime(startDate, startHour);
        LocalDateTime endTime = getLocalDateTime(endDate, endHour);

        final Set<LocalDateTime> reservedSlots = new HashSet<>();
        for (String person : personEmails) {
            Set<LocalDateTime> personReservedSlots = reservedPersonTimeSlotsMap.getOrDefault(person, Collections.emptySet());
            for (LocalDateTime slot : personReservedSlots) {
                if ((slot.isEqual(startTime) || slot.isAfter(startTime)) && (slot.isEqual(endTime) || slot.isBefore(endTime))) {
                    reservedSlots.add(slot);
                }
            }
        }

        while (startTime.isBefore(endTime)) {
            boolean allAvailable = !reservedSlots.contains(startTime);
            if (allAvailable) {
                availableTimeSlots.add(startTime);
            }
            startTime = startTime.plusHours(1);
        }

        return availableTimeSlots;
    }

    @Override
    public Set<Meeting> getMeetings(final LocalDate date, int hour) {
        final LocalDateTime startTime = getLocalDateTime(date, hour);
        return meetingsSortedByTimeSlotMap.getOrDefault(startTime, Collections.emptySet());
    }

    @Override
    public Map<String, Person> getPersonsMap() {
        return personsMap;
    }

    private void initializePersons(final Set<Person> persons) {
        for (Person person : persons) {
            createPerson(person.name(), person.email());
        }
    }

    private void initializeMeetings(final Set<Meeting> meetings) {
        for (Meeting meeting : meetings) {
            createMeeting(meeting.persons(), meeting.startTime().toLocalDate(), meeting.startTime().getHour());
        }
    }
}
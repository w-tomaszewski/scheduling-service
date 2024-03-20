package test.java.com.wtomaszewski.schedulingservice.service;

import main.java.com.wtomaszewski.schedulingservice.exception.DataInitializationException;
import main.java.com.wtomaszewski.schedulingservice.exception.MeetingTimeslotConflictException;
import main.java.com.wtomaszewski.schedulingservice.exception.NotUniquePersonException;
import main.java.com.wtomaszewski.schedulingservice.exception.PersonNotExistException;
import main.java.com.wtomaszewski.schedulingservice.model.Meeting;
import main.java.com.wtomaszewski.schedulingservice.model.Person;
import main.java.com.wtomaszewski.schedulingservice.repository.InMemoryMeetingRepository;
import main.java.com.wtomaszewski.schedulingservice.repository.InMemoryPersonRepository;
import main.java.com.wtomaszewski.schedulingservice.repository.MeetingRepository;
import main.java.com.wtomaszewski.schedulingservice.repository.PersonRepository;
import main.java.com.wtomaszewski.schedulingservice.service.MinimalSchedulingService;
import main.java.com.wtomaszewski.schedulingservice.service.SchedulingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static main.java.com.wtomaszewski.schedulingservice.service.MinimalSchedulingService.INVALID_INPUT_ERROR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static test.java.com.wtomaszewski.schedulingservice.TestConstants.END_HOUR;
import static test.java.com.wtomaszewski.schedulingservice.TestConstants.PERSON_1_EMAIL;
import static test.java.com.wtomaszewski.schedulingservice.TestConstants.PERSON_1_NAME;
import static test.java.com.wtomaszewski.schedulingservice.TestConstants.PERSON_2_EMAIL;
import static test.java.com.wtomaszewski.schedulingservice.TestConstants.PERSON_2_NAME;
import static test.java.com.wtomaszewski.schedulingservice.TestConstants.START_HOUR;
import static test.java.com.wtomaszewski.schedulingservice.TestConstants.START_LOCAL_DATE;

public class MinimalSchedulingServiceTest {

    private SchedulingService schedulingService;

    @BeforeEach
    void setUp() {
        PersonRepository personRepository = new InMemoryPersonRepository(new HashSet<>());
        MeetingRepository meetingRepository = new InMemoryMeetingRepository(new HashSet<>());

        schedulingService = new MinimalSchedulingService(personRepository, meetingRepository);
    }

    @Test
    void shouldThrowDataInitializationExceptionWhenWrongInitRepositoryData() {
        final String expectedExceptionMessage = DataInitializationException.EXCEPTION_MSG;
        PersonRepository personRepository = new InMemoryPersonRepository(Set.of(new Person(PERSON_1_NAME, PERSON_1_EMAIL), new Person(PERSON_2_NAME, PERSON_1_EMAIL)));
        MeetingRepository meetingRepository = new InMemoryMeetingRepository(new HashSet<>());

        DataInitializationException expectedThrown = assertThrows(DataInitializationException.class, () -> new MinimalSchedulingService(personRepository, meetingRepository));

        assertEquals(expectedExceptionMessage, expectedThrown.getMessage());
    }

    @Test
    void shouldCreatePerson() {
        schedulingService.createPerson(PERSON_1_NAME, PERSON_1_EMAIL);

        assertEquals(1, schedulingService.getPersonsMap().size());
    }

    @Test
    void shouldThrowExceptionWhenCreatingPersonWithExistingEmail() {
        final String expectedExceptionMessage = String.format(NotUniquePersonException.EXCEPTION_MSG_FORMAT, PERSON_1_EMAIL);

        schedulingService.createPerson(PERSON_1_NAME, PERSON_1_EMAIL);
        NotUniquePersonException expectedThrown = assertThrows(NotUniquePersonException.class, () -> schedulingService.createPerson(PERSON_2_NAME, PERSON_1_EMAIL));

        assertEquals(expectedExceptionMessage, expectedThrown.getMessage());
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenCreateMeeting() {
        Set<String> attendees = new HashSet<>();
        attendees.add(PERSON_1_EMAIL);

        IllegalArgumentException expectedThrown = assertThrows(IllegalArgumentException.class, () ->
                schedulingService.createMeeting(attendees, START_LOCAL_DATE, 30)
                );

        assertEquals(INVALID_INPUT_ERROR, expectedThrown.getMessage());
    }

    @Test
    void shouldCreateMeetingWithOnePerson() {
        Set<String> attendees = new HashSet<>();
        attendees.add(PERSON_1_EMAIL);

        schedulingService.createPerson(PERSON_1_NAME, PERSON_1_EMAIL);
        schedulingService.createMeeting(attendees, START_LOCAL_DATE, START_HOUR);

        assertEquals(1, schedulingService.getMeetings(START_LOCAL_DATE, START_HOUR).size());
    }

    @Test
    void shouldCreateMeetingWithTwoPersons() {
        Set<String> attendees = new HashSet<>();
        attendees.add(PERSON_1_EMAIL);
        attendees.add(PERSON_2_EMAIL);

        schedulingService.createPerson(PERSON_1_NAME, PERSON_1_EMAIL);
        schedulingService.createPerson(PERSON_2_NAME, PERSON_2_EMAIL);
        schedulingService.createMeeting(attendees, START_LOCAL_DATE, START_HOUR);

        assertEquals(1, schedulingService.getMeetings(START_LOCAL_DATE, START_HOUR).size());
    }

    @Test
    void shouldThrowMeetingTimeslotConflictExceptionWhenCreateMeeting() {
        final String expectedExceptionMessage = String.format(MeetingTimeslotConflictException.EXCEPTION_MSG_FORMAT, PERSON_2_EMAIL);
        Set<String> meeting1Attendees = new HashSet<>();
        meeting1Attendees.add(PERSON_1_EMAIL);
        meeting1Attendees.add(PERSON_2_EMAIL);
        Set<String> meeting2Attendees = new HashSet<>();
        meeting2Attendees.add(PERSON_2_EMAIL);

        schedulingService.createPerson(PERSON_1_NAME, PERSON_1_EMAIL);
        schedulingService.createPerson(PERSON_2_NAME, PERSON_2_EMAIL);
        schedulingService.createMeeting(meeting1Attendees, START_LOCAL_DATE, START_HOUR, true);

        MeetingTimeslotConflictException expectedThrown = assertThrows(MeetingTimeslotConflictException.class, () ->
                schedulingService.createMeeting(meeting2Attendees, START_LOCAL_DATE, START_HOUR, true)
        );

        assertEquals(expectedExceptionMessage, expectedThrown.getMessage());
    }

    @Test
    void shouldThrowPersonNotExistExceptionWhenCreateMeeting() {
        final String expectedExceptionMessage = String.format(PersonNotExistException.EXCEPTION_MSG_FORMAT, PERSON_2_EMAIL);

        Set<String> attendees = new HashSet<>();
        attendees.add(PERSON_1_EMAIL);
        attendees.add(PERSON_2_EMAIL);

        schedulingService.createPerson(PERSON_1_NAME, PERSON_1_EMAIL);

        PersonNotExistException expectedThrown = assertThrows(PersonNotExistException.class, () ->
                schedulingService.createMeeting(attendees, START_LOCAL_DATE, START_HOUR)
        );

        assertEquals(expectedExceptionMessage, expectedThrown.getMessage());
    }

    @Test
    void shouldGetScheduleMatchForTheSameMeetingForTwoPersons() {
        Set<String> attendees = new HashSet<>();
        attendees.add(PERSON_1_EMAIL);
        attendees.add(PERSON_2_EMAIL);

        schedulingService.createPerson(PERSON_1_NAME, PERSON_1_EMAIL);
        schedulingService.createPerson(PERSON_2_NAME, PERSON_2_EMAIL);
        schedulingService.createMeeting(attendees, START_LOCAL_DATE, START_HOUR);
        Set<Meeting> person1Meetings = schedulingService.getSchedule(PERSON_1_EMAIL, START_LOCAL_DATE, START_HOUR);
        Set<Meeting> person2Meetings = schedulingService.getSchedule(PERSON_2_EMAIL, START_LOCAL_DATE, START_HOUR);

        assertEquals(person1Meetings.iterator().next(), person2Meetings.iterator().next());
    }

    @Test
    void shouldGetScheduleForLargeAmountOfRecords() {
        LocalDateTime startDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(1, 0));
        LocalDateTime fixedDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(1, 0));
        Person person = new Person(PERSON_1_NAME, PERSON_1_EMAIL);
        schedulingService.createPerson(person.name(), person.email());

        for(int i=0; i<1000; i++) {
            schedulingService.createMeeting(Set.of(person.email()), fixedDateTime.toLocalDate(), fixedDateTime.getHour());
            fixedDateTime = fixedDateTime.plusHours(1);
            Set<Meeting> personMeetings = schedulingService.getSchedule(PERSON_1_EMAIL, startDateTime.toLocalDate(), startDateTime.getHour());
            assertEquals(i + 1, personMeetings.size());
        }
    }

    @Test
    void shouldSuggestEarlierTimeSlotForOnePerson() {
        Set<String> attendees = new HashSet<>();
        attendees.add(PERSON_1_EMAIL);
        LocalDateTime expectedFirstTimeSlot = LocalDateTime.of(START_LOCAL_DATE, LocalTime.of(START_HOUR, 0)).plusHours(1);
        LocalDateTime expectedLastTimeSlot = LocalDateTime.of(START_LOCAL_DATE, LocalTime.of(END_HOUR, 0)).minusHours(1);

        schedulingService.createPerson(PERSON_1_NAME, PERSON_1_EMAIL);
        schedulingService.createMeeting(attendees, START_LOCAL_DATE, START_HOUR);

        List<LocalDateTime> timeSlots = new ArrayList<>(schedulingService.suggestTimeSlots(attendees, START_LOCAL_DATE, START_HOUR, START_LOCAL_DATE, END_HOUR));

        assertEquals(expectedFirstTimeSlot, timeSlots.get(0));
        assertEquals(expectedLastTimeSlot, timeSlots.get(timeSlots.size() - 1));
    }

    @Test
    void shouldSuggestTimeSlotForLargeAmountOrRecords() {
        Set<String> persons = new HashSet<>();
        LocalDateTime startDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(1, 0));
        LocalDateTime fixedDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(1, 0));
        for(int i=0; i<1000; i++) {
            if(i == 900) {
                fixedDateTime = fixedDateTime.plusHours(1);
                continue;
            }
            Person person = new Person(PERSON_1_NAME+i, PERSON_1_EMAIL.replace("1@", i+"@"));
            schedulingService.createPerson(person.name(), person.email());
            schedulingService.createMeeting(Set.of(person.email()), fixedDateTime.toLocalDate(), fixedDateTime.getHour());
            fixedDateTime = fixedDateTime.plusHours(1);
            persons.add(person.email());
        }

        List<LocalDateTime> timeSlotsForAllPersons = new ArrayList<>(
                schedulingService.suggestTimeSlots(persons, startDateTime.toLocalDate(), startDateTime.getHour(), fixedDateTime.toLocalDate(), fixedDateTime.getHour()));
        assertEquals(1, timeSlotsForAllPersons.size());
    }

    @Test
    void shouldSuggestTimeSlotForLargeAmountOrRecordsConcurrently() throws InterruptedException {
        Set<String> persons = new HashSet<>();
        LocalDateTime startDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(1, 0));
        LocalDateTime fixedDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(1, 0));
        for(int i=0; i<1000; i++) {
            if(i == 900) {
                fixedDateTime = fixedDateTime.plusHours(1);
                continue;
            }
            Person person = new Person(PERSON_1_NAME+i, PERSON_1_EMAIL.replace("1@", i+"@"));
            schedulingService.createPerson(person.name(), person.email());
            schedulingService.createMeeting(Set.of(person.email()), fixedDateTime.toLocalDate(), fixedDateTime.getHour());
            fixedDateTime = fixedDateTime.plusHours(1);
            persons.add(person.email());
        }

        int numThreads = 100;

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        for (int i = 0; i < numThreads; i++) {
            LocalDateTime finalFixedDateTime = fixedDateTime;
            executor.submit(() -> {
                List<LocalDateTime> timeSlotsForAllPersons = new ArrayList<>(
                        schedulingService.suggestTimeSlots(persons, startDateTime.toLocalDate(), startDateTime.getHour(), finalFixedDateTime.toLocalDate(), finalFixedDateTime.getHour()));
                assertEquals(1, timeSlotsForAllPersons.size());
            });
        }

        // Shutdown the executor and wait for all tasks to complete
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
    }
}

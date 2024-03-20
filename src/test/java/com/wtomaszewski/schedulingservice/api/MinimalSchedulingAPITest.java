package test.java.com.wtomaszewski.schedulingservice.api;

import main.java.com.wtomaszewski.schedulingservice.api.MinimalSchedulingAPI;
import main.java.com.wtomaszewski.schedulingservice.api.SchedulingAPI;
import main.java.com.wtomaszewski.schedulingservice.model.Meeting;
import main.java.com.wtomaszewski.schedulingservice.model.Person;
import main.java.com.wtomaszewski.schedulingservice.repository.InMemoryMeetingRepository;
import main.java.com.wtomaszewski.schedulingservice.repository.InMemoryPersonRepository;
import main.java.com.wtomaszewski.schedulingservice.repository.MeetingRepository;
import main.java.com.wtomaszewski.schedulingservice.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static test.java.com.wtomaszewski.schedulingservice.TestConstants.PERSON_1_EMAIL;
import static test.java.com.wtomaszewski.schedulingservice.TestConstants.PERSON_1_NAME;
import static test.java.com.wtomaszewski.schedulingservice.TestConstants.PERSON_2_EMAIL;
import static test.java.com.wtomaszewski.schedulingservice.TestConstants.PERSON_2_NAME;

public class MinimalSchedulingAPITest {

    SchedulingAPI schedulingAPI;
    final Person person1 = new Person(PERSON_1_NAME, PERSON_1_EMAIL);
    final Person person2 = new Person(PERSON_2_NAME, PERSON_2_EMAIL);

    final Set<Person> bothPersonsSet = Set.of(person1, person2);
    final Set<Person> onlyPersons1Set = Set.of(person1);
    final Set<Person> onlyPersons2Set = Set.of(person2);

    final LocalDate localDateNow = LocalDate.now();

    @BeforeEach
    void setUp() {
        Set<Meeting> meetings = Set.of(
                new Meeting(onlyPersons1Set.stream().map(Person::email).collect(Collectors.toSet()), LocalDateTime.of(localDateNow, LocalTime.of(1, 0))),
                new Meeting(onlyPersons1Set.stream().map(Person::email).collect(Collectors.toSet()), LocalDateTime.of(localDateNow, LocalTime.of(2, 0))),
                new Meeting(onlyPersons1Set.stream().map(Person::email).collect(Collectors.toSet()), LocalDateTime.of(localDateNow, LocalTime.of(3, 0))),
                new Meeting(onlyPersons2Set.stream().map(Person::email).collect(Collectors.toSet()), LocalDateTime.of(localDateNow, LocalTime.of(5, 0))),
                new Meeting(onlyPersons2Set.stream().map(Person::email).collect(Collectors.toSet()), LocalDateTime.of(localDateNow, LocalTime.of(6, 0))),
                new Meeting(bothPersonsSet.stream().map(Person::email).collect(Collectors.toSet()), LocalDateTime.of(localDateNow, LocalTime.of(8, 0))),
                new Meeting(onlyPersons1Set.stream().map(Person::email).collect(Collectors.toSet()), LocalDateTime.of(localDateNow, LocalTime.of(9, 0))),
                new Meeting(onlyPersons2Set.stream().map(Person::email).collect(Collectors.toSet()), LocalDateTime.of(localDateNow, LocalTime.of(9, 0))),
                new Meeting(bothPersonsSet.stream().map(Person::email).collect(Collectors.toSet()), LocalDateTime.of(localDateNow, LocalTime.of(11, 0)))
        );

        PersonRepository personRepository = new InMemoryPersonRepository(bothPersonsSet);
        MeetingRepository meetingRepository = new InMemoryMeetingRepository(meetings);

        schedulingAPI = new MinimalSchedulingAPI(personRepository, meetingRepository);
    }

    @Test
    void shouldReturnCorrectPerson1Meetings() {
        Set<Meeting> person1Meetings = schedulingAPI.getSchedule(person1.email(), localDateNow, 1);

        assertEquals(6, person1Meetings.size());
    }

    @Test
    void shouldReturnCorrectPerson2Meetings() {
        Set<Meeting> person2Meetings = schedulingAPI.getSchedule(person2.email(), localDateNow, 1);

        assertEquals(5, person2Meetings.size());
    }

    @Test
    void shouldSuggestCorrectAvailableTimeslotsForPersons1() {
        Set<LocalDateTime> availableTimeslots = schedulingAPI.suggestTimeSlots(onlyPersons1Set.stream().map(Person::email).collect(Collectors.toSet()), localDateNow, 1, localDateNow, 12);

        assertEquals(Set.of(
                LocalDateTime.of(localDateNow, LocalTime.of(4, 0)),
                LocalDateTime.of(localDateNow, LocalTime.of(5, 0)),
                LocalDateTime.of(localDateNow, LocalTime.of(6, 0)),
                LocalDateTime.of(localDateNow, LocalTime.of(7, 0)),
                LocalDateTime.of(localDateNow, LocalTime.of(10, 0))
        ), availableTimeslots);
    }

    @Test
    void shouldSuggestCorrectAvailableTimeslotsForPersons2() {
        Set<LocalDateTime> availableTimeslots = schedulingAPI.suggestTimeSlots(onlyPersons2Set.stream().map(Person::email).collect(Collectors.toSet()), localDateNow, 1, localDateNow, 12);

        assertEquals(Set.of(
                LocalDateTime.of(localDateNow, LocalTime.of(1, 0)),
                LocalDateTime.of(localDateNow, LocalTime.of(2, 0)),
                LocalDateTime.of(localDateNow, LocalTime.of(3, 0)),
                LocalDateTime.of(localDateNow, LocalTime.of(4, 0)),
                LocalDateTime.of(localDateNow, LocalTime.of(7, 0)),
                LocalDateTime.of(localDateNow, LocalTime.of(10, 0))
        ), availableTimeslots);
    }

    @Test
    void shouldSuggestCorrectAvailableTimeslotsForBothPersons() {
        Set<LocalDateTime> availableTimeslots = schedulingAPI.suggestTimeSlots(bothPersonsSet.stream().map(Person::email).collect(Collectors.toSet()), localDateNow, 1, localDateNow, 12);

        assertEquals(Set.of(
                LocalDateTime.of(localDateNow, LocalTime.of(4, 0)),
                LocalDateTime.of(localDateNow, LocalTime.of(7, 0)),
                LocalDateTime.of(localDateNow, LocalTime.of(10, 0))), availableTimeslots);
    }
}

package main.java.com.wtomaszewski.schedulingservice.api;

import main.java.com.wtomaszewski.schedulingservice.model.Meeting;
import main.java.com.wtomaszewski.schedulingservice.repository.InMemoryMeetingRepository;
import main.java.com.wtomaszewski.schedulingservice.repository.InMemoryPersonRepository;
import main.java.com.wtomaszewski.schedulingservice.repository.MeetingRepository;
import main.java.com.wtomaszewski.schedulingservice.repository.PersonRepository;
import main.java.com.wtomaszewski.schedulingservice.service.MinimalSchedulingService;
import main.java.com.wtomaszewski.schedulingservice.service.SchedulingService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * A meeting scheduling service that facilitates creating meetings and managing schedules.
 * Requirements clarification:
 * - Added 'checkConflicts' parameter to createMeeting method to indicate whether to check for conflicts.
 * - If conflicts are detected and 'checkConflicts' is set to true, an exception is thrown.
 * - Initial data for persons is provided via an in-memory person repository, which sets up some predefined persons and meetings for testing and demonstration purposes.
 * Note on time zones:
 * - The system's default time zone is used for handling current date and time.
 * - LocalDateTime.now() returns the current date and time based on the system clock of the local machine.
 * Potential implementation compromises:
 * - Implementing support for time zones might require refactoring to use OffsetDateTime instead.
 * - The error handling and validation mechanisms covers basic scenarios and could be improved f.e. if an exception occurs during data initialization,
 *   the entire service is marked as failed.
 * - The current implementation may not scale well for a large number of persons or meetings but the code could be refactored to be safe for multiple threads
 *   by replace HashMap with ConcurrentHashMap, synchronize methods or critical sections.
 * - Repository classes are only for preparation for future improvements as currently they are using to read the data but not for save
 */
public class MinimalSchedulingAPI implements SchedulingAPI {
    private final SchedulingService schedulingService;

    public MinimalSchedulingAPI() {
        this.schedulingService = new MinimalSchedulingService(new InMemoryPersonRepository(), new InMemoryMeetingRepository());
    }

    public MinimalSchedulingAPI(final PersonRepository personRepository, final MeetingRepository meetingRepository) {
        this.schedulingService = new MinimalSchedulingService(personRepository, meetingRepository);
    }

    @Override
    public void createPerson(final String name, final String email) {
        schedulingService.createPerson(name, email);
    }

    @Override
    public void createMeeting(final Set<String> personEmails, final LocalDate date, final int hour, final boolean checkConflicts) {
        schedulingService.createMeeting(personEmails, date, hour, checkConflicts);
    }

    @Override
    public Set<Meeting> getSchedule(final String email, final LocalDate date, final int hour) {
        return schedulingService.getSchedule(email, date, hour);
    }

    @Override
    public Set<LocalDateTime> suggestTimeSlots(final Set<String> personEmails, final LocalDate startDate, final int startHour,
                                                final LocalDate endDate, final int endHour) {
        return schedulingService.suggestTimeSlots(personEmails, startDate, startHour, endDate, endHour);
    }
}
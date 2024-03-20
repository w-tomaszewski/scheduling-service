package main.java.com.wtomaszewski.schedulingservice.repository;

import main.java.com.wtomaszewski.schedulingservice.model.Meeting;

import java.util.HashSet;
import java.util.Set;

public class InMemoryMeetingRepository implements MeetingRepository {

    private final Set<Meeting> meetings;

    public InMemoryMeetingRepository() {
        this.meetings = new HashSet<>();
    }

    public InMemoryMeetingRepository(final Set<Meeting> meetings) {
        this.meetings = meetings;
    }

    @Override
    public Set<Meeting> getMeetings() {
        return meetings;
    }
}

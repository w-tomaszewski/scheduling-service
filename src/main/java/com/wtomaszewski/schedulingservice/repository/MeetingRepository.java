package main.java.com.wtomaszewski.schedulingservice.repository;

import main.java.com.wtomaszewski.schedulingservice.model.Meeting;

import java.util.Set;

public interface MeetingRepository {

    Set<Meeting> getMeetings();
}

package main.java.com.wtomaszewski.schedulingservice.model;

import java.time.LocalDateTime;
import java.util.Set;

public record Meeting(Set<String> persons, LocalDateTime startTime) implements Comparable<Meeting> {

    @Override
    public int compareTo(Meeting other) {
        return this.startTime.compareTo(other.startTime);
    }
}

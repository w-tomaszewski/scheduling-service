package main.java.com.wtomaszewski.schedulingservice.exception;

public class MeetingTimeslotConflictException extends RuntimeException {

    public static final String EXCEPTION_MSG_FORMAT = "Person with email %s has other meeting in the same time";

    public MeetingTimeslotConflictException(final String email) {
        super(String.format(EXCEPTION_MSG_FORMAT, email));
    }
}

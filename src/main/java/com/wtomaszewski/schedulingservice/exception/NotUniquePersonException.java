package main.java.com.wtomaszewski.schedulingservice.exception;

public class NotUniquePersonException extends RuntimeException {

    public static final String EXCEPTION_MSG_FORMAT = "Person with email %s already exists";

    public NotUniquePersonException(final String email) {
        super(String.format(EXCEPTION_MSG_FORMAT, email));
    }
}

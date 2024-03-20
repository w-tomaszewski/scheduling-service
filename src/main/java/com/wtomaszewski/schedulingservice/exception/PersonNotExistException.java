package main.java.com.wtomaszewski.schedulingservice.exception;

public class PersonNotExistException extends RuntimeException {

    public static final String EXCEPTION_MSG_FORMAT = "There is no existing person with %s email.";

    public PersonNotExistException(final String email) {
        super(String.format(EXCEPTION_MSG_FORMAT, email));
    }
}

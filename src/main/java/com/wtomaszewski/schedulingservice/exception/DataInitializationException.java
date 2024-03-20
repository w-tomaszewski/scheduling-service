package main.java.com.wtomaszewski.schedulingservice.exception;

public class DataInitializationException extends RuntimeException {

    public static final String EXCEPTION_MSG = "There was an issue during data initialization";

    public DataInitializationException(final Exception e) {
        super(EXCEPTION_MSG, e);
    }
}

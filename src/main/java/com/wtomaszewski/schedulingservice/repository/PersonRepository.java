package main.java.com.wtomaszewski.schedulingservice.repository;

import main.java.com.wtomaszewski.schedulingservice.model.Person;

import java.util.Set;

public interface PersonRepository {

    Set<Person> getPersons();
}

package main.java.com.wtomaszewski.schedulingservice.repository;

import main.java.com.wtomaszewski.schedulingservice.model.Person;

import java.util.HashSet;
import java.util.Set;

public class InMemoryPersonRepository implements PersonRepository {

    private final Set<Person> persons;

    public InMemoryPersonRepository() {
        this.persons = new HashSet<>();
    }

    public InMemoryPersonRepository(final Set<Person> persons) {
        this.persons = persons;
    }

    @Override
    public Set<Person> getPersons() {
        return persons;
    }
}

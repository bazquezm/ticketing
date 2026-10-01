package io.github.bazquezm.ticketing.event;

import java.time.Clock;
import java.time.ZonedDateTime;

public class Event {

    private final String name;
    private final ZonedDateTime start;
    private final int capacity;
    private EventStatus status;

    public static Event create(String name, ZonedDateTime start, int capacity, Clock clock) {

        if (!start.isAfter(ZonedDateTime.now(clock))) {
            throw new IllegalArgumentException("The start date must be in the future");
        }

        return new Event(name, start, capacity, EventStatus.DRAFT);
    }

    private Event(String name, ZonedDateTime start, int capacity, EventStatus status) {

        if (capacity < 1 || capacity > 100_000) {
            throw new IllegalArgumentException("The capacity must be between 1 and 100,000, but was " + capacity);
        }

        if (name.isBlank()){
            throw new IllegalArgumentException("The event name is required");
        }


        this.name = name;
        this.start = start;
        this.capacity = capacity;
        this.status = status;
    }

    public String name() {
        return this.name;
    }

    public ZonedDateTime start() {
        return this.start;
    }

    public int capacity() {
        return this.capacity;
    }

    public EventStatus status() {
        return this.status;
    }
}

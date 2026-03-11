package com.denzo.traderisk.event;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class EventStore {

    private final List<DomainEvent> events = new CopyOnWriteArrayList<>();

    public void append(DomainEvent event) {
        events.add(event);
    }

    public List<DomainEvent> getAll() {
        return List.copyOf(events);
    }

    public void clear() {
        events.clear(); // for tests only
    }
}

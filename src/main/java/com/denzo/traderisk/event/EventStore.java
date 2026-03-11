package com.denzo.traderisk.event;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

@Component
public class EventStore {

    private final Queue<DomainEvent> events = new ConcurrentLinkedQueue<>();

    public void append(DomainEvent event) {
        events.offer(event);
    }

    public List<DomainEvent> getAll() {
        return List.copyOf(events);
    }

    public void clear() {
        events.clear();
    }
}

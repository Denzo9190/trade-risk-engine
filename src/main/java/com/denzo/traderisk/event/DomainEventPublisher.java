package com.denzo.traderisk.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DomainEventPublisher {

    private final ApplicationEventPublisher publisher;
    private final EventStore eventStore;

    public void publish(DomainEvent event) {
        eventStore.append(event);
        publisher.publishEvent(event);
    }
}

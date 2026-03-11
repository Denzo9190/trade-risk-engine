package com.denzo.traderisk.event;

import java.time.Instant;

public interface DomainEvent {
    Instant occurredAt();
}

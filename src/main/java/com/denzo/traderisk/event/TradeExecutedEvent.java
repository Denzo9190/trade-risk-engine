package com.denzo.traderisk.event;

import com.denzo.traderisk.domain.Side;

import java.math.BigDecimal;
import java.time.Instant;

public record TradeExecutedEvent(
        Long tradeId,
        String symbol,
        BigDecimal quantity,
        BigDecimal price,
        Side side
) implements DomainEvent {

    @Override
    public Instant occurredAt() {
        return Instant.now();
    }
}

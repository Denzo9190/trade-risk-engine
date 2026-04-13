package com.denzo.traderisk.event;

import com.denzo.traderisk.domain.Side;
import java.math.BigDecimal;
import java.time.Instant;

public record PositionUpdatedEvent(
        String symbol,
        BigDecimal totalQuantity,
        BigDecimal averagePrice,
        Side side,
        Instant occurredAt
) implements DomainEvent {
    public PositionUpdatedEvent(String symbol, BigDecimal totalQuantity, BigDecimal averagePrice, Side side) {
        this(symbol, totalQuantity, averagePrice, side, Instant.now());
    }
    @Override
    public Instant occurredAt() { return occurredAt; }
}

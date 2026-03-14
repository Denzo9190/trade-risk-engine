package com.denzo.traderisk.strategy;

import com.denzo.traderisk.domain.Side;

import java.math.BigDecimal;
import java.time.Instant;

public record Signal(
        String symbol,
        Side side,
        BigDecimal quantity,
        String strategyName,
        Instant timestamp
) {
    public Signal {
        if (symbol == null || symbol.isBlank()) {
            throw new IllegalArgumentException("Symbol must not be empty");
        }
        if (side == null) {
            throw new IllegalArgumentException("Side must not be null");
        }
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (strategyName == null || strategyName.isBlank()) {
            throw new IllegalArgumentException("Strategy name must not be empty");
        }
        if (timestamp == null) {
            timestamp = Instant.now();
        }
    }
}

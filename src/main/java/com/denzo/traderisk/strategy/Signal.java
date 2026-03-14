package com.denzo.traderisk.strategy;

import java.math.BigDecimal;
import java.time.Instant;

public record Signal(
        String symbol,
        String side,          // "BUY" или "SELL"
        BigDecimal quantity,
        String strategyName,
        Instant timestamp
) {
    public Signal {
        if (symbol == null || symbol.isBlank()) {
            throw new IllegalArgumentException("Symbol must not be empty");
        }
        if (!"BUY".equals(side) && !"SELL".equals(side)) {
            throw new IllegalArgumentException("Side must be BUY or SELL");
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

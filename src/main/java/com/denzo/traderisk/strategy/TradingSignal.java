package com.denzo.traderisk.strategy;

import java.math.BigDecimal;
import java.util.UUID;

public record TradingSignal(
        UUID id,
        String symbol,
        SignalType type,
        BigDecimal price,
        BigDecimal quantity
) {
    public TradingSignal(String symbol, SignalType type, BigDecimal price, BigDecimal quantity) {
        this(UUID.randomUUID(), symbol, type, price, quantity);
    }
}

package com.denzo.traderisk.strategy;

import com.denzo.traderisk.domain.Side;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Торговый сигнал, генерируемый стратегией.
 *
 * @param symbol       инструмент
 * @param side         направление (BUY/SELL)
 * @param quantity     количество
 * @param price        цена (для limit-ордера)
 * @param strategyName имя стратегии, сгенерировавшей сигнал
 * @param timestamp    время генерации сигнала
 */
public record Signal(
        String symbol,
        Side side,
        BigDecimal quantity,
        BigDecimal price,
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
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        if (strategyName == null || strategyName.isBlank()) {
            throw new IllegalArgumentException("Strategy name must not be empty");
        }
        if (timestamp == null) {
            throw new IllegalArgumentException("Timestamp must not be null");
        }
    }
}

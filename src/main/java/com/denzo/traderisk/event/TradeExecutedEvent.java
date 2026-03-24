package com.denzo.traderisk.event;

import com.denzo.traderisk.domain.Side;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Событие, возникающее после исполнения ордера на бирже.
 * <p>
 * В текущей реализации предполагается полное исполнение ордера (full fill).
 * В будущем для поддержки partial fills может потребоваться заменить на FillEvent.
 * </p>
 */
public record TradeExecutedEvent(
        String symbol,
        BigDecimal executedQuantity,
        BigDecimal executedPrice,
        Side side,
        String exchangeOrderId,
        Instant occurredAt
) implements DomainEvent {

    public TradeExecutedEvent(String symbol, BigDecimal executedQuantity,
                              BigDecimal executedPrice, Side side,
                              String exchangeOrderId) {
        this(symbol, executedQuantity, executedPrice, side, exchangeOrderId, Instant.now());
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}

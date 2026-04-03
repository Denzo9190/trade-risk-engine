package com.denzo.traderisk.execution;

import java.math.BigDecimal;
import java.time.Instant;

public record ExecutionResult(
        String symbol,
        BigDecimal executedPrice,
        BigDecimal executedQuantity,
        String exchangeOrderId,
        Instant timestamp
) {
    public ExecutionResult(String symbol, BigDecimal executedPrice, BigDecimal executedQuantity, String exchangeOrderId) {
        this(symbol, executedPrice, executedQuantity, exchangeOrderId, Instant.now());
    }
}

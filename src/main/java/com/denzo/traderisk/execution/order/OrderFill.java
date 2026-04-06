package com.denzo.traderisk.execution.order;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderFill(
        String orderId,
        BigDecimal price,
        BigDecimal quantity,
        Instant timestamp
) {
    public OrderFill(String orderId, BigDecimal price, BigDecimal quantity) {
        this(orderId, price, quantity, Instant.now());
    }
}

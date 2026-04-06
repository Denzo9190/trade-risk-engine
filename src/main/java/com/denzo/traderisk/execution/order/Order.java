package com.denzo.traderisk.execution.order;

import com.denzo.traderisk.domain.Side;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
public class Order {

    private final String id;
    private final String symbol;
    private final Side side;
    private final BigDecimal quantity;
    private final OrderType type;
    private OrderStatus status;
    private final Instant createdAt;

    public Order(String symbol, Side side, BigDecimal quantity, OrderType type) {
        this.id = UUID.randomUUID().toString();
        this.symbol = symbol;
        this.side = side;
        this.quantity = quantity;
        this.type = type;
        this.status = OrderStatus.NEW;
        this.createdAt = Instant.now();
    }

    public void markSubmitted() {
        this.status = OrderStatus.SUBMITTED;
    }

    public void markFilled() {
        this.status = OrderStatus.FILLED;
    }

    public void markPartiallyFilled() {
        this.status = OrderStatus.PARTIALLY_FILLED;
    }

    public void markCancelled() {
        this.status = OrderStatus.CANCELLED;
    }

    public void markRejected() {
        this.status = OrderStatus.REJECTED;
    }
}

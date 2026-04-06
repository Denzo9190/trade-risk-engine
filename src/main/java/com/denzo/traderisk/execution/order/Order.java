package com.denzo.traderisk.execution.order;

import com.denzo.traderisk.domain.Side;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@EqualsAndHashCode(of = "id")
public class Order {

    private final String id;
    private final String symbol;
    private final Side side;
    private final BigDecimal quantity;
    private final OrderType type;
    private final BigDecimal price;        // для LIMIT ордеров
    private OrderStatus status;
    private final Instant createdAt;
    private Instant submittedAt;
    private BigDecimal filledQuantity;

    // для MARKET ордеров (цена не нужна)
    public Order(String symbol, Side side, BigDecimal quantity, OrderType type) {
        this(symbol, side, quantity, type, null);
    }

    // для LIMIT ордеров
    public Order(String symbol, Side side, BigDecimal quantity, OrderType type, BigDecimal price) {
        if (type == OrderType.LIMIT && price == null) {
            throw new IllegalArgumentException("LIMIT order requires price");
        }
        this.id = UUID.randomUUID().toString();
        this.symbol = symbol;
        this.side = side;
        this.quantity = quantity;
        this.type = type;
        this.price = price;
        this.status = OrderStatus.NEW;
        this.createdAt = Instant.now();
        this.filledQuantity = BigDecimal.ZERO;
    }

    public void markSubmitted() {
        this.status = OrderStatus.SUBMITTED;
        this.submittedAt = Instant.now();
    }

    public void applyFill(BigDecimal fillQty) {
        this.filledQuantity = this.filledQuantity.add(fillQty);
        if (this.filledQuantity.compareTo(this.quantity) >= 0) {
            this.status = OrderStatus.FILLED;
            this.filledQuantity = this.quantity; // фикс переполнения
        } else {
            this.status = OrderStatus.PARTIALLY_FILLED;
        }
    }

    public void markCancelled() {
        this.status = OrderStatus.CANCELLED;
    }

    public void markRejected() {
        this.status = OrderStatus.REJECTED;
    }

    public boolean isFilled() {
        return status == OrderStatus.FILLED;
    }

    public BigDecimal getRemainingQuantity() {
        return quantity.subtract(filledQuantity);
    }
}

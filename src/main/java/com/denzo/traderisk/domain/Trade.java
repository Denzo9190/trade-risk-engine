package com.denzo.traderisk.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "trades", indexes = {
        @Index(name = "idx_trades_symbol_created", columnList = "symbol, created_at")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String symbol;

    @Column(nullable = false)
    private BigDecimal quantity;

    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Side side;

    @Column(name = "exchange_order_id", length = 64)
    private String exchangeOrderId;   // ← добавлено

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    public Trade(String symbol, BigDecimal quantity, BigDecimal price, Side side) {
        this(symbol, quantity, price, side, null);
    }

    public Trade(String symbol, BigDecimal quantity, BigDecimal price, Side side, String exchangeOrderId) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.side = side;
        this.exchangeOrderId = exchangeOrderId;
    }
}

package com.denzo.traderisk.domain;

import com.denzo.traderisk.math.FinancialMath;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "ledger_entries", indexes = {
        @Index(name = "idx_ledger_symbol_timestamp", columnList = "symbol, timestamp")
})
@Getter
@NoArgsConstructor
public class LedgerEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String symbol;

    @Column(nullable = false)
    private Instant timestamp;

    private Long tradeId; // ссылка на сделку (может быть null)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private LedgerEventType eventType;

    // Детали сделки (если применимо)
    @Column(precision = 20, scale = 8)
    private BigDecimal tradeQuantity;

    @Column(precision = 20, scale = 8)
    private BigDecimal tradePrice;

    @Column(length = 10)
    private String tradeSide; // BUY/SELL

    // Состояние позиции после события
    @Column(precision = 20, scale = 8)
    private BigDecimal positionQty;

    @Column(precision = 20, scale = 8)
    private BigDecimal avgPrice;

    // Финансовые результаты после события
    @Column(precision = 20, scale = 8)
    private BigDecimal realisedPnl;

    @Column(precision = 20, scale = 8)
    private BigDecimal unrealisedPnl;

    // Дополнительная информация (опционально)
    @Column(length = 255)
    private String description;

    public LedgerEntry(String symbol, Long tradeId, LedgerEventType eventType,
                       BigDecimal tradeQuantity, BigDecimal tradePrice, String tradeSide,
                       BigDecimal positionQty, BigDecimal avgPrice,
                       BigDecimal realisedPnl, BigDecimal unrealisedPnl,
                       String description) {
        this.symbol = symbol;
        this.timestamp = Instant.now();
        this.tradeId = tradeId;
        this.eventType = eventType;
        this.tradeQuantity = tradeQuantity;
        this.tradePrice = tradePrice;
        this.tradeSide = tradeSide;
        this.positionQty = positionQty;
        this.avgPrice = avgPrice;
        // Применяем единое округление через FinancialMath (хотя значения уже должны быть округлены)
        this.realisedPnl = FinancialMath.money(realisedPnl);
        this.unrealisedPnl = FinancialMath.money(unrealisedPnl);
        this.description = description;
    }
}

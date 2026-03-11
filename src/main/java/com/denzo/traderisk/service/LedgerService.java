package com.denzo.traderisk.service;

import com.denzo.traderisk.domain.LedgerEntry;
import com.denzo.traderisk.domain.LedgerEventType;
import com.denzo.traderisk.dto.PositionResponse;
import com.denzo.traderisk.event.TradeExecutedEvent;
import com.denzo.traderisk.repository.LedgerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LedgerService {

    private final LedgerRepository ledgerRepository;

    @Transactional
    public void recordTrade(TradeExecutedEvent event, PositionResponse positionAfter, BigDecimal realisedPnlAfter) {
        LedgerEntry entry = new LedgerEntry(
                event.symbol(),
                event.tradeId(),
                LedgerEventType.TRADE_EXECUTED,
                event.quantity(),
                event.price(),
                event.side().name(),
                positionAfter.totalQuantity(),
                positionAfter.averagePrice(),
                realisedPnlAfter,
                positionAfter.unrealisedPnl(),
                "Trade executed"
        );
        ledgerRepository.save(entry);
    }

    @Transactional
    public void recordReconciliation(String symbol, BigDecimal totalPnl, BigDecimal realisedPnl,
                                     BigDecimal unrealisedPnl, boolean passed) {
        LedgerEntry entry = new LedgerEntry(
                symbol,
                null,
                LedgerEventType.RECONCILIATION_RUN,
                null,
                null,
                null,
                null,
                null,
                realisedPnl,
                unrealisedPnl,
                "Reconciliation " + (passed ? "PASSED" : "FAILED") + " totalPnl=" + totalPnl
        );
        ledgerRepository.save(entry);
    }

    public List<LedgerEntry> getHistory(String symbol) {
        return ledgerRepository.findBySymbolOrderByTimestampAsc(symbol);
    }

    public List<LedgerEntry> getHistory(String symbol, Instant from, Instant to) {
        return ledgerRepository.findBySymbolAndTimestampBetweenOrderByTimestampAsc(symbol, from, to);
    }

    public List<LedgerEntry> getAll() {
        return ledgerRepository.findAll();
    }
}

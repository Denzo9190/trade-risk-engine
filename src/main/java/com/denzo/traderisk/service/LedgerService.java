package com.denzo.traderisk.service;

import com.denzo.traderisk.domain.LedgerEntry;
import com.denzo.traderisk.domain.LedgerEventType;
import com.denzo.traderisk.domain.Trade;
import com.denzo.traderisk.dto.PositionResponse;
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

    /**
     * Запись исполненной сделки.
     */
    @Transactional
    public void recordTrade(Trade trade, PositionResponse positionAfter, BigDecimal realisedPnlAfter) {
        LedgerEntry entry = new LedgerEntry(
                trade.getSymbol(),
                trade.getId(),
                LedgerEventType.TRADE_EXECUTED,
                trade.getQuantity(),
                trade.getPrice(),
                trade.getSide().name(),
                positionAfter.totalQuantity(),
                positionAfter.averagePrice(),
                realisedPnlAfter,
                positionAfter.unrealisedPnl(),
                "Trade executed"
        );
        ledgerRepository.save(entry);
    }

    /**
     * Запись результата сверки PnL.
     */
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

    /**
     * Получить историю по символу.
     */
    public List<LedgerEntry> getHistory(String symbol) {
        return ledgerRepository.findBySymbolOrderByTimestampAsc(symbol);
    }

    /**
     * Получить историю по символу за период.
     */
    public List<LedgerEntry> getHistory(String symbol, Instant from, Instant to) {
        return ledgerRepository.findBySymbolAndTimestampBetweenOrderByTimestampAsc(symbol, from, to);
    }

    /**
     * Получить всю историю (для отладки).
     */
    public List<LedgerEntry> getAll() {
        return ledgerRepository.findAll();
    }
}

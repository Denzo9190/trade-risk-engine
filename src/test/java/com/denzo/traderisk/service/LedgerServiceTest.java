package com.denzo.traderisk.service;

import com.denzo.traderisk.domain.LedgerEntry;
import com.denzo.traderisk.domain.LedgerEventType;
import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.dto.PositionResponse;
import com.denzo.traderisk.event.TradeExecutedEvent;
import com.denzo.traderisk.repository.LedgerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LedgerServiceTest {

    @Mock
    private LedgerRepository ledgerRepository;

    @InjectMocks
    private LedgerService ledgerService;

    @Captor
    private ArgumentCaptor<LedgerEntry> entryCaptor;

    @Test
    void shouldRecordTrade() {
        // given
        TradeExecutedEvent event = new TradeExecutedEvent("BTCUSDT", BigDecimal.valueOf(2), BigDecimal.valueOf(60000), Side.BUY, "1");

        PositionResponse positionAfter = new PositionResponse(
                "BTCUSDT",
                BigDecimal.valueOf(2),
                BigDecimal.valueOf(60000),
                BigDecimal.ZERO
        );
        BigDecimal realisedPnl = BigDecimal.ZERO;

        // when
        ledgerService.recordTrade(event, positionAfter, realisedPnl);

        // then
        verify(ledgerRepository).save(entryCaptor.capture());
        LedgerEntry captured = entryCaptor.getValue();

        assertThat(captured.getSymbol()).isEqualTo("BTCUSDT");
        assertThat(captured.getExchangeOrderId()).isEqualTo("1");
        assertThat(captured.getEventType()).isEqualTo(LedgerEventType.TRADE_EXECUTED);
        assertThat(captured.getTradeQuantity()).isEqualByComparingTo(BigDecimal.valueOf(2));
        assertThat(captured.getTradePrice()).isEqualByComparingTo(BigDecimal.valueOf(60000));
        assertThat(captured.getTradeSide()).isEqualTo("BUY");
        assertThat(captured.getPositionQty()).isEqualByComparingTo(BigDecimal.valueOf(2));
        assertThat(captured.getAvgPrice()).isEqualByComparingTo(BigDecimal.valueOf(60000));
        assertThat(captured.getRealisedPnl()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(captured.getUnrealisedPnl()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(captured.getDescription()).isEqualTo("Trade executed");
    }

    @Test
    void shouldRecordReconciliation() {
        // given
        String symbol = "BTCUSDT";
        BigDecimal totalPnl = new BigDecimal("1000");
        BigDecimal realisedPnl = new BigDecimal("500");
        BigDecimal unrealisedPnl = new BigDecimal("500");
        boolean passed = true;

        // when
        ledgerService.recordReconciliation(symbol, totalPnl, realisedPnl, unrealisedPnl, passed);

        // then
        verify(ledgerRepository).save(entryCaptor.capture());
        LedgerEntry captured = entryCaptor.getValue();

        assertThat(captured.getSymbol()).isEqualTo(symbol);
        assertThat(captured.getExchangeOrderId()).isNull();
        assertThat(captured.getEventType()).isEqualTo(LedgerEventType.RECONCILIATION_RUN);
        assertThat(captured.getTradeQuantity()).isNull();
        assertThat(captured.getTradePrice()).isNull();
        assertThat(captured.getTradeSide()).isNull();
        assertThat(captured.getPositionQty()).isNull();
        assertThat(captured.getAvgPrice()).isNull();
        assertThat(captured.getRealisedPnl()).isEqualByComparingTo(realisedPnl);
        assertThat(captured.getUnrealisedPnl()).isEqualByComparingTo(unrealisedPnl);
        assertThat(captured.getDescription()).contains("PASSED");
    }

    @Test
    void shouldGetHistory() {
        // given
        when(ledgerRepository.findBySymbolOrderByTimestampAsc("BTCUSDT"))
                .thenReturn(List.of(new LedgerEntry()));

        // when
        List<LedgerEntry> history = ledgerService.getHistory("BTCUSDT");

        // then
        assertThat(history).hasSize(1);
    }

    @Test
    void shouldGetHistoryWithDateRange() {
        // given
        Instant from = Instant.now().minusSeconds(3600);
        Instant to = Instant.now();
        when(ledgerRepository.findBySymbolAndTimestampBetweenOrderByTimestampAsc("BTCUSDT", from, to))
                .thenReturn(List.of(new LedgerEntry()));

        // when
        List<LedgerEntry> history = ledgerService.getHistory("BTCUSDT", from, to);

        // then
        assertThat(history).hasSize(1);
    }

    @Test
    void shouldGetAll() {
        // given
        when(ledgerRepository.findAll()).thenReturn(List.of(new LedgerEntry(), new LedgerEntry()));

        // when
        List<LedgerEntry> all = ledgerService.getAll();

        // then
        assertThat(all).hasSize(2);
    }
}

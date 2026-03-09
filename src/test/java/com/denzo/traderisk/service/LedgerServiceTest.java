package com.denzo.traderisk.service;

import com.denzo.traderisk.domain.LedgerEntry;
import com.denzo.traderisk.domain.LedgerEventType;
import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.domain.Trade;
import com.denzo.traderisk.dto.PositionResponse;
import com.denzo.traderisk.repository.LedgerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
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
        Trade trade = new Trade("BTCUSDT", BigDecimal.ONE, new BigDecimal("60000"), Side.BUY);
        ReflectionTestUtils.setField(trade, "id", 1L); // устанавливаем ID, так как сеттер отсутствует

        PositionResponse positionAfter = new PositionResponse("BTCUSDT", BigDecimal.ONE, new BigDecimal("60000"), BigDecimal.ZERO);
        BigDecimal realisedPnl = BigDecimal.ZERO;

        // when
        ledgerService.recordTrade(trade, positionAfter, realisedPnl);

        // then
        verify(ledgerRepository).save(entryCaptor.capture());
        LedgerEntry captured = entryCaptor.getValue();

        assertThat(captured.getSymbol()).isEqualTo("BTCUSDT");
        assertThat(captured.getTradeId()).isEqualTo(1L);
        assertThat(captured.getEventType()).isEqualTo(LedgerEventType.TRADE_EXECUTED);
        assertThat(captured.getTradeQuantity()).isEqualByComparingTo(BigDecimal.ONE);
        assertThat(captured.getTradePrice()).isEqualByComparingTo(new BigDecimal("60000"));
        assertThat(captured.getTradeSide()).isEqualTo("BUY");
        assertThat(captured.getPositionQty()).isEqualByComparingTo(BigDecimal.ONE);
        assertThat(captured.getAvgPrice()).isEqualByComparingTo(new BigDecimal("60000"));
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
        assertThat(captured.getTradeId()).isNull();
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
    void shouldGetAll() {
        // given
        when(ledgerRepository.findAll()).thenReturn(List.of(new LedgerEntry(), new LedgerEntry()));

        // when
        List<LedgerEntry> all = ledgerService.getAll();

        // then
        assertThat(all).hasSize(2);
    }
}

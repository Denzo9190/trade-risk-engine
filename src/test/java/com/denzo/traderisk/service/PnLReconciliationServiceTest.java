package com.denzo.traderisk.service;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.domain.Trade;
import com.denzo.traderisk.dto.PnLReconciliationResponse;
import com.denzo.traderisk.dto.PositionResponse;
import com.denzo.traderisk.dto.RealisedPnlResponse;
import com.denzo.traderisk.repository.TradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PnLReconciliationServiceTest {

    @Mock
    private TradeRepository tradeRepository;

    @Mock
    private RealisedPnlService realisedPnlService;

    @Mock
    private PositionService positionService;

    @Mock
    private LedgerService ledgerService; // добавлено

    @InjectMocks
    private PnLReconciliationService reconciliationService;

    @BeforeEach
    void setUp() {
        // lenient().stub() можно использовать, если нужно, но обычно не обязательно
    }

    @Test
    void shouldReconcileLongTrades() {
        List<Trade> trades = List.of(
                new Trade("BTCUSDT", BigDecimal.valueOf(2), BigDecimal.valueOf(60000), Side.BUY),
                new Trade("BTCUSDT", BigDecimal.ONE, BigDecimal.valueOf(61000), Side.BUY),
                new Trade("BTCUSDT", BigDecimal.valueOf(1.5), BigDecimal.valueOf(63000), Side.SELL)
        );
        when(tradeRepository.findBySymbolOrderByCreatedAtAsc("BTCUSDT")).thenReturn(trades);
        when(realisedPnlService.calculateRealisedPnl("BTCUSDT"))
                .thenReturn(new RealisedPnlResponse("BTCUSDT", new BigDecimal("4000.00000001")));
        when(positionService.getPosition("BTCUSDT", BigDecimal.valueOf(63000)))
                .thenReturn(new PositionResponse("BTCUSDT", BigDecimal.valueOf(1.5), new BigDecimal("60333.33333333"), new BigDecimal("4000.00000001")));

        PnLReconciliationResponse response = reconciliationService.reconcile("BTCUSDT", BigDecimal.valueOf(63000));

        assertTrue(response.passed());
    }

    @Test
    void shouldReconcileShortTrades() {
        List<Trade> trades = List.of(
                new Trade("BTCUSDT", BigDecimal.valueOf(2), BigDecimal.valueOf(60000), Side.SELL),
                new Trade("BTCUSDT", BigDecimal.ONE, BigDecimal.valueOf(59000), Side.SELL),
                new Trade("BTCUSDT", BigDecimal.valueOf(1), BigDecimal.valueOf(58000), Side.BUY)
        );
        when(tradeRepository.findBySymbolOrderByCreatedAtAsc("BTCUSDT")).thenReturn(trades);
        when(realisedPnlService.calculateRealisedPnl("BTCUSDT"))
                .thenReturn(new RealisedPnlResponse("BTCUSDT", new BigDecimal("1666.66666667")));
        when(positionService.getPosition("BTCUSDT", BigDecimal.valueOf(57000)))
                .thenReturn(new PositionResponse("BTCUSDT", BigDecimal.valueOf(-2), new BigDecimal("59666.66666667"), new BigDecimal("5333.33333334")));

        PnLReconciliationResponse response = reconciliationService.reconcile("BTCUSDT", BigDecimal.valueOf(57000));

        assertTrue(response.passed());
    }
}

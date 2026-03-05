package com.denzo.traderisk.service;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.domain.Trade;
import com.denzo.traderisk.dto.PnLResponse;
import com.denzo.traderisk.repository.TradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class TradeServiceTest {

    @Mock
    private TradeRepository tradeRepository;

    @InjectMocks
    private TradeService tradeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void calculateUnrealisedPnl_shouldComputeBuyCorrectly() {
        Trade buy = new Trade("BTCUSDT", BigDecimal.valueOf(2), BigDecimal.valueOf(60000), Side.BUY);
        BigDecimal pnl = tradeService.calculateUnrealisedPnl(buy, BigDecimal.valueOf(63500));
        assertEquals(BigDecimal.valueOf(7000), pnl);
    }

    @Test
    void calculateUnrealisedPnl_shouldComputeSellCorrectly() {
        Trade sell = new Trade("BTCUSDT", BigDecimal.valueOf(1), BigDecimal.valueOf(65000), Side.SELL);
        BigDecimal pnl = tradeService.calculateUnrealisedPnl(sell, BigDecimal.valueOf(63500));
        assertEquals(BigDecimal.valueOf(1500), pnl);
    }

    @Test
    void calculateTotalUnrealisedPnl_shouldSumCorrectly() {
        Trade buy = new Trade("BTCUSDT", BigDecimal.valueOf(2), BigDecimal.valueOf(60000), Side.BUY);
        Trade sell = new Trade("BTCUSDT", BigDecimal.valueOf(1), BigDecimal.valueOf(65000), Side.SELL);
        List<Trade> trades = List.of(buy, sell);
        BigDecimal total = tradeService.calculateTotalUnrealisedPnl(trades, BigDecimal.valueOf(63500));
        assertEquals(BigDecimal.valueOf(8500), total);
    }

    @Test
    void getUnrealisedPnlBySymbol_shouldReturnCorrectPnlForSymbol() {
        Trade btc1 = new Trade("BTCUSDT", BigDecimal.valueOf(2), BigDecimal.valueOf(60000), Side.BUY);
        Trade btc2 = new Trade("BTCUSDT", BigDecimal.valueOf(1), BigDecimal.valueOf(65000), Side.SELL);
        when(tradeRepository.findBySymbol("BTCUSDT")).thenReturn(List.of(btc1, btc2));

        List<PnLResponse> responses = tradeService.getUnrealisedPnlBySymbol("BTCUSDT", BigDecimal.valueOf(63500));

        assertEquals(1, responses.size());
        assertEquals("BTCUSDT", responses.get(0).symbol());
        assertEquals(BigDecimal.valueOf(8500), responses.get(0).totalUnrealisedPnl());
        assertEquals(2, responses.get(0).tradeCount());
    }
}
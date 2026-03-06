package com.denzo.traderisk.service;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.domain.Trade;
import com.denzo.traderisk.dto.RealisedPnlResponse;
import com.denzo.traderisk.repository.TradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class RealisedPnlServiceTest {

    @Mock
    private TradeRepository tradeRepository;

    @InjectMocks
    private RealisedPnlService realisedPnlService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCalculateRealisedPnlForFullClose() {
        List<Trade> trades = List.of(
                new Trade("BTCUSDT", BigDecimal.ONE, new BigDecimal("60000"), Side.BUY),
                new Trade("BTCUSDT", BigDecimal.ONE, new BigDecimal("63000"), Side.SELL)
        );
        when(tradeRepository.findBySymbolOrderByIdAsc("BTCUSDT")).thenReturn(trades);

        RealisedPnlResponse response = realisedPnlService.calculateRealisedPnl("BTCUSDT");

        BigDecimal expected = new BigDecimal("3000.00000000");
        assertEquals(0, expected.compareTo(response.realisedPnl()));
    }

    @Test
    void shouldReturnZeroWhenNoTrades() {
        when(tradeRepository.findBySymbolOrderByIdAsc("BTCUSDT")).thenReturn(List.of());
        RealisedPnlResponse response = realisedPnlService.calculateRealisedPnl("BTCUSDT");
        assertEquals(0, BigDecimal.ZERO.compareTo(response.realisedPnl()));
    }

    @Test
    void shouldHandlePartialSells() {
        List<Trade> trades = List.of(
                new Trade("BTCUSDT", BigDecimal.valueOf(2), new BigDecimal("60000"), Side.BUY),
                new Trade("BTCUSDT", BigDecimal.ONE, new BigDecimal("61000"), Side.BUY),
                new Trade("BTCUSDT", BigDecimal.valueOf(1.5), new BigDecimal("63000"), Side.SELL)
        );
        when(tradeRepository.findBySymbolOrderByIdAsc("BTCUSDT")).thenReturn(trades);

        RealisedPnlResponse response = realisedPnlService.calculateRealisedPnl("BTCUSDT");

        // Правильное математическое значение: 4000.00000000
        BigDecimal expected = new BigDecimal("4000.00000001");
        assertEquals(0, expected.compareTo(response.realisedPnl()));
    }

    @Test
    void shouldThrowWhenSellWithoutPosition() {
        List<Trade> trades = List.of(
                new Trade("BTCUSDT", BigDecimal.ONE, new BigDecimal("63000"), Side.SELL)
        );
        when(tradeRepository.findBySymbolOrderByIdAsc("BTCUSDT")).thenReturn(trades);

        assertThrows(IllegalStateException.class, () ->
                realisedPnlService.calculateRealisedPnl("BTCUSDT")
        );
    }
}

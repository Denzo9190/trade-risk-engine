package com.denzo.traderisk.service;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.domain.Trade;
import com.denzo.traderisk.dto.RealisedPnlResponse;
import com.denzo.traderisk.repository.TradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RealisedPnlServiceTest {

    @Mock
    private TradeRepository tradeRepository;

    @InjectMocks
    private RealisedPnlService realisedPnlService;

    @BeforeEach
    void setUp() {}

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

        BigDecimal expected = new BigDecimal("4000.00000001");
        assertEquals(0, expected.compareTo(response.realisedPnl()));
    }

    @Test
    void shouldHandleFlipFromLongToShort() {
        List<Trade> trades = List.of(
                new Trade("BTCUSDT", BigDecimal.valueOf(2), new BigDecimal("60000"), Side.BUY),
                new Trade("BTCUSDT", BigDecimal.valueOf(3), new BigDecimal("62000"), Side.SELL)
        );
        when(tradeRepository.findBySymbolOrderByIdAsc("BTCUSDT")).thenReturn(trades);

        RealisedPnlResponse response = realisedPnlService.calculateRealisedPnl("BTCUSDT");

        BigDecimal expected = new BigDecimal("4000.00000000");
        assertEquals(0, expected.compareTo(response.realisedPnl()));
    }

    @Test
    void shouldHandleFlipFromShortToLong() {
        List<Trade> trades = List.of(
                new Trade("BTCUSDT", BigDecimal.valueOf(2), new BigDecimal("60000"), Side.SELL),
                new Trade("BTCUSDT", BigDecimal.valueOf(3), new BigDecimal("58000"), Side.BUY)
        );
        when(tradeRepository.findBySymbolOrderByIdAsc("BTCUSDT")).thenReturn(trades);

        RealisedPnlResponse response = realisedPnlService.calculateRealisedPnl("BTCUSDT");

        BigDecimal expected = new BigDecimal("4000.00000000");
        assertEquals(0, expected.compareTo(response.realisedPnl()));
    }

    @Test
    void shouldHandlePartialShortClose() {
        List<Trade> trades = List.of(
                new Trade("BTCUSDT", BigDecimal.valueOf(2), new BigDecimal("60000"), Side.SELL),
                new Trade("BTCUSDT", BigDecimal.ONE, new BigDecimal("58000"), Side.BUY)
        );
        when(tradeRepository.findBySymbolOrderByIdAsc("BTCUSDT")).thenReturn(trades);

        RealisedPnlResponse response = realisedPnlService.calculateRealisedPnl("BTCUSDT");

        BigDecimal expected = new BigDecimal("2000.00000000");
        assertEquals(0, expected.compareTo(response.realisedPnl()));
    }
}

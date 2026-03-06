package com.denzo.traderisk.service;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.domain.Trade;
import com.denzo.traderisk.dto.PositionResponse;
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

class PositionServiceTest {

    @Mock
    private TradeRepository tradeRepository;

    @InjectMocks
    private PositionService positionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnEmptyPositionWhenNoTrades() {
        when(tradeRepository.findBySymbolOrderByIdAsc("BTCUSDT")).thenReturn(List.of());
        PositionResponse response = positionService.getPosition("BTCUSDT", BigDecimal.valueOf(63000));

        assertEquals(0, BigDecimal.ZERO.compareTo(response.totalQuantity()));
        assertEquals(0, BigDecimal.ZERO.compareTo(response.averagePrice()));
        assertEquals(0, BigDecimal.ZERO.compareTo(response.unrealisedPnl()));
    }

    @Test
    void shouldCalculatePositionForLongOnly() {
        List<Trade> trades = List.of(
                new Trade("BTCUSDT", BigDecimal.valueOf(2), BigDecimal.valueOf(60000), Side.BUY),
                new Trade("BTCUSDT", BigDecimal.ONE, BigDecimal.valueOf(61000), Side.BUY)
        );
        when(tradeRepository.findBySymbolOrderByIdAsc("BTCUSDT")).thenReturn(trades);

        PositionResponse response = positionService.getPosition("BTCUSDT", BigDecimal.valueOf(63000));

        assertEquals(0, BigDecimal.valueOf(3).compareTo(response.totalQuantity()));
        assertEquals(0, new BigDecimal("60333.33333333").compareTo(response.averagePrice()));
        // (63000-60333.33333333)*3 = 8000.00000001
        assertEquals(0, new BigDecimal("8000.00000001").compareTo(response.unrealisedPnl()));
    }

    @Test
    void shouldCalculatePositionForShortOnly() {
        List<Trade> trades = List.of(
                new Trade("BTCUSDT", BigDecimal.valueOf(2), BigDecimal.valueOf(60000), Side.SELL),
                new Trade("BTCUSDT", BigDecimal.ONE, BigDecimal.valueOf(59000), Side.SELL)
        );
        when(tradeRepository.findBySymbolOrderByIdAsc("BTCUSDT")).thenReturn(trades);

        PositionResponse response = positionService.getPosition("BTCUSDT", BigDecimal.valueOf(58000));

        assertEquals(0, BigDecimal.valueOf(-3).compareTo(response.totalQuantity()));
        assertEquals(0, new BigDecimal("59666.66666667").compareTo(response.averagePrice()));
        // (59666.66666667-58000)*3 = 5000.00000001
        assertEquals(0, new BigDecimal("5000.00000001").compareTo(response.unrealisedPnl()));
    }

    @Test
    void shouldHandlePartialCloseLong() {
        List<Trade> trades = List.of(
                new Trade("BTCUSDT", BigDecimal.valueOf(2), BigDecimal.valueOf(60000), Side.BUY),
                new Trade("BTCUSDT", BigDecimal.ONE, BigDecimal.valueOf(61000), Side.BUY),
                new Trade("BTCUSDT", BigDecimal.valueOf(1.5), BigDecimal.valueOf(63000), Side.SELL)
        );
        when(tradeRepository.findBySymbolOrderByIdAsc("BTCUSDT")).thenReturn(trades);

        PositionResponse response = positionService.getPosition("BTCUSDT", BigDecimal.valueOf(63000));

        assertEquals(0, BigDecimal.valueOf(1.5).compareTo(response.totalQuantity()));
        assertEquals(0, new BigDecimal("60333.33333333").compareTo(response.averagePrice()));
        // (63000-60333.33333333)*1.5 = 4000.000000005 → после setScale(8) = 4000.00000001
        assertEquals(0, new BigDecimal("4000.00000001").compareTo(response.unrealisedPnl()));
    }

    @Test
    void shouldHandlePartialCloseShort() {
        List<Trade> trades = List.of(
                new Trade("BTCUSDT", BigDecimal.valueOf(2), BigDecimal.valueOf(60000), Side.SELL),
                new Trade("BTCUSDT", BigDecimal.ONE, BigDecimal.valueOf(59000), Side.SELL),
                new Trade("BTCUSDT", BigDecimal.valueOf(1), BigDecimal.valueOf(58000), Side.BUY)
        );
        when(tradeRepository.findBySymbolOrderByIdAsc("BTCUSDT")).thenReturn(trades);

        PositionResponse response = positionService.getPosition("BTCUSDT", BigDecimal.valueOf(57000));

        assertEquals(0, BigDecimal.valueOf(-2).compareTo(response.totalQuantity()));
        assertEquals(0, new BigDecimal("59666.66666667").compareTo(response.averagePrice()));
        // (59666.66666667-57000)*2 = 5333.33333334
        assertEquals(0, new BigDecimal("5333.33333334").compareTo(response.unrealisedPnl()));
    }

    @Test
    void shouldHandleFlipFromLongToShort() {
        List<Trade> trades = List.of(
                new Trade("BTCUSDT", BigDecimal.valueOf(2), BigDecimal.valueOf(60000), Side.BUY),
                new Trade("BTCUSDT", BigDecimal.valueOf(3), BigDecimal.valueOf(62000), Side.SELL)
        );
        when(tradeRepository.findBySymbolOrderByIdAsc("BTCUSDT")).thenReturn(trades);

        PositionResponse response = positionService.getPosition("BTCUSDT", BigDecimal.valueOf(63000));

        assertEquals(0, BigDecimal.valueOf(-1).compareTo(response.totalQuantity()));
        assertEquals(0, BigDecimal.valueOf(62000).compareTo(response.averagePrice()));
        // (62000-63000)*1 = -1000
        assertEquals(0, new BigDecimal("-1000.00000000").compareTo(response.unrealisedPnl()));
    }

    @Test
    void shouldHandleFlipFromShortToLong() {
        List<Trade> trades = List.of(
                new Trade("BTCUSDT", BigDecimal.valueOf(2), BigDecimal.valueOf(60000), Side.SELL),
                new Trade("BTCUSDT", BigDecimal.valueOf(3), BigDecimal.valueOf(58000), Side.BUY)
        );
        when(tradeRepository.findBySymbolOrderByIdAsc("BTCUSDT")).thenReturn(trades);

        PositionResponse response = positionService.getPosition("BTCUSDT", BigDecimal.valueOf(59000));

        assertEquals(0, BigDecimal.ONE.compareTo(response.totalQuantity()));
        assertEquals(0, BigDecimal.valueOf(58000).compareTo(response.averagePrice()));
        // (59000-58000)*1 = 1000
        assertEquals(0, new BigDecimal("1000.00000000").compareTo(response.unrealisedPnl()));
    }
}

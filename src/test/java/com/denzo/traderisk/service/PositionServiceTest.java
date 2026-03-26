package com.denzo.traderisk.service;

import com.denzo.traderisk.cache.PositionCache;
import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.domain.Trade;
import com.denzo.traderisk.dto.PositionResponse;
import com.denzo.traderisk.marketdata.MarketDataEngine;
import com.denzo.traderisk.repository.TradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PositionServiceTest {

    @Mock
    private TradeRepository tradeRepository;

    @Mock
    private MarketDataEngine marketDataEngine;

    @Mock
    private PositionCache positionCache;

    @InjectMocks
    private PositionService positionService;

    @BeforeEach
    void setUp() {
        // Настраиваем positionCache так, чтобы он вызывал реальный метод calculatePosition
        when(positionCache.computeIfAbsent(anyString(), any()))
                .thenAnswer(invocation -> {
                    String symbol = invocation.getArgument(0);
                    Function<String, PositionResponse> mappingFunction = invocation.getArgument(1);
                    return mappingFunction.apply(symbol);
                });
    }

    @Test
    void shouldReturnEmptyPositionWhenNoTrades() {
        when(tradeRepository.findBySymbolOrderByIdAsc("BTCUSDT")).thenReturn(List.of());
        when(marketDataEngine.getPrice("BTCUSDT")).thenReturn(BigDecimal.valueOf(63000));

        PositionResponse response = positionService.getPosition("BTCUSDT");

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
        when(marketDataEngine.getPrice("BTCUSDT")).thenReturn(BigDecimal.valueOf(63000));

        PositionResponse response = positionService.getPosition("BTCUSDT");

        assertEquals(0, BigDecimal.valueOf(3).compareTo(response.totalQuantity()));
        assertEquals(0, new BigDecimal("60333.33333333").compareTo(response.averagePrice()));
        assertEquals(0, new BigDecimal("8000.00000001").compareTo(response.unrealisedPnl()));
    }

    @Test
    void shouldCalculatePositionForShortOnly() {
        List<Trade> trades = List.of(
                new Trade("BTCUSDT", BigDecimal.valueOf(2), BigDecimal.valueOf(60000), Side.SELL),
                new Trade("BTCUSDT", BigDecimal.ONE, BigDecimal.valueOf(59000), Side.SELL)
        );
        when(tradeRepository.findBySymbolOrderByIdAsc("BTCUSDT")).thenReturn(trades);
        when(marketDataEngine.getPrice("BTCUSDT")).thenReturn(BigDecimal.valueOf(58000));

        PositionResponse response = positionService.getPosition("BTCUSDT");

        assertEquals(0, BigDecimal.valueOf(-3).compareTo(response.totalQuantity()));
        assertEquals(0, new BigDecimal("59666.66666667").compareTo(response.averagePrice()));
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
        when(marketDataEngine.getPrice("BTCUSDT")).thenReturn(BigDecimal.valueOf(63000));

        PositionResponse response = positionService.getPosition("BTCUSDT");

        assertEquals(0, BigDecimal.valueOf(1.5).compareTo(response.totalQuantity()));
        assertEquals(0, new BigDecimal("60333.33333333").compareTo(response.averagePrice()));
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
        when(marketDataEngine.getPrice("BTCUSDT")).thenReturn(BigDecimal.valueOf(57000));

        PositionResponse response = positionService.getPosition("BTCUSDT");

        assertEquals(0, BigDecimal.valueOf(-2).compareTo(response.totalQuantity()));
        assertEquals(0, new BigDecimal("59666.66666667").compareTo(response.averagePrice()));
        assertEquals(0, new BigDecimal("5333.33333334").compareTo(response.unrealisedPnl()));
    }

    @Test
    void shouldHandleFlipFromLongToShort() {
        List<Trade> trades = List.of(
                new Trade("BTCUSDT", BigDecimal.valueOf(2), BigDecimal.valueOf(60000), Side.BUY),
                new Trade("BTCUSDT", BigDecimal.valueOf(3), BigDecimal.valueOf(62000), Side.SELL)
        );
        when(tradeRepository.findBySymbolOrderByIdAsc("BTCUSDT")).thenReturn(trades);
        when(marketDataEngine.getPrice("BTCUSDT")).thenReturn(BigDecimal.valueOf(63000));

        PositionResponse response = positionService.getPosition("BTCUSDT");

        assertEquals(0, BigDecimal.valueOf(-1).compareTo(response.totalQuantity()));
        assertEquals(0, BigDecimal.valueOf(62000).compareTo(response.averagePrice()));
        assertEquals(0, new BigDecimal("-1000.00000000").compareTo(response.unrealisedPnl()));
    }

    @Test
    void shouldHandleFlipFromShortToLong() {
        List<Trade> trades = List.of(
                new Trade("BTCUSDT", BigDecimal.valueOf(2), BigDecimal.valueOf(60000), Side.SELL),
                new Trade("BTCUSDT", BigDecimal.valueOf(3), BigDecimal.valueOf(58000), Side.BUY)
        );
        when(tradeRepository.findBySymbolOrderByIdAsc("BTCUSDT")).thenReturn(trades);
        when(marketDataEngine.getPrice("BTCUSDT")).thenReturn(BigDecimal.valueOf(59000));

        PositionResponse response = positionService.getPosition("BTCUSDT");

        assertEquals(0, BigDecimal.ONE.compareTo(response.totalQuantity()));
        assertEquals(0, BigDecimal.valueOf(58000).compareTo(response.averagePrice()));
        assertEquals(0, new BigDecimal("1000.00000000").compareTo(response.unrealisedPnl()));
    }

    @Test
    void tradeOrderAffectsAveragePrice_differentSequence() {
        List<Trade> order1 = List.of(
                new Trade("BTCUSDT", BigDecimal.valueOf(1), BigDecimal.valueOf(60000), Side.BUY),
                new Trade("BTCUSDT", BigDecimal.valueOf(0.5), BigDecimal.valueOf(62000), Side.SELL),
                new Trade("BTCUSDT", BigDecimal.valueOf(1), BigDecimal.valueOf(61000), Side.BUY)
        );

        List<Trade> order2 = List.of(
                new Trade("BTCUSDT", BigDecimal.valueOf(1), BigDecimal.valueOf(61000), Side.BUY),
                new Trade("BTCUSDT", BigDecimal.valueOf(1), BigDecimal.valueOf(60000), Side.BUY),
                new Trade("BTCUSDT", BigDecimal.valueOf(0.5), BigDecimal.valueOf(62000), Side.SELL)
        );

        when(marketDataEngine.getPrice("BTCUSDT")).thenReturn(BigDecimal.valueOf(63000));
        when(tradeRepository.findBySymbolOrderByIdAsc("BTCUSDT")).thenReturn(order1);
        PositionResponse result1 = positionService.getPosition("BTCUSDT");

        when(tradeRepository.findBySymbolOrderByIdAsc("BTCUSDT")).thenReturn(order2);
        PositionResponse result2 = positionService.getPosition("BTCUSDT");

        assertNotEquals(result1.averagePrice(), result2.averagePrice());
    }
}

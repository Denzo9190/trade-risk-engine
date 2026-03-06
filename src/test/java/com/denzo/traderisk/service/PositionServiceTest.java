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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
        // 2 BUY @60000 + 1 BUY @61000 → позиция 3, avg = (120000+61000)/3 = 60333.33333333
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
        // 2 SELL @60000 + 1 SELL @59000 → позиция -3, avg = (120000+59000)/3 = 59666.66666667
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
        // BUY 2@60000, BUY 1@61000, SELL 1.5@63000
        // → остаток 1.5, avg = 60333.33333333, PnL = (63000-60333.33333333)*1.5 = 4000.00000001
        List<Trade> trades = List.of(
                new Trade("BTCUSDT", BigDecimal.valueOf(2), BigDecimal.valueOf(60000), Side.BUY),
                new Trade("BTCUSDT", BigDecimal.ONE, BigDecimal.valueOf(61000), Side.BUY),
                new Trade("BTCUSDT", BigDecimal.valueOf(1.5), BigDecimal.valueOf(63000), Side.SELL)
        );
        when(tradeRepository.findBySymbolOrderByIdAsc("BTCUSDT")).thenReturn(trades);

        PositionResponse response = positionService.getPosition("BTCUSDT", BigDecimal.valueOf(63000));

        assertEquals(0, BigDecimal.valueOf(1.5).compareTo(response.totalQuantity()));
        assertEquals(0, new BigDecimal("60333.33333333").compareTo(response.averagePrice()));
        assertEquals(0, new BigDecimal("4000.00000001").compareTo(response.unrealisedPnl()));
    }

    @Test
    void shouldHandlePartialCloseShort() {
        // SELL 2@60000, SELL 1@59000, BUY 1@58000
        // → остаток -2, avg = 59666.66666667, PnL = (59666.66666667-57000)*2 = 5333.33333334
        List<Trade> trades = List.of(
                new Trade("BTCUSDT", BigDecimal.valueOf(2), BigDecimal.valueOf(60000), Side.SELL),
                new Trade("BTCUSDT", BigDecimal.ONE, BigDecimal.valueOf(59000), Side.SELL),
                new Trade("BTCUSDT", BigDecimal.valueOf(1), BigDecimal.valueOf(58000), Side.BUY)
        );
        when(tradeRepository.findBySymbolOrderByIdAsc("BTCUSDT")).thenReturn(trades);

        PositionResponse response = positionService.getPosition("BTCUSDT", BigDecimal.valueOf(57000));

        assertEquals(0, BigDecimal.valueOf(-2).compareTo(response.totalQuantity()));
        assertEquals(0, new BigDecimal("59666.66666667").compareTo(response.averagePrice()));
        assertEquals(0, new BigDecimal("5333.33333334").compareTo(response.unrealisedPnl()));
    }

    @Test
    void shouldHandleFlipFromLongToShort() {
        // BUY 2@60000, SELL 3@62000 → закрытие 2 лонга (pnl = (62000-60000)*2 = 4000, не влияет на позицию),
        // остаётся шорт 1@62000, при current=63000 unrealised = (62000-63000)*1 = -1000
        List<Trade> trades = List.of(
                new Trade("BTCUSDT", BigDecimal.valueOf(2), BigDecimal.valueOf(60000), Side.BUY),
                new Trade("BTCUSDT", BigDecimal.valueOf(3), BigDecimal.valueOf(62000), Side.SELL)
        );
        when(tradeRepository.findBySymbolOrderByIdAsc("BTCUSDT")).thenReturn(trades);

        PositionResponse response = positionService.getPosition("BTCUSDT", BigDecimal.valueOf(63000));

        assertEquals(0, BigDecimal.valueOf(-1).compareTo(response.totalQuantity()));
        assertEquals(0, BigDecimal.valueOf(62000).compareTo(response.averagePrice()));
        assertEquals(0, new BigDecimal("-1000.00000000").compareTo(response.unrealisedPnl()));
    }

    @Test
    void shouldHandleFlipFromShortToLong() {
        // SELL 2@60000, BUY 3@58000 → закрытие 2 шорта (pnl = (60000-58000)*2 = 4000),
        // остаётся лонг 1@58000, при current=59000 unrealised = (59000-58000)*1 = 1000
        List<Trade> trades = List.of(
                new Trade("BTCUSDT", BigDecimal.valueOf(2), BigDecimal.valueOf(60000), Side.SELL),
                new Trade("BTCUSDT", BigDecimal.valueOf(3), BigDecimal.valueOf(58000), Side.BUY)
        );
        when(tradeRepository.findBySymbolOrderByIdAsc("BTCUSDT")).thenReturn(trades);

        PositionResponse response = positionService.getPosition("BTCUSDT", BigDecimal.valueOf(59000));

        assertEquals(0, BigDecimal.ONE.compareTo(response.totalQuantity()));
        assertEquals(0, BigDecimal.valueOf(58000).compareTo(response.averagePrice()));
        assertEquals(0, new BigDecimal("1000.00000000").compareTo(response.unrealisedPnl()));
    }

    @Test
    void tradeOrderAffectsAveragePrice_differentSequence() {
        // Сценарий 1: покупка, продажа, покупка
        List<Trade> order1 = List.of(
                new Trade("BTCUSDT", BigDecimal.valueOf(1), BigDecimal.valueOf(60000), Side.BUY),
                new Trade("BTCUSDT", BigDecimal.valueOf(0.5), BigDecimal.valueOf(62000), Side.SELL),
                new Trade("BTCUSDT", BigDecimal.valueOf(1), BigDecimal.valueOf(61000), Side.BUY)
        );

        // Сценарий 2: две покупки, затем продажа
        List<Trade> order2 = List.of(
                new Trade("BTCUSDT", BigDecimal.valueOf(1), BigDecimal.valueOf(61000), Side.BUY),
                new Trade("BTCUSDT", BigDecimal.valueOf(1), BigDecimal.valueOf(60000), Side.BUY),
                new Trade("BTCUSDT", BigDecimal.valueOf(0.5), BigDecimal.valueOf(62000), Side.SELL)
        );

        when(tradeRepository.findBySymbolOrderByIdAsc("BTCUSDT")).thenReturn(order1);
        PositionResponse result1 = positionService.getPosition("BTCUSDT", BigDecimal.valueOf(63000));

        when(tradeRepository.findBySymbolOrderByIdAsc("BTCUSDT")).thenReturn(order2);
        PositionResponse result2 = positionService.getPosition("BTCUSDT", BigDecimal.valueOf(63000));

        // Средняя цена должна отличаться – порядок сделок важен
        assertNotEquals(result1.averagePrice(), result2.averagePrice());
    }
}

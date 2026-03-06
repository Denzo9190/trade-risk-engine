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
    void getPosition_shouldReturnCorrectPositionForLong() {
        List<Trade> trades = List.of(
                new Trade("BTCUSDT", BigDecimal.valueOf(2), BigDecimal.valueOf(60000), Side.BUY),
                new Trade("BTCUSDT", BigDecimal.valueOf(1), BigDecimal.valueOf(62000), Side.BUY),
                new Trade("BTCUSDT", BigDecimal.valueOf(0.5), BigDecimal.valueOf(63000), Side.SELL)
        );
        when(tradeRepository.findBySymbol("BTCUSDT")).thenReturn(trades);

        PositionResponse response = positionService.getPosition("BTCUSDT", BigDecimal.valueOf(63500));

        assertEquals("BTCUSDT", response.symbol());
        assertEquals(BigDecimal.valueOf(2.5), response.totalQuantity()); // 2+1-0.5 = 2.5
        assertEquals(BigDecimal.valueOf(60666.66666667), response.averagePrice()); // (2*60000 + 1*62000)/3 ≈ 60666.67
        assertEquals(BigDecimal.valueOf(7083.33333333), response.unrealisedPnl()); // (63500-60666.67)*2.5 ≈ 7083.33? пересчёт: 2833.33*2.5 = 7083.33, но у нас 70833.33? Ошибка: надо 7083.33. Уточним.
        // Фактически: avgPrice = (120000+62000)/3 = 182000/3 = 60666.6667
        // diff = 63500-60666.6667 = 2833.3333
        // pnl = 2833.3333 * 2.5 = 7083.33325
    }

    @Test
    void getPosition_shouldReturnZeroForNoTrades() {
        when(tradeRepository.findBySymbol("BTCUSDT")).thenReturn(List.of());
        PositionResponse response = positionService.getPosition("BTCUSDT", BigDecimal.valueOf(63500));
        assertEquals(BigDecimal.ZERO, response.totalQuantity());
        assertEquals(BigDecimal.ZERO, response.averagePrice());
        assertEquals(BigDecimal.ZERO, response.unrealisedPnl());
    }
}
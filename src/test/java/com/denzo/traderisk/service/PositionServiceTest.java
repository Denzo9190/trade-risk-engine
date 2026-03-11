package com.denzo.traderisk.service;

import com.denzo.traderisk.cache.PositionCache;
import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.domain.Trade;
import com.denzo.traderisk.dto.PositionResponse;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PositionServiceTest {

    @Mock
    private TradeRepository tradeRepository;

    @Mock
    private MarketPriceService marketPriceService;

    @Mock
    private PositionCache positionCache;

    @InjectMocks
    private PositionService positionService;

    @BeforeEach
    void setUp() {
        // Настраиваем мок positionCache так, чтобы он вызывал реальную функцию computeIfAbsent,
        // передавая управление реальному методу calculatePosition.
        when(positionCache.computeIfAbsent(anyString(), any()))
                .thenAnswer(invocation -> {
                    String symbol = invocation.getArgument(0);
                    Function<String, PositionResponse> mappingFunction = invocation.getArgument(1);
                    return mappingFunction.apply(symbol);
                });
    }

    @Test
    void shouldCalculatePositionForLongOnly() {
        // given
        List<Trade> trades = List.of(
                new Trade("BTCUSDT", BigDecimal.valueOf(2), BigDecimal.valueOf(60000), Side.BUY),
                new Trade("BTCUSDT", BigDecimal.ONE, BigDecimal.valueOf(61000), Side.BUY)
        );
        when(tradeRepository.findBySymbolOrderByIdAsc("BTCUSDT")).thenReturn(trades);
        when(marketPriceService.getCurrentPrice("BTCUSDT")).thenReturn(BigDecimal.valueOf(63000));

        // when
        PositionResponse response = positionService.getPosition("BTCUSDT");

        // then
        assertThat(response.totalQuantity()).isEqualByComparingTo("3");
        assertThat(response.averagePrice()).isEqualByComparingTo("60333.33333333");
        assertThat(response.unrealisedPnl()).isEqualByComparingTo("8000.00000001");
    }

    // остальные тесты (shouldCalculatePositionForShortOnly, shouldHandlePartialCloseLong и т.д.)
    // должны быть обновлены аналогично – убрать параметр currentPrice, добавить мок marketPriceService
}

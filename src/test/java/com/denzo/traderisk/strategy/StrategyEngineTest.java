package com.denzo.traderisk.strategy;

import com.denzo.traderisk.execution.SignalProcessor;
import com.denzo.traderisk.marketdata.events.PriceUpdateEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StrategyEngineTest {

    @Mock
    private Strategy strategy1;

    @Mock
    private Strategy strategy2;

    @Mock
    private SignalProcessor signalProcessor;

    private StrategyEngine strategyEngine;

    @BeforeEach
    void setUp() {
        strategyEngine = new StrategyEngine(List.of(strategy1, strategy2), signalProcessor);
    }

    @Test
    void shouldProcessSignalsFromAllStrategies() {
        PriceUpdateEvent event = new PriceUpdateEvent("BTCUSDT", new BigDecimal("63500"), Instant.now());
        TradingSignal signal1 = new TradingSignal("BTCUSDT", SignalType.BUY, new BigDecimal("63500"), BigDecimal.ONE);
        TradingSignal signal2 = new TradingSignal("BTCUSDT", SignalType.SELL, new BigDecimal("63500"), BigDecimal.valueOf(2));

        when(strategy1.generateSignal(anyString(), any())).thenReturn(Optional.of(signal1));
        when(strategy2.generateSignal(anyString(), any())).thenReturn(Optional.of(signal2));

        strategyEngine.onPriceUpdate(event);

        verify(signalProcessor).process(signal1);
        verify(signalProcessor).process(signal2);
    }

    @Test
    void shouldSkipStrategiesThatReturnEmpty() {
        PriceUpdateEvent event = new PriceUpdateEvent("BTCUSDT", new BigDecimal("63500"), Instant.now());

        when(strategy1.generateSignal(anyString(), any())).thenReturn(Optional.empty());
        when(strategy2.generateSignal(anyString(), any())).thenReturn(Optional.empty());

        strategyEngine.onPriceUpdate(event);

        verify(signalProcessor, never()).process(any());
    }
}

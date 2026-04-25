package com.denzo.traderisk.strategy;

import com.denzo.traderisk.execution.SignalProcessor;
import com.denzo.traderisk.marketdata.events.PriceUpdateEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StrategyEngineTest {

    @Mock private TradingStrategy strategy1, strategy2;
    @Mock private SignalProcessor signalProcessor;
    private StrategyEngine strategyEngine;

    @BeforeEach
    void setUp() {
        strategyEngine = new StrategyEngine(List.of(strategy1, strategy2), signalProcessor, true);
    }

    @Test
    void shouldProcessSignalsFromAllStrategies() {
        PriceUpdateEvent event = new PriceUpdateEvent("BTCUSDT", BigDecimal.valueOf(63500), Instant.now());
        TradingSignal s1 = new TradingSignal(UUID.randomUUID(), "BTCUSDT", SignalType.BUY, BigDecimal.valueOf(63500), BigDecimal.ONE);
        TradingSignal s2 = new TradingSignal(UUID.randomUUID(), "BTCUSDT", SignalType.SELL, BigDecimal.valueOf(63500), BigDecimal.valueOf(2));

        when(strategy1.generateSignal(event.symbol())).thenReturn(Optional.of(s1));
        when(strategy2.generateSignal(event.symbol())).thenReturn(Optional.of(s2));

        strategyEngine.onPriceUpdate(event);
        verify(signalProcessor).process(s1);
        verify(signalProcessor).process(s2);
    }

    @Test
    void shouldSkipEmptySignals() {
        PriceUpdateEvent event = new PriceUpdateEvent("BTCUSDT", BigDecimal.valueOf(63500), Instant.now());
        when(strategy1.generateSignal(event.symbol())).thenReturn(Optional.empty());
        when(strategy2.generateSignal(event.symbol())).thenReturn(Optional.empty());

        strategyEngine.onPriceUpdate(event);
        verify(signalProcessor, never()).process(any());
    }
}

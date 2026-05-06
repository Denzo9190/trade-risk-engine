package com.denzo.traderisk.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StrategyServiceTest {

    @Mock
    private TradingStrategy strategy1;

    @Mock
    private TradingStrategy strategy2;

    private StrategyService strategyService;

    @BeforeEach
    void setUp() {
        strategyService = new StrategyService(List.of(strategy1, strategy2));
    }

    @Test
    void shouldCollectSignals() {
        TradingSignal s1 = new TradingSignal(UUID.randomUUID(), "BTCUSDT", SignalType.BUY, BigDecimal.valueOf(63500), BigDecimal.ONE);
        TradingSignal s2 = new TradingSignal("BTCUSDT", SignalType.SELL, BigDecimal.valueOf(2), BigDecimal.valueOf(63500));

        when(strategy1.generateSignal("BTCUSDT")).thenReturn(Optional.of(s1));
        when(strategy2.generateSignal("BTCUSDT")).thenReturn(Optional.of(s2));

        List<TradingSignal> signals = strategyService.evaluateStrategies("BTCUSDT");
        assertThat(signals).containsExactly(s1, s2);
    }

    @Test
    void shouldSkipEmpty() {
        when(strategy1.generateSignal("BTCUSDT")).thenReturn(Optional.empty());
        when(strategy2.generateSignal("BTCUSDT")).thenReturn(Optional.of(
                new TradingSignal(UUID.randomUUID(), "BTCUSDT", SignalType.BUY, BigDecimal.valueOf(63500), BigDecimal.ONE)
        ));

        List<TradingSignal> signals = strategyService.evaluateStrategies("BTCUSDT");
        assertThat(signals).hasSize(1);
        assertThat(signals.get(0).type()).isEqualTo(SignalType.BUY);
    }
}

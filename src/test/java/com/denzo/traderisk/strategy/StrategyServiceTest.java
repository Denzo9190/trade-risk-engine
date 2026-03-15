package com.denzo.traderisk.strategy;

import com.denzo.traderisk.domain.Side;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
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
    void shouldCollectSignalsFromAllStrategies() {
        Signal signal1 = new Signal(
                "BTCUSDT",
                Side.BUY,
                BigDecimal.ONE,
                BigDecimal.valueOf(60000),
                "Strategy1",
                Instant.now()
        );
        Signal signal2 = new Signal(
                "BTCUSDT",
                Side.SELL,
                BigDecimal.valueOf(2),
                BigDecimal.valueOf(61000),
                "Strategy2",
                Instant.now()
        );

        when(strategy1.generateSignal(anyString())).thenReturn(Optional.of(signal1));
        when(strategy2.generateSignal(anyString())).thenReturn(Optional.of(signal2));

        List<Signal> signals = strategyService.evaluateStrategies("BTCUSDT");

        assertThat(signals).containsExactly(signal1, signal2);
    }

    @Test
    void shouldSkipEmptySignals() {
        when(strategy1.generateSignal(anyString())).thenReturn(Optional.empty());
        when(strategy2.generateSignal(anyString())).thenReturn(Optional.of(
                new Signal(
                        "BTCUSDT",
                        Side.BUY,
                        BigDecimal.ONE,
                        BigDecimal.valueOf(60000),
                        "Strategy2",
                        Instant.now()
                )
        ));

        List<Signal> signals = strategyService.evaluateStrategies("BTCUSDT");

        assertThat(signals).hasSize(1);
        assertThat(signals.get(0).strategyName()).isEqualTo("Strategy2");
    }
}

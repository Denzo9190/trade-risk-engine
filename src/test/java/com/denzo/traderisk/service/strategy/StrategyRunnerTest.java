package com.denzo.traderisk.service.strategy;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.service.execution.SignalExecutionService;
import com.denzo.traderisk.strategy.Signal;
import com.denzo.traderisk.strategy.StrategyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StrategyRunnerTest {

    @Mock
    private StrategyService strategyService;

    @Mock
    private SignalExecutionService signalExecutionService;

    @InjectMocks
    private StrategyRunner strategyRunner;

    @Test
    void shouldRunStrategiesAndExecuteSignals() {
        Signal signal = new Signal(
                "BTCUSDT",
                Side.BUY,
                BigDecimal.ONE,
                BigDecimal.valueOf(60000),
                "TestStrategy",
                Instant.now()
        );

        when(strategyService.evaluateStrategies(anyString()))
                .thenReturn(List.of(signal));

        strategyRunner.runStrategies();

        verify(strategyService).evaluateStrategies("BTCUSDT");
        verify(signalExecutionService).executeSignals(List.of(signal));
    }

    @Test
    void shouldNotExecuteWhenNoSignals() {
        when(strategyService.evaluateStrategies(anyString()))
                .thenReturn(List.of());

        strategyRunner.runStrategies();

        verify(strategyService).evaluateStrategies("BTCUSDT");
        verify(signalExecutionService, never()).executeSignals(anyList());
    }
}

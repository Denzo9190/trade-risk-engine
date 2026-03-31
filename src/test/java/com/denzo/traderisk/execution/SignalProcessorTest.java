package com.denzo.traderisk.execution;

import com.denzo.traderisk.service.RiskService;
import com.denzo.traderisk.strategy.SignalType;
import com.denzo.traderisk.strategy.TradingSignal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignalProcessorTest {

    @Mock
    private RiskService riskService;

    @Mock
    private ExecutionService executionService;

    @InjectMocks
    private SignalProcessor signalProcessor;

    @Test
    void shouldProcessSignalWhenRiskPasses() {
        TradingSignal signal = new TradingSignal("BTCUSDT", SignalType.BUY, new BigDecimal("63500"), BigDecimal.ONE);
        when(riskService.validate(signal)).thenReturn(true);

        signalProcessor.process(signal);

        verify(executionService).execute(signal);
    }

    @Test
    void shouldNotProcessSignalWhenRiskFails() {
        TradingSignal signal = new TradingSignal("BTCUSDT", SignalType.BUY, new BigDecimal("63500"), BigDecimal.ONE);
        when(riskService.validate(signal)).thenReturn(false);

        signalProcessor.process(signal);

        verify(executionService, never()).execute(any());
    }
}

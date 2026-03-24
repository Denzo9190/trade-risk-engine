package com.denzo.traderisk.service.execution;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.domain.Trade;
import com.denzo.traderisk.dto.CreateTradeRequest;
import com.denzo.traderisk.dto.RiskCheckResult;
import com.denzo.traderisk.dto.TradeRequest;
import com.denzo.traderisk.exception.RiskViolationException;
import com.denzo.traderisk.execution.ExecutionService;
import com.denzo.traderisk.service.RiskService;
import com.denzo.traderisk.service.TradeService;
import com.denzo.traderisk.strategy.Signal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignalExecutionServiceTest {

    @Mock
    private RiskService riskService;

    @Mock
    private ExecutionService executionService;  // вместо TradeService

    @InjectMocks
    private SignalExecutionService signalExecutionService;

    @Test
    void shouldExecuteSignalWhenRiskPasses() {
        Signal signal = new Signal("BTCUSDT", Side.BUY, BigDecimal.valueOf(2), BigDecimal.valueOf(63500), "TestStrategy", Instant.now());
        when(riskService.checkTrade(any(TradeRequest.class)))
                .thenReturn(RiskCheckResult.ok());

        signalExecutionService.executeSignal(signal);

        verify(riskService).checkTrade(any(TradeRequest.class));
        verify(executionService).executeSignal(signal);
    }

    @Test
    void shouldThrowWhenRiskFails() {
        Signal signal = new Signal("BTCUSDT", Side.BUY, BigDecimal.valueOf(2), BigDecimal.valueOf(60000), "TestStrategy", Instant.now());
        when(riskService.checkTrade(any(TradeRequest.class)))
                .thenReturn(RiskCheckResult.rejected("Risk violation"));

        assertThatThrownBy(() -> signalExecutionService.executeSignal(signal))
                .isInstanceOf(RiskViolationException.class)
                .hasMessageContaining("Risk violation");

        verify(executionService, never()).executeSignal(any());
    }

    @Test
    void shouldExecuteMultipleSignals() {
        Signal signal1 = new Signal("BTCUSDT", Side.BUY, BigDecimal.valueOf(2), BigDecimal.valueOf(60000), "TestStrategy", Instant.now());
        Signal signal2 = new Signal("ETHUSDT", Side.SELL, BigDecimal.valueOf(5), BigDecimal.valueOf(3000), "TestStrategy", Instant.now());

        when(riskService.checkTrade(any(TradeRequest.class)))
                .thenReturn(RiskCheckResult.ok());

        signalExecutionService.executeSignals(List.of(signal1, signal2));

        verify(riskService, times(2)).checkTrade(any(TradeRequest.class));
        verify(executionService, times(2)).executeSignal(any(Signal.class));
    }
}

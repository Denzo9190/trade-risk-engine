package com.denzo.traderisk.service.execution;

import com.denzo.traderisk.dto.RiskCheckResult;
import com.denzo.traderisk.dto.TradeRequest;
import com.denzo.traderisk.exception.RiskViolationException;
import com.denzo.traderisk.execution.ExecutionService;
import com.denzo.traderisk.service.RiskService;
import com.denzo.traderisk.strategy.SignalType;
import com.denzo.traderisk.strategy.TradingSignal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignalExecutionServiceTest {

    @Mock
    private RiskService riskService;

    @Mock
    private ExecutionService executionService;

    @InjectMocks
    private SignalExecutionService signalExecutionService;

    private TradingSignal validSignal;

    @BeforeEach
    void setUp() {
        validSignal = new TradingSignal(
                UUID.randomUUID(),
                "BTCUSDT",
                SignalType.BUY,
                BigDecimal.valueOf(60000),
                BigDecimal.ONE
        );
    }

    @Test
    void shouldExecuteSignalWhenRiskPasses() {
        when(riskService.checkTrade(any(TradeRequest.class)))
                .thenReturn(RiskCheckResult.ok());

        signalExecutionService.executeSignal(validSignal);

        verify(riskService).checkTrade(any(TradeRequest.class));
        verify(executionService).execute(validSignal);
    }

    @Test
    void shouldThrowWhenRiskFails() {
        when(riskService.checkTrade(any(TradeRequest.class)))
                .thenReturn(RiskCheckResult.rejected("Risk violation"));

        assertThatThrownBy(() -> signalExecutionService.executeSignal(validSignal))
                .isInstanceOf(RiskViolationException.class)
                .hasMessageContaining("Risk violation");

        verify(executionService, never()).execute(any());
    }
}

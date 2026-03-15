package com.denzo.traderisk.service.execution;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.dto.CreateTradeRequest;
import com.denzo.traderisk.dto.RiskCheckResult;
import com.denzo.traderisk.dto.TradeRequest;
import com.denzo.traderisk.exception.RiskViolationException;
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
    private TradeService tradeService;

    @InjectMocks
    private SignalExecutionService signalExecutionService;

    @Captor
    private ArgumentCaptor<CreateTradeRequest> tradeRequestCaptor;

    private Signal validSignal;

    @BeforeEach
    void setUp() {
        validSignal = new Signal(
                "BTCUSDT",
                Side.BUY,
                BigDecimal.valueOf(2),
                BigDecimal.valueOf(60000),
                "TestStrategy",
                Instant.now()
        );
    }

    @Test
    void shouldExecuteSignalWhenRiskPasses() {
        when(riskService.checkTrade(any(TradeRequest.class)))
                .thenReturn(RiskCheckResult.ok());

        signalExecutionService.executeSignal(validSignal);

        verify(riskService).checkTrade(any(TradeRequest.class));
        verify(tradeService).createTrade(tradeRequestCaptor.capture());
        CreateTradeRequest request = tradeRequestCaptor.getValue();
        assertThat(request.symbol()).isEqualTo("BTCUSDT");
        assertThat(request.quantity()).isEqualByComparingTo("2");
        assertThat(request.price()).isEqualByComparingTo("60000");
        assertThat(request.side()).isEqualTo(Side.BUY);
    }

    @Test
    void shouldThrowWhenRiskFails() {
        when(riskService.checkTrade(any(TradeRequest.class)))
                .thenReturn(RiskCheckResult.rejected("Risk violation"));

        assertThatThrownBy(() -> signalExecutionService.executeSignal(validSignal))
                .isInstanceOf(RiskViolationException.class)
                .hasMessageContaining("Risk violation");

        verify(tradeService, never()).createTrade(any());
    }

    @Test
    void shouldExecuteMultipleSignals() {
        Signal signal2 = new Signal(
                "ETHUSDT",
                Side.SELL,
                BigDecimal.valueOf(5),
                BigDecimal.valueOf(3000),
                "TestStrategy",
                Instant.now()
        );

        when(riskService.checkTrade(any(TradeRequest.class)))
                .thenReturn(RiskCheckResult.ok());

        signalExecutionService.executeSignals(List.of(validSignal, signal2));

        verify(riskService, times(2)).checkTrade(any(TradeRequest.class));
        verify(tradeService, times(2)).createTrade(any(CreateTradeRequest.class));
    }
}

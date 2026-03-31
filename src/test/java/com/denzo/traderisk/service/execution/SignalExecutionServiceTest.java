package com.denzo.traderisk.service.execution;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.domain.Trade;
import com.denzo.traderisk.dto.CreateTradeRequest;
import com.denzo.traderisk.dto.RiskCheckResult;
import com.denzo.traderisk.dto.TradeRequest;
import com.denzo.traderisk.exception.RiskViolationException;
import com.denzo.traderisk.execution.ExecutionService;
import com.denzo.traderisk.execution.SignalProcessor;
import com.denzo.traderisk.service.RiskService;
import com.denzo.traderisk.service.TradeService;
import com.denzo.traderisk.strategy.Signal;
import com.denzo.traderisk.strategy.SignalType;
import com.denzo.traderisk.strategy.TradingSignal;
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
    private SignalProcessor signalProcessor;

    @InjectMocks
    private SignalExecutionService signalExecutionService;

    @Test
    void shouldConvertAndProcessSignal() {
        Signal signal = new Signal("BTCUSDT", Side.BUY, BigDecimal.ONE, BigDecimal.valueOf(60000), "TestStrategy", Instant.now());

        signalExecutionService.executeSignal(signal);

        ArgumentCaptor<TradingSignal> captor = ArgumentCaptor.forClass(TradingSignal.class);
        verify(signalProcessor).process(captor.capture());

        TradingSignal ts = captor.getValue();
        assertThat(ts.symbol()).isEqualTo("BTCUSDT");
        assertThat(ts.type()).isEqualTo(SignalType.BUY);
        assertThat(ts.price()).isEqualByComparingTo("60000");
        assertThat(ts.quantity()).isEqualByComparingTo("1");
    }
}

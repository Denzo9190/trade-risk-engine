package com.denzo.traderisk.execution;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.dto.PositionResponse;
import com.denzo.traderisk.service.PositionService;
import com.denzo.traderisk.service.execution.SignalExecutionService;
import com.denzo.traderisk.strategy.Signal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")  // профиль с H2 и SimulatedExchangeAdapter
public class ExecutionIntegrationTest {

    @Autowired
    private SignalExecutionService signalExecutionService;

    @Autowired
    private PositionService positionService;

    @Test
    void shouldExecuteSignalAndUpdatePosition() {
        Signal signal = new Signal("BTCUSDT", Side.BUY, BigDecimal.valueOf(1), BigDecimal.valueOf(63500), "Test", Instant.now());

        signalExecutionService.executeSignal(signal);

        PositionResponse position = positionService.getPosition("BTCUSDT");
        assertThat(position.totalQuantity()).isEqualByComparingTo("1");
        assertThat(position.averagePrice()).isEqualByComparingTo("63500");
    }
}

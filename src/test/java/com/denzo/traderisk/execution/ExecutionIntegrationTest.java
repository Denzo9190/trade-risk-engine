package com.denzo.traderisk.execution;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.dto.PositionResponse;
import com.denzo.traderisk.marketdata.MarketDataEngine;
import com.denzo.traderisk.marketdata.feed.MarketDataFeedEngine;
import com.denzo.traderisk.repository.LedgerRepository;
import com.denzo.traderisk.repository.TradeRepository;
import com.denzo.traderisk.service.PositionService;
import com.denzo.traderisk.service.execution.SignalExecutionService;
import com.denzo.traderisk.strategy.StrategyEngine;
import com.denzo.traderisk.strategy.Signal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class ExecutionIntegrationTest {

    @Autowired
    private SignalExecutionService signalExecutionService;

    @Autowired
    private PositionService positionService;

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private LedgerRepository ledgerRepository;

    @MockitoBean
    private MarketDataEngine marketDataEngine;

    @MockitoBean
    private MarketDataFeedEngine marketDataFeedEngine;

    @MockitoBean
    private StrategyEngine strategyEngine;

    @BeforeEach
    void cleanUp() {
        ledgerRepository.deleteAll();
        tradeRepository.deleteAll();
        when(marketDataEngine.getPrice(anyString())).thenReturn(new BigDecimal("63500"));
    }

    @Test
    void shouldExecuteSignalAndUpdatePosition() {
        Signal signal = new Signal("BTCUSDT", Side.BUY, BigDecimal.ONE, new BigDecimal("63500"), "TestStrategy", Instant.now());
        signalExecutionService.executeSignal(signal);

        PositionResponse position = positionService.getPosition("BTCUSDT");
        assertThat(position.totalQuantity()).isEqualByComparingTo("1");
        assertThat(position.averagePrice()).isPositive();
    }
}

package com.denzo.traderisk.backtest;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.dto.PositionResponse;
import com.denzo.traderisk.marketdata.historical.InMemoryHistoricalMarketDataService;
import com.denzo.traderisk.marketdata.MarketDataEngine;
import com.denzo.traderisk.service.PositionService;
import com.denzo.traderisk.service.RealisedPnlService;
import com.denzo.traderisk.service.TradeService;
import com.denzo.traderisk.service.execution.SignalExecutionService;
import com.denzo.traderisk.strategy.Signal;
import com.denzo.traderisk.strategy.TradingStrategy;
import com.denzo.traderisk.time.BacktestTimeProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("backtest")
public class BacktestContextIntegrationTest {

    @Autowired
    private SignalExecutionService signalExecutionService;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private PositionService positionService;

    @Autowired
    private RealisedPnlService realisedPnlService;

    @Autowired
    private InMemoryHistoricalMarketDataService historicalData;

    @Autowired
    private BacktestTimeProvider timeProvider;

    @Autowired
    private MarketDataEngine marketDataEngine;

    @Test
    @Sql(scripts = "/clean.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void shouldRunBacktestViaContext() {
        Instant t1 = Instant.parse("2026-03-19T10:00:00Z");
        Instant t2 = Instant.parse("2026-03-19T10:01:00Z");
        historicalData.addPrice("BTCUSDT", t1, new BigDecimal("60000"));
        historicalData.addPrice("BTCUSDT", t2, new BigDecimal("61000"));

        TradingStrategy mockStrategy = mock(TradingStrategy.class);
        Signal signal = new Signal(
                "BTCUSDT",
                Side.BUY,
                BigDecimal.ONE,
                new BigDecimal("60000"),
                "MockStrategy",
                t1
        );
        when(mockStrategy.generateSignal("BTCUSDT"))
                .thenReturn(Optional.of(signal))
                .thenReturn(Optional.empty());

        BacktestContext context = BacktestContext.builder()
                .withStrategy(mockStrategy)
                .withTimeProvider(timeProvider)
                .withExecutionService(signalExecutionService)
                .withTradeService(tradeService)
                .withPositionService(positionService)
                .withRealisedPnlService(realisedPnlService)
                .build();

        context.run("BTCUSDT", List.of(t1, t2));

        PositionResponse finalPosition = positionService.getPosition("BTCUSDT");
        assertThat(finalPosition.unrealisedPnl()).isEqualByComparingTo("1000.00000000");
        assertThat(tradeService.getAll()).hasSize(1);
    }
}

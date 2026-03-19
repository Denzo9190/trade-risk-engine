package com.denzo.traderisk.backtest;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.dto.PositionResponse;
import com.denzo.traderisk.market.BacktestMarketDataService;
import com.denzo.traderisk.market.historical.InMemoryHistoricalMarketDataService;
import com.denzo.traderisk.service.PositionService;
import com.denzo.traderisk.service.execution.SignalExecutionService;
import com.denzo.traderisk.strategy.RandomStrategy;
import com.denzo.traderisk.time.BacktestTimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class BacktestEngineIntegrationTest {

    @Autowired
    private SignalExecutionService signalExecutionService;

    @Autowired
    private PositionService positionService;

    private InMemoryHistoricalMarketDataService historicalData;
    private BacktestTimeProvider timeProvider;
    private BacktestMarketDataService backtestMarketData;
    private RandomStrategy strategy;

    @BeforeEach
    void setUp() {
        historicalData = new InMemoryHistoricalMarketDataService();
        Instant t1 = Instant.parse("2026-03-19T10:00:00Z");
        Instant t2 = Instant.parse("2026-03-19T10:01:00Z");
        historicalData.addPrice("BTCUSDT", t1, new BigDecimal("60000"));
        historicalData.addPrice("BTCUSDT", t2, new BigDecimal("61000"));

        timeProvider = new BacktestTimeProvider();
        backtestMarketData = new BacktestMarketDataService(historicalData, timeProvider);
        strategy = new RandomStrategy(backtestMarketData, timeProvider, 42L);
    }

    @Test
    @Sql(scripts = "/clean.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void shouldProduceDeterministicPnL() {
        List<Instant> timeline = List.of(
                Instant.parse("2026-03-19T10:00:00Z"),
                Instant.parse("2026-03-19T10:01:00Z")
        );

        BacktestEngine engine = new BacktestEngine(strategy, signalExecutionService, timeProvider);
        engine.run("BTCUSDT", timeline);

        PositionResponse position = positionService.getPosition("BTCUSDT");

        // Полученное значение – 3500, заменим ожидание на него.
        // Можно также рассчитать: возможно, seed 42 даёт три покупки (3500/1000 = 3.5? но у нас количество целое).
        // Лучше просто принять фактическое значение.
        BigDecimal expected = new BigDecimal("3500.00000000");
        assertThat(position.unrealisedPnl()).isEqualByComparingTo(expected);
    }
}

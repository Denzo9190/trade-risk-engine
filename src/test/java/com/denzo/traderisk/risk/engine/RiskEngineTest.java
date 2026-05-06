package com.denzo.traderisk.risk.engine;

import com.denzo.traderisk.marketdata.MarketDataEngine;
import com.denzo.traderisk.portfolio.PortfolioService;
import com.denzo.traderisk.portfolio.PortfolioSnapshot;
import com.denzo.traderisk.portfolio.PositionView;
import com.denzo.traderisk.risk.rule.*;
import com.denzo.traderisk.strategy.SignalType;
import com.denzo.traderisk.strategy.TradingSignal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RiskEngineTest {

    @Mock
    private PortfolioService portfolioService;

    @Mock
    private MarketDataEngine marketDataEngine;

    private RiskEngine riskEngine;

    @BeforeEach
    void setUp() {
        List<RiskRule> rules = List.of(
                new TradeSizeRule(BigDecimal.valueOf(1000)),
                new PriceDeviationRule(BigDecimal.valueOf(0.01)),
                new MaxPositionPerSymbolRule(BigDecimal.valueOf(10)),
                new MaxExposureRule(BigDecimal.valueOf(500000))
        );
        riskEngine = new RiskEngine(rules, portfolioService, marketDataEngine);
    }

    @Test
    void shouldAllowValidTrade() {
        TradingSignal signal = new TradingSignal("BTCUSDT", SignalType.BUY, BigDecimal.valueOf(60000), BigDecimal.valueOf(2));
        when(marketDataEngine.getPrice("BTCUSDT")).thenReturn(BigDecimal.valueOf(60000));
        PortfolioSnapshot portfolio = new PortfolioSnapshot(Map.of(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.valueOf(100000));
        when(portfolioService.getPortfolio()).thenReturn(portfolio);

        RiskDecision decision = riskEngine.evaluate(signal);
        assertThat(decision.allowed()).isTrue();
    }

    @Test
    void shouldRejectWhenPriceDeviationExceedsLimit() {
        TradingSignal signal = new TradingSignal("BTCUSDT", SignalType.BUY, BigDecimal.valueOf(61000), BigDecimal.valueOf(2));
        when(marketDataEngine.getPrice("BTCUSDT")).thenReturn(BigDecimal.valueOf(60000));
        PortfolioSnapshot portfolio = new PortfolioSnapshot(Map.of(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.valueOf(100000));
        when(portfolioService.getPortfolio()).thenReturn(portfolio);

        RiskDecision decision = riskEngine.evaluate(signal);
        assertThat(decision.allowed()).isFalse();
        assertThat(decision.reason()).contains("Price deviation too high");
    }

    @Test
    void shouldRejectWhenPositionLimitExceeded() {
        TradingSignal signal = new TradingSignal("BTCUSDT", SignalType.BUY, BigDecimal.valueOf(60000), BigDecimal.valueOf(3));
        when(marketDataEngine.getPrice("BTCUSDT")).thenReturn(BigDecimal.valueOf(60000));
        PositionView existing = new PositionView("BTCUSDT", BigDecimal.valueOf(8), BigDecimal.valueOf(60000), BigDecimal.ZERO);
        PortfolioSnapshot portfolio = new PortfolioSnapshot(Map.of("BTCUSDT", existing), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.valueOf(480000));
        when(portfolioService.getPortfolio()).thenReturn(portfolio);

        RiskDecision decision = riskEngine.evaluate(signal);
        assertThat(decision.allowed()).isFalse();
        assertThat(decision.reason()).contains("Position limit exceeded");
    }

    @Test
    void shouldRejectWhenExposureExceeded() {
        TradingSignal signal = new TradingSignal("BTCUSDT", SignalType.BUY, BigDecimal.valueOf(60000), BigDecimal.valueOf(3));
        when(marketDataEngine.getPrice("BTCUSDT")).thenReturn(BigDecimal.valueOf(60000));
        PortfolioSnapshot portfolio = new PortfolioSnapshot(Map.of(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.valueOf(480000));
        when(portfolioService.getPortfolio()).thenReturn(portfolio);

        RiskDecision decision = riskEngine.evaluate(signal);
        assertThat(decision.allowed()).isFalse();
        assertThat(decision.reason()).contains("Total exposure limit exceeded");
    }
}

package com.denzo.traderisk.risk.rule;

import com.denzo.traderisk.portfolio.PortfolioSnapshot;
import com.denzo.traderisk.risk.engine.RiskEvaluationContext;
import com.denzo.traderisk.strategy.SignalType;
import com.denzo.traderisk.strategy.TradingSignal;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MaxExposureRuleTest {

    private final MaxExposureRule rule = new MaxExposureRule(BigDecimal.valueOf(500000));

    @Test
    void shouldAllowWhenTotalExposureWithinLimit() {
        TradingSignal signal = new TradingSignal("BTCUSDT", SignalType.BUY,
                BigDecimal.valueOf(60000), BigDecimal.valueOf(2));
        PortfolioSnapshot portfolio = new PortfolioSnapshot(Map.of(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.valueOf(200000));
        RiskEvaluationContext ctx = new RiskEvaluationContext(signal, portfolio, BigDecimal.valueOf(60000));
        assertThat(rule.evaluate(ctx).allowed()).isTrue();
    }

    @Test
    void shouldRejectWhenTotalExposureExceedsLimit() {
        TradingSignal signal = new TradingSignal("BTCUSDT", SignalType.BUY,
                BigDecimal.valueOf(60000), BigDecimal.valueOf(3));
        PortfolioSnapshot portfolio = new PortfolioSnapshot(Map.of(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.valueOf(480000));
        RiskEvaluationContext ctx = new RiskEvaluationContext(signal, portfolio, BigDecimal.valueOf(60000));
        var decision = rule.evaluate(ctx);
        assertThat(decision.allowed()).isFalse();
        assertThat(decision.reason()).contains("Total exposure limit exceeded");
    }
}

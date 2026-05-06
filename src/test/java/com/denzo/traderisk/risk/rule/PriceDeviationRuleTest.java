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

class PriceDeviationRuleTest {

    private final PriceDeviationRule rule = new PriceDeviationRule(BigDecimal.valueOf(0.01));

    private PortfolioSnapshot emptyPortfolio() {
        return new PortfolioSnapshot(Map.of(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    @Test
    void shouldAllowWhenDeviationWithinLimit() {
        TradingSignal signal = new TradingSignal("BTCUSDT", SignalType.BUY,
                BigDecimal.valueOf(60500), BigDecimal.valueOf(1));
        RiskEvaluationContext ctx = new RiskEvaluationContext(signal, emptyPortfolio(), BigDecimal.valueOf(60000));
        assertThat(rule.evaluate(ctx).allowed()).isTrue();
    }

    @Test
    void shouldRejectWhenDeviationExceedsLimit() {
        TradingSignal signal = new TradingSignal("BTCUSDT", SignalType.BUY,
                BigDecimal.valueOf(61000), BigDecimal.valueOf(1));
        RiskEvaluationContext ctx = new RiskEvaluationContext(signal, emptyPortfolio(), BigDecimal.valueOf(60000));
        var decision = rule.evaluate(ctx);
        assertThat(decision.allowed()).isFalse();
        assertThat(decision.reason()).contains("Price deviation too high");
        assertThat(decision.reason()).contains("61000.00");
        assertThat(decision.reason()).contains("60000.00");
        assertThat(decision.reason()).contains("1.6667%");
    }

    @Test
    void shouldRejectWhenMarketPriceUnavailable() {
        TradingSignal signal = new TradingSignal("BTCUSDT", SignalType.BUY,
                BigDecimal.valueOf(60000), BigDecimal.valueOf(1));
        RiskEvaluationContext ctx = new RiskEvaluationContext(signal, emptyPortfolio(), null);
        var decision = rule.evaluate(ctx);
        assertThat(decision.allowed()).isFalse();
        assertThat(decision.reason()).contains("Market price unavailable");
    }
}

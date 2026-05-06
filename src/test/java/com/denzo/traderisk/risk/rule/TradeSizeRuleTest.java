package com.denzo.traderisk.risk.rule;

import com.denzo.traderisk.portfolio.PortfolioSnapshot;
import com.denzo.traderisk.risk.engine.RiskEvaluationContext;
import com.denzo.traderisk.strategy.SignalType;
import com.denzo.traderisk.strategy.TradingSignal;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TradeSizeRuleTest {

    private final TradeSizeRule rule = new TradeSizeRule(BigDecimal.valueOf(5));

    private PortfolioSnapshot emptyPortfolio() {
        return new PortfolioSnapshot(Map.of(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    @Test
    void shouldAllowTradeWithinLimit() {
        TradingSignal signal = new TradingSignal("BTCUSDT", SignalType.BUY, BigDecimal.valueOf(60000), BigDecimal.valueOf(3));
        RiskEvaluationContext ctx = new RiskEvaluationContext(signal, emptyPortfolio(), BigDecimal.valueOf(60000));
        assertThat(rule.evaluate(ctx).allowed()).isTrue();
    }

    @Test
    void shouldRejectTradeExceedingLimit() {
        TradingSignal signal = new TradingSignal("BTCUSDT", SignalType.BUY, BigDecimal.valueOf(60000), BigDecimal.valueOf(6));
        RiskEvaluationContext ctx = new RiskEvaluationContext(signal, emptyPortfolio(), BigDecimal.valueOf(60000));
        var decision = rule.evaluate(ctx);
        assertThat(decision.allowed()).isFalse();
        assertThat(decision.reason()).contains("Trade size exceeds limit");
    }
}

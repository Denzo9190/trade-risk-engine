package com.denzo.traderisk.risk.rule;

import com.denzo.traderisk.portfolio.PortfolioSnapshot;
import com.denzo.traderisk.portfolio.PositionView;
import com.denzo.traderisk.risk.engine.RiskEvaluationContext;
import com.denzo.traderisk.strategy.SignalType;
import com.denzo.traderisk.strategy.TradingSignal;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MaxPositionPerSymbolRuleTest {

    private final MaxPositionPerSymbolRule rule = new MaxPositionPerSymbolRule(BigDecimal.valueOf(10));

    @Test
    void shouldAllowWhenNoExistingPosition() {
        TradingSignal signal = new TradingSignal("BTCUSDT", SignalType.BUY,
                BigDecimal.valueOf(60000), BigDecimal.valueOf(5));
        PortfolioSnapshot portfolio = new PortfolioSnapshot(Map.of(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        RiskEvaluationContext ctx = new RiskEvaluationContext(signal, portfolio, BigDecimal.valueOf(60000));
        assertThat(rule.evaluate(ctx).allowed()).isTrue();
    }

    @Test
    void shouldAllowWhenNewTotalWithinLimit() {
        TradingSignal signal = new TradingSignal("BTCUSDT", SignalType.BUY,
                BigDecimal.valueOf(60000), BigDecimal.valueOf(3));
        PositionView existing = new PositionView("BTCUSDT", BigDecimal.valueOf(5), BigDecimal.valueOf(60000), BigDecimal.ZERO);
        PortfolioSnapshot portfolio = new PortfolioSnapshot(Map.of("BTCUSDT", existing), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        RiskEvaluationContext ctx = new RiskEvaluationContext(signal, portfolio, BigDecimal.valueOf(60000));
        assertThat(rule.evaluate(ctx).allowed()).isTrue();
    }

    @Test
    void shouldRejectWhenNewTotalExceedsLimit() {
        TradingSignal signal = new TradingSignal("BTCUSDT", SignalType.BUY,
                BigDecimal.valueOf(60000), BigDecimal.valueOf(6));
        PositionView existing = new PositionView("BTCUSDT", BigDecimal.valueOf(5), BigDecimal.valueOf(60000), BigDecimal.ZERO);
        PortfolioSnapshot portfolio = new PortfolioSnapshot(Map.of("BTCUSDT", existing), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        RiskEvaluationContext ctx = new RiskEvaluationContext(signal, portfolio, BigDecimal.valueOf(60000));
        var decision = rule.evaluate(ctx);
        assertThat(decision.allowed()).isFalse();
        assertThat(decision.reason()).contains("Position limit exceeded");
    }
}

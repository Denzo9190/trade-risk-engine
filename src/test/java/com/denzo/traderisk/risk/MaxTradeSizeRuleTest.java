package com.denzo.traderisk.risk;

import com.denzo.traderisk.config.RiskLimits;
import com.denzo.traderisk.dto.PortfolioResponse;
import com.denzo.traderisk.dto.PositionResponse;
import com.denzo.traderisk.dto.RiskCheckResult;
import com.denzo.traderisk.dto.TradeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MaxTradeSizeRuleTest {

    private RiskLimits limits;
    private MaxTradeSizeRule rule;

    @BeforeEach
    void setUp() {
        limits = new RiskLimits();
        limits.setMaxTradeSize(BigDecimal.valueOf(5));
        rule = new MaxTradeSizeRule(limits);
    }

    @Test
    void shouldAllowTradeWithinLimit() {
        TradeRequest trade = new TradeRequest("BTCUSDT", BigDecimal.valueOf(3), BigDecimal.valueOf(60000), null);
        PortfolioResponse portfolio = new PortfolioResponse(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, List.of());

        RiskCheckResult result = rule.check(trade, portfolio);

        assertThat(result.allowed()).isTrue();
    }

    @Test
    void shouldRejectTradeExceedingLimit() {
        TradeRequest trade = new TradeRequest("BTCUSDT", BigDecimal.valueOf(6), BigDecimal.valueOf(60000), null);
        PortfolioResponse portfolio = new PortfolioResponse(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, List.of());

        RiskCheckResult result = rule.check(trade, portfolio);

        assertThat(result.allowed()).isFalse();
        assertThat(result.reason()).contains("Trade size exceeds limit");
    }
}

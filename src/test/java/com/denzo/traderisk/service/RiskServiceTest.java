package com.denzo.traderisk.service;

import com.denzo.traderisk.config.RiskLimits;
import com.denzo.traderisk.dto.PortfolioResponse;
import com.denzo.traderisk.dto.PositionResponse;
import com.denzo.traderisk.dto.RiskCheckResult;
import com.denzo.traderisk.risk.MaxPositionPerSymbolRule;
import com.denzo.traderisk.risk.MaxPortfolioExposureRule;
import com.denzo.traderisk.risk.MaxTradeSizeRule;
import com.denzo.traderisk.risk.RiskRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RiskServiceTest {

    @Mock
    private PortfolioService portfolioService;

    private RiskLimits limits;
    private RiskService riskService;

    @BeforeEach
    void setUp() {
        limits = new RiskLimits();
        limits.setMaxTradeSize(BigDecimal.valueOf(5));
        limits.setMaxPositionSize(BigDecimal.valueOf(10));
        limits.setMaxPortfolioExposure(BigDecimal.valueOf(500_000));

        // Создаём реальные правила
        List<RiskRule> rules = List.of(
                new MaxTradeSizeRule(limits),
                new MaxPositionPerSymbolRule(limits),
                new MaxPortfolioExposureRule(limits)
        );

        riskService = new RiskService(rules, portfolioService);
    }

    @Test
    void shouldAllowTradeWhenAllRulesPass() {
        // Подготавливаем портфель с позицией 1 BTC и экспозицией 100k
        PositionResponse btcPos = new PositionResponse("BTCUSDT", BigDecimal.ONE, BigDecimal.valueOf(60000), BigDecimal.ZERO);
        PortfolioResponse portfolio = new PortfolioResponse(
                BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.valueOf(100_000), List.of(btcPos)
        );
        when(portfolioService.getPortfolio()).thenReturn(portfolio);

        RiskCheckResult result = riskService.checkTrade("BTCUSDT", BigDecimal.valueOf(2), BigDecimal.valueOf(60000));

        assertThat(result.allowed()).isTrue();
    }

    @Test
    void shouldRejectWhenTradeSizeExceedsLimit() {
        PortfolioResponse emptyPortfolio = new PortfolioResponse(
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, List.of()
        );
        when(portfolioService.getPortfolio()).thenReturn(emptyPortfolio);

        RiskCheckResult result = riskService.checkTrade("BTCUSDT", BigDecimal.valueOf(6), BigDecimal.valueOf(60000));

        assertThat(result.allowed()).isFalse();
        assertThat(result.reason()).contains("Trade size exceeds limit");
    }

    @Test
    void shouldRejectWhenPositionLimitExceeded() {
        PositionResponse btcPos = new PositionResponse("BTCUSDT", BigDecimal.valueOf(8), BigDecimal.valueOf(60000), BigDecimal.ZERO);
        PortfolioResponse portfolio = new PortfolioResponse(
                BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.valueOf(480_000), List.of(btcPos)
        );
        when(portfolioService.getPortfolio()).thenReturn(portfolio);

        RiskCheckResult result = riskService.checkTrade("BTCUSDT", BigDecimal.valueOf(3), BigDecimal.valueOf(60000));

        assertThat(result.allowed()).isFalse();
        assertThat(result.reason()).contains("Position limit exceeded");
    }

    @Test
    void shouldRejectWhenPortfolioExposureExceeded() {
        PositionResponse btcPos = new PositionResponse("BTCUSDT", BigDecimal.ONE, BigDecimal.valueOf(60000), BigDecimal.ZERO);
        PortfolioResponse portfolio = new PortfolioResponse(
                BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.valueOf(400_000), List.of(btcPos)
        );
        when(portfolioService.getPortfolio()).thenReturn(portfolio);

        RiskCheckResult result = riskService.checkTrade("BTCUSDT", BigDecimal.valueOf(2), BigDecimal.valueOf(60000));

        assertThat(result.allowed()).isFalse();
        assertThat(result.reason()).contains("Portfolio exposure limit exceeded");
    }
}

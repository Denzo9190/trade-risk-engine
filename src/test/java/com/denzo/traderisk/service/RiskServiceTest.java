package com.denzo.traderisk.service;

import com.denzo.traderisk.config.RiskLimits;
import com.denzo.traderisk.dto.PortfolioResponse;
import com.denzo.traderisk.dto.PositionResponse;
import com.denzo.traderisk.dto.RiskCheckResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RiskServiceTest {

    @Mock
    private PositionService positionService;

    @Mock
    private PortfolioService portfolioService;

    private RiskLimits limits;

    @InjectMocks
    private RiskService riskService;

    @BeforeEach
    void setUp() {
        limits = new RiskLimits();
        limits.setMaxTradeSize(BigDecimal.valueOf(5));
        limits.setMaxPositionSize(BigDecimal.valueOf(10));
        limits.setMaxPortfolioExposure(BigDecimal.valueOf(500_000));
        riskService = new RiskService(positionService, portfolioService, limits);
    }

    @Test
    void shouldAllowTradeWithinLimits() {
        String symbol = "BTCUSDT";
        BigDecimal qty = BigDecimal.valueOf(2);
        BigDecimal price = BigDecimal.valueOf(60000);

        when(positionService.getPosition(symbol))
                .thenReturn(new PositionResponse(symbol, BigDecimal.valueOf(1), BigDecimal.valueOf(60000), BigDecimal.ZERO));
        when(portfolioService.getPortfolio())
                .thenReturn(new PortfolioResponse(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.valueOf(100_000), List.of()));

        RiskCheckResult result = riskService.checkTrade(symbol, qty, price);

        assertThat(result.allowed()).isTrue();
    }

    @Test
    void shouldRejectTradeWhenTradeSizeExceedsLimit() {
        String symbol = "BTCUSDT";
        BigDecimal qty = BigDecimal.valueOf(6); // больше 5
        BigDecimal price = BigDecimal.valueOf(60000);

        RiskCheckResult result = riskService.checkTrade(symbol, qty, price);

        assertThat(result.allowed()).isFalse();
        assertThat(result.reason()).contains("Trade size exceeds limit");
    }

    @Test
    void shouldRejectTradeWhenPositionLimitExceeded() {
        String symbol = "BTCUSDT";
        BigDecimal qty = BigDecimal.valueOf(5);
        BigDecimal price = BigDecimal.valueOf(60000);

        when(positionService.getPosition(symbol))
                .thenReturn(new PositionResponse(symbol, BigDecimal.valueOf(7), BigDecimal.valueOf(60000), BigDecimal.ZERO)); // 7+5=12 > 10

        RiskCheckResult result = riskService.checkTrade(symbol, qty, price);

        assertThat(result.allowed()).isFalse();
        assertThat(result.reason()).contains("Position limit exceeded");
    }

    @Test
    void shouldRejectTradeWhenPortfolioExposureExceeded() {
        String symbol = "BTCUSDT";
        BigDecimal qty = BigDecimal.valueOf(3);
        BigDecimal price = BigDecimal.valueOf(60000); // exposure = 180_000

        when(positionService.getPosition(symbol))
                .thenReturn(new PositionResponse(symbol, BigDecimal.ONE, BigDecimal.valueOf(60000), BigDecimal.ZERO));
        when(portfolioService.getPortfolio())
                .thenReturn(new PortfolioResponse(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.valueOf(400_000), List.of())); // 400k + 180k = 580k > 500k

        RiskCheckResult result = riskService.checkTrade(symbol, qty, price);

        assertThat(result.allowed()).isFalse();
        assertThat(result.reason()).contains("Portfolio exposure limit exceeded");
    }
}

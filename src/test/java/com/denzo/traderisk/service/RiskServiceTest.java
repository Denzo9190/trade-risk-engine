package com.denzo.traderisk.service;

import com.denzo.traderisk.config.RiskLimits;
import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.dto.PortfolioResponse;
import com.denzo.traderisk.dto.PositionResponse;
import com.denzo.traderisk.dto.RiskCheckResult;
import com.denzo.traderisk.dto.TradeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RiskServiceTest {

    @Mock
    private PositionService positionService;

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
        riskService = new RiskService(positionService, portfolioService, limits);
    }

    @Test
    void shouldAllowTradeWithinLimits() {
        TradeRequest request = new TradeRequest("BTCUSDT", BigDecimal.valueOf(2), BigDecimal.valueOf(60000), Side.BUY);

        when(positionService.getPosition("BTCUSDT"))
                .thenReturn(new PositionResponse("BTCUSDT", BigDecimal.valueOf(1), BigDecimal.valueOf(60000), BigDecimal.ZERO));
        when(portfolioService.getPortfolio())
                .thenReturn(new PortfolioResponse(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.valueOf(100_000), List.of()));

        RiskCheckResult result = riskService.checkTrade(request);

        assertThat(result.allowed()).isTrue();
    }

    @Test
    void shouldRejectWhenTradeSizeExceedsLimit() {
        TradeRequest request = new TradeRequest("BTCUSDT", BigDecimal.valueOf(6), BigDecimal.valueOf(60000), Side.BUY);
        RiskCheckResult result = riskService.checkTrade(request);
        assertThat(result.allowed()).isFalse();
        assertThat(result.reason()).contains("Trade size exceeds limit");
    }

    @Test
    void shouldRejectWhenPositionLimitExceeded() {
        TradeRequest request = new TradeRequest("BTCUSDT", BigDecimal.valueOf(5), BigDecimal.valueOf(60000), Side.BUY);

        when(positionService.getPosition("BTCUSDT"))
                .thenReturn(new PositionResponse("BTCUSDT", BigDecimal.valueOf(7), BigDecimal.valueOf(60000), BigDecimal.ZERO));

        RiskCheckResult result = riskService.checkTrade(request);
        assertThat(result.allowed()).isFalse();
        assertThat(result.reason()).contains("Position limit exceeded");
    }

    @Test
    void shouldRejectWhenPortfolioExposureExceeded() {
        TradeRequest request = new TradeRequest("BTCUSDT", BigDecimal.valueOf(3), BigDecimal.valueOf(60000), Side.BUY);

        when(positionService.getPosition("BTCUSDT"))
                .thenReturn(new PositionResponse("BTCUSDT", BigDecimal.ONE, BigDecimal.valueOf(60000), BigDecimal.ZERO));
        when(portfolioService.getPortfolio())
                .thenReturn(new PortfolioResponse(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.valueOf(400_000), List.of()));

        RiskCheckResult result = riskService.checkTrade(request);
        assertThat(result.allowed()).isFalse();
        assertThat(result.reason()).contains("Portfolio exposure limit exceeded");
    }
}

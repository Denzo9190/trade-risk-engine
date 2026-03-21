package com.denzo.traderisk.service;

import com.denzo.traderisk.config.RiskLimits;
import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.dto.*;
import com.denzo.traderisk.market.MarketDataNotFoundException;
import com.denzo.traderisk.market.MarketDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RiskServiceTest {

    @Mock
    private PositionService positionService;
    @Mock
    private PortfolioService portfolioService;
    @Mock
    private MarketDataService marketDataService;

    private RiskLimits limits;
    private RiskService riskService;

    @BeforeEach
    void setUp() {
        limits = new RiskLimits();
        limits.setMaxTradeSize(BigDecimal.valueOf(5));
        limits.setMaxPositionSize(BigDecimal.valueOf(10));
        limits.setMaxPortfolioExposure(BigDecimal.valueOf(500_000));
        limits.setMaxPriceDeviation(BigDecimal.valueOf(0.01));

        riskService = new RiskService(positionService, portfolioService, marketDataService, limits);
    }

    @Test
    void shouldAcceptWhenPriceDeviationWithinLimit() {
        TradeRequest request = new TradeRequest("BTCUSDT", BigDecimal.valueOf(2), new BigDecimal("60500"), Side.BUY);
        when(marketDataService.getPrice("BTCUSDT")).thenReturn(new BigDecimal("60000"));
        when(positionService.getPosition("BTCUSDT")).thenReturn(new PositionResponse("BTCUSDT", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        when(portfolioService.getPortfolio()).thenReturn(new PortfolioResponse(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, List.of()));

        RiskCheckResult result = riskService.checkTrade(request);
        assertThat(result.allowed()).isTrue();
    }

    @Test
    void shouldRejectWhenPriceDeviationExceedsLimit() {
        TradeRequest request = new TradeRequest("BTCUSDT", BigDecimal.valueOf(2), new BigDecimal("61000"), Side.BUY);
        when(marketDataService.getPrice("BTCUSDT")).thenReturn(new BigDecimal("60000"));
        // моки не нужны, т.к. проверка цены идёт первой

        RiskCheckResult result = riskService.checkTrade(request);
        assertThat(result.allowed()).isFalse();
        assertThat(result.reason()).contains("Price deviation too high");
        assertThat(result.reason()).contains("signal=61000.00");
        assertThat(result.reason()).contains("market=60000.00");
        assertThat(result.reason()).contains("deviation=1.6667%");
    }

    @Test
    void shouldRejectWhenMarketPriceUnavailable() {
        TradeRequest request = new TradeRequest("BTCUSDT", BigDecimal.valueOf(2), new BigDecimal("60000"), Side.BUY);
        when(marketDataService.getPrice("BTCUSDT")).thenThrow(new MarketDataNotFoundException("BTCUSDT"));

        RiskCheckResult result = riskService.checkTrade(request);
        assertThat(result.allowed()).isFalse();
        assertThat(result.reason()).contains("Market price unavailable for BTCUSDT");
    }

    @Test
    void shouldRejectWhenTradeSizeExceedsLimit() {
        TradeRequest request = new TradeRequest("BTCUSDT", BigDecimal.valueOf(6), BigDecimal.valueOf(60000), Side.BUY);
        when(marketDataService.getPrice("BTCUSDT")).thenReturn(BigDecimal.valueOf(60000));

        RiskCheckResult result = riskService.checkTrade(request);
        assertThat(result.allowed()).isFalse();
        assertThat(result.reason()).contains("Trade size exceeds limit");
    }

    @Test
    void shouldAllowTradeWithinSizeLimit() {
        TradeRequest request = new TradeRequest("BTCUSDT", BigDecimal.valueOf(4), BigDecimal.valueOf(60000), Side.BUY);
        when(marketDataService.getPrice("BTCUSDT")).thenReturn(BigDecimal.valueOf(60000));
        when(positionService.getPosition("BTCUSDT")).thenReturn(new PositionResponse("BTCUSDT", BigDecimal.ONE, BigDecimal.valueOf(60000), BigDecimal.ZERO));
        when(portfolioService.getPortfolio()).thenReturn(new PortfolioResponse(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.valueOf(100_000), List.of()));

        RiskCheckResult result = riskService.checkTrade(request);
        assertThat(result.allowed()).isTrue();
    }

    @Test
    void shouldRejectWhenPositionLimitExceeded() {
        TradeRequest request = new TradeRequest("BTCUSDT", BigDecimal.valueOf(5), BigDecimal.valueOf(60000), Side.BUY);
        when(marketDataService.getPrice("BTCUSDT")).thenReturn(BigDecimal.valueOf(60000));
        when(positionService.getPosition("BTCUSDT")).thenReturn(new PositionResponse("BTCUSDT", BigDecimal.valueOf(7), BigDecimal.valueOf(60000), BigDecimal.ZERO));

        RiskCheckResult result = riskService.checkTrade(request);
        assertThat(result.allowed()).isFalse();
        assertThat(result.reason()).contains("Position limit exceeded");
    }

    @Test
    void shouldRejectWhenPortfolioExposureExceeded() {
        TradeRequest request = new TradeRequest("BTCUSDT", BigDecimal.valueOf(3), BigDecimal.valueOf(60000), Side.BUY);
        when(marketDataService.getPrice("BTCUSDT")).thenReturn(BigDecimal.valueOf(60000));
        when(positionService.getPosition("BTCUSDT")).thenReturn(new PositionResponse("BTCUSDT", BigDecimal.ONE, BigDecimal.valueOf(60000), BigDecimal.ZERO));
        when(portfolioService.getPortfolio()).thenReturn(new PortfolioResponse(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.valueOf(400_000), List.of()));

        RiskCheckResult result = riskService.checkTrade(request);
        assertThat(result.allowed()).isFalse();
        assertThat(result.reason()).contains("Portfolio exposure limit exceeded");
    }

    @Test
    void shouldAllowTradeWhenAllChecksPass() {
        TradeRequest request = new TradeRequest("BTCUSDT", BigDecimal.valueOf(2), BigDecimal.valueOf(60000), Side.BUY);
        when(marketDataService.getPrice("BTCUSDT")).thenReturn(BigDecimal.valueOf(60000));
        when(positionService.getPosition("BTCUSDT")).thenReturn(new PositionResponse("BTCUSDT", BigDecimal.ONE, BigDecimal.valueOf(60000), BigDecimal.ZERO));
        when(portfolioService.getPortfolio()).thenReturn(new PortfolioResponse(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.valueOf(100_000), List.of()));

        RiskCheckResult result = riskService.checkTrade(request);
        assertThat(result.allowed()).isTrue();
    }
}

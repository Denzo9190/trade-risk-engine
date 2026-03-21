package com.denzo.traderisk.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
@ConfigurationProperties(prefix = "risk")
public class RiskLimits {

    private BigDecimal maxTradeSize = BigDecimal.valueOf(5);
    private BigDecimal maxPositionSize = BigDecimal.valueOf(10);
    private BigDecimal maxPortfolioExposure = BigDecimal.valueOf(500_000);
    private BigDecimal maxPriceDeviation = BigDecimal.valueOf(0.01); // 1%

    // геттеры и сеттеры
    public BigDecimal getMaxTradeSize() { return maxTradeSize; }
    public void setMaxTradeSize(BigDecimal maxTradeSize) { this.maxTradeSize = maxTradeSize; }

    public BigDecimal getMaxPositionSize() { return maxPositionSize; }
    public void setMaxPositionSize(BigDecimal maxPositionSize) { this.maxPositionSize = maxPositionSize; }

    public BigDecimal getMaxPortfolioExposure() { return maxPortfolioExposure; }
    public void setMaxPortfolioExposure(BigDecimal maxPortfolioExposure) { this.maxPortfolioExposure = maxPortfolioExposure; }

    public BigDecimal getMaxPriceDeviation() { return maxPriceDeviation; }
    public void setMaxPriceDeviation(BigDecimal maxPriceDeviation) { this.maxPriceDeviation = maxPriceDeviation; }
}

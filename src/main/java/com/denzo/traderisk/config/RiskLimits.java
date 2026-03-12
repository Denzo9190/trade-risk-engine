package com.denzo.traderisk.config;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Setter
@Configuration
@ConfigurationProperties(prefix = "risk")
public class RiskLimits {
    private BigDecimal maxTradeSize = BigDecimal.valueOf(5);
    private BigDecimal maxPositionSize = BigDecimal.valueOf(10);
    private BigDecimal maxPortfolioExposure = BigDecimal.valueOf(500_000);

    // геттеры и сеттеры
    public BigDecimal maxTradeSize() { return maxTradeSize; }

    public BigDecimal maxPositionSize() { return maxPositionSize; }

    public BigDecimal maxPortfolioExposure() { return maxPortfolioExposure; }
}

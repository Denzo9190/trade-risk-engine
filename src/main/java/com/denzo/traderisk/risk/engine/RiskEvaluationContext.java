package com.denzo.traderisk.risk.engine;

import com.denzo.traderisk.portfolio.PortfolioSnapshot;
import com.denzo.traderisk.strategy.TradingSignal;
import java.math.BigDecimal;

public record RiskEvaluationContext(
        TradingSignal signal,
        PortfolioSnapshot portfolio,
        BigDecimal currentPrice
) {}

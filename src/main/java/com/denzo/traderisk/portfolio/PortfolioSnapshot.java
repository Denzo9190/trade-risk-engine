package com.denzo.traderisk.portfolio;

import java.math.BigDecimal;
import java.util.Map;

public record PortfolioSnapshot(
        Map<String, PositionView> positions,
        BigDecimal totalUnrealisedPnl,
        BigDecimal totalRealisedPnl,
        BigDecimal totalEquity,
        BigDecimal totalExposure
) {}

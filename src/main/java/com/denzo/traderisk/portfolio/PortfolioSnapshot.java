package com.denzo.traderisk.portfolio;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

public record PortfolioSnapshot(
        Instant timestamp,
        Map<String, PositionView> positions,
        BigDecimal totalUnrealisedPnl,
        BigDecimal totalRealisedPnl,
        BigDecimal totalEquity,
        BigDecimal totalExposure
) {
    public PortfolioSnapshot(Map<String, PositionView> positions,
                             BigDecimal totalUnrealisedPnl,
                             BigDecimal totalRealisedPnl,
                             BigDecimal totalEquity,
                             BigDecimal totalExposure) {
        this(Instant.now(), positions, totalUnrealisedPnl, totalRealisedPnl, totalEquity, totalExposure);
    }
}

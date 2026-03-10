package com.denzo.traderisk.dto;

import java.math.BigDecimal;
import java.util.List;

public record PortfolioResponse(
        BigDecimal totalRealisedPnl,
        BigDecimal totalUnrealisedPnl,
        BigDecimal totalExposure,
        List<PositionResponse> positions
) {}

package com.denzo.traderisk.dto;

import java.math.BigDecimal;
import java.util.List;

public record PortfolioSnapshot(
        BigDecimal totalExposure,
        List<PositionSnapshot> positions
) {}

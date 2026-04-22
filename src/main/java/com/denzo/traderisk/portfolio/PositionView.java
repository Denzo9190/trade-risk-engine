package com.denzo.traderisk.portfolio;

import java.math.BigDecimal;

public record PositionView(
        String symbol,
        BigDecimal quantity,
        BigDecimal averagePrice,
        BigDecimal unrealisedPnl
) {}

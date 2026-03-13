package com.denzo.traderisk.dto;

import java.math.BigDecimal;

public record PositionSnapshot(
        String symbol,
        BigDecimal quantity,
        BigDecimal avgPrice
) {}

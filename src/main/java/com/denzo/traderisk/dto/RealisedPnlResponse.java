package com.denzo.traderisk.dto;

import java.math.BigDecimal;

public record RealisedPnlResponse(
        String symbol,
        BigDecimal realisedPnl
) {}

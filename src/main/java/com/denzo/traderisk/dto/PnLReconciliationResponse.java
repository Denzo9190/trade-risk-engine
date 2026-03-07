package com.denzo.traderisk.dto;

import java.math.BigDecimal;

public record PnLReconciliationResponse(
        String symbol,
        BigDecimal totalPnl,
        BigDecimal realisedPnl,
        BigDecimal unrealisedPnl,
        BigDecimal sum,
        BigDecimal difference,
        boolean passed
) {}

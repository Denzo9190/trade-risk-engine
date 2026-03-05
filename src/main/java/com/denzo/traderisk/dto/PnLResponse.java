package com.denzo.traderisk.dto;

import java.math.BigDecimal;

public record PnLResponse(
        String symbol,
        BigDecimal totalUnrealisedPnl,
        int tradeCount
) {}
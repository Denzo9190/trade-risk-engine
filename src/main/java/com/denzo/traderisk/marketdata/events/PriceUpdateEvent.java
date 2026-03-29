package com.denzo.traderisk.marketdata.events;

import java.math.BigDecimal;
import java.time.Instant;

public record PriceUpdateEvent(
        String symbol,
        BigDecimal price,
        Instant timestamp
) {}

package com.denzo.traderisk.dto;

import com.denzo.traderisk.domain.Side;
import java.math.BigDecimal;

/**
 * DTO для передачи данных сделки в риск-движок.
 */
public record TradeRequest(
        String symbol,
        BigDecimal quantity,
        BigDecimal price,
        Side side
) {}

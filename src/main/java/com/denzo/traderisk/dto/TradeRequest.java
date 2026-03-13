package com.denzo.traderisk.dto;

import com.denzo.traderisk.domain.Side;

import java.math.BigDecimal;

/**
 * Обёртка для данных входящей сделки, используемая правилами риск-движка.
 */
public record TradeRequest(
        String symbol,
        BigDecimal quantity,
        BigDecimal price,
        Side side
) {}

package com.denzo.traderisk.dto;

import com.denzo.traderisk.domain.Side;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateTradeRequest(
        @NotNull String symbol,
        @NotNull @Positive BigDecimal quantity,
        @NotNull @Positive BigDecimal price,
        @NotNull Side side
) {}

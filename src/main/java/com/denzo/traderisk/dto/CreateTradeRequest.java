package com.denzo.traderisk.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateTradeRequest(

        @NotBlank String symbol,

        @NotNull @Positive BigDecimal quantity,

        @NotNull @Positive BigDecimal price,

        @NotNull String side
) {}

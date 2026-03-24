package com.denzo.traderisk.execution;

import java.math.BigDecimal;

public record OrderResult(
        String symbol,
        BigDecimal executedQuantity,
        BigDecimal executedPrice,
        String exchangeOrderId
) {}

package com.denzo.traderisk.execution;

import com.denzo.traderisk.domain.Side;
import java.math.BigDecimal;

public record OrderRequest(
        String symbol,
        BigDecimal quantity,
        BigDecimal price,
        Side side
) {}

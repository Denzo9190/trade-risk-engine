package com.denzo.traderisk.dto;

import java.math.BigDecimal;

public record PositionResponse(
        String symbol,
        BigDecimal totalQuantity,      // нетто‑позиция (положительная = long, отрицательная = short)
        BigDecimal averagePrice,       // средневзвешенная цена входа
        BigDecimal unrealisedPnl       // нереализованная прибыль/убыток при текущей цене
) {}

package com.denzo.traderisk.marketdata;

import java.math.BigDecimal;

public interface PriceCache {
    BigDecimal get(String symbol);
    void put(String symbol, BigDecimal price);
}

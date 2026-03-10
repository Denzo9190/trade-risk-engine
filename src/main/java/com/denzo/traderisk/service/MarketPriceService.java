package com.denzo.traderisk.service;

import java.math.BigDecimal;

public interface MarketPriceService {
    BigDecimal getCurrentPrice(String symbol);
}

package com.denzo.traderisk.marketdata.events;

import java.math.BigDecimal;

public interface MarketDataEventPublisher {
    void publishPriceUpdate(String symbol, BigDecimal price);
}

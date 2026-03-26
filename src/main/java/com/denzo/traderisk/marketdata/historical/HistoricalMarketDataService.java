package com.denzo.traderisk.marketdata.historical;

import java.math.BigDecimal;
import java.time.Instant;

public interface HistoricalMarketDataService {
    BigDecimal getPrice(String symbol, Instant timestamp);
}

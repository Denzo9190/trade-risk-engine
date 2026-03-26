package com.denzo.traderisk.marketdata.historical;

import com.denzo.traderisk.marketdata.MarketDataAdapter;
import com.denzo.traderisk.time.TimeProvider;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class HistoricalMarketDataAdapter implements MarketDataAdapter {

    private final HistoricalMarketDataService historical;
    private final TimeProvider timeProvider;

    @Override
    public BigDecimal getPrice(String symbol) {
        return historical.getPrice(symbol, timeProvider.now());
    }
}

package com.denzo.traderisk.marketdata.historical;

import com.denzo.traderisk.marketdata.adapter.MarketDataAdapter;
import com.denzo.traderisk.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;

@Component
@Profile("backtest")
@RequiredArgsConstructor
public class HistoricalMarketDataAdapter implements MarketDataAdapter {

    private final HistoricalMarketDataService historical;
    private final TimeProvider timeProvider;
    private static final Logger log = LoggerFactory.getLogger(HistoricalMarketDataAdapter.class);

    @Override
    public BigDecimal getPrice(String symbol) {
        Instant now = timeProvider.now();
        log.debug("HistoricalMarketDataAdapter: fetching price for {} at {}", symbol, now);
        BigDecimal price = historical.getPrice(symbol, now);
        log.debug("HistoricalMarketDataAdapter: price = {}", price);
        return price;
    }
}

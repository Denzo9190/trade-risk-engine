package com.denzo.traderisk.market;

import com.denzo.traderisk.market.historical.HistoricalMarketDataService;
import com.denzo.traderisk.time.TimeProvider;

import java.math.BigDecimal;

/**
 * Адаптер, который подменяет live-цены историческими,
 * используя текущее время из TimeProvider.
 * Стратегия не знает, что работает в backtest.
 */
public class BacktestMarketDataService implements MarketDataService {

    private final HistoricalMarketDataService historical;
    private final TimeProvider timeProvider;

    public BacktestMarketDataService(HistoricalMarketDataService historical, TimeProvider timeProvider) {
        this.historical = historical;
        this.timeProvider = timeProvider;
    }

    @Override
    public BigDecimal getPrice(String symbol) {
        BigDecimal price = historical.getPrice(symbol, timeProvider.now());
        System.out.println(">>> BacktestMarketDataService: price for " + symbol + " at " + timeProvider.now() + " = " + price);
        return price;
    }
}

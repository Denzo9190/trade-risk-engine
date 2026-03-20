package com.denzo.traderisk.config;

import com.denzo.traderisk.market.BacktestMarketDataService;
import com.denzo.traderisk.market.MarketDataService;
import com.denzo.traderisk.market.historical.HistoricalMarketDataService;
import com.denzo.traderisk.time.BacktestTimeProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("backtest")
public class BacktestConfig {

    @Bean
    @Primary
    public BacktestTimeProvider backtestTimeProvider() {
        return new BacktestTimeProvider();
    }

    @Bean
    @Primary
    public MarketDataService backtestMarketDataService(HistoricalMarketDataService historical,
                                                       BacktestTimeProvider timeProvider) {
        return new BacktestMarketDataService(historical, timeProvider);
    }
}

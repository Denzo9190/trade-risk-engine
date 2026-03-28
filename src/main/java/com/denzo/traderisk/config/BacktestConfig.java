package com.denzo.traderisk.config;

import com.denzo.traderisk.marketdata.adapter.MarketDataAdapter;
import com.denzo.traderisk.marketdata.historical.HistoricalMarketDataAdapter;
import com.denzo.traderisk.marketdata.historical.HistoricalMarketDataService;
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
}

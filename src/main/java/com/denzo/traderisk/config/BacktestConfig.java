package com.denzo.traderisk.config;

import com.denzo.traderisk.execution.BacktestExecutionAdapter;
import com.denzo.traderisk.execution.ExecutionAdapter;
import com.denzo.traderisk.marketdata.adapter.MarketDataAdapter;
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
    public ExecutionAdapter backtestExecutionAdapter(BacktestTimeProvider timeProvider,
                                                     MarketDataAdapter historicalMarketDataAdapter) {
        return new BacktestExecutionAdapter(timeProvider, historicalMarketDataAdapter);
    }
}

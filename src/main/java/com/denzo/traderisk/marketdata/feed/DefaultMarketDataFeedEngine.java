package com.denzo.traderisk.marketdata.feed;

import com.denzo.traderisk.marketdata.MarketDataEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!backtest")
public class DefaultMarketDataFeedEngine {

    private final MarketDataEngine marketDataEngine;

    @Scheduled(fixedDelayString = "${market.refresh-interval-ms:1000}")
    public void tick() {
        marketDataEngine.refreshAll();
    }
}

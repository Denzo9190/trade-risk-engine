package com.denzo.traderisk.marketdata.feed;

import com.denzo.traderisk.marketdata.PriceCache;
import com.denzo.traderisk.marketdata.adapter.MarketDataAdapter;
import com.denzo.traderisk.marketdata.events.MarketDataEventPublisher;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!backtest")
public class DefaultMarketDataFeedEngine implements MarketDataFeedEngine {

    private final MarketDataAdapter adapter;
    private final PriceCache priceCache;
    private final MarketDataEventPublisher eventPublisher;   // final

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    @PostConstruct
    @Override
    public void start() {
        log.info("Starting market data feed engine for BTCUSDT");
        executor.scheduleAtFixedRate(() -> {
            try {
                BigDecimal price = adapter.getPrice("BTCUSDT");
                priceCache.put("BTCUSDT", price);
                eventPublisher.publishPriceUpdate("BTCUSDT", price);
                log.debug("Updated price cache and published event: BTCUSDT = {}", price);
            } catch (Exception e) {
                log.warn("Failed to fetch price from adapter", e);
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    @PreDestroy
    @Override
    public void stop() {
        log.info("Stopping market data feed engine");
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}

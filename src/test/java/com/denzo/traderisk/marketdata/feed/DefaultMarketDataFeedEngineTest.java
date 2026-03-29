package com.denzo.traderisk.marketdata.feed;

import com.denzo.traderisk.marketdata.PriceCache;
import com.denzo.traderisk.marketdata.adapter.MarketDataAdapter;
import com.denzo.traderisk.marketdata.events.MarketDataEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultMarketDataFeedEngineTest {

    @Mock
    private MarketDataAdapter adapter;

    @Mock
    private PriceCache priceCache;

    @Mock
    private MarketDataEventPublisher eventPublisher;   // добавляем мок

    @InjectMocks
    private DefaultMarketDataFeedEngine engine;

    @Test
    void shouldFetchPriceAndUpdateCacheOnStart() {
        when(adapter.getPrice(anyString())).thenReturn(new BigDecimal("63500"));

        engine.start();

        await().atMost(2, SECONDS)
                .untilAsserted(() -> verify(adapter, atLeastOnce()).getPrice("BTCUSDT"));

        await().atMost(2, SECONDS)
                .untilAsserted(() -> verify(priceCache, atLeastOnce()).put("BTCUSDT", new BigDecimal("63500")));

        engine.stop();
    }

    @Test
    void shouldHandleAdapterFailureGracefully() {
        when(adapter.getPrice(anyString()))
                .thenThrow(new RuntimeException("API error"))
                .thenReturn(new BigDecimal("63500"));

        engine.start();

        await().atMost(3, SECONDS)
                .untilAsserted(() -> verify(adapter, atLeast(2)).getPrice("BTCUSDT"));

        await().atMost(3, SECONDS)
                .untilAsserted(() -> verify(priceCache, atLeastOnce()).put("BTCUSDT", new BigDecimal("63500")));

        engine.stop();
    }

    @Test
    void shouldPublishEventAfterPriceUpdate() {
        when(adapter.getPrice(anyString())).thenReturn(new BigDecimal("63500"));

        engine.start();

        await().atMost(2, SECONDS)
                .untilAsserted(() -> verify(eventPublisher, atLeastOnce())
                        .publishPriceUpdate("BTCUSDT", new BigDecimal("63500")));

        engine.stop();
    }
}

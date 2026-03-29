package com.denzo.traderisk.marketdata;

import com.denzo.traderisk.marketdata.events.MarketDataEventPublisher;
import com.denzo.traderisk.marketdata.events.PriceUpdateEvent;
import com.denzo.traderisk.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class SpringMarketDataEventPublisher implements MarketDataEventPublisher {

    private final ApplicationEventPublisher publisher;
    private final TimeProvider timeProvider;  // добавлено

    @Override
    public void publishPriceUpdate(String symbol, BigDecimal price) {
        publisher.publishEvent(new PriceUpdateEvent(symbol, price, timeProvider.now()));
    }
}

package com.denzo.traderisk.strategy;

import com.denzo.traderisk.marketdata.events.PriceUpdateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PriceUpdateEventListener {

    @EventListener
    public void onPriceUpdate(PriceUpdateEvent event) {
        log.info("Price update event received: {} = {}", event.symbol(), event.price());
        // Здесь можно запускать стратегии или обновлять риск-модели
    }
}

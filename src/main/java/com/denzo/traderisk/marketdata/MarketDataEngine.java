package com.denzo.traderisk.marketdata;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Единая точка доступа к рыночным ценам.
 * Всегда читает цену из кэша. Если цена отсутствует, бросает исключение.
 * Кэш должен обновляться через MarketDataFeedEngine.
 */
@Service
@RequiredArgsConstructor
public class MarketDataEngine {

    private final PriceCache cache;

    public BigDecimal getPrice(String symbol) {
        BigDecimal price = cache.get(symbol);
        if (price == null) {
            throw new IllegalStateException("Price not available for symbol: " + symbol);
        }
        return price;
    }
}

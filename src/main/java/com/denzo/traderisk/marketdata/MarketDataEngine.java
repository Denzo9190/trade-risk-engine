package com.denzo.traderisk.marketdata;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Центральный сервис для доступа к рыночным данным.
 * Все компоненты системы (стратегии, риск, портфель) используют его для получения цен.
 */
@Service
@RequiredArgsConstructor
public class MarketDataEngine {

    private final MarketDataAdapter adapter;
    private final PriceCache cache;

    public BigDecimal getPrice(String symbol) {
        BigDecimal cached = cache.get(symbol);
        if (cached != null) {
            return cached;
        }
        BigDecimal price = adapter.getPrice(symbol);
        cache.put(symbol, price);
        return price;
    }

    // Опционально: метод для принудительного обновления кэша
    public void refreshPrice(String symbol) {
        BigDecimal price = adapter.getPrice(symbol);
        cache.put(symbol, price);
    }
}

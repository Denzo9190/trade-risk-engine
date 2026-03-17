package com.denzo.traderisk.market;

import com.google.common.annotations.VisibleForTesting;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Простейшая in-memory реализация MarketDataService.
 * Инициализируется тестовыми ценами для основных символов.
 * Потокобезопасна (ConcurrentHashMap).
 */
@Service
public class InMemoryMarketDataService implements MarketDataService {

    private final Map<String, BigDecimal> prices = new ConcurrentHashMap<>();

    public InMemoryMarketDataService() {
        // Задаём тестовые цены для популярных инструментов
        prices.put("BTCUSDT", new BigDecimal("63500"));
        prices.put("ETHUSDT", new BigDecimal("3200"));
        prices.put("AAPL", new BigDecimal("175"));
    }

    /**
     * Обновляет цену для символа. Только для тестовых целей.
     */
    @VisibleForTesting
    public void updatePrice(String symbol, BigDecimal price) {
        prices.put(symbol, price);
    }

    @Override
    public BigDecimal getPrice(String symbol) {
        BigDecimal price = prices.get(symbol);
        if (price == null) {
            throw new MarketDataNotFoundException(symbol);
        }
        return price;
    }
}

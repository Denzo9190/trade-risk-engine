package com.denzo.traderisk.marketdata;

import com.denzo.traderisk.marketdata.model.Symbol;
import com.denzo.traderisk.marketdata.adapter.MarketDataAdapter;
import com.denzo.traderisk.marketdata.registry.SymbolRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketDataEngine {

    private final MarketDataAdapter adapter;
    private final PriceCache priceCache;
    private final SymbolRegistry registry;

    public BigDecimal getPrice(String symbol) {
        BigDecimal price = priceCache.get(symbol);
        if (price == null) {
            throw new PriceNotAvailableException(symbol);
        }
        return price;
    }

    public BigDecimal getPrice(Symbol symbol) {
        return getPrice(symbol.value());
    }

    public void refreshAll() {
        Map<String, BigDecimal> freshPrices = adapter.getPrices(registry.getAll());
        int valid = 0;
        for (Map.Entry<String, BigDecimal> entry : freshPrices.entrySet()) {
            BigDecimal price = entry.getValue();
            if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
                log.warn("Skipping invalid price {} for symbol {}", price, entry.getKey());
                continue;
            }
            priceCache.put(entry.getKey(), price);
            valid++;
        }
        log.debug("Refreshed {} valid prices out of {} symbols", valid, freshPrices.size());
    }
}

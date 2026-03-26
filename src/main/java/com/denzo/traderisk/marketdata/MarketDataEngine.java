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

    public BigDecimal getPrice(String symbol) {
        return adapter.getPrice(symbol);
    }
}

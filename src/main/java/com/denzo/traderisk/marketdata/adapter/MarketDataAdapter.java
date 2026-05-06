package com.denzo.traderisk.marketdata.adapter;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

/**
 * Адаптер для получения рыночных данных (цен).
 * Позволяет подменять источник цен: биржа, мок, исторические данные.
 */
public interface MarketDataAdapter {

    /**
     * Возвращает текущую цену для указанного символа.
     *
     * @param symbol инструмент (например, "BTC")
     * @return актуальная цена
     * @throws IllegalArgumentException если символ неизвестен или данные недоступны
     */
    BigDecimal getPrice(String symbol);
    Map<String, BigDecimal> getPrices(Set<String> symbols);
}

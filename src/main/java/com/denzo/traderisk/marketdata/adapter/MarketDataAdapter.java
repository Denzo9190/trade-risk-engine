package com.denzo.traderisk.marketdata.adapter;

import java.math.BigDecimal;

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
}

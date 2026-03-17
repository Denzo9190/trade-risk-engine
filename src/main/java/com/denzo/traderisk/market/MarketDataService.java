package com.denzo.traderisk.market;

import java.math.BigDecimal;

/**
 * Интерфейс сервиса рыночных данных.
 * Предоставляет актуальные цены для инструментов.
 * Реализация должна быть потокобезопасной и быстрой.
 */
public interface MarketDataService {

    /**
     * Возвращает текущую цену для указанного символа.
     *
     * @param symbol символ инструмента (например, "BTCUSDT")
     * @return актуальная цена
     * @throws MarketDataNotFoundException если данные для символа отсутствуют
     */
    BigDecimal getPrice(String symbol);
}

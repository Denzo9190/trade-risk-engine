package com.denzo.traderisk.time;

import java.time.Instant;

/**
 * Интерфейс поставщика текущего времени.
 * Позволяет подменять время в тестах и backtest.
 */
public interface TimeProvider {
    Instant now();
}

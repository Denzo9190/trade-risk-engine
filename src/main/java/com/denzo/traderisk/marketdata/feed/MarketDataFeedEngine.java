package com.denzo.traderisk.marketdata.feed;

public interface MarketDataFeedEngine {

    /**
     * Запускает получение рыночных данных.
     */
    void start();

    /**
     * Останавливает получение рыночных данных.
     */
    void stop();
}

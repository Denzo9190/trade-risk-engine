package com.denzo.traderisk.market;

/**
 * Исключение, выбрасываемое при отсутствии рыночных данных для запрошенного символа.
 */
public class MarketDataNotFoundException extends RuntimeException {

    public MarketDataNotFoundException(String symbol) {
        super("No market data for symbol: " + symbol);
    }
}

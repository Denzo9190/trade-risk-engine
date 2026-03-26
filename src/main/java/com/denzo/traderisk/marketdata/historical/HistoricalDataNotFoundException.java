package com.denzo.traderisk.marketdata.historical;

/**
 * Исключение, выбрасываемое при отсутствии исторических данных для указанного символа и времени.
 */
public class HistoricalDataNotFoundException extends RuntimeException {

    public HistoricalDataNotFoundException(String symbol, Object timestamp) {
        super("No historical data for " + symbol + " at " + timestamp);
    }
}

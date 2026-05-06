package com.denzo.traderisk.marketdata;

public class PriceNotAvailableException extends RuntimeException {
    public PriceNotAvailableException(String symbol) {
        super("Price not available in cache for " + symbol);
    }
}

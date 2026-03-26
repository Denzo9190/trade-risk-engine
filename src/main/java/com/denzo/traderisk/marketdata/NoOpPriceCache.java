package com.denzo.traderisk.marketdata;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Profile("backtest")
public class NoOpPriceCache implements PriceCache {

    @Override
    public BigDecimal get(String symbol) {
        return null;
    }

    @Override
    public void put(String symbol, BigDecimal price) {
        // no-op
    }
}

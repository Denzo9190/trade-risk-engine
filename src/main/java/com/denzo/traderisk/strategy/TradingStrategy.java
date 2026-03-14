package com.denzo.traderisk.strategy;

import java.util.Optional;

public interface TradingStrategy {
    Optional<Signal> generateSignal(String symbol);
}

package com.denzo.traderisk.strategy;

import java.util.Optional;

public interface TradingStrategy {
    Optional<TradingSignal> generateSignal(String symbol);
}

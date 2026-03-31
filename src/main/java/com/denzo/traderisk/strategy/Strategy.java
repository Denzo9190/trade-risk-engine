package com.denzo.traderisk.strategy;

import java.math.BigDecimal;
import java.util.Optional;

public interface Strategy {
    Optional<TradingSignal> generateSignal(String symbol, BigDecimal currentPrice);
}

package com.denzo.traderisk.strategy;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.marketdata.MarketDataEngine;
import com.denzo.traderisk.time.TimeProvider;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

@Component
public class RandomStrategy implements TradingStrategy {

    private final MarketDataEngine marketDataEngine;
    private final TimeProvider timeProvider;

    public RandomStrategy(MarketDataEngine marketDataEngine, TimeProvider timeProvider) {
        this.marketDataEngine = marketDataEngine;
        this.timeProvider = timeProvider;
    }

    @Override
    public Optional<Signal> generateSignal(String symbol) {
        // Временный код: всегда генерируем сигнал для демонстрации кэша
        BigDecimal price = marketDataEngine.getPrice(symbol);
        return Optional.of(new Signal(
                symbol,
                Side.BUY,
                BigDecimal.ONE,
                price,
                "RandomStrategy",
                timeProvider.now()
        ));
    }
}

package com.denzo.traderisk.strategy;

import com.denzo.traderisk.marketdata.MarketDataEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RandomStrategy implements TradingStrategy {

    private final MarketDataEngine marketDataEngine;

    @Override
    public Optional<TradingSignal> generateSignal(String symbol) {
        BigDecimal currentPrice = marketDataEngine.getPrice(symbol);
        log.debug("RandomStrategy generated BUY signal for {} at price {}", symbol, currentPrice);
        return Optional.of(new TradingSignal(
                UUID.randomUUID(),
                symbol,
                SignalType.BUY,
                currentPrice,
                BigDecimal.ONE
        ));
    }
}

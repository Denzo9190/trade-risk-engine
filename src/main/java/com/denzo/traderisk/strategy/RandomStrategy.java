package com.denzo.traderisk.strategy;

import com.denzo.traderisk.marketdata.MarketDataEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class RandomStrategy implements Strategy {

    private final Random random = new Random();

    @Override
    public Optional<TradingSignal> generateSignal(String symbol, BigDecimal currentPrice) {
        // временно: всегда генерируем сигнал для демонстрации
        log.debug("RandomStrategy generated BUY signal for {} at price {}", symbol, currentPrice);
        return Optional.of(new TradingSignal(
                symbol,
                SignalType.BUY,
                currentPrice,
                BigDecimal.ONE
        ));
    }
}

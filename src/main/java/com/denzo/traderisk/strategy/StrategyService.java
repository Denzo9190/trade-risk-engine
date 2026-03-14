package com.denzo.traderisk.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StrategyService {

    private final List<TradingStrategy> strategies;

    public List<Signal> evaluateStrategies(String symbol) {
        List<Signal> signals = new ArrayList<>();
        for (TradingStrategy strategy : strategies) {
            strategy.generateSignal(symbol).ifPresent(signals::add);
        }
        return signals;
    }
}

package com.denzo.traderisk.strategy;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StrategyService {

    private final List<TradingStrategy> strategies;

    public StrategyService(List<TradingStrategy> strategies) {
        this.strategies = strategies;
    }

    public List<Signal> evaluateStrategies(String symbol) {
        return strategies.stream()
                .map(s -> s.generateSignal(symbol))
                .flatMap(Optional::stream)
                .toList();
    }
}

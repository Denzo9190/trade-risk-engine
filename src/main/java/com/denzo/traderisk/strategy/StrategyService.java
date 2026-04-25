package com.denzo.traderisk.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StrategyService {

    private final List<TradingStrategy> strategies;

    public List<TradingSignal> evaluateStrategies(String symbol) {
        return strategies.stream()
                .map(s -> s.generateSignal(symbol))
                .flatMap(Optional::stream)
                .toList();
    }
}

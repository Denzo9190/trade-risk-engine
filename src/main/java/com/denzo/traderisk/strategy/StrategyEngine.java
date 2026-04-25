package com.denzo.traderisk.strategy;

import com.denzo.traderisk.execution.SignalProcessor;
import com.denzo.traderisk.marketdata.events.PriceUpdateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class StrategyEngine {

    private final List<TradingStrategy> strategies;
    private final SignalProcessor signalProcessor;
    private final boolean autoEnabled;

    public StrategyEngine(List<TradingStrategy> strategies,
                          SignalProcessor signalProcessor,
                          @Value("${strategy.auto.enabled:true}") boolean autoEnabled) {
        this.strategies = strategies;
        this.signalProcessor = signalProcessor;
        this.autoEnabled = autoEnabled;
    }

    @EventListener
    public void onPriceUpdate(PriceUpdateEvent event) {
        if (!autoEnabled) {
            log.debug("Auto strategy execution disabled, skipping price update for {}", event.symbol());
            return;
        }
        log.debug("Price update received: {} = {}", event.symbol(), event.price());
        for (TradingStrategy strategy : strategies) {
            Optional<TradingSignal> signal = strategy.generateSignal(event.symbol());
            signal.ifPresent(signalProcessor::process);
        }
    }
}

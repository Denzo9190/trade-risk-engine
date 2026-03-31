package com.denzo.traderisk.strategy;

import com.denzo.traderisk.marketdata.events.PriceUpdateEvent;
import com.denzo.traderisk.execution.SignalProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class StrategyEngine {

    private final List<Strategy> strategies;
    private final SignalProcessor signalProcessor;

    @EventListener
    public void onPriceUpdate(PriceUpdateEvent event) {
        log.debug("Price update received: {} = {}", event.symbol(), event.price());

        for (Strategy strategy : strategies) {
            Optional<TradingSignal> signal = strategy.generateSignal(event.symbol(), event.price());
            signal.ifPresent(signalProcessor::process);
        }
    }
}

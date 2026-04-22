package com.denzo.traderisk.strategy;

import com.denzo.traderisk.marketdata.events.PriceUpdateEvent;
import com.denzo.traderisk.execution.SignalProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${strategy.auto.enabled:true}")
    private boolean autoEnabled;

    @EventListener
    public void onPriceUpdate(PriceUpdateEvent event) {
        if (!autoEnabled) {
            // Автоматическая генерация сигналов отключена – просто логируем и выходим
            log.debug("Auto strategy execution disabled, skipping price update for {}", event.symbol());
            return;
        }

        // Автоматическая генерация включена – обрабатываем событие
        log.debug("Price update received: {} = {}", event.symbol(), event.price());
        for (Strategy strategy : strategies) {
            Optional<TradingSignal> signal = strategy.generateSignal(event.symbol(), event.price());
            signal.ifPresent(signalProcessor::process);
        }
    }
}

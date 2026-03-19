package com.denzo.traderisk.backtest;

import com.denzo.traderisk.service.execution.SignalExecutionService;
import com.denzo.traderisk.strategy.TradingStrategy;
import com.denzo.traderisk.time.BacktestTimeProvider;

import java.time.Instant;
import java.util.List;

/**
 * Движок для прогона стратегии на исторических данных.
 * Управляет временем и отправляет сигналы в стандартный execution pipeline.
 */
public class BacktestEngine {

    private final TradingStrategy strategy;
    private final SignalExecutionService executionService;
    private final BacktestTimeProvider timeProvider;

    public BacktestEngine(TradingStrategy strategy,
                          SignalExecutionService executionService,
                          BacktestTimeProvider timeProvider) {
        this.strategy = strategy;
        this.executionService = executionService;
        this.timeProvider = timeProvider;
    }

    /**
     * Запускает backtest на заданной временной шкале.
     *
     * @param symbol   инструмент
     * @param timeline список временных меток (тиков)
     */
    public void run(String symbol, List<Instant> timeline) {
        for (Instant tick : timeline) {
            timeProvider.setTime(tick);
            strategy.generateSignal(symbol)
                    .ifPresent(executionService::executeSignal);
        }
    }
}

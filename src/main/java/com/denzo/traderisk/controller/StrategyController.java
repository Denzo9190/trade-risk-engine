package com.denzo.traderisk.controller;

import com.denzo.traderisk.service.execution.SignalExecutionService;
import com.denzo.traderisk.strategy.StrategyService;
import com.denzo.traderisk.strategy.TradingSignal;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/strategies")
@RequiredArgsConstructor
public class StrategyController {

    private final StrategyService strategyService;
    private final SignalExecutionService signalExecutionService;

    @GetMapping("/run")
    public List<TradingSignal> runStrategies(@RequestParam String symbol) {
        return strategyService.evaluateStrategies(symbol);
    }

    @PostMapping("/execute")
    public void executeStrategies(@RequestParam String symbol) {
        List<TradingSignal> signals = strategyService.evaluateStrategies(symbol);
        signalExecutionService.executeSignals(signals);
    }

    /**
     * Ручной однократный запуск стратегий (для тестирования).
     */
    @PostMapping("/run-once")
    public void runOnce(@RequestParam String symbol) {
        List<TradingSignal> signals = strategyService.evaluateStrategies(symbol);
        signalExecutionService.executeSignals(signals);
    }
}

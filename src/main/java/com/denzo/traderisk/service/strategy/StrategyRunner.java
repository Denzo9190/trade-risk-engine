package com.denzo.traderisk.service.strategy;

import com.denzo.traderisk.service.execution.SignalExecutionService;
import com.denzo.traderisk.strategy.Signal;
import com.denzo.traderisk.strategy.StrategyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Компонент, запускающий стратегии по расписанию.
 * Полученные сигналы автоматически отправляются в {@link SignalExecutionService}.
 */
@Component
public class StrategyRunner {

    private static final Logger log = LoggerFactory.getLogger(StrategyRunner.class);

    private final StrategyService strategyService;
    private final SignalExecutionService signalExecutionService;

    @Value("${strategy.runner.interval:10000}")
    private long interval;

    public StrategyRunner(StrategyService strategyService,
                          SignalExecutionService signalExecutionService) {
        this.strategyService = strategyService;
        this.signalExecutionService = signalExecutionService;
    }

    /**
     * Запускает все стратегии для заданного символа и исполняет сигналы.
     * Периодичность задаётся в application.yml (strategy.runner.interval).
     */
    //@Scheduled(fixedRateString = "${strategy.runner.interval}")
    public void runStrategies() {
        // На данный момент захардкожен символ BTCUSDT.
        // В будущем можно брать список символов из конфигурации.
        String symbol = "BTCUSDT";
        log.info("Running strategies for symbol {}", symbol);

        List<Signal> signals = strategyService.evaluateStrategies(symbol);
        log.info("Generated {} signals", signals.size());

        if (!signals.isEmpty()) {
            signalExecutionService.executeSignals(signals);
            log.info("Executed {} signals", signals.size());
        }
    }
}

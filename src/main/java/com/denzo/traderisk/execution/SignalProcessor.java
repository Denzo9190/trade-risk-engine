package com.denzo.traderisk.execution;

import com.denzo.traderisk.risk.engine.RiskEngine;
import com.denzo.traderisk.strategy.TradingSignal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignalProcessor {

    private final RiskEngine riskEngine;
    private final ExecutionService executionService;

    public void process(TradingSignal signal) {
        log.debug("Processing signal: {}", signal);
        var decision = riskEngine.evaluate(signal);
        if (!decision.allowed()) {
            log.warn("Signal rejected by risk engine: {}", decision.reason());
            return;
        }
        executionService.execute(signal);
    }
}

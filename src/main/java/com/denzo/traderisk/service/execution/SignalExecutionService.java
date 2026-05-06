package com.denzo.traderisk.service.execution;

import com.denzo.traderisk.exception.RiskViolationException;
import com.denzo.traderisk.execution.ExecutionService;
import com.denzo.traderisk.risk.engine.RiskEngine;
import com.denzo.traderisk.strategy.TradingSignal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SignalExecutionService {

    private final RiskEngine riskEngine;
    private final ExecutionService executionService;

    public void executeSignal(TradingSignal signal) {
        var decision = riskEngine.evaluate(signal);
        if (!decision.allowed()) {
            throw new RiskViolationException(decision.reason());
        }
        executionService.execute(signal);
    }

    public void executeSignals(List<TradingSignal> signals) {
        signals.forEach(this::executeSignal);
    }
}

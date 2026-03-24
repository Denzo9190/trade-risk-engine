package com.denzo.traderisk.service.execution;

import com.denzo.traderisk.dto.RiskCheckResult;
import com.denzo.traderisk.dto.TradeRequest;
import com.denzo.traderisk.exception.RiskViolationException;
import com.denzo.traderisk.execution.ExecutionService;
import com.denzo.traderisk.service.RiskService;
import com.denzo.traderisk.strategy.Signal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignalExecutionService {

    private final RiskService riskService;
    private final ExecutionService executionService;

    public void executeSignal(Signal signal) {
        // 1. Создаём запрос для риска (можно прямо из сигнала)
        TradeRequest riskRequest = new TradeRequest(
                signal.symbol(),
                signal.quantity(),
                signal.price(),
                signal.side()
        );

        // 2. Проверяем риск
        RiskCheckResult riskCheck = riskService.checkTrade(riskRequest);
        if (!riskCheck.allowed()) {
            throw new RiskViolationException(riskCheck.reason());
        }

        // 3. Исполняем через Exchange Adapter
        executionService.executeSignal(signal);
    }

    public void executeSignals(java.util.List<Signal> signals) {
        signals.forEach(this::executeSignal);
    }
}

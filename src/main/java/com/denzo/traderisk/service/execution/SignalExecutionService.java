package com.denzo.traderisk.service.execution;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.dto.RiskCheckResult;
import com.denzo.traderisk.dto.TradeRequest;
import com.denzo.traderisk.exception.RiskViolationException;
import com.denzo.traderisk.execution.ExecutionService;
import com.denzo.traderisk.execution.SignalProcessor;
import com.denzo.traderisk.service.RiskService;
import com.denzo.traderisk.strategy.TradingSignal;
import com.denzo.traderisk.strategy.SignalType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SignalExecutionService {

    private final RiskService riskService;
    private final ExecutionService executionService;

    public void executeSignal(TradingSignal signal) {
        // Создаём TradeRequest для RiskService
        TradeRequest request = new TradeRequest(
                signal.symbol(),
                signal.quantity(),
                signal.price(),
                signal.type() == SignalType.BUY ? Side.BUY : Side.SELL
        );
        RiskCheckResult riskCheck = riskService.checkTrade(request);
        if (!riskCheck.allowed()) {
            throw new RiskViolationException(riskCheck.reason());
        }
        executionService.execute(signal);
    }

    public void executeSignals(List<TradingSignal> signals) {
        signals.forEach(this::executeSignal);
    }
}

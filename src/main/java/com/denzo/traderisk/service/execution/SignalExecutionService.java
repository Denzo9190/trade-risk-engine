package com.denzo.traderisk.service.execution;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.execution.SignalProcessor;
import com.denzo.traderisk.strategy.Signal;
import com.denzo.traderisk.strategy.TradingSignal;
import com.denzo.traderisk.strategy.SignalType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SignalExecutionService {

    private final SignalProcessor signalProcessor;

    public void executeSignal(Signal signal) {
        TradingSignal tradingSignal = new TradingSignal(
                signal.symbol(),
                signal.side() == Side.BUY ? SignalType.BUY : SignalType.SELL,
                signal.price(),
                signal.quantity()
        );
        signalProcessor.process(tradingSignal);
    }

    public void executeSignals(List<Signal> signals) {
        signals.forEach(this::executeSignal);
    }
}

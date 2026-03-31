package com.denzo.traderisk.execution;

import com.denzo.traderisk.service.RiskService;
import com.denzo.traderisk.strategy.TradingSignal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignalProcessor {

    private final RiskService riskService;
    private final ExecutionService executionService;

    public void process(TradingSignal signal) {
        log.debug("Processing signal: id={} {} {} {} @ {}",
                signal.id(), signal.type(), signal.quantity(), signal.symbol(), signal.price());

        if (!riskService.validate(signal)) {
            log.warn("Signal rejected by risk engine: {}", signal);
            return;
        }

        executionService.execute(signal);
    }
}

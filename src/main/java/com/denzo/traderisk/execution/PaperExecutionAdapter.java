package com.denzo.traderisk.execution;

import com.denzo.traderisk.strategy.TradingSignal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@Primary
public class PaperExecutionAdapter implements ExecutionAdapter {

    @Override
    public ExecutionResult execute(TradingSignal signal) {
        log.info("Paper execution: {} {} {} @ {}", signal.type(), signal.quantity(), signal.symbol(), signal.price());
        return new ExecutionResult(
                signal.symbol(),
                signal.price(),
                signal.quantity(),
                UUID.randomUUID().toString()
        );
    }
}

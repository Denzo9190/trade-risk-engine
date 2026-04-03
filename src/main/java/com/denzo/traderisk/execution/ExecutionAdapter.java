package com.denzo.traderisk.execution;

import com.denzo.traderisk.strategy.TradingSignal;

public interface ExecutionAdapter {
    ExecutionResult execute(TradingSignal signal);
}

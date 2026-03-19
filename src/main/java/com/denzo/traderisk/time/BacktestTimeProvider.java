package com.denzo.traderisk.time;

import java.time.Instant;

public class BacktestTimeProvider implements TimeProvider {

    private Instant currentTime;

    public void setTime(Instant time) {
        this.currentTime = time;
    }

    @Override
    public Instant now() {
        if (currentTime == null) {
            throw new IllegalStateException("Backtest time not set");
        }
        return currentTime;
    }
}

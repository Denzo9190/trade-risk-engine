package com.denzo.traderisk.backtest;

import com.denzo.traderisk.domain.Trade;

import java.math.BigDecimal;
import java.util.List;

/**
 * Результат прогона backtest.
 */
public class BacktestResult {

    private final List<Trade> trades;
    private final BigDecimal totalPnl;          // realised + unrealised
    private final BigDecimal realisedPnl;
    private final BigDecimal unrealisedPnl;
    private final BigDecimal maxDrawdown;
    private final BigDecimal winRate;

    public BacktestResult(List<Trade> trades,
                          BigDecimal realisedPnl,
                          BigDecimal unrealisedPnl,
                          BigDecimal maxDrawdown,
                          BigDecimal winRate) {
        this.trades = trades;
        this.realisedPnl = realisedPnl;
        this.unrealisedPnl = unrealisedPnl;
        this.totalPnl = realisedPnl.add(unrealisedPnl);
        this.maxDrawdown = maxDrawdown;
        this.winRate = winRate;
    }

    public List<Trade> getTrades() { return trades; }
    public BigDecimal getTotalPnl() { return totalPnl; }
    public BigDecimal getRealisedPnl() { return realisedPnl; }
    public BigDecimal getUnrealisedPnl() { return unrealisedPnl; }
    public BigDecimal getMaxDrawdown() { return maxDrawdown; }
    public BigDecimal getWinRate() { return winRate; }
}

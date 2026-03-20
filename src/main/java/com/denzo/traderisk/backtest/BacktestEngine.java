package com.denzo.traderisk.backtest;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.domain.Trade;
import com.denzo.traderisk.dto.PositionResponse;
import com.denzo.traderisk.service.PositionService;
import com.denzo.traderisk.service.RealisedPnlService;
import com.denzo.traderisk.service.TradeService;
import com.denzo.traderisk.service.execution.SignalExecutionService;
import com.denzo.traderisk.strategy.TradingStrategy;
import com.denzo.traderisk.time.BacktestTimeProvider;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;

public class BacktestEngine {

    private final TradingStrategy strategy;
    private final SignalExecutionService executionService;
    private final BacktestTimeProvider timeProvider;
    private final TradeService tradeService;
    private final PositionService positionService;
    private final RealisedPnlService realisedPnlService;  // добавили для realised PnL

    public BacktestEngine(TradingStrategy strategy,
                          SignalExecutionService executionService,
                          BacktestTimeProvider timeProvider,
                          TradeService tradeService,
                          PositionService positionService,
                          RealisedPnlService realisedPnlService) {
        this.strategy = strategy;
        this.executionService = executionService;
        this.timeProvider = timeProvider;
        this.tradeService = tradeService;
        this.positionService = positionService;
        this.realisedPnlService = realisedPnlService;
    }

    public BacktestResult run(String symbol, List<Instant> timeline) {
        for (Instant tick : timeline) {
            timeProvider.setTime(tick);
            strategy.generateSignal(symbol)
                    .ifPresent(executionService::executeSignal);
        }

        List<Trade> trades = tradeService.getAll();
        BigDecimal realisedPnl = realisedPnlService.calculateRealisedPnl(symbol).realisedPnl();
        BigDecimal unrealisedPnl = positionService.getPosition(symbol).unrealisedPnl();

        // простейший winRate: доля сделок с положительным PnL (по realised)
        long totalTrades = trades.size();
        long winningTrades = trades.stream()
                .filter(t -> t.getSide() == Side.SELL && t.getPrice().compareTo(t.getQuantity()) > 0) // упрощённо
                .count();
        BigDecimal winRate = totalTrades == 0 ? BigDecimal.ZERO :
                BigDecimal.valueOf(winningTrades).divide(BigDecimal.valueOf(totalTrades), 4, RoundingMode.HALF_UP);

        // drawdown пока заглушка
        BigDecimal maxDrawdown = BigDecimal.ZERO;

        return new BacktestResult(trades, realisedPnl, unrealisedPnl, maxDrawdown, winRate);
    }
}

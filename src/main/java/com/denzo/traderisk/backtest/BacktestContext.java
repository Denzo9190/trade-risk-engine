package com.denzo.traderisk.backtest;

import com.denzo.traderisk.marketdata.PriceCache;
import com.denzo.traderisk.marketdata.adapter.MarketDataAdapter;
import com.denzo.traderisk.service.PositionService;
import com.denzo.traderisk.service.RealisedPnlService;
import com.denzo.traderisk.service.TradeService;
import com.denzo.traderisk.service.execution.SignalExecutionService;
import com.denzo.traderisk.strategy.TradingStrategy;
import com.denzo.traderisk.time.BacktestTimeProvider;

import java.time.Instant;
import java.util.List;

public class BacktestContext {

    private final TradingStrategy strategy;
    private final BacktestTimeProvider timeProvider;
    private final SignalExecutionService executionService;
    private final TradeService tradeService;
    private final PositionService positionService;
    private final RealisedPnlService realisedPnlService;
    private final MarketDataAdapter historicalAdapter;
    private final PriceCache priceCache;

    private BacktestContext(Builder builder) {
        this.strategy = builder.strategy;
        this.timeProvider = builder.timeProvider;
        this.executionService = builder.executionService;
        this.tradeService = builder.tradeService;
        this.positionService = builder.positionService;
        this.realisedPnlService = builder.realisedPnlService;
        this.historicalAdapter = builder.historicalAdapter;
        this.priceCache = builder.priceCache;
    }

    public BacktestResult run(String symbol, List<Instant> timeline) {
        BacktestEngine engine = new BacktestEngine(
                strategy, executionService, timeProvider,
                tradeService, positionService, realisedPnlService,
                historicalAdapter, priceCache
        );
        return engine.run(symbol, timeline);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private TradingStrategy strategy;
        private BacktestTimeProvider timeProvider;
        private SignalExecutionService executionService;
        private TradeService tradeService;
        private PositionService positionService;
        private RealisedPnlService realisedPnlService;
        private MarketDataAdapter historicalAdapter;
        private PriceCache priceCache;

        public Builder withStrategy(TradingStrategy strategy) {
            this.strategy = strategy;
            return this;
        }
        public Builder withTimeProvider(BacktestTimeProvider timeProvider) {
            this.timeProvider = timeProvider;
            return this;
        }
        public Builder withExecutionService(SignalExecutionService executionService) {
            this.executionService = executionService;
            return this;
        }
        public Builder withTradeService(TradeService tradeService) {
            this.tradeService = tradeService;
            return this;
        }
        public Builder withPositionService(PositionService positionService) {
            this.positionService = positionService;
            return this;
        }
        public Builder withRealisedPnlService(RealisedPnlService realisedPnlService) {
            this.realisedPnlService = realisedPnlService;
            return this;
        }
        public Builder withHistoricalAdapter(MarketDataAdapter historicalAdapter) {
            this.historicalAdapter = historicalAdapter;
            return this;
        }
        public Builder withPriceCache(PriceCache priceCache) {
            this.priceCache = priceCache;
            return this;
        }

        public BacktestContext build() {
            if (strategy == null) throw new IllegalStateException("Strategy must be set");
            if (timeProvider == null) throw new IllegalStateException("TimeProvider must be set");
            if (executionService == null) throw new IllegalStateException("ExecutionService must be set");
            if (tradeService == null) throw new IllegalStateException("TradeService must be set");
            if (positionService == null) throw new IllegalStateException("PositionService must be set");
            if (realisedPnlService == null) throw new IllegalStateException("RealisedPnlService must be set");
            if (historicalAdapter == null) throw new IllegalStateException("HistoricalAdapter must be set");
            if (priceCache == null) throw new IllegalStateException("PriceCache must be set");
            return new BacktestContext(this);
        }
    }
}

package com.denzo.traderisk.risk.engine;

import com.denzo.traderisk.marketdata.MarketDataEngine;
import com.denzo.traderisk.portfolio.PortfolioService;
import com.denzo.traderisk.portfolio.PortfolioSnapshot;
import com.denzo.traderisk.risk.rule.RiskRule;
import com.denzo.traderisk.strategy.TradingSignal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RiskEngine {

    private final List<RiskRule> rules;
    private final PortfolioService portfolioService;
    private final MarketDataEngine marketDataEngine;

    private List<RiskRule> sortedRules; // ленивая инициализация

    private List<RiskRule> getSortedRules() {
        if (sortedRules == null) {
            sortedRules = rules.stream()
                    .sorted(Comparator.comparingInt(RiskRule::priority))
                    .collect(Collectors.toList());
        }
        return sortedRules;
    }

    public RiskDecision evaluate(TradingSignal signal) {
        return evaluate(signal, portfolioService.getPortfolio());
    }

    public RiskDecision evaluate(TradingSignal signal, PortfolioSnapshot portfolio) {
        BigDecimal price = marketDataEngine.getPrice(signal.symbol());
        if (price == null) {
            return RiskDecision.reject("No market price available for " + signal.symbol());
        }
        RiskEvaluationContext context = new RiskEvaluationContext(signal, portfolio, price);
        for (RiskRule rule : getSortedRules()) {
            RiskDecision decision = rule.evaluate(context);
            if (!decision.allowed()) {
                log.warn("Risk rejected: {}", decision.reason());
                return decision;
            }
        }
        return RiskDecision.allow();
    }
}

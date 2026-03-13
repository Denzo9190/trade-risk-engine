package com.denzo.traderisk.service;

import com.denzo.traderisk.dto.PortfolioResponse;
import com.denzo.traderisk.dto.RiskCheckResult;
import com.denzo.traderisk.dto.TradeRequest;
import com.denzo.traderisk.risk.RiskRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RiskService {

    private final List<RiskRule> rules;          // Spring внедрит все бины, реализующие RiskRule
    private final PortfolioService portfolioService;

    /**
     * Проверяет сделку на соответствие всем правилам риск-движка.
     */
    public RiskCheckResult checkTrade(String symbol, BigDecimal quantity, BigDecimal price) {
        TradeRequest request = new TradeRequest(symbol, quantity, price, null);
        PortfolioResponse portfolio = portfolioService.getPortfolio();

        for (RiskRule rule : rules) {
            RiskCheckResult result = rule.check(request, portfolio);
            if (!result.allowed()) {
                return result;
            }
        }
        return RiskCheckResult.ok();
    }
}

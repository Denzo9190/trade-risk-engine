package com.denzo.traderisk.service;

import com.denzo.traderisk.config.RiskLimits;
import com.denzo.traderisk.dto.PortfolioResponse;
import com.denzo.traderisk.dto.PositionResponse;
import com.denzo.traderisk.dto.RiskCheckResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class RiskService {

    private final PositionService positionService;
    private final PortfolioService portfolioService;
    private final RiskLimits limits;

    public RiskCheckResult checkTrade(String symbol, BigDecimal quantity, BigDecimal price) {
        // 1. Проверка размера сделки
        if (quantity.abs().compareTo(limits.maxTradeSize()) > 0) {
            return RiskCheckResult.rejected("Trade size exceeds limit (max " + limits.maxTradeSize() + ")");
        }

        // 2. Проверка размера позиции после сделки
        PositionResponse currentPos = positionService.getPosition(symbol);
        BigDecimal newPosition = currentPos.totalQuantity().add(quantity);
        if (newPosition.abs().compareTo(limits.maxPositionSize()) > 0) {
            return RiskCheckResult.rejected("Position limit exceeded (max " + limits.maxPositionSize() + ")");
        }

        // 3. Проверка общей экспозиции портфеля после сделки
        PortfolioResponse portfolio = portfolioService.getPortfolio();
        BigDecimal tradeExposure = quantity.abs().multiply(price);
        BigDecimal newExposure = portfolio.totalExposure().add(tradeExposure);
        if (newExposure.compareTo(limits.maxPortfolioExposure()) > 0) {
            return RiskCheckResult.rejected("Portfolio exposure limit exceeded (max $" + limits.maxPortfolioExposure() + ")");
        }

        return RiskCheckResult.ok();
    }
}

package com.denzo.traderisk.service;

import com.denzo.traderisk.config.RiskLimits;
import com.denzo.traderisk.dto.PortfolioResponse;
import com.denzo.traderisk.dto.PositionResponse;
import com.denzo.traderisk.dto.RiskCheckResult;
import com.denzo.traderisk.dto.TradeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Сервис предторговой проверки рисков.
 * Проверяет лимиты размера сделки, позиции и общей экспозиции портфеля.
 */
@Service
@RequiredArgsConstructor
public class RiskService {
    // TODO (Day 21–22): добавить правило отклонения цены
    //   if (abs(signal.price - marketPrice) / marketPrice > threshold) -> reject

    private final PositionService positionService;
    private final PortfolioService portfolioService;
    private final RiskLimits limits;

    public RiskCheckResult checkTrade(TradeRequest request) {
        System.out.println(">>> RiskService: checking trade " + request.symbol() + " qty=" + request.quantity() + " price=" + request.price());
        // 1. Проверка размера сделки
        if (request.quantity().abs().compareTo(limits.maxTradeSize()) > 0) {
            System.out.println(">>> RiskService: passed");
            return RiskCheckResult.rejected("Trade size exceeds limit (max " + limits.maxTradeSize() + ")");
        }

        // 2. Проверка размера позиции после сделки
        PositionResponse currentPos = positionService.getPosition(request.symbol());
        BigDecimal newPosition = currentPos.totalQuantity().add(request.quantity());
        if (newPosition.abs().compareTo(limits.maxPositionSize()) > 0) {
            System.out.println(">>> RiskService: passed");
            return RiskCheckResult.rejected("Position limit exceeded (max " + limits.maxPositionSize() + ")");
        }

        // 3. Проверка общей экспозиции портфеля после сделки
        PortfolioResponse portfolio = portfolioService.getPortfolio();
        BigDecimal tradeExposure = request.quantity().abs().multiply(request.price());
        BigDecimal newExposure = portfolio.totalExposure().add(tradeExposure);
        if (newExposure.compareTo(limits.maxPortfolioExposure()) > 0) {
            System.out.println(">>> RiskService: passed");
            return RiskCheckResult.rejected("Portfolio exposure limit exceeded (max $" + limits.maxPortfolioExposure() + ")");
        }

        return RiskCheckResult.ok();
    }
}

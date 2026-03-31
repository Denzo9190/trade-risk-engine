package com.denzo.traderisk.service;

import com.denzo.traderisk.config.RiskLimits;
import com.denzo.traderisk.dto.*;
import com.denzo.traderisk.marketdata.MarketDataEngine;
import com.denzo.traderisk.strategy.TradingSignal;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class RiskService {

    private static final Logger log = LoggerFactory.getLogger(RiskService.class);
    private static final BigDecimal PERCENT = BigDecimal.valueOf(100);

    private final PositionService positionService;
    private final PortfolioService portfolioService;
    private final MarketDataEngine marketDataEngine;
    private final RiskLimits limits;

    public RiskCheckResult checkTrade(TradeRequest request) {
        // 1. Проверка цены
        BigDecimal marketPrice;
        try {
            marketPrice = marketDataEngine.getPrice(request.symbol());
        } catch (Exception e) {
            log.warn("Failed to get market price for {}: {}", request.symbol(), e.getMessage());
            return RiskCheckResult.rejected("Market price unavailable for " + request.symbol());
        }
        if (marketPrice == null || marketPrice.compareTo(BigDecimal.ZERO) == 0) {
            return RiskCheckResult.rejected("Market price is zero or null for " + request.symbol());
        }

        BigDecimal deviation = request.price()
                .subtract(marketPrice)
                .abs()
                .divide(marketPrice, 6, RoundingMode.HALF_UP);

        if (deviation.compareTo(limits.getMaxPriceDeviation()) > 0) {
            String reason = String.format(Locale.US,
                    "Price deviation too high: signal=%.2f, market=%.2f, deviation=%.4f%%",
                    request.price().doubleValue(),
                    marketPrice.doubleValue(),
                    deviation.multiply(PERCENT).doubleValue()
            );
            return RiskCheckResult.rejected(reason);
        }

        // 2. Проверка размера сделки
        if (request.quantity().abs().compareTo(limits.getMaxTradeSize()) > 0) {
            return RiskCheckResult.rejected("Trade size exceeds limit (max " + limits.getMaxTradeSize() + ")");
        }

        // 3. Проверка размера позиции после сделки
        PositionResponse currentPos = positionService.getPosition(request.symbol());
        BigDecimal newPosition = currentPos.totalQuantity().add(request.quantity());
        if (newPosition.abs().compareTo(limits.getMaxPositionSize()) > 0) {
            return RiskCheckResult.rejected("Position limit exceeded (max " + limits.getMaxPositionSize() + ")");
        }

        // 4. Проверка общей экспозиции портфеля
        PortfolioResponse portfolio = portfolioService.getPortfolio();
        BigDecimal tradeExposure = request.quantity().abs().multiply(request.price());
        BigDecimal newExposure = portfolio.totalExposure().add(tradeExposure);
        if (newExposure.compareTo(limits.getMaxPortfolioExposure()) > 0) {
            return RiskCheckResult.rejected("Portfolio exposure limit exceeded (max $" + limits.getMaxPortfolioExposure() + ")");
        }

        return RiskCheckResult.ok();
    }

    public boolean validate(TradingSignal signal) {
        //TODO validate
        // Пример простой валидации: можно проверить размер позиции, отклонение цены и т.д.
        // Пока возвращаем true.
        return true;
    }
}

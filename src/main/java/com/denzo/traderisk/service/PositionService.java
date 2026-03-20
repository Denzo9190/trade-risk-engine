package com.denzo.traderisk.service;

import com.denzo.traderisk.cache.PositionCache;
import com.denzo.traderisk.config.FinancialConstants;
import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.domain.Trade;
import com.denzo.traderisk.dto.PositionResponse;
import com.denzo.traderisk.market.MarketDataService;
import com.denzo.traderisk.math.FinancialMath;
import com.denzo.traderisk.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PositionService {

    private final TradeRepository tradeRepository;
    private final MarketDataService marketDataService;
    private final PositionCache positionCache;

    public PositionResponse getPosition(String symbol) {
        System.out.println(">>> PositionService.getPosition: " + symbol);
        return positionCache.computeIfAbsent(symbol, this::calculatePosition);
    }

    private PositionResponse calculatePosition(String symbol) {
        List<Trade> trades = tradeRepository.findBySymbolOrderByIdAsc(symbol);
        System.out.println(">>> PositionService.calculatePosition: trades count = " + trades.size());
        trades.forEach(t -> System.out.println("   trade: id=" + t.getId() + " side=" + t.getSide() + " qty=" + t.getQuantity() + " price=" + t.getPrice()));
        BigDecimal signedQty = BigDecimal.ZERO;
        BigDecimal avgPrice = BigDecimal.ZERO;

        for (Trade trade : trades) {
            BigDecimal tradeQty = trade.getQuantity();
            BigDecimal tradePrice = trade.getPrice();
            BigDecimal tradeSignedQty = trade.getSide() == Side.BUY ? tradeQty : tradeQty.negate();

            BigDecimal closingQty = BigDecimal.ZERO;
            if (signedQty.signum() != 0 && signedQty.signum() != tradeSignedQty.signum()) {
                closingQty = tradeQty.min(signedQty.abs());
            }
            BigDecimal openingQty = tradeQty.subtract(closingQty);

            BigDecimal newSignedQty = signedQty.add(tradeSignedQty);

            if (openingQty.compareTo(BigDecimal.ZERO) > 0) {
                if (signedQty.signum() == 0) {
                    avgPrice = tradePrice;
                } else if (signedQty.signum() == tradeSignedQty.signum()) {
                    BigDecimal oldValue = avgPrice.multiply(signedQty.abs());
                    BigDecimal tradeValue = tradePrice.multiply(openingQty);
                    avgPrice = FinancialMath.money(
                            oldValue.add(tradeValue)
                                    .divide(newSignedQty.abs(), FinancialConstants.PRICE_SCALE, RoundingMode.HALF_UP)
                    );
                } else {
                    avgPrice = tradePrice;
                }
            }

            signedQty = newSignedQty;
        }

        BigDecimal currentPrice = marketDataService.getPrice(symbol);
        System.out.println(">>> PositionService.calculatePosition: currentPrice=" + currentPrice);
        BigDecimal unrealisedPnl;
        if (signedQty.signum() > 0) {
            unrealisedPnl = FinancialMath.multiply(currentPrice.subtract(avgPrice), signedQty);
        } else if (signedQty.signum() < 0) {
            unrealisedPnl = FinancialMath.multiply(avgPrice.subtract(currentPrice), signedQty.abs());
        } else {
            unrealisedPnl = BigDecimal.ZERO;
        }

        avgPrice = FinancialMath.money(avgPrice);
        unrealisedPnl = FinancialMath.money(unrealisedPnl);

        return new PositionResponse(symbol, signedQty, avgPrice, unrealisedPnl);
    }

    public void updatePosition(String symbol, BigDecimal quantity, BigDecimal price) {
        System.out.println(">>> PositionService.updatePosition: symbol=" + symbol + " qty=" + quantity + " price=" + price);
        positionCache.remove(symbol);
    }
}

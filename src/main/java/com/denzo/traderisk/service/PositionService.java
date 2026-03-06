package com.denzo.traderisk.service;

import com.denzo.traderisk.config.FinancialConstants;
import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.domain.Trade;
import com.denzo.traderisk.dto.PositionResponse;
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

    public PositionResponse getPosition(String symbol, BigDecimal currentPrice) {
        List<Trade> trades = tradeRepository.findBySymbol(symbol);
        if (trades.isEmpty()) {
            return new PositionResponse(symbol, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }

        BigDecimal longQty = trades.stream()
                .filter(t -> t.getSide() == Side.BUY)
                .map(Trade::getQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal shortQty = trades.stream()
                .filter(t -> t.getSide() == Side.SELL)
                .map(Trade::getQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netQty = longQty.subtract(shortQty);

        BigDecimal avgPrice;
        BigDecimal unrealisedPnl;

        if (netQty.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal totalCost = trades.stream()
                    .filter(t -> t.getSide() == Side.BUY)
                    .map(t -> t.getPrice().multiply(t.getQuantity()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            avgPrice = totalCost.divide(longQty, FinancialConstants.PRICE_SCALE, RoundingMode.HALF_UP);
            unrealisedPnl = currentPrice.subtract(avgPrice).multiply(netQty);
            unrealisedPnl = unrealisedPnl.setScale(FinancialConstants.PNL_SCALE, RoundingMode.HALF_UP);
        } else if (netQty.compareTo(BigDecimal.ZERO) < 0) {
            BigDecimal totalRevenue = trades.stream()
                    .filter(t -> t.getSide() == Side.SELL)
                    .map(t -> t.getPrice().multiply(t.getQuantity()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            avgPrice = totalRevenue.divide(shortQty, FinancialConstants.PRICE_SCALE, RoundingMode.HALF_UP);
            unrealisedPnl = avgPrice.subtract(currentPrice).multiply(netQty.abs());
            unrealisedPnl = unrealisedPnl.setScale(FinancialConstants.PNL_SCALE, RoundingMode.HALF_UP);
        } else {
            return new PositionResponse(symbol, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }

        return new PositionResponse(symbol, netQty, avgPrice, unrealisedPnl);
    }
}

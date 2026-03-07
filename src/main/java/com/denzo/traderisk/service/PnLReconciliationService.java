package com.denzo.traderisk.service;

import com.denzo.traderisk.config.FinancialConstants;
import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.domain.Trade;
import com.denzo.traderisk.dto.PnLReconciliationResponse;
import com.denzo.traderisk.dto.PositionResponse;
import com.denzo.traderisk.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PnLReconciliationService {

    private final TradeRepository tradeRepository;
    private final RealisedPnlService realisedPnlService;
    private final PositionService positionService;

    public PnLReconciliationResponse reconcile(String symbol, BigDecimal currentPrice) {
        List<Trade> trades = tradeRepository.findBySymbolOrderByCreatedAtAsc(symbol);

        BigDecimal totalRevenue = trades.stream()
                .filter(t -> t.getSide() == Side.SELL)
                .map(t -> t.getPrice().multiply(t.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCost = trades.stream()
                .filter(t -> t.getSide() == Side.BUY)
                .map(t -> t.getPrice().multiply(t.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        PositionResponse position = positionService.getPosition(symbol, currentPrice);
        BigDecimal currentPositionValue = position.totalQuantity().multiply(currentPrice);

        // TODO: include fees once fee model is implemented
        BigDecimal totalPnl = totalRevenue.add(currentPositionValue).subtract(totalCost)
                .setScale(FinancialConstants.PNL_SCALE, RoundingMode.HALF_UP);

        BigDecimal realisedPnl = realisedPnlService.calculateRealisedPnl(symbol).realisedPnl();
        BigDecimal unrealisedPnl = position.unrealisedPnl();

        BigDecimal sum = realisedPnl.add(unrealisedPnl)
                .setScale(FinancialConstants.PNL_SCALE, RoundingMode.HALF_UP);

        BigDecimal difference = sum.subtract(totalPnl)
                .abs()
                .setScale(FinancialConstants.PNL_SCALE, RoundingMode.HALF_UP);

        boolean passed = difference.compareTo(FinancialConstants.PNL_TOLERANCE) <= 0;

        return new PnLReconciliationResponse(
                symbol,
                totalPnl,
                realisedPnl,
                unrealisedPnl,
                sum,
                difference,
                passed
        );
    }
}

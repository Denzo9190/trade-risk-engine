package com.denzo.traderisk.service;

import com.denzo.traderisk.config.FinancialConstants;
import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.domain.Trade;
import com.denzo.traderisk.dto.PnLReconciliationResponse;
import com.denzo.traderisk.dto.PositionResponse;
import com.denzo.traderisk.math.FinancialMath;
import com.denzo.traderisk.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PnLReconciliationService {

    private final TradeRepository tradeRepository;
    private final RealisedPnlService realisedPnlService;
    private final PositionService positionService;

    /**
     * Выполняет сверку PnL для указанного символа.
     * Проверяет тождество: realised PnL + unrealised PnL == total PnL
     * с учётом допустимой погрешности.
     *
     * @param symbol       символ
     * @param currentPrice текущая рыночная цена
     * @return результат сверки
     */
    public PnLReconciliationResponse reconcile(String symbol, BigDecimal currentPrice) {
        // Получаем все сделки в хронологическом порядке для детерминизма
        List<Trade> trades = tradeRepository.findBySymbolOrderByCreatedAtAsc(symbol);

        // Суммарная выручка от всех SELL
        BigDecimal totalRevenue = trades.stream()
                .filter(t -> t.getSide() == Side.SELL)
                .map(t -> FinancialMath.multiply(t.getPrice(), t.getQuantity()))
                .reduce(BigDecimal.ZERO, FinancialMath::add);

        // Суммарные затраты на все BUY
        BigDecimal totalCost = trades.stream()
                .filter(t -> t.getSide() == Side.BUY)
                .map(t -> FinancialMath.multiply(t.getPrice(), t.getQuantity()))
                .reduce(BigDecimal.ZERO, FinancialMath::add);

        // Текущая позиция
        PositionResponse position = positionService.getPosition(symbol, currentPrice);
        // Стоимость текущей позиции по рыночной цене
        BigDecimal currentPositionValue = FinancialMath.multiply(position.totalQuantity(), currentPrice);

        // TODO: include fees once fee model is implemented
        // total PnL = (revenue + currentPositionValue) - cost
        BigDecimal totalPnl = FinancialMath.subtract(
                FinancialMath.add(totalRevenue, currentPositionValue),
                totalCost
        );

        BigDecimal realisedPnl = realisedPnlService.calculateRealisedPnl(symbol).realisedPnl();
        BigDecimal unrealisedPnl = position.unrealisedPnl();

        BigDecimal sum = FinancialMath.add(realisedPnl, unrealisedPnl);
        BigDecimal difference = FinancialMath.subtract(sum, totalPnl).abs();

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

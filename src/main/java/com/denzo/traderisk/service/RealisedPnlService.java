package com.denzo.traderisk.service;

import com.denzo.traderisk.config.FinancialConstants;
import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.domain.Trade;
import com.denzo.traderisk.dto.RealisedPnlResponse;
import com.denzo.traderisk.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RealisedPnlService {

    private final TradeRepository tradeRepository;

    public RealisedPnlResponse calculateRealisedPnl(String symbol) {
        // Гарантированный порядок сделок
        List<Trade> trades = tradeRepository.findBySymbolOrderByIdAsc(symbol);

        BigDecimal positionQty = BigDecimal.ZERO;
        BigDecimal avgPrice = BigDecimal.ZERO;
        BigDecimal realisedPnl = BigDecimal.ZERO;

        for (Trade trade : trades) {
            BigDecimal qty = trade.getQuantity();
            BigDecimal price = trade.getPrice();

            if (trade.getSide() == Side.BUY) {
                // Увеличиваем лонг-позицию
                BigDecimal newQty = positionQty.add(qty);
                if (newQty.compareTo(BigDecimal.ZERO) != 0) {
                    avgPrice = avgPrice.multiply(positionQty)
                            .add(price.multiply(qty))
                            .divide(newQty, FinancialConstants.PRICE_SCALE, RoundingMode.HALF_UP);
                }
                positionQty = newQty;
            } else {
                // Продажа (закрытие лонга) – защита от отрицательной позиции
                if (positionQty.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalStateException("Cannot sell when position is " + positionQty);
                }
                BigDecimal closingQty = qty.min(positionQty);
                BigDecimal pnl = price.subtract(avgPrice)
                        .multiply(closingQty)
                        .setScale(FinancialConstants.PNL_SCALE, RoundingMode.HALF_UP);
                realisedPnl = realisedPnl.add(pnl);
                positionQty = positionQty.subtract(closingQty);
            }
        }

        realisedPnl = realisedPnl.setScale(FinancialConstants.PNL_SCALE, RoundingMode.HALF_UP);
        return new RealisedPnlResponse(symbol, realisedPnl);
    }
}

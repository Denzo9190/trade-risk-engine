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

    /**
     * Возвращает текущую позицию по символу: знаковое количество, среднюю цену и нереализованный PnL.
     * signedQty > 0 – лонг, signedQty < 0 – шорт, 0 – позиция закрыта.
     */
    public PositionResponse getPosition(String symbol, BigDecimal currentPrice) {
        List<Trade> trades = tradeRepository.findBySymbolOrderByIdAsc(symbol);

        BigDecimal signedQty = BigDecimal.ZERO;          // знаковое количество
        BigDecimal avgPrice = BigDecimal.ZERO;           // средняя цена (всегда положительная)

        for (Trade trade : trades) {
            BigDecimal tradeQty = trade.getQuantity();
            BigDecimal tradePrice = trade.getPrice();
            BigDecimal tradeSignedQty = trade.getSide() == Side.BUY ? tradeQty : tradeQty.negate();

            // 1. Определяем closing quantity (часть, которая закрывает существующую позицию)
            BigDecimal closingQty = BigDecimal.ZERO;
            if (signedQty.signum() != 0 && signedQty.signum() != tradeSignedQty.signum()) {
                closingQty = tradeQty.min(signedQty.abs());
            }
            BigDecimal openingQty = tradeQty.subtract(closingQty); // часть, открывающая новую позицию в направлении сделки

            // 2. Обновляем позицию
            BigDecimal newSignedQty = signedQty.add(tradeSignedQty);

            // 3. Пересчёт средней цены
            if (openingQty.compareTo(BigDecimal.ZERO) > 0) {
                // есть открываемая часть
                if (signedQty.signum() == 0) {
                    // открытие с нуля
                    avgPrice = tradePrice;
                } else if (signedQty.signum() == tradeSignedQty.signum()) {
                    // то же направление – взвешенная средняя
                    BigDecimal oldValue = avgPrice.multiply(signedQty.abs());
                    BigDecimal tradeValue = tradePrice.multiply(openingQty);
                    avgPrice = oldValue.add(tradeValue)
                            .divide(signedQty.abs().add(openingQty), FinancialConstants.PRICE_SCALE, RoundingMode.HALF_UP);
                } else {
                    // противоположное направление – после закрытия открывается новая позиция по цене сделки
                    avgPrice = tradePrice;
                }
            } // иначе closingQty > 0 – средняя цена не меняется

            signedQty = newSignedQty;
        }

        // Расчёт unrealised PnL
        BigDecimal unrealisedPnl;
        if (signedQty.signum() > 0) {
            unrealisedPnl = currentPrice.subtract(avgPrice).multiply(signedQty);
        } else if (signedQty.signum() < 0) {
            unrealisedPnl = avgPrice.subtract(currentPrice).multiply(signedQty.abs());
        } else {
            unrealisedPnl = BigDecimal.ZERO;
        }
        unrealisedPnl = unrealisedPnl.setScale(FinancialConstants.PNL_SCALE, RoundingMode.HALF_UP);

        return new PositionResponse(symbol, signedQty, avgPrice, unrealisedPnl);
    }
}

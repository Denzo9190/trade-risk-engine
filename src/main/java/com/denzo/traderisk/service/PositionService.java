package com.denzo.traderisk.service;

import com.denzo.traderisk.config.FinancialConstants;
import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.domain.Trade;
import com.denzo.traderisk.dto.PositionResponse;
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

    /**
     * Возвращает текущую позицию по символу: знаковое количество, среднюю цену и нереализованный PnL.
     * signedQty > 0 – лонг, signedQty < 0 – шорт, signedQty == 0 – позиция закрыта.
     * Алгоритм использует closingQty/openingQty, что обеспечивает корректную обработку частичных закрытий и флипов.
     */
    public PositionResponse getPosition(String symbol, BigDecimal currentPrice) {
        List<Trade> trades = tradeRepository.findBySymbolOrderByIdAsc(symbol);

        BigDecimal signedQty = BigDecimal.ZERO;          // знаковое количество текущей позиции
        BigDecimal avgPrice = BigDecimal.ZERO;           // средняя цена (всегда положительная)

        for (Trade trade : trades) {
            BigDecimal tradeQty = trade.getQuantity();
            BigDecimal tradePrice = trade.getPrice();
            BigDecimal tradeSignedQty = trade.getSide() == Side.BUY ? tradeQty : tradeQty.negate();

            // Определяем closing quantity – часть сделки, закрывающая существующую позицию (противоположное направление)
            BigDecimal closingQty = BigDecimal.ZERO;
            if (signedQty.signum() != 0 && signedQty.signum() != tradeSignedQty.signum()) {
                closingQty = tradeQty.min(signedQty.abs());
            }
            BigDecimal openingQty = tradeQty.subtract(closingQty); // часть, открывающая новую позицию в направлении сделки

            // Новая позиция после сделки
            BigDecimal newSignedQty = signedQty.add(tradeSignedQty);

            // Пересчёт средней цены (только если есть открываемая часть)
            if (openingQty.compareTo(BigDecimal.ZERO) > 0) {
                if (signedQty.signum() == 0) {
                    // Открытие с нуля
                    avgPrice = tradePrice;
                } else if (signedQty.signum() == tradeSignedQty.signum()) {
                    // То же направление – взвешенная средняя
                    BigDecimal oldValue = avgPrice.multiply(signedQty.abs());
                    BigDecimal tradeValue = tradePrice.multiply(openingQty);
                    avgPrice = FinancialMath.money(
                            oldValue.add(tradeValue)
                                    .divide(newSignedQty.abs(), FinancialConstants.PRICE_SCALE, RoundingMode.HALF_UP)
                    );
                } else {
                    // Смена знака (флип) – новая позиция открывается по цене сделки
                    avgPrice = tradePrice;
                }
            }
            // Если только закрытие (openingQty == 0), средняя цена не меняется

            signedQty = newSignedQty;
        }

        // Расчёт unrealised PnL в зависимости от знака позиции
        BigDecimal unrealisedPnl;
        if (signedQty.signum() > 0) {
            // Лонг: (currentPrice - avgPrice) * количество
            unrealisedPnl = FinancialMath.multiply(currentPrice.subtract(avgPrice), signedQty);
        } else if (signedQty.signum() < 0) {
            // Шорт: (avgPrice - currentPrice) * |количество|
            unrealisedPnl = FinancialMath.multiply(avgPrice.subtract(currentPrice), signedQty.abs());
        } else {
            unrealisedPnl = BigDecimal.ZERO;
        }

        return new PositionResponse(symbol, signedQty, avgPrice, unrealisedPnl);
    }
}

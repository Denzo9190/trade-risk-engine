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

    /**
     * Рассчитывает реализованную прибыль/убыток для указанного символа.
     * Использует знаковое представление позиции:
     * положительное количество = лонг, отрицательное = шорт, ноль = позиции нет.
     * Алгоритм основан на определении закрываемой части (closingQty) и работает для любых направлений,
     * включая переворот позиции (flip).
     */
    public RealisedPnlResponse calculateRealisedPnl(String symbol) {
        // Получаем все сделки по символу в порядке возрастания ID (хронологический порядок)
        List<Trade> trades = tradeRepository.findBySymbolOrderByIdAsc(symbol);

        BigDecimal positionQty = BigDecimal.ZERO;       // знаковое количество текущей позиции
        BigDecimal avgPrice = BigDecimal.ZERO;          // средняя цена текущей позиции (всегда положительная)
        BigDecimal realisedPnl = BigDecimal.ZERO;       // накопленная реализованная прибыль

        for (Trade trade : trades) {
            BigDecimal tradeQty = trade.getQuantity();
            BigDecimal tradePrice = trade.getPrice();
            // знаковое количество сделки: + для BUY, - для SELL
            BigDecimal tradeSignedQty = trade.getSide() == Side.BUY ? tradeQty : tradeQty.negate();

            // 1. Определяем количество, которое закрывает существующую позицию
            BigDecimal closingQty = BigDecimal.ZERO;
            if (positionQty.signum() != 0 && positionQty.signum() != tradeSignedQty.signum()) {
                // направления противоположны – часть позиции закрывается
                closingQty = tradeQty.min(positionQty.abs());
            }

            // 2. Расчёт реализованной прибыли для закрываемой части
            if (closingQty.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal exitPrice = tradePrice;
                BigDecimal pnl;
                if (positionQty.signum() > 0) { // закрытие лонга
                    pnl = exitPrice.subtract(avgPrice).multiply(closingQty);
                } else { // закрытие шорта
                    pnl = avgPrice.subtract(exitPrice).multiply(closingQty);
                }
                // округляем и добавляем к общей сумме
                realisedPnl = realisedPnl.add(pnl.setScale(FinancialConstants.PNL_SCALE, RoundingMode.HALF_UP));
            }

            // 3. Обновляем количество позиции (знаковое)
            BigDecimal newPositionQty = positionQty.add(tradeSignedQty);

            // 4. Пересчёт средней цены в зависимости от новой позиции
            if (newPositionQty.signum() == 0) {
                // позиция полностью закрыта
                avgPrice = BigDecimal.ZERO;
            } else if (positionQty.signum() == 0) {
                // открытие новой позиции с нуля
                avgPrice = tradePrice;
            } else if (positionQty.signum() == newPositionQty.signum()) {
                // направление не изменилось – средняя взвешенная
                BigDecimal oldValue = avgPrice.multiply(positionQty.abs());
                BigDecimal tradeValue = tradePrice.multiply(tradeQty);
                avgPrice = oldValue.add(tradeValue)
                        .divide(newPositionQty.abs(), FinancialConstants.PRICE_SCALE, RoundingMode.HALF_UP);
            } else {
                // знак изменился – произошёл переворот: новая позиция открыта по цене сделки
                avgPrice = tradePrice;
            }

            positionQty = newPositionQty;
        }

        // Финальное округление realised PnL
        realisedPnl = realisedPnl.setScale(FinancialConstants.PNL_SCALE, RoundingMode.HALF_UP);
        return new RealisedPnlResponse(symbol, realisedPnl);
    }
}

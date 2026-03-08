package com.denzo.traderisk.service;

import com.denzo.traderisk.config.FinancialConstants;
import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.domain.Trade;
import com.denzo.traderisk.dto.RealisedPnlResponse;
import com.denzo.traderisk.math.FinancialMath;
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

        BigDecimal signedQty = BigDecimal.ZERO;       // знаковое количество текущей позиции
        BigDecimal avgPrice = BigDecimal.ZERO;        // средняя цена текущей позиции (всегда положительная)
        BigDecimal realisedPnl = BigDecimal.ZERO;     // накопленная реализованная прибыль

        for (Trade trade : trades) {
            BigDecimal tradeQty = trade.getQuantity();
            BigDecimal tradePrice = trade.getPrice();
            // знаковое количество сделки: + для BUY, - для SELL
            BigDecimal tradeSignedQty = trade.getSide() == Side.BUY ? tradeQty : tradeQty.negate();

            // 1. Определяем количество, которое закрывает существующую позицию
            BigDecimal closingQty = BigDecimal.ZERO;
            if (signedQty.signum() != 0 && signedQty.signum() != tradeSignedQty.signum()) {
                // направления противоположны – часть позиции закрывается
                closingQty = tradeQty.min(signedQty.abs());
            }
            BigDecimal openingQty = tradeQty.subtract(closingQty); // часть, открывающая новую позицию

            // 2. Расчёт реализованной прибыли для закрываемой части
            if (closingQty.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal pnl;
                if (signedQty.signum() > 0) { // закрытие лонга
                    pnl = FinancialMath.multiply(tradePrice.subtract(avgPrice), closingQty);
                } else { // закрытие шорта
                    pnl = FinancialMath.multiply(avgPrice.subtract(tradePrice), closingQty);
                }
                // добавляем к общей сумме
                realisedPnl = FinancialMath.add(realisedPnl, pnl);
            }

            // 3. Обновляем количество позиции (знаковое)
            BigDecimal newSignedQty = signedQty.add(tradeSignedQty);

            // 4. Пересчёт средней цены (только если есть открываемая часть)
            if (openingQty.compareTo(BigDecimal.ZERO) > 0) {
                if (signedQty.signum() == 0) {
                    // открытие новой позиции с нуля
                    avgPrice = tradePrice;
                } else if (signedQty.signum() == tradeSignedQty.signum()) {
                    // направление не изменилось – средняя взвешенная
                    BigDecimal oldValue = avgPrice.multiply(signedQty.abs());
                    BigDecimal tradeValue = tradePrice.multiply(openingQty);
                    avgPrice = FinancialMath.money(
                            oldValue.add(tradeValue)
                                    .divide(newSignedQty.abs(), FinancialConstants.PRICE_SCALE, RoundingMode.HALF_UP)
                    );
                } else {
                    // знак изменился – произошёл переворот: новая позиция открыта по цене сделки
                    avgPrice = tradePrice;
                }
            }

            signedQty = newSignedQty;
        }

        // realisedPnl уже имеет правильный масштаб благодаря FinancialMath
        return new RealisedPnlResponse(symbol, realisedPnl);
    }
}

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

    public RealisedPnlResponse calculateRealisedPnl(String symbol) {
        List<Trade> trades = tradeRepository.findBySymbolOrderByIdAsc(symbol);

        BigDecimal signedQty = BigDecimal.ZERO;
        BigDecimal avgPrice = BigDecimal.ZERO;
        BigDecimal realisedPnl = BigDecimal.ZERO;

        for (Trade trade : trades) {
            BigDecimal tradeQty = trade.getQuantity();
            BigDecimal tradePrice = trade.getPrice();
            BigDecimal tradeSignedQty = trade.getSide() == Side.BUY ? tradeQty : tradeQty.negate();

            BigDecimal closingQty = BigDecimal.ZERO;
            if (signedQty.signum() != 0 && signedQty.signum() != tradeSignedQty.signum()) {
                closingQty = tradeQty.min(signedQty.abs());
            }
            BigDecimal openingQty = tradeQty.subtract(closingQty);

            if (closingQty.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal pnl;
                if (signedQty.signum() > 0) { // закрытие лонга
                    pnl = FinancialMath.multiply(tradePrice.subtract(avgPrice), closingQty);
                } else { // закрытие шорта
                    pnl = FinancialMath.multiply(avgPrice.subtract(tradePrice), closingQty);
                }
                realisedPnl = FinancialMath.add(realisedPnl, pnl);
            }

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
        realisedPnl = FinancialMath.money(realisedPnl);
        return new RealisedPnlResponse(symbol, realisedPnl);
    }
}

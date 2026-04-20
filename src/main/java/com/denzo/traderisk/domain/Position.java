package com.denzo.traderisk.domain;

import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
public class Position {

    private final String symbol;
    private BigDecimal quantity = BigDecimal.ZERO;
    private BigDecimal averagePrice = BigDecimal.ZERO;
    private BigDecimal realisedPnl = BigDecimal.ZERO;

    public Position(String symbol) {
        this.symbol = symbol;
    }

    public void applyTrade(BigDecimal tradeQty, BigDecimal tradePrice, Side side) {
        BigDecimal signedQty = side == Side.BUY ? tradeQty : tradeQty.negate();

        if (quantity.signum() == 0 || quantity.signum() == signedQty.signum()) {
            // Same direction or zero position
            BigDecimal totalCost = averagePrice.multiply(quantity)
                    .add(tradePrice.multiply(signedQty));
            quantity = quantity.add(signedQty);
            if (quantity.signum() != 0) {
                averagePrice = totalCost.divide(quantity, 8, RoundingMode.HALF_UP);
            } else {
                averagePrice = BigDecimal.ZERO;
            }
        } else {
            // Opposite direction – closing or flipping
            BigDecimal closingQty = signedQty.abs().min(quantity.abs());
            BigDecimal pnlPerUnit = tradePrice.subtract(averagePrice)
                    .multiply(BigDecimal.valueOf(quantity.signum()));
            realisedPnl = realisedPnl.add(pnlPerUnit.multiply(closingQty));

            quantity = quantity.add(signedQty);
            if (quantity.signum() == 0) {
                averagePrice = BigDecimal.ZERO;
            } else if (quantity.signum() == signedQty.signum()) {
                // Flipped to opposite side
                averagePrice = tradePrice;
            }
        }
    }
}

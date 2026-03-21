package com.denzo.traderisk.risk;

import com.denzo.traderisk.config.RiskLimits;
import com.denzo.traderisk.dto.PortfolioResponse;
import com.denzo.traderisk.dto.PositionResponse;
import com.denzo.traderisk.dto.RiskCheckResult;
import com.denzo.traderisk.dto.TradeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MaxPositionPerSymbolRule implements RiskRule {

    private final RiskLimits limits;

    @Override
    public RiskCheckResult check(TradeRequest trade, PortfolioResponse portfolio) {
        // Найти позицию по данному символу
        Optional<PositionResponse> optPosition = portfolio.positions().stream()
                .filter(p -> p.symbol().equals(trade.symbol()))
                .findFirst();

        BigDecimal currentQty = optPosition.map(PositionResponse::totalQuantity).orElse(BigDecimal.ZERO);
        BigDecimal newQty = currentQty.add(trade.quantity());

        if (newQty.abs().compareTo(limits.getMaxPortfolioExposure()) > 0) {
            return RiskCheckResult.rejected(
                    "Position limit exceeded for " + trade.symbol() +
                            " (max " + limits.getMaxPortfolioExposure() + ")"
            );
        }
        return RiskCheckResult.ok();
    }
}

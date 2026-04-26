package com.denzo.traderisk.portfolio;

import com.denzo.traderisk.domain.Position;
import com.denzo.traderisk.marketdata.MarketDataEngine;
import com.denzo.traderisk.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PositionRepository positionRepository;
    private final MarketDataEngine marketDataEngine;

    public PortfolioSnapshot getPortfolio() {
        var positions = positionRepository.findAll();

        Map<String, PositionView> views = new HashMap<>();
        BigDecimal totalUnrealised = BigDecimal.ZERO;
        BigDecimal totalExposure = BigDecimal.ZERO;

        for (Position p : positions) {
            if (p.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
                continue; // пропускаем нулевые позиции
            }
            BigDecimal currentPrice;
            try {
                currentPrice = marketDataEngine.getPrice(p.getSymbol());
            } catch (Exception e) {
                log.warn("Price not available for {}, skipping unrealised PnL", p.getSymbol());
                currentPrice = null;
            }
            BigDecimal unrealised;
            if (currentPrice == null) {
                unrealised = BigDecimal.ZERO;
            } else {
                unrealised = currentPrice.subtract(p.getAveragePrice())
                        .multiply(p.getQuantity());
            }
            PositionView view = new PositionView(
                    p.getSymbol(),
                    p.getQuantity(),
                    p.getAveragePrice(),
                    unrealised
            );
            views.put(p.getSymbol(), view);
            totalUnrealised = totalUnrealised.add(unrealised);
            totalExposure = totalExposure.add(p.getQuantity().abs().multiply(p.getAveragePrice()));
        }

        BigDecimal totalRealised = positions.stream()
                .map(Position::getRealisedPnl)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalEquity = totalRealised.add(totalUnrealised);

        return new PortfolioSnapshot(views, totalUnrealised, totalRealised, totalEquity, totalExposure);
    }
}

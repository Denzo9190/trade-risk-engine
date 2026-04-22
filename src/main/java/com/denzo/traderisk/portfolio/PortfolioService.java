package com.denzo.traderisk.portfolio;

import com.denzo.traderisk.domain.Position;
import com.denzo.traderisk.marketdata.MarketDataEngine;
import com.denzo.traderisk.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PositionRepository positionRepository;
    private final MarketDataEngine marketDataEngine;

    public PortfolioSnapshot getPortfolio() {
        var positions = positionRepository.findAll();

        Map<String, PositionView> views = positions.stream()
                .collect(Collectors.toMap(
                        Position::getSymbol,
                        p -> {
                            BigDecimal marketPrice = marketDataEngine.getPrice(p.getSymbol());
                            BigDecimal unrealised = marketPrice
                                    .subtract(p.getAveragePrice())
                                    .multiply(p.getQuantity());
                            return new PositionView(
                                    p.getSymbol(),
                                    p.getQuantity(),
                                    p.getAveragePrice(),
                                    unrealised
                            );
                        }
                ));

        BigDecimal totalUnrealised = views.values().stream()
                .map(PositionView::unrealisedPnl)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRealised = positions.stream()
                .map(Position::getRealisedPnl)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalEquity = totalRealised.add(totalUnrealised);

        BigDecimal totalExposure = views.values().stream()
                .map(v -> v.quantity().abs().multiply(v.averagePrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new PortfolioSnapshot(views, totalUnrealised, totalRealised, totalEquity, totalExposure);
    }
}

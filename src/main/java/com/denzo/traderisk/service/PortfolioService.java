package com.denzo.traderisk.service;

import com.denzo.traderisk.dto.PortfolioResponse;
import com.denzo.traderisk.dto.PositionResponse;
import com.denzo.traderisk.dto.RealisedPnlResponse;
import com.denzo.traderisk.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final TradeRepository tradeRepository;
    private final PositionService positionService;
    private final RealisedPnlService realisedPnlService;
    private final MarketPriceService marketPriceService; // добавлено

    public PortfolioResponse getPortfolio() {
        List<String> symbols = tradeRepository.findDistinctSymbols();

        BigDecimal totalRealised = BigDecimal.ZERO;
        BigDecimal totalUnrealised = BigDecimal.ZERO;
        BigDecimal totalExposure = BigDecimal.ZERO;
        List<PositionResponse> positions = new ArrayList<>();

        for (String symbol : symbols) {
            PositionResponse position = positionService.getPosition(symbol);
            RealisedPnlResponse realised = realisedPnlService.calculateRealisedPnl(symbol);

            totalRealised = totalRealised.add(realised.realisedPnl());
            totalUnrealised = totalUnrealised.add(position.unrealisedPnl());
            BigDecimal exposure = position.totalQuantity().abs()
                    .multiply(position.averagePrice());
            totalExposure = totalExposure.add(exposure);

            positions.add(position);
        }

        return new PortfolioResponse(
                totalRealised,
                totalUnrealised,
                totalExposure,
                positions
        );
    }
}

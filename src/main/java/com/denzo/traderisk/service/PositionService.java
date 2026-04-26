package com.denzo.traderisk.service;

import com.denzo.traderisk.domain.Position;
import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.dto.PositionResponse;
import com.denzo.traderisk.event.DomainEventPublisher;
import com.denzo.traderisk.event.PositionUpdatedEvent;
import com.denzo.traderisk.event.TradeExecutedEvent;
import com.denzo.traderisk.marketdata.MarketDataEngine;
import com.denzo.traderisk.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PositionService {

    private final PositionRepository positionRepository;
    private final DomainEventPublisher eventPublisher;
    private final MarketDataEngine marketDataEngine;  // добавлено для unrealised PnL

    // === Новый инкрементальный метод ===
    public void applyTrade(TradeExecutedEvent event) {
        Position position = positionRepository.findBySymbol(event.symbol())
                .orElseGet(() -> new Position(event.symbol()));

        position.applyTrade(event.executedQuantity(), event.executedPrice(), event.side());

        positionRepository.save(position);

        eventPublisher.publish(new PositionUpdatedEvent(
                position.getSymbol(),
                position.getQuantity(),
                position.getAveragePrice(),
                position.getQuantity().signum() >= 0 ? Side.BUY : Side.SELL
        ));
        log.debug("Position updated for {}: qty={}, avg={}, realisedPnl={}",
                position.getSymbol(), position.getQuantity(), position.getAveragePrice(), position.getRealisedPnl());
    }

    // === Метод для получения DTO (используется старыми компонентами) ===
    public PositionResponse getPositionResponse(String symbol) {
        Position position = positionRepository.findBySymbol(symbol).orElse(new Position(symbol));
        BigDecimal currentPrice = marketDataEngine.getPrice(symbol);
        BigDecimal unrealisedPnl = position.getQuantity().multiply(currentPrice.subtract(position.getAveragePrice()));
        return new PositionResponse(
                position.getSymbol(),
                position.getQuantity(),
                position.getAveragePrice(),
                unrealisedPnl
        );
    }

    // === Заглушка для обратной совместимости (старый метод updatePosition) ===
    public void updatePosition(String symbol, BigDecimal quantity, BigDecimal price) {
        // no-op – позиция теперь обновляется через applyTrade
        log.debug("updatePosition called (no-op) for {} with qty={}, price={}", symbol, quantity, price);
    }

    // === Старый метод getPosition (возвращал PositionResponse) – перенаправляем ===
    public PositionResponse getPosition(String symbol) {
        return getPositionResponse(symbol);
    }

    public List<Position> getAllPositions() {
        return positionRepository.findAll();
    }
}

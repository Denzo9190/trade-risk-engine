package com.denzo.traderisk.service;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.domain.Trade;
import com.denzo.traderisk.dto.CreateTradeRequest;
import com.denzo.traderisk.event.DomainEventPublisher;
import com.denzo.traderisk.event.TradeExecutedEvent;
import com.denzo.traderisk.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final TradeRepository tradeRepository;
    private final DomainEventPublisher eventPublisher;

    @Transactional
    public Trade createTrade(CreateTradeRequest request) {
        // валидация
        if (request.quantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (request.price().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }

        Trade trade = new Trade(
                request.symbol(),
                request.quantity(),
                request.price(),
                Side.valueOf(request.side().toUpperCase())
        );
        Trade saved = tradeRepository.save(trade);

        // публикация события
        TradeExecutedEvent event = new TradeExecutedEvent(
                saved.getId(),
                saved.getSymbol(),
                saved.getQuantity(),
                saved.getPrice(),
                saved.getSide()
        );
        eventPublisher.publish(event);

        return saved;
    }

    public List<Trade> getAll() {
        return tradeRepository.findAll();
    }
}

package com.denzo.traderisk.service;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.event.DomainEventPublisher;
import com.denzo.traderisk.event.PositionUpdatedEvent;
import com.denzo.traderisk.event.TradeExecutedEvent;
import com.denzo.traderisk.repository.PositionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PositionServiceTest {

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private PositionService positionService;

    @BeforeEach
    void setUp() {
        when(positionRepository.findBySymbol("BTCUSDT")).thenReturn(Optional.empty());
    }

    @Test
    void shouldCreateNewPositionOnFirstTrade() {
        TradeExecutedEvent event = new TradeExecutedEvent("BTCUSDT", BigDecimal.ONE, new BigDecimal("60000"), Side.BUY, "order-1");
        positionService.applyTrade(event);

        ArgumentCaptor<com.denzo.traderisk.domain.Position> positionCaptor = ArgumentCaptor.forClass(com.denzo.traderisk.domain.Position.class);
        verify(positionRepository).save(positionCaptor.capture());
        com.denzo.traderisk.domain.Position saved = positionCaptor.getValue();

        assertThat(saved.getQuantity()).isEqualByComparingTo("1");
        assertThat(saved.getAveragePrice()).isEqualByComparingTo("60000");

        ArgumentCaptor<PositionUpdatedEvent> eventCaptor = ArgumentCaptor.forClass(PositionUpdatedEvent.class);
        verify(eventPublisher).publish(eventCaptor.capture());
        PositionUpdatedEvent published = eventCaptor.getValue();
        assertThat(published.totalQuantity()).isEqualByComparingTo("1");
    }
}

package com.denzo.traderisk.execution;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.domain.Trade;
import com.denzo.traderisk.event.DomainEventPublisher;
import com.denzo.traderisk.event.TradeExecutedEvent;
import com.denzo.traderisk.repository.TradeRepository;
import com.denzo.traderisk.strategy.SignalType;
import com.denzo.traderisk.strategy.TradingSignal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExecutionServiceTest {

    @Mock
    private TradeRepository tradeRepository;

    @Mock
    private DomainEventPublisher domainEventPublisher;

    @InjectMocks
    private ExecutionService executionService;

    @Test
    void shouldExecuteAndPublishEvent() {
        TradingSignal signal = new TradingSignal(
                "BTCUSDT",
                SignalType.BUY,
                new BigDecimal("63500"),
                BigDecimal.ONE
        );

        Trade savedTrade = new Trade(
                "BTCUSDT",
                BigDecimal.ONE,
                new BigDecimal("63500"),
                Side.BUY,
                "order-123"
        );
        when(tradeRepository.save(any(Trade.class))).thenReturn(savedTrade);

        executionService.execute(signal);

        verify(tradeRepository).save(any(Trade.class));
        ArgumentCaptor<TradeExecutedEvent> eventCaptor = ArgumentCaptor.forClass(TradeExecutedEvent.class);
        verify(domainEventPublisher).publish(eventCaptor.capture());

        TradeExecutedEvent event = eventCaptor.getValue();
        assertThat(event.symbol()).isEqualTo("BTCUSDT");
        assertThat(event.executedQuantity()).isEqualByComparingTo("1");
        assertThat(event.executedPrice()).isEqualByComparingTo("63500");
        assertThat(event.side()).isEqualTo(Side.BUY);
        assertThat(event.exchangeOrderId()).isEqualTo("order-123");
    }
}

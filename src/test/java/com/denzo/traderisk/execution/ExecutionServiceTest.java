package com.denzo.traderisk.execution;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.domain.Trade;
import com.denzo.traderisk.event.DomainEventPublisher;
import com.denzo.traderisk.event.TradeExecutedEvent;
import com.denzo.traderisk.repository.TradeRepository;
import com.denzo.traderisk.strategy.Signal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExecutionServiceTest {

    @Mock
    private ExchangeAdapter exchangeAdapter;

    @Mock
    private TradeRepository tradeRepository;

    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private ExecutionService executionService;

    @Captor
    private ArgumentCaptor<Trade> tradeCaptor;

    @Captor
    private ArgumentCaptor<TradeExecutedEvent> eventCaptor;

    @Test
    void shouldExecuteSignalAndPublishEvent() {
        Signal signal = new Signal("BTCUSDT", Side.BUY, BigDecimal.valueOf(2), BigDecimal.valueOf(60000), "Test", Instant.now());
        OrderResult result = new OrderResult("BTCUSDT", BigDecimal.valueOf(2), BigDecimal.valueOf(60000), "ORD-123");
        when(exchangeAdapter.placeOrder(any(OrderRequest.class))).thenReturn(result);

        executionService.executeSignal(signal);

        // Проверяем сохранение сделки
        verify(tradeRepository).save(tradeCaptor.capture());
        Trade saved = tradeCaptor.getValue();
        assertThat(saved.getSymbol()).isEqualTo("BTCUSDT");
        assertThat(saved.getQuantity()).isEqualByComparingTo("2");
        assertThat(saved.getPrice()).isEqualByComparingTo("60000");
        assertThat(saved.getSide()).isEqualTo(Side.BUY);

        // Проверяем публикацию события
        verify(eventPublisher).publish(eventCaptor.capture());
        TradeExecutedEvent event = eventCaptor.getValue();
        assertThat(event.symbol()).isEqualTo("BTCUSDT");
        assertThat(event.executedQuantity()).isEqualByComparingTo("2");
        assertThat(event.executedPrice()).isEqualByComparingTo("60000");
        assertThat(event.side()).isEqualTo(Side.BUY);
        assertThat(event.exchangeOrderId()).isEqualTo("ORD-123");
    }
}

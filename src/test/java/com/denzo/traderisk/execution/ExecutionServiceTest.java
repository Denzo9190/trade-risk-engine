package com.denzo.traderisk.execution;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.domain.Trade;
import com.denzo.traderisk.event.DomainEventPublisher;
import com.denzo.traderisk.event.TradeExecutedEvent;
import com.denzo.traderisk.execution.order.Order;
import com.denzo.traderisk.execution.order.OrderFill;
import com.denzo.traderisk.execution.order.OrderType;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExecutionServiceTest {

    @Mock private OrderManager orderManager;
    @Mock private TradeBuilder tradeBuilder;
    @Mock private TradeRepository tradeRepository;
    @Mock private DomainEventPublisher domainEventPublisher;
    @InjectMocks private ExecutionService executionService;

    @Test
    void shouldExecuteFullFlow() {
        TradingSignal signal = new TradingSignal("BTCUSDT", SignalType.BUY, new BigDecimal("63500"), BigDecimal.ONE);
        Order order = new Order("BTCUSDT", Side.BUY, BigDecimal.ONE, OrderType.MARKET);
        when(orderManager.createOrder(signal)).thenReturn(order);
        List<OrderFill> fills = List.of(new OrderFill(order.getId(), new BigDecimal("63500"), BigDecimal.ONE));
        when(orderManager.submitOrder(order)).thenReturn(fills);
        Trade trade = new Trade("BTCUSDT", BigDecimal.ONE, new BigDecimal("63500"), Side.BUY, order.getId());
        when(tradeBuilder.buildTrades(order, fills)).thenReturn(List.of(trade));
        when(tradeRepository.save(any(Trade.class))).thenReturn(trade);

        executionService.execute(signal);

        verify(orderManager).createOrder(signal);
        verify(orderManager).submitOrder(order);
        verify(tradeRepository).save(trade);
        verify(domainEventPublisher).publish(any(TradeExecutedEvent.class));
    }
}

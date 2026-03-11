package com.denzo.traderisk.event;

import com.denzo.traderisk.domain.Side;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DomainEventPublisherTest {

    @Mock
    private ApplicationEventPublisher publisher;

    @Mock
    private EventStore eventStore;

    @InjectMocks
    private DomainEventPublisher domainEventPublisher;

    @Test
    void shouldPublishAndStoreEvent() {
        TradeExecutedEvent event = new TradeExecutedEvent(
                1L, "BTCUSDT", BigDecimal.ONE, BigDecimal.valueOf(60000), Side.BUY
        );
        domainEventPublisher.publish(event);
        verify(eventStore).append(event);
        verify(publisher).publishEvent(event);
    }
}

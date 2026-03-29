package com.denzo.traderisk.marketdata;

import com.denzo.traderisk.marketdata.events.PriceUpdateEvent;
import com.denzo.traderisk.time.TimeProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpringMarketDataEventPublisherTest {

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private TimeProvider timeProvider;  // добавлено

    @InjectMocks
    private SpringMarketDataEventPublisher publisher;

    @Test
    void shouldPublishPriceUpdateEvent() {
        String symbol = "BTCUSDT";
        BigDecimal price = new BigDecimal("63500");
        Instant fixedTime = Instant.parse("2026-03-29T10:00:00Z");

        when(timeProvider.now()).thenReturn(fixedTime);

        publisher.publishPriceUpdate(symbol, price);

        ArgumentCaptor<PriceUpdateEvent> captor = ArgumentCaptor.forClass(PriceUpdateEvent.class);
        verify(applicationEventPublisher).publishEvent(captor.capture());

        PriceUpdateEvent event = captor.getValue();
        assertThat(event.symbol()).isEqualTo(symbol);
        assertThat(event.price()).isEqualByComparingTo(price);
        assertThat(event.timestamp()).isEqualTo(fixedTime);
    }
}

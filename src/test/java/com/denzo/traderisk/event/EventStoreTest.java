package com.denzo.traderisk.event;

import com.denzo.traderisk.domain.Side;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class EventStoreTest {

    private EventStore eventStore;

    @BeforeEach
    void setUp() {
        eventStore = new EventStore();
    }

    @Test
    void shouldStoreAndRetrieveEvents() {
        TradeExecutedEvent event = new TradeExecutedEvent("BTCUSDT", BigDecimal.ONE, BigDecimal.valueOf(60000), Side.BUY, "1");
        eventStore.append(event);
        var events = eventStore.getAll();
        assertThat(events).hasSize(1);
        assertThat(events.get(0)).isInstanceOf(TradeExecutedEvent.class);
        TradeExecutedEvent stored = (TradeExecutedEvent) events.get(0);
        assertThat(stored.exchangeOrderId()).isEqualTo("1");
    }

    @Test
    void shouldClearEvents() {
        eventStore.append(new TradeExecutedEvent("BTCUSDT", BigDecimal.ONE, BigDecimal.valueOf(60000), Side.BUY, "1"));
        eventStore.clear();
        assertThat(eventStore.getAll()).isEmpty();
    }
}

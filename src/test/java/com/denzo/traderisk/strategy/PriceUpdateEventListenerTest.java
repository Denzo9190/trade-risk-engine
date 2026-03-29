package com.denzo.traderisk.strategy;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.denzo.traderisk.marketdata.events.PriceUpdateEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class PriceUpdateEventListenerTest {

    private PriceUpdateEventListener listener;
    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setUp() {
        listener = new PriceUpdateEventListener();

        // Настраиваем логгер для проверки вывода
        Logger logger = (Logger) LoggerFactory.getLogger(PriceUpdateEventListener.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @Test
    void shouldLogPriceUpdateEvent() {
        PriceUpdateEvent event = new PriceUpdateEvent("BTCUSDT", new BigDecimal("63500"), Instant.now());

        listener.onPriceUpdate(event);

        assertThat(listAppender.list)
                .isNotEmpty()
                .anyMatch(log -> log.getLevel() == Level.INFO
                        && log.getFormattedMessage().contains("Price update event received: BTCUSDT = 63500"));
    }
}

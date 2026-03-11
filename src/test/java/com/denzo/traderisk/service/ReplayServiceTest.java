package com.denzo.traderisk.service;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.event.EventStore;
import com.denzo.traderisk.event.TradeExecutedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReplayServiceTest {

    @Mock
    private EventStore eventStore;

    @Mock
    private PositionService positionService;

    @Mock
    private RealisedPnlService realisedPnlService;

    @Mock
    private LedgerService ledgerService;

    @InjectMocks
    private ReplayService replayService;

    @BeforeEach
    void setUp() {
        // Настраиваем, чтобы EventStore возвращал список событий
        TradeExecutedEvent event = new TradeExecutedEvent(
                1L, "BTCUSDT", BigDecimal.valueOf(2), BigDecimal.valueOf(60000), Side.BUY
        );
        when(eventStore.getAll()).thenReturn(List.of(event));
    }

    @Test
    void shouldReplayEvents() {
        replayService.replayAll();
        verify(positionService).updatePosition("BTCUSDT", BigDecimal.valueOf(2), BigDecimal.valueOf(60000));
        verify(realisedPnlService).calculateRealisedPnl("BTCUSDT");
        // ledgerService не вызывается напрямую в replay, так как события уже записаны,
        // но при желании можно проверить, что метод recordTrade не вызывается повторно.
    }
}

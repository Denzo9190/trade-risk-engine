package com.denzo.traderisk.service;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.event.EventStore;
import com.denzo.traderisk.event.TradeExecutedEvent;
import com.denzo.traderisk.repository.PositionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReplayServiceTest {

    @Mock
    private EventStore eventStore;

    @Mock
    private PositionService positionService;

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private RealisedPnlService realisedPnlService;

    @Mock
    private LedgerService ledgerService;

    @InjectMocks
    private ReplayService replayService;

    @Test
    void shouldReplayEvents() {
        TradeExecutedEvent event1 = new TradeExecutedEvent("BTCUSDT", BigDecimal.ONE, new BigDecimal("60000"), Side.BUY, "order1");
        TradeExecutedEvent event2 = new TradeExecutedEvent("BTCUSDT", BigDecimal.ONE, new BigDecimal("61000"), Side.SELL, "order2");
        when(eventStore.getAll()).thenReturn(List.of(event1, event2));

        replayService.replayAll();

        verify(positionRepository).clear();
        verify(positionService, times(2)).applyTrade(any(TradeExecutedEvent.class));
        verify(realisedPnlService, times(2)).calculateRealisedPnl("BTCUSDT");
    }
}

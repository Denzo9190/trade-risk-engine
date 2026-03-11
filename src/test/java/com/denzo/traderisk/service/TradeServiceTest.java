package com.denzo.traderisk.service;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.domain.Trade;
import com.denzo.traderisk.dto.CreateTradeRequest;
import com.denzo.traderisk.dto.PositionResponse;
import com.denzo.traderisk.dto.RealisedPnlResponse;
import com.denzo.traderisk.event.DomainEventPublisher;
import com.denzo.traderisk.event.TradeExecutedEvent;
import com.denzo.traderisk.repository.TradeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradeServiceTest {

    @Mock
    private TradeRepository tradeRepository;

    @Mock
    private PositionService positionService;

    @Mock
    private RealisedPnlService realisedPnlService;

    @Mock
    private LedgerService ledgerService;

    @Mock
    private MarketPriceService marketPriceService;

    @Mock
    private DomainEventPublisher eventPublisher; // добавлен мок

    @InjectMocks
    private TradeService tradeService;

    @Captor
    private ArgumentCaptor<TradeExecutedEvent> eventCaptor;

    @Test
    void shouldCreateTradeSuccessfully() {
        // given
        CreateTradeRequest request = new CreateTradeRequest(
                "BTCUSDT",
                BigDecimal.valueOf(2),
                BigDecimal.valueOf(60000),
                "BUY"
        );
        Trade savedTrade = new Trade("BTCUSDT", BigDecimal.valueOf(2), BigDecimal.valueOf(60000), Side.BUY);
        ReflectionTestUtils.setField(savedTrade, "id", 1L);

        when(tradeRepository.save(any(Trade.class))).thenReturn(savedTrade);

        // when
        Trade result = tradeService.createTrade(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(tradeRepository).save(any(Trade.class));

        // проверяем публикацию события
        verify(eventPublisher).publish(eventCaptor.capture());
        TradeExecutedEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.tradeId()).isEqualTo(1L);
        assertThat(capturedEvent.symbol()).isEqualTo("BTCUSDT");
        assertThat(capturedEvent.quantity()).isEqualByComparingTo(BigDecimal.valueOf(2));
        assertThat(capturedEvent.price()).isEqualByComparingTo(BigDecimal.valueOf(60000));
        assertThat(capturedEvent.side()).isEqualTo(Side.BUY);

        // проверяем, что positionService и realisedPnlService не вызывались напрямую
        verify(positionService, never()).getPosition(anyString());
        verify(realisedPnlService, never()).calculateRealisedPnl(anyString());
    }

    @Test
    void shouldThrowWhenQuantityIsZeroOrNegative() {
        CreateTradeRequest request = new CreateTradeRequest(
                "BTCUSDT",
                BigDecimal.ZERO,
                BigDecimal.valueOf(60000),
                "BUY"
        );
        assertThrows(IllegalArgumentException.class, () -> tradeService.createTrade(request));
    }

    @Test
    void shouldThrowWhenPriceIsZeroOrNegative() {
        CreateTradeRequest request = new CreateTradeRequest(
                "BTCUSDT",
                BigDecimal.ONE,
                BigDecimal.ZERO,
                "BUY"
        );
        assertThrows(IllegalArgumentException.class, () -> tradeService.createTrade(request));
    }

    @Test
    void shouldGetAllTrades() {
        Trade trade = new Trade("BTCUSDT", BigDecimal.ONE, BigDecimal.valueOf(60000), Side.BUY);
        ReflectionTestUtils.setField(trade, "id", 1L);
        when(tradeRepository.findAll()).thenReturn(List.of(trade));

        List<Trade> trades = tradeService.getAll();

        assertThat(trades).hasSize(1);
        assertThat(trades.getFirst().getId()).isEqualTo(1L);
    }
}

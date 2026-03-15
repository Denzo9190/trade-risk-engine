package com.denzo.traderisk.service;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.domain.Trade;
import com.denzo.traderisk.dto.CreateTradeRequest;
import com.denzo.traderisk.dto.RiskCheckResult;
import com.denzo.traderisk.dto.TradeRequest;
import com.denzo.traderisk.event.DomainEventPublisher;
import com.denzo.traderisk.event.TradeExecutedEvent;
import com.denzo.traderisk.exception.RiskViolationException;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradeServiceTest {

    @Mock
    private TradeRepository tradeRepository;

    @Mock
    private DomainEventPublisher domainEventPublisher;

    @Mock
    private RiskService riskService;

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
                Side.BUY
        );
        Trade savedTrade = new Trade("BTCUSDT", BigDecimal.valueOf(2), BigDecimal.valueOf(60000), Side.BUY);
        ReflectionTestUtils.setField(savedTrade, "id", 1L);

        when(riskService.checkTrade(any(TradeRequest.class)))
                .thenReturn(RiskCheckResult.ok());
        when(tradeRepository.save(any(Trade.class))).thenReturn(savedTrade);

        // when
        Trade result = tradeService.createTrade(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(riskService).checkTrade(any(TradeRequest.class));
        verify(tradeRepository).save(any(Trade.class));
        verify(domainEventPublisher).publish(eventCaptor.capture());
        TradeExecutedEvent publishedEvent = eventCaptor.getValue();
        assertThat(publishedEvent.symbol()).isEqualTo("BTCUSDT");
        assertThat(publishedEvent.quantity()).isEqualByComparingTo("2");
        assertThat(publishedEvent.price()).isEqualByComparingTo("60000");
        assertThat(publishedEvent.side()).isEqualTo(Side.BUY);
        verifyNoMoreInteractions(riskService, tradeRepository, domainEventPublisher);
    }

    @Test
    void shouldThrowWhenQuantityIsZeroOrNegative() {
        CreateTradeRequest request = new CreateTradeRequest(
                "BTCUSDT",
                BigDecimal.ZERO,
                BigDecimal.valueOf(60000),
                Side.BUY
        );
        assertThrows(IllegalArgumentException.class, () -> tradeService.createTrade(request));
    }

    @Test
    void shouldThrowWhenPriceIsZeroOrNegative() {
        CreateTradeRequest request = new CreateTradeRequest(
                "BTCUSDT",
                BigDecimal.ONE,
                BigDecimal.ZERO,
                Side.BUY
        );
        assertThrows(IllegalArgumentException.class, () -> tradeService.createTrade(request));
    }

    @Test
    void shouldThrowWhenRiskCheckFails() {
        CreateTradeRequest request = new CreateTradeRequest(
                "BTCUSDT",
                BigDecimal.valueOf(6),
                BigDecimal.valueOf(60000),
                Side.BUY
        );
        when(riskService.checkTrade(any(TradeRequest.class)))
                .thenReturn(RiskCheckResult.rejected("Trade size exceeds limit"));

        RiskViolationException exception = assertThrows(RiskViolationException.class,
                () -> tradeService.createTrade(request));
        assertThat(exception.getMessage()).contains("Trade size exceeds limit");
        verify(tradeRepository, never()).save(any(Trade.class));
        verify(domainEventPublisher, never()).publish(any());
    }

    @Test
    void shouldGetAllTrades() {
        Trade trade = new Trade("BTCUSDT", BigDecimal.ONE, BigDecimal.valueOf(60000), Side.BUY);
        ReflectionTestUtils.setField(trade, "id", 1L);
        when(tradeRepository.findAll()).thenReturn(List.of(trade));

        List<Trade> trades = tradeService.getAll();

        assertThat(trades).hasSize(1);
        assertThat(trades.getFirst().getSymbol()).isEqualTo("BTCUSDT");
        assertThat(trades.getFirst().getId()).isEqualTo(1L);
    }
}

package com.denzo.traderisk.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PositionTest {

    @Test
    void shouldIncreaseLongPosition() {
        Position pos = new Position("BTCUSDT");
        pos.applyTrade(BigDecimal.ONE, new BigDecimal("60000"), Side.BUY);
        assertThat(pos.getQuantity()).isEqualByComparingTo("1");
        assertThat(pos.getAveragePrice()).isEqualByComparingTo("60000");
        assertThat(pos.getRealisedPnl()).isEqualByComparingTo("0");
    }

    @Test
    void shouldPartiallyCloseLongPosition() {
        Position pos = new Position("BTCUSDT");
        pos.applyTrade(BigDecimal.valueOf(2), new BigDecimal("60000"), Side.BUY);
        pos.applyTrade(BigDecimal.ONE, new BigDecimal("61000"), Side.SELL);
        assertThat(pos.getQuantity()).isEqualByComparingTo("1");
        assertThat(pos.getAveragePrice()).isEqualByComparingTo("60000");
        assertThat(pos.getRealisedPnl()).isEqualByComparingTo("1000");
    }

    @Test
    void shouldFullyCloseAndRealisePnl() {
        Position pos = new Position("BTCUSDT");
        pos.applyTrade(BigDecimal.ONE, new BigDecimal("60000"), Side.BUY);
        pos.applyTrade(BigDecimal.ONE, new BigDecimal("61000"), Side.SELL);
        assertThat(pos.getQuantity()).isEqualByComparingTo("0");
        assertThat(pos.getAveragePrice()).isEqualByComparingTo("0");
        assertThat(pos.getRealisedPnl()).isEqualByComparingTo("1000");
    }

    @Test
    void shouldFlipFromLongToShort() {
        Position pos = new Position("BTCUSDT");
        pos.applyTrade(BigDecimal.valueOf(2), new BigDecimal("60000"), Side.BUY);
        pos.applyTrade(BigDecimal.valueOf(3), new BigDecimal("61000"), Side.SELL);
        assertThat(pos.getQuantity()).isEqualByComparingTo("-1");
        assertThat(pos.getAveragePrice()).isEqualByComparingTo("61000");
        // Realised PnL from closing 2 longs: (61000-60000)*2 = 2000
        assertThat(pos.getRealisedPnl()).isEqualByComparingTo("2000");
    }
}

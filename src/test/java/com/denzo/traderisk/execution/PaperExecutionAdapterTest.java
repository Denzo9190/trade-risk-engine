package com.denzo.traderisk.execution;

import com.denzo.traderisk.strategy.SignalType;
import com.denzo.traderisk.strategy.TradingSignal;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PaperExecutionAdapterTest {

    private final PaperExecutionAdapter adapter = new PaperExecutionAdapter();

    @Test
    void shouldExecuteWithFullFill() {
        TradingSignal signal = new TradingSignal("BTCUSDT", SignalType.BUY, new BigDecimal("63500"), BigDecimal.ONE);
        ExecutionResult result = adapter.execute(signal);

        assertThat(result.symbol()).isEqualTo("BTCUSDT");
        assertThat(result.executedPrice()).isEqualByComparingTo("63500");
        assertThat(result.executedQuantity()).isEqualByComparingTo("1");
        assertThat(result.exchangeOrderId()).isNotNull();
    }
}

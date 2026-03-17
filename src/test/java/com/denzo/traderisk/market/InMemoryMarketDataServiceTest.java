package com.denzo.traderisk.market;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemoryMarketDataServiceTest {

    private final InMemoryMarketDataService service = new InMemoryMarketDataService();

    @Test
    void shouldReturnPriceForKnownSymbol() {
        BigDecimal price = service.getPrice("BTCUSDT");
        assertThat(price).isNotNull();
        assertThat(price).isGreaterThan(BigDecimal.ZERO);
    }

    @Test
    void shouldThrowForUnknownSymbol() {
        assertThatThrownBy(() -> service.getPrice("UNKNOWN"))
                .isInstanceOf(MarketDataNotFoundException.class)
                .hasMessageContaining("UNKNOWN");
    }

    @Test
    void shouldAllowUpdatingPriceForTests() {
        service.updatePrice("BTCUSDT", new BigDecimal("70000"));
        BigDecimal price = service.getPrice("BTCUSDT");
        assertThat(price).isEqualByComparingTo("70000");
    }
}

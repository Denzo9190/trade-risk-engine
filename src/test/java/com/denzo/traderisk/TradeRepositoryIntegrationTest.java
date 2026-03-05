package com.denzo.traderisk;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.domain.Trade;
import com.denzo.traderisk.repository.TradeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TradeRepositoryIntegrationTest {

    @Autowired
    private TradeRepository tradeRepository;

    @Test
    void shouldSaveAndLoadTrade() {

        Trade trade = new Trade(
                "AAPL",
                new BigDecimal("10"),
                new BigDecimal("150.50"),
                Side.BUY
        );

        Trade saved = tradeRepository.save(trade);

        assertThat(saved.getId()).isNotNull();

        var loaded = tradeRepository.findById(saved.getId());

        assertThat(loaded).isPresent();
        assertThat(loaded.get().getSymbol()).isEqualTo("AAPL");
    }
}

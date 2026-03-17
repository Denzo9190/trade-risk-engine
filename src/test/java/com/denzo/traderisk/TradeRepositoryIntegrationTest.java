package com.denzo.traderisk;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.domain.Trade;
import com.denzo.traderisk.repository.TradeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TradeRepositoryIntegrationTest {

    @Autowired
    private TradeRepository tradeRepository;

    @Test
    void shouldSaveAndLoadTrade() {
        Trade trade = new Trade(
                "BTCUSDT",
                BigDecimal.valueOf(2),
                BigDecimal.valueOf(60000),
                Side.BUY
        );

        Trade saved = tradeRepository.save(trade);

        Optional<Trade> loaded = tradeRepository.findById(saved.getId());
        assertThat(loaded).isPresent();
        assertThat(loaded.get().getSymbol()).isEqualTo("BTCUSDT");
        assertThat(loaded.get().getQuantity()).isEqualByComparingTo("2");
        assertThat(loaded.get().getPrice()).isEqualByComparingTo("60000");
        assertThat(loaded.get().getSide()).isEqualTo(Side.BUY);
    }
}

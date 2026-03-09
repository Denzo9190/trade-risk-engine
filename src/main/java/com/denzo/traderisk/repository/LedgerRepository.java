package com.denzo.traderisk.repository;

import com.denzo.traderisk.domain.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.List;

public interface LedgerRepository extends JpaRepository<LedgerEntry, Long> {
    List<LedgerEntry> findBySymbolOrderByTimestampAsc(String symbol);
    List<LedgerEntry> findBySymbolAndTimestampBetweenOrderByTimestampAsc(String symbol, Instant from, Instant to);
    List<LedgerEntry> findByTradeId(Long tradeId);
}

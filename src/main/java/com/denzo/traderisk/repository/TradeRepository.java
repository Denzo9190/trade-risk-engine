package com.denzo.traderisk.repository;

import com.denzo.traderisk.domain.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TradeRepository extends JpaRepository<Trade, Long> {
    List<Trade> findBySymbol(String symbol);
    List<Trade> findBySymbolOrderByIdAsc(String symbol); // для гарантии порядка
    List<Trade> findBySymbolOrderByCreatedAtAsc(String symbol);

    @Query("SELECT DISTINCT t.symbol FROM Trade t")
    List<String> findDistinctSymbols();
}

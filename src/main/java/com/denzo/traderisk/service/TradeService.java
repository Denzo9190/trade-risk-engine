package com.denzo.traderisk.service;

import com.denzo.traderisk.domain.Trade;
import com.denzo.traderisk.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final TradeRepository tradeRepository;

    public List<Trade> getAll() {
        return tradeRepository.findAll();
    }
}

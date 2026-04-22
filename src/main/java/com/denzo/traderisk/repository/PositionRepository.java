package com.denzo.traderisk.repository;

import com.denzo.traderisk.domain.Position;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class PositionRepository {

    private final Map<String, Position> positions = new ConcurrentHashMap<>();

    public Optional<Position> findBySymbol(String symbol) {
        return Optional.ofNullable(positions.get(symbol));
    }

    public Position save(Position position) {
        positions.put(position.getSymbol(), position);
        return position;
    }

    public void clear() {
        positions.clear();
    }

    public List<Position> findAll() {
        return new ArrayList<>(positions.values());
    }
}

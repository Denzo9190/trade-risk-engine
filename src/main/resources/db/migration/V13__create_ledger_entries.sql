CREATE TABLE ledger_entries (
                                id BIGSERIAL PRIMARY KEY,
                                symbol VARCHAR(20) NOT NULL,
                                timestamp TIMESTAMP NOT NULL,
                                trade_id BIGINT,
                                event_type VARCHAR(30) NOT NULL,
                                trade_quantity DECIMAL(20,8),
                                trade_price DECIMAL(20,8),
                                trade_side VARCHAR(10),
                                position_qty DECIMAL(20,8),
                                avg_price DECIMAL(20,8),
                                realised_pnl DECIMAL(20,8),
                                unrealised_pnl DECIMAL(20,8),
                                description VARCHAR(255)
);

CREATE INDEX idx_ledger_symbol_timestamp ON ledger_entries(symbol, timestamp);
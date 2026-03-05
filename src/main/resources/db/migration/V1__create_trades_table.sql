CREATE TABLE trades (
                        id BIGSERIAL PRIMARY KEY,

                        symbol VARCHAR(50) NOT NULL,

                        quantity NUMERIC(19,8) NOT NULL,
                        price NUMERIC(19,8) NOT NULL,

                        side VARCHAR(10) NOT NULL,

                        created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_trades_symbol ON trades(symbol);
CREATE INDEX idx_trades_created_at ON trades(created_at);
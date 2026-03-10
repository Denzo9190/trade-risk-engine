# Trading Risk Engine

A backend engine for deterministic trade processing, PnL accounting, portfolio analytics and audit logging.  
Designed as a core component for algorithmic trading systems, investment platforms, and arbitrage engines.

## Features

- **Deterministic trade processing** – order‑invariant results via fixed sort (`ORDER BY id`)
- **Realised / unrealised PnL** – average cost accounting model
- **Financial math layer** – centralised rounding, 8‑digit precision
- **PnL reconciliation** – audit‑grade consistency check (`realised + unrealised = total`)
- **Full audit trail** – immutable Ledger with event sourcing
- **Portfolio aggregation** – consolidated view of all positions, total PnL and exposure
- **Market price abstraction** – pluggable price source (in‑memory implementation included)
- **Strategy interface** – extensible for algorithmic trading

## Tech Stack

- Java 21
- Spring Boot 3.5
- PostgreSQL 16
- JPA / Hibernate
- Maven
- Docker

## Architecture

```mermaid
flowchart TD
    API[REST API] --> TradeService
    API --> PortfolioService

    TradeService --> TradeRepository
    TradeService --> PositionService
    TradeService --> RealisedPnlService
    TradeService --> LedgerService

    PositionService --> FinancialMath
    RealisedPnlService --> FinancialMath

    PositionService --> PnLReconciliationService

    LedgerService --> LedgerRepository

    PortfolioService --> PositionService
    PortfolioService --> RealisedPnlService

    MarketPriceService --> PositionService
    MarketPriceService --> RealisedPnlService
    MarketPriceService --> PortfolioService
    MarketPriceService --> PnLReconciliationService

    StrategyEngine --> MarketPriceService
    StrategyEngine --> TradeService
```
## Engineering Principles

- **Deterministic processing** – trades always processed in fixed order (`ORDER BY id`) to ensure reproducible results.
- **Financial precision** – all arithmetic routed through `FinancialMath`, guaranteeing uniform rounding and scale.
- **Auditability** – every state change recorded in immutable `Ledger`, enabling full traceability.
- **Layered architecture** – clear separation between API, domain, services, and persistence.

## API

### Trades

- `POST /trades` – execute a trade (body: `CreateTradeRequest`)

### Positions

- `GET /positions/{symbol}` – current position

### PnL Reconciliation

- `GET /positions/{symbol}/reconcile` – verify accounting identity

### Ledger

- `GET /ledger` – all entries
- `GET /ledger/{symbol}` – history for symbol

### Portfolio

- `GET /portfolio` – aggregated portfolio view

## Example Workflow

1. **Execute trades**  
   `BUY 2 BTC @60000`  
   `BUY 1 BTC @61000`  
   `SELL 1.5 BTC @63000`

2. **Position engine**  
   `quantity = 1.5`  
   `average price = 60333.33333333`

3. **Realised PnL**  
   `(63000 - 60333.33333333) * 1.5 = 4000.00000001`

4. **Unrealised PnL** (with current price 63500)  
   `(63500 - 60333.33333333) * 1.5 = 4750.00000001`

5. **Reconciliation**  
   `realised + unrealised = 8750.00000002`  
   `total = revenue + current value - cost` → matches.

6. **Ledger** records all steps.

## Running Locally

**Requirements:**

- Java 21
- Docker
- PostgreSQL (via Docker)

```bash
# Start PostgreSQL
docker compose up -d

# Run application
./mvnw spring-boot:run
```
Application will be available at `http://localhost:8080`.

## Future Improvements

- Real‑time market data (WebSocket/Kafka)
- Risk engine (exposure limits, drawdown)
- Streaming trade ingestion
- Multi‑asset support
- High‑load optimisations (caching, partitioning)

## License

MIT
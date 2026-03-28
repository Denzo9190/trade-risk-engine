# ADR-014 — Market Data Adapter Layer

Date: 2026-03-24  
Status: adopted

## Context

The trading system requires a clear separation between the source of market prices
and the core business logic.

Strategies, risk checks, portfolio valuation and PnL calculations depend on
market prices. Directly coupling these components to a specific data source
(exchange API, in-memory data, historical data) would make testing difficult
and reduce architectural flexibility.

The system already introduced an Exchange Adapter layer for trade execution.
A similar abstraction is needed for inbound market data.

## Decision

Introduce a Market Data Adapter layer.

Core components:

- `MarketDataAdapter` — interface defining price retrieval
- `MockMarketDataAdapter` — deterministic test implementation
- `MarketDataEngine` — central service used by system components

All components access prices via `MarketDataEngine`.

Backtest mode will use a separate implementation:

- `HistoricalMarketDataAdapter`

## Consequences

Benefits:

- Decouples trading logic from price sources
- Enables deterministic testing
- Allows integration with real exchanges in the future
- Enables historical data replay for backtesting
- Aligns architecture with institutional trading systems

Trade-offs:

- Slight increase in abstraction layers
- Requires adapter implementations for each data source

## Related Components

- Strategy Engine
- Risk Engine
- Portfolio Engine
- PnL Engine
- Backtesting Engine
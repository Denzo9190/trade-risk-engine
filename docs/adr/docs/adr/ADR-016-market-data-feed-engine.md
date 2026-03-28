# ADR-016 — Market Data Feed Engine

Date: 2026-03-27  
Status: adopted

## Context

Market data flow previously had no clear orchestration layer.
MarketDataAdapter was used directly by multiple components,
which mixed integration logic with data flow control.

## Decision

Introduce **MarketDataFeedEngine** as the orchestrator
responsible for retrieving prices from MarketDataAdapter
and updating PriceCache.

MarketDataEngine now reads prices **only from PriceCache**.

Separate implementations are used for different Spring profiles:

- Main profile: MockMarketDataAdapter + InMemoryPriceCache + Feed Engine
- Backtest profile: HistoricalMarketDataAdapter + NoOpPriceCache

## Consequences

Benefits:

- single source of price updates
- clear separation of integration and orchestration
- decoupled architecture
- easier migration to WebSocket market feeds in the future
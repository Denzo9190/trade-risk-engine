# ADR-010 — Market Data Abstraction

Status: Accepted  
Date: 2026-03-17

## Context

Market prices can come from different sources:

- live exchange feeds
- historical datasets
- simulations

Direct coupling to one provider limits system flexibility.

## Decision

Introduce MarketDataService abstraction.

Implementations may include:

LiveMarketDataService  
HistoricalMarketDataService  
BacktestMarketDataService

## Consequences

Positive:

- flexible data sources
- easier testing
- consistent pricing interface
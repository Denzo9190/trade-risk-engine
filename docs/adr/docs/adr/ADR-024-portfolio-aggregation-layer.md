# ADR-024: Portfolio Aggregation Layer

**Date:** 2026-04-12  
**Status:** adopted

## Context
System tracks positions per symbol but lacks a unified portfolio view.

## Decision
Introduce `PortfolioService` that aggregates all `Position` entities and calculates:
- unrealised PnL per symbol
- total unrealised PnL
- total realised PnL
- total equity (realised + unrealised)

Uses `MarketDataEngine` for current pricing.

## Consequences
- Portfolio becomes a read‑model (projection).
- No persistent portfolio state – recalculated on demand.
- Basis for UI, dashboards, risk limits.

## Benefits
- Full account visibility.
- Aggregated risk metrics.

## Related ADRs
- ADR-023: Position Engine Refactor
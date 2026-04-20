# ADR-022: Position Integration Layer

**Date:** 2026-04-08  
**Status:** adopted

## Context
Execution pipeline produced `TradeExecutedEvent`, but these events were not automatically integrated with `PositionService` and `LedgerService`.

## Decision
Introduce `PositionUpdateHandler` that listens to `TradeExecutedEvent` and updates `PositionService` and `LedgerService`.  
For backtest profile, `BacktestMarketDataEngine` replaces standard `MarketDataEngine` to read historical prices directly.

## Consequences
- Full pipeline becomes reactive.
- Position, PnL, Portfolio update automatically.
- Backtest now uses correct historical prices for PnL calculation.

## Benefits
- Event‑driven consistency
- Centralized position update
- Fixed backtest without hacks

## Related ADRs
- ADR-019, ADR-020, ADR-021
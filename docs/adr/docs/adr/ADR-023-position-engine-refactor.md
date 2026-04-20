# ADR-023: Position Engine Refactor (Incremental Model)

**Date:** 2026-04-09  
**Status:** adopted

## Context
Position was calculated by aggregating trades on each request, leading to O(n) complexity and tight coupling with trade history.

## Decision
Introduce `Position` as a stateful aggregate that is updated incrementally via `TradeExecutedEvent`.  
`PositionRepository` stores current state. `PositionService.applyTrade()` applies the trade to the position and saves it.

## Consequences
- No need to recalculate position from trade list.
- O(1) updates instead of O(n).
- Position becomes a first-class domain entity.
- PnL is calculated incrementally.

## Benefits
- Better performance and scalability.
- Aligns with event‑sourcing principles.
- Enables advanced features (margin, liquidation, risk limits).

## Related ADRs
- ADR-022: Position Integration Layer
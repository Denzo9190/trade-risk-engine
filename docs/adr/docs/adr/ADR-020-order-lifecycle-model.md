# ADR-020: Order Lifecycle Model

**Date:** 2026-04-05  
**Status:** adopted

## Context
`ExecutionService` previously created `Trade` directly from `TradingSignal`. Real exchanges operate with orders that have a lifecycle: NEW → SUBMITTED → PARTIALLY_FILLED → FILLED (or CANCELLED/REJECTED). Without an explicit order model, partial fills, cancellations, and realistic exchange interaction are impossible.

## Decision
Introduce separate `Order` entity with `OrderStatus` and `OrderType`. `ExecutionAdapter` now works with `Order`, and `ExecutionService` creates an `Order` before calling the adapter. `PaperExecutionAdapter` simulates immediate full fill.

## Consequences
- Execution pipeline now matches real exchange API patterns.
- System can later support partial fills, order cancellation, and order book execution.
- `Trade` still exists as final result of fills.

## Benefits
- Better alignment with exchange APIs.
- Foundation for realistic backtesting (slippage, latency).
- Clear separation between order and trade.

## Alternatives Considered
- Continue with direct trade creation – rejected because it cannot model real exchange behavior.
- Use existing `Trade` as order – rejected because trade is immutable and cannot represent order state.
# ADR-021: Execution Realism Layer

**Date:** 2026-04-06  
**Status:** adopted

## Context
`PaperExecutionAdapter` executed orders instantly and completely, ignoring real-world factors: latency, slippage, partial fills.

## Decision
Introduce an execution simulation layer:
- `SlippageModel` – price deviation up to 0.05%, depending on order side (worse price for both BUY and SELL).
- `LatencyModel` – random delay configurable via `execution.latency.min/max`.
- `PartialFillModel` – splits order into three fills (40/30/30) – MVP, can be parameterized later.
- `ExecutionSimulator` – orchestrates the three models.

`PaperExecutionAdapter` uses `ExecutionSimulator` and current market price from `MarketDataEngine`.  
It returns a list of `OrderFill` objects and applies fills to the order (with overfill protection).

## Consequences
- Execution becomes realistic for testing and backtesting.
- Slippage now correctly penalizes both buy and sell orders.
- Latency is configurable.
- Order lifecycle supports partial fills with overfill safety check.

## Benefits
- Realistic simulation
- Configurable latency
- Side‑aware slippage
- Foundation for more advanced models (spread, liquidity)

## Related ADRs
- ADR-019: Execution Service Architecture
- ADR-020: Order Lifecycle Model
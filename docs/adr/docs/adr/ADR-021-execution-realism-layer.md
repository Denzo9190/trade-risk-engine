# ADR-021: Execution Realism Layer

**Date:** 2026-04-06  
**Status:** adopted

## Context

`PaperExecutionAdapter` previously executed orders instantly and completely (full fill at signal price). This behavior does not reflect real exchange execution where orders experience:

- latency (time between submission and execution)
- slippage (price difference between signal and fill)
- partial fills (order filled in multiple chunks)

## Decision

Introduce an **execution simulation layer** composed of:

- `SlippageModel` – random price deviation up to ±0.05%
- `LatencyModel` – random delay between 20 and 120 milliseconds
- `PartialFillModel` – splits order into three fills: 40%, 30%, 30%
- `ExecutionSimulator` – orchestrates the three models

`PaperExecutionAdapter` now uses `ExecutionSimulator` and obtains the current market price from `MarketDataEngine`. It applies fills to the order and returns a list of `OrderFill` objects.

## Consequences

- Execution behavior becomes closer to real markets.
- Backtesting and strategy evaluation become more realistic.
- Order lifecycle now correctly handles partial fills.
- Tests that rely on exact prices must be adjusted (slippage tolerance).

## Benefits

- Realistic latency simulation
- Price slippage
- Partial order fills

## Alternatives Considered

- Keep instant execution – rejected because unrealistic.
- Use external simulator – overkill for current stage.

## Related ADRs

- ADR-019: Execution Service Architecture
- ADR-020: Order Lifecycle Model
# ADR-001 — Deterministic Trade Processing

Status: Accepted  
Date: 2026-03-06

## Context

Financial engines must produce identical results regardless of runtime environment or execution order.

Non-deterministic processing can produce different PnL results.

## Decision

Trades must always be processed in fixed order:

ORDER BY id

## Consequences

Positive:

- Deterministic financial calculations
- Reproducible backtests
- Easier debugging

Negative:

- Slight performance overhead
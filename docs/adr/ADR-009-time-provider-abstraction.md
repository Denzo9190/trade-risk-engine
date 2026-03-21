# ADR-009 — Deterministic Trade Processing

Status: Accepted
Date: 2026-03-06

## Context

Trade order affects PnL calculation.

Non-deterministic ordering produces inconsistent results.

## Decision

Trades must always be processed in deterministic order.

Repository method:

findBySymbolOrderByIdAsc

## Consequences

Backtests and calculations become reproducible.
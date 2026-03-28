# ADR-012 — Portfolio Aggregation Layer

Status: Accepted  
Date: 2026-03-10

## Context

Users require consolidated portfolio view across all instruments.

Calculating portfolio metrics manually from individual positions
is inefficient.

## Decision

Introduce Portfolio Engine.

Portfolio aggregates:

- all positions
- total exposure
- realised PnL
- unrealised PnL

## Consequences

Positive:

- portfolio-level analytics
- simplified client API
- foundation for risk management
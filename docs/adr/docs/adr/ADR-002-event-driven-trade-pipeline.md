# ADR-002 — Event Driven Trade Pipeline

Status: Accepted  
Date: 2026-03-11

## Context

Direct service coupling created rigid architecture and reduced scalability.

## Decision

Trade execution produces TradeExecutedEvent.

Handlers process events independently:

- Position Handler
- PnL Handler
- Ledger Handler

Events stored in EventStore.

## Consequences

Positive:

- Loose coupling
- Event replay capability
- High-load scalability

Negative:

- Slightly more complex architecture
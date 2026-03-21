# ADR-011 — Financial Math Layer

Status: Accepted
Date: 2026-03-08

## Context

Financial calculations were scattered.

Multiple setScale calls caused rounding inconsistencies.

## Decision

Introduce FinancialMath utility.

Centralizes:

- multiply
- add
- subtract
- scale
- rounding

## Consequences

Consistent financial calculations across services.
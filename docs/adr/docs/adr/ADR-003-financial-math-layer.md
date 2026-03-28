# ADR-003 — Financial Math Layer

Status: Accepted  
Date: 2026-03-08

## Context

Financial calculations require strict precision and consistent rounding.

Different rounding strategies across services create accounting drift.

## Decision

All financial calculations must pass through FinancialMath layer.

## Consequences

Positive:

- Centralized financial logic
- Consistent rounding
- Accounting safety
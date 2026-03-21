# ADR-010 — PnL Reconciliation

Status: Accepted
Date: 2026-03-07

## Decision

Introduce reconciliation service verifying:

realised + unrealised = total PnL

Tolerance:

1e-7

Add database index:

(symbol, created_at)
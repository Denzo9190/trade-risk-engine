# ADR-008 — Signed Quantity Position Model

Status: Accepted
Date: 2026-03-05

## Context

Previous model used separate long and short quantities.

This created inconsistencies with PnL calculations.

## Decision

Use signed quantity model.

Positive → long  
Negative → short

Trade processing uses:

closingQty / openingQty logic.

## Consequences

Simplified position accounting.
Unified logic with realised PnL engine.
# ADR-012 — Trade Ledger

Status: Accepted
Date: 2026-03-09

## Context

System requires auditability and traceability.

## Decision

Introduce LedgerEntry entity
and LedgerService.

Every state-changing operation
must create a ledger record.

## Consequences

Full trade history traceability.
Supports future compliance and audit.
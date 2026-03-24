# ADR-007 — Immutable Ledger

Status: Accepted  
Date: 2026-03-09

## Context

Financial systems require full auditability.

Mutating accounting records can break traceability.

## Decision

Ledger entries are immutable.

Every accounting change creates a new entry.

Ledger acts as audit log for:

- position updates
- realised PnL
- system events

## Consequences

Positive:

- full audit trail
- regulatory-grade accounting
- easy forensic analysis

Negative:

- larger storage usage
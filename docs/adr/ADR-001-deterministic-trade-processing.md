# ADR-001 — Dual Knowledge System

Status: Accepted
Date: 2026-02

## Context

The project contains both:

1. architectural decisions
2. implementation knowledge and experiments

Mixing them in one archive creates confusion and reduces traceability.

## Decision

Split knowledge into two layers:

1. Governance layer
    - ADR (architectural decisions)
    - Decision Log
    - Monthly Snapshots

2. Knowledge layer
    - Engineering Handbook
    - Archive blocks
    - research notes

## Consequences

Architectural decisions remain small, explicit and traceable.
Reusable knowledge evolves independently from governance.
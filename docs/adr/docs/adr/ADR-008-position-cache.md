# ADR-008 — Position Cache

Status: Accepted  
Date: 2026-03-11

## Context

Position calculations require frequent reads.

Repeated database queries create latency under load.

## Decision

Introduce in-memory PositionCache.

Cache updated by event handlers when trades execute.

## Consequences

Positive:

- faster portfolio calculations
- reduced database load

Negative:

- need to maintain cache consistency
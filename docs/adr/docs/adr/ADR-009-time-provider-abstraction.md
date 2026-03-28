# ADR-009 — Time Provider Abstraction

Status: Accepted  
Date: 2026-03-18

## Context

Using Instant.now() directly prevents deterministic backtesting.

Backtests require simulated time.

## Decision

Introduce TimeProvider abstraction.

Implementations:

SystemTimeProvider  
BacktestTimeProvider

## Consequences

Positive:

- deterministic backtests
- controllable timeline

Negative:

- small architectural overhead
# ADR-005 — Signed Position Model

Status: Accepted  
Date: 2026-03-04

## Context

Trading systems must support both long and short positions.

Separate logic branches for long and short positions increase complexity
and introduce edge cases when position direction flips.

## Decision

Positions are represented using a signed quantity model:

positive quantity → long  
negative quantity → short

Example:

BUY 2 → position = +2  
SELL 3 → position = -1

## Consequences

Positive:

- unified logic for long and short
- correct handling of position flips
- simpler PnL calculations

Negative:

- requires careful validation when computing closing quantities
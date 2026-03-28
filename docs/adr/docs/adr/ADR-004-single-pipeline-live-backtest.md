# ADR-004 — Unified Pipeline for Live and Backtest

Status: Accepted  
Date: 2026-03-18

## Context

Separate pipelines for backtesting and live trading lead to logic divergence.

## Decision

Backtesting uses the same execution pipeline.

Only two abstractions differ:

TimeProvider  
MarketDataService

## Consequences

Positive:

- Backtest equals production logic
- Strategy validation reliability
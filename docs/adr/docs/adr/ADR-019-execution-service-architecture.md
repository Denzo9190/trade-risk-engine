# ADR-019: Execution Service Architecture

**Date:** 2026-04-02  
**Status:** adopted

## Context
After Day 29, the system generates and validates `TradingSignal`, but lacks a concrete execution layer. The `ExecutionService` only logged signals.

## Decision
Introduce `ExecutionAdapter` interface and `PaperExecutionAdapter` implementation.  
`ExecutionService` now:
- Receives a `TradingSignal`
- Delegates to `ExecutionAdapter` to obtain an `ExecutionResult`
- Persists the trade
- Publishes `TradeExecutedEvent`

This decouples the trading core from exchange specifics and allows easy addition of real adapters later.

## Consequences
- `ExecutionService` now depends on `ExecutionAdapter` (interface)
- `PaperExecutionAdapter` simulates full fills (deterministic, good for testing)
- Pipeline becomes: Signal → ExecutionService → ExecutionAdapter → Trade → Event
- Future adapters (Binance, Bybit) can be added without changing core logic.

## Benefits
- Clear execution boundary
- Support for paper trading
- Foundation for slippage, partial fills, order types
# ADR-013: Exchange Adapter Layer

Date: 2026-03-23  
Status: Adopted

## Context

The trading engine must remain exchange-agnostic in order to support
multiple venues and enable testing without real exchange connectivity.

Previously, trades were simulated internally without a dedicated
execution abstraction layer.

## Decision

Introduce an ExchangeAdapter interface responsible for placing orders
on an exchange.

ExecutionService orchestrates execution by:

1. receiving a signal
2. placing an order via ExchangeAdapter
3. persisting the trade
4. publishing TradeExecutedEvent

DTO objects are introduced:

- OrderRequest
- OrderResult

A SimulatedExchangeAdapter implementation is provided for testing.

## Consequences

Benefits:

- decouples engine from exchange APIs
- enables simulation and testing
- allows future adapters (Binance, Bybit)

Limitations:

Current implementation assumes full fill execution.

Future adapters may emit partial fills.

## Status

Adopted.
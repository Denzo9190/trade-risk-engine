# ADR-015 — Market Data Cache

Date: 2026-03-26  
Status: adopted

## Context

MarketDataEngine previously called MarketDataAdapter
on every price request.

This created two issues:

- potential overload of external APIs
- increased latency for trading components

## Decision

Introduce an in-memory **PriceCache**.

MarketDataEngine first checks the cache.
If the price is missing, it retrieves the value
from MarketDataAdapter and stores it in the cache.

## Consequences

Benefits:

- reduced number of external API calls
- lower latency for strategies and risk checks
- improved system stability

The cache is disabled in the **backtest profile**
using a NoOpPriceCache implementation to preserve
deterministic historical replay.
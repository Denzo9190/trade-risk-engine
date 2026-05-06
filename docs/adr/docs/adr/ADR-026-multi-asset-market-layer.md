# ADR-026: Multi-Asset Market Layer

**Date:** 2026-05-06  
**Status:** adopted

## Context

The market data layer was originally tied to a single symbol `BTCUSDT` via `MockMarketDataAdapter`.  
Support for other assets (`ETHUSDT`, `SOLUSDT`, etc.) was missing, and price access was non‑deterministic (fallback to adapter).  
This limited scalability and violated the single‑source‑of‑truth principle.

## Decision

Introduce a **Multi-Asset Market Layer** that provides:

1. **Explicit symbol model**
    - `Symbol` – value object for validation.
    - `AssetType` – future extension (CRYPTO, FOREX, STOCK).

2. **Centralised registry**
    - `SymbolRegistry` – list of supported symbols loaded from `application.yml` (`market.symbols`).
    - Enables dynamic configuration of assets without code changes.

3. **Cache‑only price access**
    - `PriceCache` – single source of truth.
    - `MarketDataEngine.getPrice()` **never calls the adapter**; if price is missing, it throws `PriceNotAvailableException`.
    - Guarantees determinism and consistent state across components.

4. **Batch price refresh**
    - `MarketDataAdapter.getPrices(Set<String>)` – bulk price fetch.
    - `MarketDataEngine.refreshAll()` – updates cache for all symbols in one call.
    - Validation during update (skip null, zero, negative prices).

5. **Stateless adapter**
    - `MockMarketDataAdapter` stores no internal state; generates prices on the fly.
    - Supports BTCUSDT, ETHUSDT, SOLUSDT with reasonable ranges.

6. **Managed feed engine**
    - `DefaultMarketDataFeedEngine` switched to Spring `@Scheduled` with configurable interval (`market.refresh-interval-ms`).
    - Removed manual `ExecutorService` and `start()`/`stop()` methods.

## Consequences

- System now supports **any number of assets** (just add symbol to config).
- Portfolio, risk engine, and strategies automatically work with new symbols.
- Price access is **deterministic** and **consistent** across calls.
- Simplified integration with real exchanges (adapter stateless, cache fed by scheduled refresh).

## Benefits

- **Scalability** – easy to add new assets.
- **Reliability** – no hidden adapter calls.
- **Configurability** – symbols and refresh interval in `application.yml`.
- **Testability** – `refreshAll` logic covered by unit tests.
- **Production‑ready** – exceptions, validation, logging.

## Related ADRs

- ADR-024 (Portfolio Aggregation Layer)
- ADR-025 (Unified Risk Engine)
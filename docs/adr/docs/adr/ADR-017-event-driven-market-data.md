# ADR-017: Event-Driven Market Data

**Date:** 2026-03-28  
**Status:** adopted

## Context
MarketDataEngine currently provides prices via pull model. Strategies and risk components must request price manually, which introduces latency and prevents reactive behavior.

## Decision
Introduce event-driven market data architecture. Price updates are published as `PriceUpdateEvent` through Spring event bus.  
`MarketDataFeedEngine` becomes the source of price updates and publishes events after updating the cache.

## Consequences
- Reactive strategy execution
- Compatibility with WebSocket streams
- Decoupled system components
- Foundation for real-time trading
- Slightly increased complexity (event listeners)

## Alternatives Considered
- Continue with pull-only – rejected because it doesn’t scale for real-time systems.
- Use separate message broker (Kafka) – overkill for current stage.
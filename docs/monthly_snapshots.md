# Monthly Snapshots

Purpose:
- Track real progress vs declared strategy
- Detect strategic drift
- Maintain synchronization between decisions and implementation

---

# Snapshot — March 2026

Baseline Version: 1.0.0

## Strategic Focus

- Stabilize project governance
- Restore backend anchor momentum
- Eliminate context-loss risk

---

# Current State

- Knowledge architecture rebuilt
- Several days lost to context recovery
- Backend progress temporarily slowed

---

# Identified Risks

- Momentum drop
- Emotional frustration spillover
- Urge to overcompensate by starting new initiatives

---

# Countermeasures

- Controlled 7-day acceleration
- No new initiatives
- Strict anchor-first execution discipline

---

# Execution Timeline

## Day 1–2

Infrastructure stabilization and REST foundation.

Completed:

- REST API for Trade entity
- PostgreSQL 16 connection
- Hibernate persistence verification
- DTO validation
- End-to-end POST/GET tested via Postman
- UTC timestamping confirmed

Validation results:

- Missing symbol → HTTP 400
- Empty symbol → HTTP 200

Action:

Validation rule corrected  
`@NotNull → @NotBlank`

Infrastructure fixes:

- Flyway/PostgreSQL compatibility resolved
- Runtime classpath mismatch fixed
- First repository integration test passed

---

## Day 4

Runtime discipline introduced.

Completed:

- Flyway runtime mismatch resolved
- Maven wrapper enforced
- Clean build verification rule introduced

Documentation:

- Engineering Handbook initiated

---

## March 1

Transition from CRUD to business logic.

Implemented:

- Unrealised PnL calculation
- BTC test price: **$63,500**
- TradeService refactoring
- Mockito-based unit tests

New endpoint:
GET /api/trades/unrealised-pnl


Symbol filter introduced to avoid cross-asset price misuse.

---

## March 2

Position Engine implemented.

Features:

- Trade aggregation
- Average price calculation
- Unrealised PnL calculation

---

## March 3

Realised PnL engine implemented (long positions).

Features:

- Closing quantity logic
- Order validation
- Correct financial math

---

## March 4

Short position support added.

Key upgrade:

- Signed quantity model
- Correct position flip handling

---

## March 5

Position service upgraded.

Changes:

- Signed quantity model
- Unrealised PnL for long and short positions
- Alignment between realised and unrealised engines

---

## March 6

Deterministic trade processing enforced.

Rule:

Trades processed in fixed order:
ORDER BY id


Purpose:

Reproducible financial results.

---

## March 7

PnL Reconciliation Engine implemented.

Audit identity introduced:
Realised PnL + Unrealised PnL = Total PnL


---

## March 8

Financial Math Layer implemented.

Purpose:

Centralized financial calculations.

Features:

- Unified rounding
- 8-digit precision
- Consistent scale handling

---

## March 9

Trade Ledger implemented.

Features:

- Immutable accounting log
- Full audit trail
- Traceability of all PnL changes

---

## March 10

Portfolio Engine implemented.

Capabilities:

- Aggregate all symbols
- Compute portfolio exposure
- Total realised / unrealised PnL
- Portfolio view endpoint

---

## March 11

Architecture upgrade.

Event-driven trade processing introduced.

New components:

- TradeExecutedEvent
- Event Store
- Position Event Handler
- PnL Event Handler
- Ledger Event Handler
- Position Cache
- Replay Service

Benefits:

- State reproducibility
- Component decoupling
- High-load readiness

---

## March 12

Risk Engine implemented.

Features:

- Pre-trade validation
- Trade size limits
- Position limits
- Portfolio exposure checks

---

## March 13

Risk engine modularized.

Architecture:
RiskRule interface


Rules discovered automatically by Spring.

---

## March 14

Strategy Engine implemented.

Capabilities:

- Strategy signal generation
- Strategy interface

Testing strategy:
RandomStrategy


---

## March 15

Signal Execution Layer implemented.

Pipeline:
Strategy → Signal → Risk Engine → Execution


---

## March 16

Strategy Runner implemented.

Capabilities:

- Scheduled strategy execution
- Autonomous strategy runtime

---

## March 17

Market Data Engine implemented.

Responsibilities:

- Provide price feed
- Supply strategies with decision price
- Mark-to-market for positions

Integration tests stabilized using H2.

---

# Research Extensions

## Trading Research Lab

- Telegram Intelligence v0.1 deployed
- ~375 channels tracked
- ~180k messages collected
- Idea extraction and ranking implemented

---

## Polymarket Agent

Experimental research branch started.

Focus:

- Rust
- LLM agents
- Market simulation

---

## March 18

Backtesting Engine implemented.

Core concept:

Live and backtest share the same pipeline.

Implementation:

- TimeProvider abstraction
- HistoricalMarketDataService
- BacktestMarketDataService
- BacktestEngine

Result:

Deterministic strategy replay on historical data.

---

## March 22

Risk improvement added.

Feature:

Price deviation validation.

RiskService now verifies:
signal price vs market price


Rejection reason includes:

- actual price
- deviation percentage
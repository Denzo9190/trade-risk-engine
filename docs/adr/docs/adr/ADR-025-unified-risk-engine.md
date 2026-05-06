# ADR-025: Unified Risk Engine v2

**Date:** 2026-04-26  
**Status:** adopted

## Context
Previous risk validation (`RiskService`) was monolithic and mixed trade‑level with portfolio‑level checks. Adding new rules required modifying core logic, making the system less maintainable and testable.

## Decision
Introduce a **unified risk engine** with:
- `RiskEvaluationContext` – immutable snapshot of all data needed for evaluation.
- `RiskRule` interface – each rule implements a single check.
- `RiskEngine` sequentially applies all registered rules.
- Rules are Spring components and can be configured via `application.yml`.

## Benefits
- Single entry point for all risk validation.
- Easy to add new rules (e.g., daily loss limit, volatility).
- Deterministic and unit‑testable rules.
- Portfolio‑level checks (exposure, drawdown) are integrated naturally.

## Consequences
- Old `RiskService` and `RiskCheckResult` are removed.
- `SignalExecutionService` now depends on `RiskEngine`.

## Related ADRs
- ADR-024 (Portfolio Aggregation Layer)
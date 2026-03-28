# ADR-006 — Modular Risk Engine

Status: Accepted  
Date: 2026-03-13

## Context

Risk validation rules evolve constantly.

Hardcoding rules inside RiskService would lead to rigid architecture
and difficult future expansion.

## Decision

Risk engine uses modular rule architecture.

Each rule implements:

RiskRule interface

Spring automatically discovers rules and executes them sequentially.

## Consequences

Positive:

- easy addition of new risk checks
- clear separation of risk policies
- scalable architecture

Negative:

- additional abstraction layer
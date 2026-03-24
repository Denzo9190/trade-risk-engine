# ADR-011 — Strategy Interface

Status: Accepted  
Date: 2026-03-14

## Context

Trading strategies must be pluggable and isolated from execution logic.

Hardcoding strategies would tightly couple strategy and engine layers.

## Decision

Define Strategy interface.

Strategies generate signals which are later executed by the engine.

Example strategies:

RandomStrategy  
Future algorithmic strategies

## Consequences

Positive:

- pluggable strategies
- separation between signal generation and execution
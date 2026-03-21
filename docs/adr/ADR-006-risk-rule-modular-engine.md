# ADR-006 — Runtime Discipline Rule

Status: Accepted
Date: 2026-02-28

## Context

Integration tests failed due to Flyway version mismatch
between build dependency graph and runtime classpath.

## Decision

Critical infrastructure dependencies must be overridden
via properties.

IDE build must delegate to Maven Wrapper.

Runtime verification via:

./mvnw clean verify

## Consequences

Eliminates build/runtime dependency mismatch.
# ADR-002: How the domain gets the current time

- Status: Accepted
- Date: 2026-10-17

## Context
`Event.create()` has a creation rule: the start must be in the future.
To check it, the domain needs "now". Tests must control "now", or a test
with a fixed date passes today and fails when that date is in the past.
Production must use the real time. The event start is a `ZonedDateTime`,
and `isAfter` compares instants on the timeline, so the time zone of
"now" does not change the result of this rule.

## Decision drivers
- Testability: tests choose "now" without waiting or sleeping.
- Safety: it should be hard to use the wrong time by mistake.
- Simplicity: few parameters and little wiring.
- No framework in the domain.

## Options

### a) Call `ZonedDateTime.now()` inside `create()`
- Good: The simplest code: no parameter, nothing to pass.
- Good: No caller can pass a wrong time.
- Bad: Tests cannot control "now", so tests with fixed dates expire.
- Bad: A hidden dependency: the signature does not show that the
  method reads the system clock.

### b) Inject a `java.time.Clock`
- Good: Tests pass `Clock.fixed(...)`; production passes a system clock.
- Good: The dependency is visible in the signature, and `Clock` is part
  of the JDK, so no library is needed.
- Bad: The clock must be passed to every method that needs time, and
  something must create it at startup.
- Bad: Easy to forget: `now()` without the clock still compiles, so the
  compiler cannot catch the mistake. Only review or a search can.

### c) Pass "now" as a parameter: `create(..., ZonedDateTime now)`
- Good: The easiest to test: `create()` becomes a pure function, and a
  test passes any instant without a `Clock`.
- Bad: Every caller decides what "now" is. A caller can pass a wrong
  value, such as yesterday, and the rule is silently weakened.
- Bad: The protection of the rule moves from the domain to every caller.

## Decision
We use option b: `create(..., Clock clock)` checks the start against
`ZonedDateTime.now(clock)`. Production creates one system clock at
startup; tests use `Clock.fixed`.

Testability rules out option a. We prefer b over c because the domain
keeps control of "now": in production there is one clock, so callers do
not choose the time themselves. We accept one more parameter and the
risk of calling `now()` without the clock.

## Consequences
- Good: Time-based tests are deterministic and never expire.
- Good: The same rule runs in tests and in production; only the clock
  changes.
- Bad: One more parameter on methods that need time. Later, Spring can
  provide the clock as a bean.
- Rule: in `src/main`, every call to `now()` takes the clock. Review
  checks this; later, a static analysis rule in CI can enforce it.
- Rule: inject only what tests must control. The random `EventId`
  decides no rule, so it is not injected.
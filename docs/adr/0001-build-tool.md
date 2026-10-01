# ADR-001: Build tool

- Status: Accepted
- Date: 2026-09-30

## Context
We are starting a new Java 25 project: an event ticketing system.
We need a build tool to compile the code, manage dependencies, and run
tests, both locally and in CI (GitHub Actions). Spring Boot will be
added later. There is one developer for now, and the repository is
public, so other people will read the build files.

The developer knows Maven well and has never used Gradle. This is also
a learning project: one goal is to practice tools that are new to me.

## Decision drivers
- Learning value: practice a tool I do not know yet.
- Build speed: with TDD, I run the tests many times per hour.
- Readability: reviewers must understand the build quickly.
- Support for Java 25 and Spring Boot.
- The same build version on my machine and in CI.

## Options

### Maven
- Good: I know it well. Simple, declarative and predictable, with
  strong conventions.
- Good: Full Spring Boot support. The Maven Wrapper pins the version.
- Bad: Slower on repeated builds; no build cache by default.
- Bad: Custom build logic needs plugins.
- Bad: No learning value for me.

### Gradle
- Good: Faster on repeated builds: incremental builds, build cache
  and a background daemon.
- Good: The Kotlin DSL is type-safe, so the IDE can autocomplete
  and check the build file.
- Good: Full Spring Boot support. The Gradle Wrapper pins the version.
- Good: New for me, so it has high learning value.
- Bad: A learning curve: the Kotlin DSL and the task model.
- Bad: Build scripts are code, so they can grow complex and become
  hard to review.

## Decision
We use Gradle 9.8 with the Kotlin DSL (`build.gradle.kts`) and the
Gradle Wrapper. Everyone, including CI, builds with `./gradlew`.

The main drivers are learning value and build speed. Maven is the
simpler choice for me today. We accept a steeper learning curve and
the risk of complex build scripts, because this project exists to learn.

## Consequences
- Good: Faster feedback during TDD, because Gradle skips work that
  did not change.
- Good: The same Gradle version everywhere, through the wrapper.
- Bad: Slower setup at the start, while I learn Gradle.
- Bad: Build logic is code, so it needs review like any other code.
- Rule: keep `build.gradle.kts` declarative, with only plugins,
  dependencies and settings. Any custom build logic needs a new ADR.
- Rule: commit the wrapper files (`gradlew`, `gradlew.bat`,
  `gradle/wrapper/`), and change the Gradle version only on purpose.
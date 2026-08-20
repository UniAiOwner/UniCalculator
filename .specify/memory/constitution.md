# UniCalculator Constitution (Bharat Pro Neumorphic Edition)

## Core Principles

### I. Mathematical Precision & Pure BigDecimal (NON-NEGOTIABLE)
- All financial, GST, percentage, and commercial calculations MUST use `java.math.BigDecimal` exclusively. Never use binary floating-point (`Double` or `Float`) for monetary values.
- Rounding mode must strictly adhere to Banker's Rounding (`RoundingMode.HALF_EVEN`) to eliminate cumulative financial bias.
- Statutory GST splits must prevent half-paisa leakage (`cgst = total / 2`, `sgst = total - cgst`).
- Formatting must strictly follow the Indian Vedic Lakh/Crore grouping system (`₹ 12,34,56,789.00`).

### II. Pure Neumorphic 3D Design System
- All interactive components (Buttons, Plates, LCD Wells, Sliders, Slabs) must adhere to the authentic $-45^\circ$ dual-directional lighting canvas model.
- Raised tactile surfaces (`CONVEX`) for clickable buttons; sunken dark cavities (`CONCAVE`) for numeric readouts and input wells.
- Active states must use dual-pass neon glow border shaders with instant low-latency tactile haptics (`PRIMITIVE_CLICK` 0.6f / `PRIMITIVE_TICK` 0.8f).

### III. Zero-Shortcut & Enterprise Clean Architecture
- `@Suppress` annotations to bypass Detekt, Spotless, or static code analysis are strictly forbidden.
- Clean 10-module hierarchy (`:app`, `:core:*`, `:feature:*`) with strictly acyclic dependency flow.
- Derived business metrics must be calculated dynamically via domain calculators, never hardcoded in static storage.

### IV. Spec-Driven & Test-First Development (TDD)
- No feature is built without a ratified specification (`spec.md`), technical plan (`plan.md`), and dependency-ordered tasks (`tasks.md`).
- Every calculation engine and formatting utility must have 100% passing automated unit tests before production deployment.

### V. Mandatory Physical Hardware Verification & Dev Log Protocol
- Every release must build cleanly (`./gradlew assembleDebug`), install on target hardware via ADB, and undergo visual verification.
- Every completed milestone must append a dated entry to `dev_logs.txt` and synchronize `walkthrough.md`.

## Technology Stack & Constraints
- **Platform**: Android 14+ (MinSdk 26, TargetSdk 35, CompileSdk 35)
- **UI Toolkit**: Jetpack Compose with Material3 & custom Neumorphic canvas shaders
- **Language**: Kotlin 2.0.21 on Java 21 JVM
- **Database**: Persistent SQLite with WAL mode and Coroutines IO dispatching
- **Build System**: Gradle 8.7.2 with Version Catalogs (`libs.versions.toml`)

## Governance
- This constitution supersedes all ad-hoc implementation suggestions.
- All code changes, refactors, and feature additions must comply with these laws.

**Version**: 1.0.0 | **Ratified**: 2026-08-19 | **Last Amended**: 2026-08-19
<!-- Signed by: Shoeb Ahmad -->


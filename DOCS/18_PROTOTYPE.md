# 🧪 18. PROTOTYPE SKELETON & MULTI-MODULE CODEBASE ARCHITECTURE
**Project**: UniCalculator (Bharat Pro Financial & GST Neumorphic Calculator)

---

## 1. Multi-Module Project Structure

```
UniCalculator/
├── build-logic/
│   └── convention/ (Gradle Convention Plugins for Compose, Android, Kotlin)
├── core/
│   ├── common/           (Base utilities, Dispatcher providers, Coroutines)
│   ├── model/            (Core domain models: TaxBreakdown, Denomination, HistoryItem)
│   ├── math-engine/      (BigDecimal Shunting-Yard expression evaluator & GST Engine)
│   ├── database/         (Room DB, Entities, DAOs, Migrations)
│   └── designsystem/     (Neumorphic Modifiers, Tactile Keypads, Colors, Typography)
├── feature/
│   ├── calculator/       (Standard Dual-Line Neumorphic Calculator UI & ViewModel)
│   ├── gst/              (Forward & Reverse GST Slabs, CGST/SGST/IGST breakdown UI)
│   ├── cash-tally/       (RBI Denomination Ledger, Words Converter, WhatsApp Slip)
│   ├── business-tools/   (Margin, Markup, Discount, Loan EMI & SIP Calculators)
│   └── history-tape/     (Audit Tape, Search, PDF / CSV Exporters)
└── app/                  (App entry point, Application class, Navigation Host)
```

---

## 2. Gradle Version Catalog (`gradle/libs.versions.toml`) Snapshot

```toml
[versions]
agp = "8.8.0"
kotlin = "2.1.0"
compose-bom = "2025.02.00"
room = "2.6.1"
hilt = "2.55"
coroutines = "1.10.1"

[libraries]
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "compose-bom" }
androidx-compose-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-compose-material3 = { group = "androidx.compose.material3", name = "material3" }
androidx-room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
androidx-room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
androidx-room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-compiler", version.ref = "hilt" }
```

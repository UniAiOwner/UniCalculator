# 📱 Walkthrough: Official Support Email & Commercial Action Buttons

## 🎯 Objective
Integrate the official publisher & support contact email (**`theunicoretech@gmail.com`**) and commercial action buttons into the **About UniCalculator** dialog in place of the public GitHub repository link.

---

## 🛠️ Changes Implemented

### 1. Publisher & Contact Support Integration
In `AboutUniCalculatorSheet.kt` (`core/designsystem/src/main/java/com/unicalculator/core/designsystem/component/AboutUniCalculatorSheet.kt`):
- **Publisher Card Layout**:
  - `🏢 Publisher & Engineering`
  - **`UniCore Technologies`** in emerald green bold
  - `✉️ theunicoretech@gmail.com` formatted in monospace
  - Attribution: `Architect & Lead Engineer: Shoeb Ahmad`
  - Purpose: `Engineered with mathematical precision for Financial, GST, Retail & Scientific calculations.`

### 2. Commercial Action Buttons
- **`✉️ Support` Button**: Triggers `mailto:theunicoretech@gmail.com` with auto-filled subject `[UniCalculator] Feedback & Support`.
- **`📤 Share App` Button**: Launches Android standard share sheet (`Intent.ACTION_SEND`) to share UniCalculator with peers.
- **Copyright & Footnote**: `© 2026 UniCore Technologies • All rights reserved.`.

---

## 🧪 Hardware Verification (Realme RMX3998)

| About Dialog - Publisher & Workstations | About Dialog - Actions & Footnote |
| :---: | :---: |
| ![About Top](file:///home/uniai/.gemini/antigravity-cli/brain/7e798e0a-32ad-4aa2-9c14-cbac4a0f4f41/345_about_top_final.png) | ![About Bottom](file:///home/uniai/.gemini/antigravity-cli/brain/7e798e0a-32ad-4aa2-9c14-cbac4a0f4f41/346_about_bottom_final.png) |

---

## 📌 Verification Status
- ✅ Unit Tests: `./gradlew testDebugUnitTest` ➔ **PASSED** (0 failures).
- ✅ Build: `./gradlew :app:assembleDebug` ➔ **BUILD SUCCESSFUL**.
- ✅ Hardware Tested: Verified live on physical Realme phone.

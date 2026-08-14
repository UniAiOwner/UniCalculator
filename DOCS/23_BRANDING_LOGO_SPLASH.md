# 🎨 23. BRANDING, APP ICON, LOGO & SPLASH SCREEN SYSTEM
**Project**: UniCalculator (Bharat Pro Financial & GST Neumorphic Calculator)

---

## 1. ❓ Backend & Cloud Architecture Decision: Do We Need Firebase / Backend?

### 💡 Recommendation: **100% Offline-First Architecture (NO Backend / Firebase Needed for v1.0)**

| Dimension | Offline-First (Room SQLite) [RECOMMENDED] | Cloud Backend / Firebase |
| :--- | :--- | :--- |
| **Startup Latency** | **< 60ms** (Instant cold launch) | 400ms - 1500ms (Network auth / token fetch) |
| **Calculation Speed** | **< 1ms** (Instant local RAM/CPU) | Dependent on network latency |
| **Merchant Privacy** | **100% Secure** (No financial data leaves phone) | Privacy concerns over cash sales & turnover |
| **No-Internet Reliability** | **Works in basements, rural mandis, offline** | Fails or shows sync spinner when offline |
| **Server & Maintenance Cost**| **₹0 / $0 Forever** (Zero infra maintenance) | Monthly Firebase / AWS bill |
| **App Permissions** | **Zero intrusive permissions** (No INTERNET needed)| Requires internet & network state permissions |

> [!TIP]
> **Architectural Verdict**:
> For **Version 1.0**, UniCalculator will be built as a **pure offline-first utility**.
> In **Version 2.0 (Optional Pro Add-on)**, we can provide an optional, opt-in **Google Drive AppData Backup** or **Firebase Firestore Cloud Sync** for businesses with multiple billing counters.

---

## 2. 📱 Adaptive App Icon Architecture (Android 13+ Themed Icons Compatible)

```
┌────────────────────────────────────────────────────────┐
│               ADAPTIVE APP ICON MATRIX                 │
│                                                        │
│   ╭───────────────── [ 512 x 512 ] ─────────────────╮  │
│   │                                                 │  │
│   │    ╭────────────── [ FOREGROUND ] ─────────╮    │  │
│   │    │                                       │    │  │
│   │    │         ╭───────────────╮             │    │  │
│   │    │         │       ₹       │  <-- 3D     │    │  │
│   │    │         │   [ +  - ]    │      Rupee  │    │  │
│   │    │         │   [ ×  ÷ ]    │      Glyph  │    │  │
│   │    │         ╰───────────────╯             │    │  │
│   │    │                                       │    │  │
│   │    ╰───────────────────────────────────────╯    │  │
│   │                                                 │  │
│   │   [ BACKGROUND: Soft 3D Neumorphic Slate Plate] │  │
│   ╰─────────────────────────────────────────────────╯  │
└────────────────────────────────────────────────────────┘
```

### 2.1 Color Tokens:
- **Foreground Glyph**: Indian Rupee Emerald Green (`#00875A`) with Saffron Amber accent (`#FF9933`).
- **Background Base**: Soft Tactile Titanium Slate (`#E8E5DF` Light / `#1E2228` Dark).
- **Shadow Offset**: Light highlight (`#FFFFFF` 75% opacity) top-left, Deep shadow (`#000000` 25% opacity) bottom-right.

---

## 3. 🏷️ App Logo & Typography Wordmark

- **Brand Name**: `UniCalculator`
- **Tagline**: `Bharat's Pro Financial & GST Calculator` / `भारत का अपना स्मार्ट कैलकुलेटर`
- **Primary Typeface**: `Plus Jakarta Sans` Bold (Wordmark) + `JetBrains Mono` (Numeral Accent).

---

## 4. 🚀 Android 12+ Splash Screen API Specification

UniCalculator implements the official Android 12+ `androidx.core.splashscreen.SplashScreen` API with zero cold-start delay:

```kotlin
// MainActivity.kt - Seamless Splash Screen Transition
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        
        // Instant exit animation without artificial sleep delays
        splashScreen.setOnExitAnimationListener { splashScreenViewProvider ->
            val slideUp = ObjectAnimator.ofFloat(
                splashScreenViewProvider.view,
                View.TRANSLATION_Y,
                0f,
                -splashScreenViewProvider.view.height.toFloat()
            )
            slideUp.interpolator = FastOutSlowInInterpolator()
            slideUp.duration = 200L
            slideUp.doOnEnd { splashScreenViewProvider.remove() }
            slideUp.start()
        }
        
        setContent {
            UniCalculatorTheme {
                UniCalculatorApp()
            }
        }
    }
}
```

---

## 5. 🎨 Vector Drawables & Asset Specifications

### 5.1 App Icon Vector (`res/drawable/ic_launcher_foreground.xml`)
```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <!-- Soft Neumorphic Outer Rounded Badge -->
    <path
        android:fillColor="#00875A"
        android:pathData="M30,20h48c5.52,0 10,4.48 10,10v48c0,5.52 -4.48,10 -10,10H30c-5.52,0 -10,-4.48 -10,-10V30c0,-5.52 4.48,-10 10,-10z" />
    <!-- ₹ Currency & Calculator Arithmetic Symbol -->
    <path
        android:fillColor="#FFFFFF"
        android:pathData="M44,34h20v4h-8.5c2.8,1.2 4.7,3.5 5.2,6.5H64v4h-3.1c-0.8,4.5 -4.3,8 -9.4,8.8l12.5,14.7H57L45.5,57.5V57h4.8c4,0 7.2,-2.7 7.8,-6.5H44v-4h14.5c-0.5,-2.2 -2.5,-3.8 -5.5,-3.8H44V34z" />
    <!-- Saffron Accent Dot / GST Indicator -->
    <path
        android:fillColor="#FF9933"
        android:pathData="M66,66m-4,0a4,4 0,1 1,8 0a4,4 0,1 1,-8 0" />
</vector>
```

### 5.2 App Icon Background (`res/drawable/ic_launcher_background.xml`)
```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <path
        android:fillColor="#E8E5DF"
        android:pathData="M0,0h108v108h-108z" />
</vector>
```

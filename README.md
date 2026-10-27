# ⚡ HealthHub (2026_HealthHub Command Center)

Executive Metabolic, Nutritional & Biomechanical Command Center for Android (Jetpack Compose + Material 3 + Room + Health Connect + Gemini 3.8 Flash).

## 🛠️ Build Toolchain & Plugin Versions (Latest 2026 Stack)
| Component | Version |
| :--- | :--- |
| **Gradle** | `9.8.0` (`gradle/wrapper/gradle-wrapper.properties`) |
| **Android Gradle Plugin (AGP)** | `9.1.1` (`com.android.application`) |
| **Kotlin / Compose Compiler Plugin** | `2.2.10` (`org.jetbrains.kotlin.plugin.compose`) |
| **Google DevTools KSP** | `2.3.5` (`com.google.devtools.ksp`) |
| **Google Services Gradle Plugin** | `4.5.0` (`com.google.gms.google-services`) |
| **Secrets Gradle Plugin** | `2.0.1` (`com.google.android.libraries.mapsplatform.secrets-gradle-plugin`) |
| **Compile / Target SDK** | `API 36.1` (`minSdk = 26`, `JDK 21` runtime) |

## ✨ Key Features
1. **Priority Daily Command Center (`CommandScreen`)**:
   - Step-by-step daily priority guide (**"ต้องดูอะไรก่อน-หลัง"**) derived from the `2026_HealthHub` Google Sheet telemetry.
   - Real-time **Energy Balance Equation** ($\text{Net} = \text{Intake} - (\text{BMR } 1608 + \text{NEAT } 400 + \text{TEF } 10\% + \text{Active})$).
2. **Gemini 3.8 Flash Camera Meal Analyzer (`AiSnapScreen`)**:
   - Capture meal photos via the device camera or photo picker and send them to the **Gemini API (`gemini-3.8-flash`)** with a structured JSON `responseSchema`.
   - Automatic **Atwater 4-4-9 Macronutrient Calculation** ($\text{Protein } 4\text{ kcal/g} + \text{Carbs } 4\text{ kcal/g} + \text{Fat } 9\text{ kcal/g}$), itemized ingredient macro table, net carbs, TEF, and Macro Quality Grade.
3. **7-Day Weekly Macronutrient Breakdown Chart (`WeeklyMacronutrientChart`)**:
   - Interactive Recharts-style **Stacked Bar** and **Composed Area + Line** chart visualizing 7-day Protein, Carbs, and Fat intake alongside the $120\text{g}$ Protein target line.
4. **Bidirectional Health Connect Sync (`GoogleHealthSyncManager`)**:
   - Reads daily caloric expenditure (`ActiveCaloriesBurnedRecord`, `TotalCaloriesBurnedRecord`) and pushes logged meal nutrients (`NutritionRecord`) to Android Health Connect.
5. **Tomorrow Action Planner & Gemini Health Coach**:
   - Personalized next-day meal, hydration, sodium-flush, and training prescriptions plus interactive Gemini Coach Q&A.

## 🚀 Building After Pulling from GitHub

1. **Prepare `.env` & Debug Keystore**:
   ```bash
   cp .env.example .env
   base64 -d debug.keystore.base64 > debug.keystore
   ```
2. **Optional — Configure Gemini API Key**:
   - In AI Studio: add `GEMINI_API_KEY` in the **Secrets** panel.
   - Locally: uncomment `GEMINI_API_KEY=your_api_key` in `.env` (which is gitignored).
3. **Compile & Run Unit Tests**:
   ```bash
   gradle assembleDebug
   gradle :app:testDebugUnitTest
   ```

# PASIT HEALTH & PERFORMANCE COMMAND CENTER (2026_HealthHub)

An executive-tier, mobile-first Android application designed with a **Polite Crystal Dark (Smoked Glassmorphism)** aesthetic, dedicated to comprehensive physical performance monitoring, daily energy balance tracking matching Google Apps Script v4.0 mathematical rules, multimodal AI health logging with Gemini 2.5 Flash, multi-scale biomechanical insights, and gym microcycle execution.

---

### User Review & Critical Decisions

> [!IMPORTANT]
> The following architectural decisions were aligned during the initial consultation:
> - **Google Apps Script Sync**: Pre-configured default Web App endpoint (`2026_HealthHub`) with an in-app editable settings modal for testing or custom deployments.
> - **AI Multimodal Snap Processing**: Fully automatic category classification using Gemini 2.5 Flash vision. Uploaded photos of meals, Huawei Watch screenshots, or Visbody/Evolt body scans will be automatically classified into `NUTRITION`, `WORKOUT`, `BODYCOMP`, or `SLEEP` without requiring pre-selection.
> - **Data Persistence**: Offline-first Room Database caching with reactive `Flow` observation in ViewModels, paired with a resilient sync pipeline for external Google Apps Script updates.

---

## 1. Overview & Core Concept

**Pasit Health & Performance Command Center (2026_HealthHub)** is built for high-performance athletes and executives who need precision biomechanics, nutritional telemetry, and recovery analytics in one place.

- **Primary Persona**: Pasit Patthanasethanon — active trail/road runner (Bang Phra, Khao Chalak), heavy lifter at Jetts Fitness, and reformer Pilates practitioner tracking anterior pelvic tilt (APT), shin splint prevention, and deep sleep HRV rebound.
- **Mathematical Grounding**: Adheres strictly to the Google Apps Script v4.0 metabolic formula:
  - Fixed Baseline: $\text{BMR} = 1608\text{ kcal}$, $\text{NEAT} = 400\text{ kcal}$, Protein Target $= 120\text{ g/day}$, Sodium Limit $= 2300\text{ mg/day}$.
  - Thermic Effect of Food: $\text{TEF} = \text{round}(\text{dailyIntake} \times 0.10)$.
  - Total Daily Energy Expenditure: $\text{TDEE} = 1608 + 400 + \text{TEF} + \text{activeWorkoutBurn}$.
  - Net Energy Balance: $\text{Net} = \text{dailyIntake} - \text{TDEE}$.
    - $\le -150\text{ kcal}$: 🔥 *Calorie Deficit (ลดไขมัน)*
    - $-150\text{ to } +150\text{ kcal}$: ⚖️ *Maintenance (สมดุลพลังงาน)*
    - $> +150\text{ kcal}$: ⚡ *Calorie Surplus (พลังงานเกิน)*

---

## 2. User Experience & Visual Design

### Visual Identity & Theme (Polite Executive Crystal Dark)
- **Obsidian Smoked Canvas**: Radial and linear gradient surfaces transitioning from Deep Obsidian (`#070A12`) to Navy Obsidian (`#0B101D`).
- **Smoked Crystal Glass Cards**: Semi-transparent midnight surfaces (`Color(0x9E121928)`), specular 1dp top border highlight (`Color(0x2BFFFFFF)`), subtle outer rim stroke (`Color(0x17FFFFFF)`), and smooth `22.dp` rounded corners.
- **Palette & Accents**:
  - Primary Crystal Ice: `#7DD3FC`
  - Deficit / Optimal Recovery: Emerald Mist `#34D399`
  - Maintenance / Caution: Warm Champagne `#FBBF24`
  - Surplus / High Strain: Soft Coral Rose `#FB7185`
  - Subdued Slate: `#94A3B8`
- **Typography & Localization**: Dual-language typography pairing Inter and Sarabun with Thai/English font scales for clean executive readability.
- **Floating Crystal Bottom Navigation**: Translucent pill navigation with active glow indicators across 4 key operational tabs.

### Tab Navigation Hierarchy

```
┌────────────────────────────────────────────────────────────────────────┐
│ PASIT HEALTH & PERFORMANCE COMMAND CENTER (2026_HealthHub)             │
│ Top App Bar: Date Selector | Sync Indicator (GAS) | Settings Modal     │
├────────────────────────────────────────────────────────────────────────┤
│                                                                        │
│  [Tab 1: 📊 Command]    [Tab 2: 📸 AI Snap]   [Tab 3: 📈 Insights]    │
│  7 Executive KPI Cards   Camera / Gallery      Multi-Scale Trends      │
│  7-Day Balance Table     Gemini 2.5 Flash      Source-Isolated Charts  │
│  Today's Meals Table     4-Category Review     4-Pillar Correlations   │
│  Quick Action Drawer     One-Click GAS Sync    14-Day Context Exporter │
│                                                                        │
│                      [Tab 4: 💪 Gym & Plan]                            │
│                      7-Day Microcycle (P-01 to P-07)                   │
│                      Progressive Set Logger & Coach Cues               │
│                      (Squat 121kg, Leg Press 375.2kg)                  │
│                                                                        │
├────────────────────────────────────────────────────────────────────────┤
│ Floating Crystal Navigation Bar: [📊 Command] [📸 AI Snap] [📈 Trends] [💪 Gym] │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 3. Key Feature Specifications across 4 Tabs

### Tab 1: 📊 Command Center (Executive Dashboard)
- **7 Executive KPI Cards**:
  1. 🍽️ **Today's Intake**: Calories kcal, Carbohydrate, and Fat breakdown.
  2. 🔥 **Total Expenditure (TDEE)**: Real-time calculation (`1608 + 400 + TEF + Active Burn`).
  3. ⚖️ **Net Energy Balance**: Colored badge (Deficit / Maintenance / Surplus) with progress ring.
  4. 🥩 **Protein Intake**: Grams consumed, percentage of 120g target, plus daily Sodium gauge (limit 2300mg).
  5. 🏃 **Active Workout**: Active kcal burned, workout type (Trail Run, Heavy Squats, Pilates), and duration.
  6. 🧍 **Latest Body Weight**: Current kg with device source chip (Huawei Scale, Visbody 3D, or Evolt 360).
  7. 📐 **Physique & Recovery Metrics**: Skeletal Muscle Mass (SMM kg), Body Fat %, Visbody APT (11.7 cm), and Sleep HRV (84 ms).
- **7-Day Energy Balance Summary Table**: Daily row-by-row breakdown of Intake, TDEE, Net, and Status.
- **Today's Meals Breakdown Table**: Meal timings, items, calories, protein, and sodium.

### Tab 2: 📸 AI Multimodal Snap & Quick Log
- **Multimodal Capture**: Camera snap or photo gallery picker using zero-permission Android Photo Picker (`ActivityResultContracts.PickVisualMedia`).
- **Gemini 2.5 Flash Integration**:
  - High-precision multimodal analysis via direct REST API with `InlineData` base64 encoding.
  - Automatically classifies input images into one of 4 categories:
    1. `🥗 NUTRITION`: Calories, protein, carbs, fat, sodium, sugar, fiber, BCAA notes.
    2. `🏃 WORKOUT`: Workout type, distance, time, active kcal, pace, D+ elevation, avg/max HR, cadence, vertical oscillation, ground contact time (GCT), left/right balance.
    3. `⚖️ BODYCOMP`: Device source (Huawei Scale, Visbody 3D, Evolt 360), weight, body fat %, muscle mass, water %, BMR, visceral fat, APT cm, shoulder imbalance degrees.
    4. `😴 SLEEP`: Total duration, score, deep sleep %, REM %, avg sleep HR, avg sleep HRV (ms).
- **Verification & One-Click Save**: Pre-filled editable form allowing manual fine-tuning before committing to the Room database and initiating the Google Apps Script Web App sync (`2026_HealthHub`).

### Tab 3: 📈 Insights & Multi-Scale Trends
- **Source-Isolated Trend Views**: Isolated tabs (`Huawei Scale`, `Visbody 3D APT`, `Evolt 360`, `Sleep HRV & HR`, `Running & Elevation D+`) preventing cross-device calibration contamination.
- **4-Pillar Clinical Correlation Cards**:
  - *Pilates & Deep Sleep vs HRV Rebound*: 89–92 ms post-reformer vs 53–57 ms after late nights/hard tempo.
  - *Evening Sodium vs Body Water Spike*: Sodium >3,200mg triggering next-day water retention (57.7%).
  - *Shin Splint Biomechanics*: Tibialis anterior raises & single-leg calf raises protecting against high strain in carbon plated sandals during Khao Chalak trail descents.
  - *Energy Deficit vs SMM Preservation*: Maintaining >1.8g/kg protein during calorie deficit to prevent skeletal muscle catabolism.
- **Gemini 14-Day Context Exporter**: Formats all 14-day telemetry into clean prompt-ready markdown and copies to clipboard with one tap.

### Tab 4: 💪 Gym Checklist & 7-Day Master Plan
- **7-Day Microcycle Schedule**:
  - `P-01 (Sun)`: Active Recovery & Mobility.
  - `P-02 (Mon)`: Reformer Pilates for Anterior Pelvic Tilt (APT) correction.
  - `P-03 (Tue)`: Heavy Leg Day & Shin Splint Prehab (Squat 11kg to 121kg, Leg Press 375.2kg with 75.2kg sled).
  - `P-04 (Wed)`: Upper Body Hypertrophy @ Jetts Fitness.
  - `P-05 (Thu)`: Speed / Tempo Intervals (Interval pace + cadence work).
  - `P-06 (Fri)`: Zone 2 Easy Aerobic Flush Run.
  - `P-07 (Sat)`: Long Trail Run Bang Phra / Khao Chalak.
- **Progressive Set Checklist**: Interactive set checkboxes, weight/rep adjustments, warm-up to top set progression, and coach cues.

---

## 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────────────────────┐
│                        Jetpack Compose UI                              │
│   DashboardScreen | SnapScreen | TrendsScreen | GymScreen | Settings   │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ UI State & User Intents
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                         HealthHubViewModel                             │
│       StateFlow<CommandUiState>, StateFlow<SnapUiState>, etc.          │
└───────────────────┬───────────────────────────────┬────────────────────┘
                    │                               │
                    ▼                               ▼
┌───────────────────────────────────────┐ ┌──────────────────────────────┐
│        Room Local Repository          │ │    Network Data Providers    │
│  - DailyLogDao & MealDao              │ │  - GeminiApiService          │
│  - WorkoutDao & BodyCompDao           │ │    (Multimodal Flash 2.5)    │
│  - SleepDao & GymPlanDao              │ │  - GoogleAppsScriptSync      │
│  (Offline-first cache with Flow)      │ │    (2026_HealthHub Webhook)  │
└───────────────────────────────────────┘ └──────────────────────────────┘
```

### Entity Schemas (Room SQLite)
- `DailyLogEntity`: `date`, `totalCalories`, `tef`, `tdee`, `netBalance`, `proteinGrams`, `sodiumMg`, `weightKg`, `notes`.
- `MealEntity`: `id`, `date`, `mealType`, `name`, `calories`, `protein`, `carbs`, `fat`, `sodium`, `timestamp`.
- `WorkoutEntity`: `id`, `date`, `title`, `category`, `activeKcal`, `durationMinutes`, `distanceKm`, `avgHr`, `elevationDPlus`, `cadence`, `gctMs`.
- `BodyCompEntity`: `id`, `date`, `sourceDevice`, `weightKg`, `bodyFatPct`, `smmKg`, `waterPct`, `visceralFat`, `aptCm`, `shoulderImbalanceDeg`.
- `SleepEntity`: `id`, `date`, `durationHours`, `sleepScore`, `deepSleepPct`, `avgSleepHr`, `avgHrvMs`.
- `GymExerciseEntity`: `id`, `dayCode`, `exerciseName`, `targetSets`, `topWeightKg`, `completedSets`, `coachCue`.
- `AppSettingsEntity`: `id`, `gasWebhookUrl`, `autoSyncEnabled`, `themePreference`.

---

## 5. Verification Plan

1. **Gradle Build & Dependency Verification**:
   - Run `compile_applet` to ensure Room annotations, KSP generation, Kotlin Serialization, and Retrofit compile cleanly.
2. **Mathematical Correctness**:
   - Verify that BMR (1608) + NEAT (400) + TEF (10% intake) + Active Burn yields exact TDEE.
   - Verify Net = Intake - TDEE, with correct color coding and badge status.
3. **Multimodal AI Pipeline**:
   - Test image loading, prompt construction, and JSON parsing logic.
   - Fallback error handling if API key is unconfigured or network is unavailable.
4. **Interactive UI Verification**:
   - Seamless tab switching between Command, AI Snap, Insights, and Gym Checklist.
   - Responsive layouts respecting Android status and navigation bar insets.

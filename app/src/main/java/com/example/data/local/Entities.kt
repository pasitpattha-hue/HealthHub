package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.model.HealthConstants

/**
 * 1. Daily Logs Entity: Core metabolic telemetry matching Google Apps Script v4.0 rules.
 * Supports offline-first persistence with sync status and timestamps.
 */
@Entity(
    tableName = "daily_logs",
    indices = [Index(value = ["date"], unique = true)]
)
data class DailyLogEntity(
    @PrimaryKey val date: String,                   // YYYY-MM-DD
    val intakeKcal: Int = 0,                         // Total caloric intake
    val carbsG: Int = 0,                             // Carbohydrates in grams
    val fatG: Int = 0,                               // Dietary fat in grams
    val proteinG: Int = 0,                           // Protein in grams (target >= 120g)
    val sodiumMg: Int = 0,                           // Sodium in mg (limit 2300mg)
    val sugarG: Int = 0,                             // Dietary sugar in grams
    val activeBurnKcal: Int = 0,                     // Workout active burn
    val workoutTitle: String = "พัก (Rest Day)",      // Workout session descriptor
    val weightKg: Double = 74.6,                     // Scale weight in kg
    val weightSource: String = "Visbody 3D",         // Visbody 3D / Huawei Scale / Evolt 360
    val smmKg: Double = 26.7,                        // Skeletal Muscle Mass in kg
    val bodyFatPct: Double = 35.4,                   // Body Fat %
    val waterPct: Double = 52.4,                     // Total Body Water %
    val aptCm: Double = 11.7,                        // Anterior Pelvic Tilt in cm
    val sleepHrvMs: Int = 84,                        // Overnight rMSSD sleep HRV in ms
    val sleepScore: Int = 84,                        // Sleep score 0-100
    val isSynced: Boolean = true,                    // Offline sync status flag
    val updatedAt: Long = System.currentTimeMillis() // Epoch millis for conflict resolution
) {
    // Metabolic formulas (Google Apps Script v4.0)
    val tef: Int get() = Math.round(intakeKcal * 0.10f)
    val tdee: Int get() = HealthConstants.BMR + HealthConstants.NEAT + tef + activeBurnKcal
    val netBalance: Int get() = intakeKcal - tdee
}

// Backward-compatible typealias for existing views
typealias DailySummaryEntity = DailyLogEntity


/**
 * 2. Nutrition Entries Entity: Individual food items, meals, snacks, and beverages.
 * Indexed by date for fast reactive Flow queries and offline caching.
 */
@Entity(
    tableName = "nutrition_entries",
    indices = [Index(value = ["date"])]
)
data class NutritionEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,                               // YYYY-MM-DD
    val time: String = "12:00",                     // HH:mm
    val mealType: String,                           // อาหารจานหลัก / ของว่าง / เครื่องดื่ม
    val name: String,                               // Food title / description
    val calories: Int,                              // Caloric value
    val proteinG: Double = 0.0,                     // Protein grams
    val carbsG: Double = 0.0,                       // Carbs grams
    val fatG: Double = 0.0,                         // Fat grams
    val sodiumMg: Int = 0,                          // Sodium mg
    val sugarG: Double = 0.0,                       // Sugar grams
    val fiberG: Double = 0.0,                       // Dietary fiber grams
    val notes: String = "",                         // Optional context
    val photoUri: String? = null,                   // Optional attached photo URI
    val isSynced: Boolean = true,                   // Offline sync status
    val createdAt: Long = System.currentTimeMillis()// Creation timestamp
)

// Backward-compatible typealias for existing views
typealias MealEntity = NutritionEntryEntity


/**
 * 3. Microcycle Workouts Entity: 7-day periodization microcycle (P-01 to P-07)
 * with progressive set tracking (warm-up -> top set) and coach cues.
 */
@Entity(
    tableName = "microcycle_workouts",
    indices = [Index(value = ["dayCode"])]
)
data class MicrocycleWorkoutEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dayCode: String,                            // P-01 to P-07
    val dayName: String,                            // Sunday, Monday, etc.
    val sessionTitle: String,                       // Heavy Leg Day, Reformer Pilates, etc.
    val exerciseName: String,                       // Barbell Back Squat, Leg Press, etc.
    val warmUpSets: String = "",                    // E.g. "11kg x 10, 41kg x 8, 61kg x 5"
    val workingSets: String = "",                   // E.g. "Top Set: 121kg x 5 reps"
    val topWeightKg: Double = 0.0,                  // Peak resistance (e.g. 121.0 or 375.2)
    val completedSets: Int = 0,                     // Sets finished by athlete
    val totalSets: Int = 3,                         // Target working sets
    val coachCue: String = "",                      // Form & biomechanics cue
    val isCompleted: Boolean = false,               // Exercise completion status
    val loggedDate: String? = null,                 // Optional execution date
    val rpe: Double = 8.0,                          // Rate of perceived exertion
    val isSynced: Boolean = true,                   // Offline sync status
    val updatedAt: Long = System.currentTimeMillis()// Last modified timestamp
)

// Backward-compatible typealias for existing views
typealias GymExerciseEntity = MicrocycleWorkoutEntity


/**
 * Workout Telemetry Entity: GPS runs, trail climbs, and aerobic sessions.
 */
@Entity(
    tableName = "workouts",
    indices = [Index(value = ["date"])]
)
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val title: String,
    val category: String,
    val activeKcal: Int,
    val durationMin: Int,
    val distanceKm: Double,
    val avgHr: Int,
    val elevationDPlus: Int,
    val cadence: Int,
    val gctMs: Int,
    val notes: String = "",
    val isSynced: Boolean = true
)


/**
 * Body Composition Entity: Multi-scale biometric scans (Visbody 3D, Huawei Scale, Evolt 360).
 */
@Entity(
    tableName = "body_comp",
    indices = [Index(value = ["source"])]
)
data class BodyCompEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val source: String,
    val weightKg: Double,
    val bodyFatPct: Double,
    val smmKg: Double,
    val waterPct: Double,
    val bmrKcal: Int = HealthConstants.BMR,
    val visceralFat: Int = 7,
    val aptCm: Double = 11.7,
    val shoulderImbalanceDeg: Double = 1.2,
    val isSynced: Boolean = true
)


/**
 * Sleep & Recovery Entity: Overnight HRV, sleep score, and sleep architecture.
 */
@Entity(
    tableName = "sleep_records",
    indices = [Index(value = ["date"])]
)
data class SleepEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val durationHours: Double,
    val score: Int,
    val deepSleepPct: Int,
    val avgHr: Int,
    val avgHrvMs: Int,
    val isSynced: Boolean = true
)


/**
 * App Settings Entity: Key-value settings persistence.
 */
@Entity(tableName = "settings")
data class SettingEntity(
    @PrimaryKey val key: String,
    val value: String
)

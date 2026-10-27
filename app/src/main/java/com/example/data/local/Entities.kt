package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.model.HealthConstants
import kotlin.math.roundToInt

/**
 * Room Entity for Daily Metabolic & Biomechanical Logs (`daily_logs`).
 */
@Entity(
    tableName = "daily_logs",
    indices = [Index(value = ["date"], unique = true)]
)
data class DailyLogEntity(
    @PrimaryKey
    val date: String,
    val intakeKcal: Int = 0,
    val carbsG: Int = 0,
    val fatG: Int = 0,
    val proteinG: Int = 0,
    val sodiumMg: Int = 0,
    val activeBurnKcal: Int = 0,
    val workoutTitle: String = "พัก (Rest Day)",
    val weightKg: Double = 74.6,
    val weightSource: String = "Visbody 3D",
    val smmKg: Double = 26.7,
    val bodyFatPct: Double = 35.4,
    val aptCm: Double = 11.7,
    val sleepHrvMs: Int = 84,
    val sleepScore: Int = 85,
    val sugarG: Double = 0.0,
    val isSynced: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis()
) {
    val tefKcal: Int
        get() = (intakeKcal * HealthConstants.TEF_FACTOR).roundToInt()

    val tdeeKcal: Int
        get() = HealthConstants.BMR_KCAL + HealthConstants.NEAT_KCAL + tefKcal + activeBurnKcal

    val netEnergyBalanceKcal: Int
        get() = intakeKcal - tdeeKcal
}

typealias DailySummaryEntity = DailyLogEntity

/**
 * Room Entity for Nutrition / Meal Entries (`nutrition_entries`).
 */
@Entity(
    tableName = "nutrition_entries",
    indices = [Index(value = ["date"]), Index(value = ["mealType"])]
)
data class NutritionEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,
    val mealType: String = "Lunch",
    val name: String,
    val calories: Int,
    val proteinG: Double,
    val carbsG: Double = 0.0,
    val fatG: Double = 0.0,
    val sodiumMg: Int = 0,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isSynced: Boolean = true
)

typealias MealEntity = NutritionEntryEntity

/**
 * Room Entity for 7-Day Microcycle Workouts (`microcycle_workouts`).
 */
@Entity(
    tableName = "microcycle_workouts",
    indices = [Index(value = ["dayCode"])]
)
data class MicrocycleWorkoutEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dayCode: String, // P-01 to P-07
    val dayName: String,
    val sessionTitle: String,
    val exerciseName: String,
    val setsReps: String = "3 Sets × 10 Reps",
    val topWeightKg: Double = 0.0,
    val targetRpe: Double = 8.0,
    val restSeconds: Int = 90,
    val completedSets: Int = 0,
    val totalSets: Int = 3,
    val isCompleted: Boolean = false,
    val notes: String = "",
    val isSynced: Boolean = true
)

typealias GymExerciseEntity = MicrocycleWorkoutEntity

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,
    val type: String,
    val title: String,
    val distanceKm: Double = 0.0,
    val durationMin: Int = 0,
    val activeBurnKcal: Int = 0,
    val avgHr: Int = 0,
    val elevationDPlus: Int = 0,
    val cadence: Int = 0,
    val gctMs: Int = 0,
    val isSynced: Boolean = true
)

@Entity(tableName = "body_comp")
data class BodyCompEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,
    val sourceDevice: String,
    val weightKg: Double,
    val bodyFatPct: Double,
    val smmKg: Double,
    val waterPct: Double = 55.0,
    val aptCm: Double = 11.7,
    val visceralFat: Int = 5,
    val isSynced: Boolean = true
)

@Entity(tableName = "sleep_records")
data class SleepEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,
    val durationHours: Double,
    val sleepScore: Int,
    val deepSleepPct: Int,
    val avgHr: Int,
    val avgHrvMs: Int,
    val isSynced: Boolean = true
)

@Entity(tableName = "settings")
data class SettingEntity(
    @PrimaryKey
    val key: String,
    val value: String
)

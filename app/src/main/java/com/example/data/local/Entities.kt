package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val time: String,
    val mealType: String,
    val name: String,
    val calories: Int,
    val proteinG: Double,
    val carbsG: Double,
    val fatG: Double,
    val sodiumMg: Int
)

@Entity(tableName = "workouts")
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
    val notes: String
)

@Entity(tableName = "body_comp")
data class BodyCompEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val source: String,
    val weightKg: Double,
    val bodyFatPct: Double,
    val smmKg: Double,
    val waterPct: Double,
    val bmrKcal: Int,
    val visceralFat: Int,
    val aptCm: Double,
    val shoulderImbalanceDeg: Double
)

@Entity(tableName = "sleep_records")
data class SleepEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val durationHours: Double,
    val score: Int,
    val deepSleepPct: Int,
    val avgHr: Int,
    val avgHrvMs: Int
)

@Entity(tableName = "gym_exercises")
data class GymExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dayCode: String,
    val dayName: String,
    val sessionTitle: String,
    val exerciseName: String,
    val warmUpSets: String,
    val workingSets: String,
    val topWeightKg: Double,
    val completedSets: Int,
    val totalSets: Int,
    val coachCue: String,
    val isCompleted: Boolean
)

@Entity(tableName = "daily_summaries")
data class DailySummaryEntity(
    @PrimaryKey val date: String,
    val intakeKcal: Int,
    val carbsG: Int,
    val fatG: Int,
    val proteinG: Int,
    val sodiumMg: Int,
    val activeBurnKcal: Int,
    val workoutTitle: String,
    val weightKg: Double,
    val weightSource: String,
    val smmKg: Double,
    val bodyFatPct: Double,
    val aptCm: Double,
    val sleepHrvMs: Int,
    val sleepScore: Int
)

@Entity(tableName = "settings")
data class SettingEntity(
    @PrimaryKey val key: String,
    val value: String
)

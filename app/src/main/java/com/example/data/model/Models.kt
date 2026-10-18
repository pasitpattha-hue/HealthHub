package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.EmeraldMist
import com.example.ui.theme.SoftCoralRose
import com.example.ui.theme.WarmChampagne

object HealthConstants {
    const val BMR = 1608
    const val NEAT = 400
    const val PROTEIN_GOAL_G = 120.0
    const val SODIUM_LIMIT_MG = 2300.0
    const val DEFAULT_GAS_URL = "https://script.google.com/macros/s/AKfycbw_2026_HealthHub_pasit_command_center/exec"
}

enum class EnergyStatus(
    val labelTh: String,
    val labelEn: String,
    val badgeIcon: String,
    val color: Color
) {
    DEFICIT("🔥 Calorie Deficit (ลดไขมัน)", "Calorie Deficit", "🔥", EmeraldMist),
    MAINTENANCE("⚖️ Maintenance (สมดุลพลังงาน)", "Maintenance", "⚖️", WarmChampagne),
    SURPLUS("⚡ Calorie Surplus (พลังงานเกิน)", "Calorie Surplus", "⚡", SoftCoralRose);

    companion object {
        fun fromNet(net: Int): EnergyStatus {
            return when {
                net <= -150 -> DEFICIT
                net <= 150 -> MAINTENANCE
                else -> SURPLUS
            }
        }
    }
}

enum class CategoryType(val title: String, val icon: String) {
    NUTRITION("🥗 NUTRITION", "🥗"),
    WORKOUT("🏃 WORKOUT", "🏃"),
    BODYCOMP("⚖️ BODYCOMP", "⚖️"),
    SLEEP("😴 SLEEP", "😴")
}

enum class DeviceSource(val label: String) {
    HUAWEI_SCALE("Huawei Scale"),
    VISBODY_3D("Visbody 3D"),
    EVOLT_360("Evolt 360")
}

data class DailySummary(
    val date: String,
    val intakeKcal: Int,
    val carbsG: Int,
    val fatG: Int,
    val proteinG: Int,
    val sodiumMg: Int,
    val activeBurnKcal: Int,
    val workoutTitle: String,
    val weightKg: Double,
    val weightSource: DeviceSource,
    val smmKg: Double,
    val bodyFatPct: Double,
    val aptCm: Double,
    val sleepHrvMs: Int,
    val sleepScore: Int = 85
) {
    val tef: Int get() = Math.round(intakeKcal * 0.10f)
    val tdee: Int get() = HealthConstants.BMR + HealthConstants.NEAT + tef + activeBurnKcal
    val netBalance: Int get() = intakeKcal - tdee
    val status: EnergyStatus get() = EnergyStatus.fromNet(netBalance)
    val proteinPct: Int get() = Math.min(200, Math.round((proteinG / HealthConstants.PROTEIN_GOAL_G) * 100).toInt())
}

data class MealItem(
    val id: Long = 0,
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

data class WorkoutItem(
    val id: Long = 0,
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
    val notes: String = ""
)

data class BodyCompItem(
    val id: Long = 0,
    val date: String,
    val source: DeviceSource,
    val weightKg: Double,
    val bodyFatPct: Double,
    val smmKg: Double,
    val waterPct: Double,
    val bmrKcal: Int = HealthConstants.BMR,
    val visceralFat: Int,
    val aptCm: Double,
    val shoulderImbalanceDeg: Double
)

data class SleepItem(
    val id: Long = 0,
    val date: String,
    val durationHours: Double,
    val score: Int,
    val deepSleepPct: Int,
    val avgHr: Int,
    val avgHrvMs: Int
)

data class GymExerciseItem(
    val id: Long = 0,
    val dayCode: String,     // P-01 to P-07
    val dayName: String,     // Monday, Tuesday, etc.
    val sessionTitle: String,
    val exerciseName: String,
    val warmUpSets: String,
    val workingSets: String,
    val topWeightKg: Double,
    val completedSets: Int,
    val totalSets: Int,
    val coachCue: String,
    val isCompleted: Boolean = false
)

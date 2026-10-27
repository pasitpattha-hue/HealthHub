package com.example.data.model

import kotlin.math.roundToInt

enum class CategoryType(val displayName: String, val iconEmoji: String) {
    NUTRITION("Nutrition", "🥗"),
    WORKOUT("Workout", "🏃"),
    BODYCOMP("Body Comp", "⚖️"),
    SLEEP("Sleep", "😴")
}

enum class DeviceSource(val title: String) {
    VISBODY_3D("Visbody 3D"),
    HUAWEI_SCALE("Huawei Scale"),
    EVOLT_360("Evolt 360"),
    HUAWEI_WATCH("Huawei Watch")
}

enum class EnergyStatus(val badgeIcon: String, val labelTh: String, val labelEn: String) {
    DEFICIT("🔥", "ลดไขมัน (Calorie Deficit)", "Calorie Deficit"),
    MAINTENANCE("⚖️", "รักษาน้ำหนัก (Maintenance)", "Maintenance"),
    SURPLUS("⚡", "สร้างกล้ามเนื้อ (Calorie Surplus)", "Calorie Surplus")
}

object HealthConstants {
    const val BMR_KCAL = 1608
    const val NEAT_KCAL = 400
    const val TEF_FACTOR = 0.10
    const val TARGET_PROTEIN_G = 120
    const val TARGET_CARBS_G = 220
    const val TARGET_FAT_G = 65
    const val MAX_SODIUM_MG = 2300
    const val TARGET_WEIGHT_KG = 65.0
    const val TARGET_BODY_FAT_PCT = 15.0
    const val TARGET_APT_CM = 5.0
    const val DEFAULT_GAS_URL = "https://script.google.com/macros/s/AKfycbzHealthHubV4SyncEndpoint/exec"
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
    val weightSource: DeviceSource = DeviceSource.VISBODY_3D,
    val smmKg: Double,
    val bodyFatPct: Double,
    val aptCm: Double,
    val sleepHrvMs: Int,
    val sleepScore: Int = 85,
    val sugarG: Double = 0.0
) {
    // GAS v4.0 Energy Balance Engine:
    // TEF = round(Intake * 0.10)
    val tef: Int
        get() = (intakeKcal * HealthConstants.TEF_FACTOR).roundToInt()

    // TDEE = BMR(1608) + NEAT(400) + TEF + ActiveBurn
    val tdee: Int
        get() = HealthConstants.BMR_KCAL + HealthConstants.NEAT_KCAL + tef + activeBurnKcal

    // Net Energy Balance = Intake - TDEE
    val netBalance: Int
        get() = intakeKcal - tdee

    val status: EnergyStatus
        get() = when {
            netBalance < -150 -> EnergyStatus.DEFICIT
            netBalance > 150 -> EnergyStatus.SURPLUS
            else -> EnergyStatus.MAINTENANCE
        }
}

data class TomorrowScheduleSlot(
    val id: String,
    val timeBlock: String,
    val title: String,
    val subtitle: String,
    val targetMetric: String,
    val iconEmoji: String,
    val isCompleted: Boolean = false
)

data class TomorrowPlanSummary(
    val targetDateLabel: String,
    val basedOnDateLabel: String,
    val headlineRecommendation: String,
    val proteinGapG: Int,
    val sodiumExcessMg: Int,
    val recommendedIntakeKcal: Int,
    val recommendedProteinG: Int,
    val recommendedBurnKcal: Int,
    val recommendedWaterMl: Int,
    val keyFocusPoints: List<String>,
    val scheduleSlots: List<TomorrowScheduleSlot>
)

data class GeminiChatMessage(
    val id: String,
    val isUser: Boolean,
    val message: String,
    val timestampLabel: String
)

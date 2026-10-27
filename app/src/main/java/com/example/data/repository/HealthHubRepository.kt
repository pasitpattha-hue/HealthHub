package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.BodyCompEntity
import com.example.data.local.DailyLogDao
import com.example.data.local.DailySummaryEntity
import com.example.data.local.GymExerciseEntity
import com.example.data.local.HealthDao
import com.example.data.local.MealEntity
import com.example.data.local.NutritionDao
import com.example.data.local.SeedData
import com.example.data.local.SettingEntity
import com.example.data.local.SleepEntity
import com.example.data.local.WorkoutDao
import com.example.data.local.WorkoutEntity
import com.example.data.model.DailySummary
import com.example.data.model.DeviceSource
import com.example.data.remote.GoogleAppsScriptService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONObject

class HealthHubRepository(
    private val database: AppDatabase,
    private val dailyLogDao: DailyLogDao = database.dailyLogDao(),
    private val nutritionDao: NutritionDao = database.nutritionDao(),
    private val workoutDao: WorkoutDao = database.workoutDao(),
    private val healthDao: HealthDao = database.healthDao(),
    private val gasService: GoogleAppsScriptService = GoogleAppsScriptService()
) {

    suspend fun ensureSeeded() {
        val summaries = dailyLogDao.getRecentDailyLogs().first()
        val hasLatestSheetDate = summaries.any { it.date == "2026-10-23" }
        if (summaries.isEmpty() || !hasLatestSheetDate) {
            dailyLogDao.upsertDailyLogs(SeedData.initialDailySummaries)
            nutritionDao.insertNutritionEntries(SeedData.initialMeals)
            workoutDao.insertMicrocycleWorkouts(SeedData.initialGymExercises)
            workoutDao.insertWorkouts(SeedData.initialWorkouts)
            healthDao.insertBodyComps(SeedData.initialBodyComp)
            healthDao.insertSleeps(SeedData.initialSleep)
        }
    }

    fun getRecentSummaries(): Flow<List<DailySummary>> {
        return dailyLogDao.getRecentDailyLogs().map { entities ->
            entities.map { entity ->
                val source = when {
                    entity.weightSource.contains("Visbody", ignoreCase = true) -> DeviceSource.VISBODY_3D
                    entity.weightSource.contains("Evolt", ignoreCase = true) -> DeviceSource.EVOLT_360
                    else -> DeviceSource.HUAWEI_SCALE
                }
                DailySummary(
                    date = entity.date,
                    intakeKcal = entity.intakeKcal,
                    carbsG = entity.carbsG,
                    fatG = entity.fatG,
                    proteinG = entity.proteinG,
                    sodiumMg = entity.sodiumMg,
                    activeBurnKcal = entity.activeBurnKcal,
                    workoutTitle = entity.workoutTitle,
                    weightKg = entity.weightKg,
                    weightSource = source,
                    smmKg = entity.smmKg,
                    bodyFatPct = entity.bodyFatPct,
                    aptCm = entity.aptCm,
                    sleepHrvMs = entity.sleepHrvMs,
                    sleepScore = entity.sleepScore,
                    sugarG = entity.sugarG
                )
            }
        }
    }

    fun getMealsForDate(date: String): Flow<List<MealEntity>> =
        nutritionDao.getNutritionEntriesForDate(date)

    fun getAllMeals(): Flow<List<MealEntity>> =
        nutritionDao.getAllNutritionEntries()

    fun getAllWorkouts(): Flow<List<WorkoutEntity>> =
        workoutDao.getAllWorkouts()

    fun getAllBodyComp(): Flow<List<BodyCompEntity>> =
        healthDao.getAllBodyComp()

    fun getAllSleep(): Flow<List<SleepEntity>> =
        healthDao.getAllSleep()

    fun getGymExercisesForDay(dayCode: String): Flow<List<GymExerciseEntity>> =
        workoutDao.getMicrocycleWorkoutsForDay(dayCode)

    suspend fun addMealAndRecalculate(meal: MealEntity) {
        nutritionDao.insertNutritionEntry(meal)
        recalculateDailySummary(meal.date)
    }

    suspend fun deleteMealAndRecalculate(id: Long, date: String) {
        nutritionDao.deleteNutritionEntryById(id)
        recalculateDailySummary(date)
    }

    suspend fun addWorkoutAndRecalculate(workout: WorkoutEntity) {
        workoutDao.insertWorkout(workout)
        recalculateDailySummary(workout.date)
    }

    suspend fun addBodyCompAndRecalculate(bodyComp: BodyCompEntity) {
        healthDao.insertBodyComp(bodyComp)
        val existing = dailyLogDao.getDailyLogByDate(bodyComp.date) ?: SeedData.initialDailySummaries.first().copy(date = bodyComp.date)
        dailyLogDao.upsertDailyLog(
            existing.copy(
                weightKg = bodyComp.weightKg,
                weightSource = bodyComp.sourceDevice,
                bodyFatPct = bodyComp.bodyFatPct,
                smmKg = bodyComp.smmKg,
                aptCm = bodyComp.aptCm,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun addSleepAndRecalculate(sleep: SleepEntity) {
        healthDao.insertSleep(sleep)
        val existing = dailyLogDao.getDailyLogByDate(sleep.date) ?: SeedData.initialDailySummaries.first().copy(date = sleep.date)
        dailyLogDao.upsertDailyLog(
            existing.copy(
                sleepHrvMs = sleep.avgHrvMs,
                sleepScore = sleep.sleepScore,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun toggleGymExercise(exercise: GymExerciseEntity) {
        val nextCompleted = !exercise.isCompleted
        val nextSets = if (nextCompleted) exercise.totalSets else 0
        workoutDao.updateMicrocycleWorkout(
            exercise.copy(
                isCompleted = nextCompleted,
                completedSets = nextSets
            )
        )
    }

    suspend fun updateGymSetCount(exercise: GymExerciseEntity, completedSets: Int) {
        val clamped = completedSets.coerceIn(0, exercise.totalSets)
        workoutDao.updateMicrocycleWorkout(
            exercise.copy(
                completedSets = clamped,
                isCompleted = clamped >= exercise.totalSets
            )
        )
    }

    suspend fun getGasWebhookUrl(defaultUrl: String): String {
        return healthDao.getSetting("gas_webhook_url") ?: defaultUrl
    }

    suspend fun saveGasWebhookUrl(url: String) {
        healthDao.setSetting(SettingEntity("gas_webhook_url", url))
    }

    suspend fun getSetting(key: String): String? {
        return healthDao.getSetting(key)
    }

    suspend fun saveSetting(key: String, value: String) {
        healthDao.setSetting(SettingEntity(key, value))
    }

    suspend fun syncToGoogleSheets(webhookUrl: String, category: String, title: String, details: Map<String, Any>): Boolean {
        val json = JSONObject().apply {
            put("category", category)
            put("title", title)
            put("timestamp", System.currentTimeMillis())
            details.forEach { (k, v) -> put(k, v) }
        }
        return gasService.syncPayloadToGas(webhookUrl, json)
    }

    suspend fun syncDailyCaloricExpenditureFromHealthConnect(
        date: String,
        activeBurnKcal: Int,
        totalBurnKcal: Int,
        isFromSystemHealthConnect: Boolean
    ) {
        val existing = dailyLogDao.getDailyLogByDate(date)
            ?: SeedData.initialDailySummaries.firstOrNull { it.date == date }
            ?: SeedData.initialDailySummaries.first().copy(date = date)

        val resolvedActiveBurn = if (isFromSystemHealthConnect && activeBurnKcal >= 0) {
            activeBurnKcal
        } else {
            existing.activeBurnKcal
        }

        dailyLogDao.upsertDailyLog(
            existing.copy(
                activeBurnKcal = resolvedActiveBurn,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    private suspend fun recalculateDailySummary(date: String) {
        val meals = nutritionDao.getNutritionEntriesForDate(date).first()
        val workouts = workoutDao.getWorkoutsForDate(date).first()
        val existing = dailyLogDao.getDailyLogByDate(date) ?: SeedData.initialDailySummaries.first().copy(date = date)

        val totalCal = meals.sumOf { it.calories }
        val totalP = meals.sumOf { it.proteinG }.toInt()
        val totalC = meals.sumOf { it.carbsG }.toInt()
        val totalF = meals.sumOf { it.fatG }.toInt()
        val totalNa = meals.sumOf { it.sodiumMg }

        val totalBurn = if (workouts.isNotEmpty()) workouts.sumOf { it.activeBurnKcal } else existing.activeBurnKcal
        val workoutLabel = workouts.firstOrNull()?.title ?: existing.workoutTitle

        dailyLogDao.upsertDailyLog(
            existing.copy(
                intakeKcal = totalCal,
                proteinG = totalP,
                carbsG = totalC,
                fatG = totalF,
                sodiumMg = totalNa,
                activeBurnKcal = totalBurn,
                workoutTitle = workoutLabel,
                updatedAt = System.currentTimeMillis()
            )
        )
    }
}

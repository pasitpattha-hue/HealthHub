package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.BodyCompEntity
import com.example.data.local.DailySummaryEntity
import com.example.data.local.GymExerciseEntity
import com.example.data.local.MealEntity
import com.example.data.local.SeedData
import com.example.data.local.SettingEntity
import com.example.data.local.SleepEntity
import com.example.data.local.WorkoutEntity
import com.example.data.model.DailySummary
import com.example.data.model.DeviceSource
import com.example.data.model.HealthConstants
import com.example.data.remote.GoogleAppsScriptService
import com.example.data.remote.SyncResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONObject

class HealthHubRepository(
    private val database: AppDatabase,
    private val gasService: GoogleAppsScriptService = GoogleAppsScriptService()
) {
    private val dao = database.healthDao()

    suspend fun initializeDatabaseIfEmpty() {
        val summaries = dao.getRecentDailySummaries().first()
        val hasLatestData = summaries.any { it.date == "2026-10-18" }
        if (summaries.isEmpty() || !hasLatestData) {
            dao.insertDailySummaries(SeedData.initialDailySummaries)
            dao.insertMeals(SeedData.initialMeals)
            dao.insertWorkouts(SeedData.initialWorkouts)
            dao.insertBodyComps(SeedData.initialBodyComp)
            dao.insertSleepRecords(SeedData.initialSleep)
            dao.insertGymExercises(SeedData.initialGymExercises)
            for (setting in SeedData.initialSettings) {
                dao.setSetting(setting)
            }
        }
    }

    // Daily Summaries
    fun getRecentSummaries(): Flow<List<DailySummary>> {
        return dao.getRecentDailySummaries().map { entities ->
            entities.map { entity ->
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
                    weightSource = when (entity.weightSource) {
                        "Visbody 3D" -> DeviceSource.VISBODY_3D
                        "Evolt 360" -> DeviceSource.EVOLT_360
                        else -> DeviceSource.HUAWEI_SCALE
                    },
                    smmKg = entity.smmKg,
                    bodyFatPct = entity.bodyFatPct,
                    aptCm = entity.aptCm,
                    sleepHrvMs = entity.sleepHrvMs,
                    sleepScore = entity.sleepScore
                )
            }
        }
    }

    // Meals
    fun getMealsForDate(date: String): Flow<List<MealEntity>> = dao.getMealsForDate(date)

    suspend fun addMeal(meal: MealEntity): Long {
        val id = dao.insertMeal(meal)
        recalculateDailySummary(meal.date)
        syncWithGas("LOG_MEAL", JSONObject().apply {
            put("action", "LOG_MEAL")
            put("date", meal.date)
            put("mealType", meal.mealType)
            put("name", meal.name)
            put("calories", meal.calories)
            put("proteinG", meal.proteinG)
            put("carbsG", meal.carbsG)
            put("fatG", meal.fatG)
            put("sodiumMg", meal.sodiumMg)
        })
        return id
    }

    suspend fun deleteMeal(id: Long, date: String) {
        dao.deleteMeal(id)
        recalculateDailySummary(date)
    }

    // Workouts
    fun getAllWorkouts(): Flow<List<WorkoutEntity>> = dao.getAllWorkouts()

    suspend fun addWorkout(workout: WorkoutEntity): Long {
        val id = dao.insertWorkout(workout)
        recalculateDailySummary(workout.date)
        syncWithGas("LOG_WORKOUT", JSONObject().apply {
            put("action", "LOG_WORKOUT")
            put("date", workout.date)
            put("title", workout.title)
            put("category", workout.category)
            put("activeKcal", workout.activeKcal)
            put("durationMin", workout.durationMin)
            put("distanceKm", workout.distanceKm)
            put("avgHr", workout.avgHr)
            put("elevationDPlus", workout.elevationDPlus)
            put("cadence", workout.cadence)
            put("gctMs", workout.gctMs)
        })
        return id
    }

    // Body Composition
    fun getAllBodyComp(): Flow<List<BodyCompEntity>> = dao.getAllBodyComp()

    suspend fun addBodyComp(bodyComp: BodyCompEntity): Long {
        val id = dao.insertBodyComp(bodyComp)
        val currentSummary = dao.getDailySummary(bodyComp.date)
        if (currentSummary != null) {
            dao.insertDailySummary(
                currentSummary.copy(
                    weightKg = bodyComp.weightKg,
                    weightSource = bodyComp.source,
                    smmKg = bodyComp.smmKg,
                    bodyFatPct = bodyComp.bodyFatPct,
                    aptCm = bodyComp.aptCm
                )
            )
        }
        syncWithGas("LOG_BODYCOMP", JSONObject().apply {
            put("action", "LOG_BODYCOMP")
            put("date", bodyComp.date)
            put("source", bodyComp.source)
            put("weightKg", bodyComp.weightKg)
            put("bodyFatPct", bodyComp.bodyFatPct)
            put("smmKg", bodyComp.smmKg)
            put("waterPct", bodyComp.waterPct)
            put("aptCm", bodyComp.aptCm)
            put("visceralFat", bodyComp.visceralFat)
        })
        return id
    }

    // Sleep
    fun getAllSleepRecords(): Flow<List<SleepEntity>> = dao.getAllSleepRecords()

    suspend fun addSleep(sleep: SleepEntity): Long {
        val id = dao.insertSleep(sleep)
        val currentSummary = dao.getDailySummary(sleep.date)
        if (currentSummary != null) {
            dao.insertDailySummary(
                currentSummary.copy(
                    sleepHrvMs = sleep.avgHrvMs,
                    sleepScore = sleep.score
                )
            )
        }
        syncWithGas("LOG_SLEEP", JSONObject().apply {
            put("action", "LOG_SLEEP")
            put("date", sleep.date)
            put("durationHours", sleep.durationHours)
            put("score", sleep.score)
            put("deepSleepPct", sleep.deepSleepPct)
            put("avgHr", sleep.avgHr)
            put("avgHrvMs", sleep.avgHrvMs)
        })
        return id
    }

    // Gym Exercises
    fun getGymExercisesForDay(dayCode: String): Flow<List<GymExerciseEntity>> =
        dao.getGymExercisesForDay(dayCode)

    suspend fun updateGymExercise(exercise: GymExerciseEntity) {
        dao.updateGymExercise(exercise)
    }

    // Settings
    suspend fun getGasWebhookUrl(): String {
        return dao.getSetting("gas_webhook_url") ?: HealthConstants.DEFAULT_GAS_URL
    }

    suspend fun setGasWebhookUrl(url: String) {
        dao.setSetting(SettingEntity("gas_webhook_url", url))
    }

    private suspend fun recalculateDailySummary(date: String) {
        val meals = dao.getMealsForDate(date).first()
        val workouts = dao.getWorkoutsForDate(date).first()
        val currentSummary = dao.getDailySummary(date)

        val totalCalories = meals.sumOf { it.calories }
        val totalCarbs = meals.sumOf { it.carbsG }.toInt()
        val totalFat = meals.sumOf { it.fatG }.toInt()
        val totalProtein = meals.sumOf { it.proteinG }.toInt()
        val totalSodium = meals.sumOf { it.sodiumMg }

        val totalActiveBurn = workouts.sumOf { it.activeKcal }
        val topWorkoutTitle = workouts.firstOrNull()?.title ?: (currentSummary?.workoutTitle ?: "Active Day")

        val updated = currentSummary?.copy(
            intakeKcal = totalCalories,
            carbsG = totalCarbs,
            fatG = totalFat,
            proteinG = totalProtein,
            sodiumMg = totalSodium,
            activeBurnKcal = totalActiveBurn,
            workoutTitle = topWorkoutTitle
        ) ?: DailySummaryEntity(
            date = date,
            intakeKcal = totalCalories,
            carbsG = totalCarbs,
            fatG = totalFat,
            proteinG = totalProtein,
            sodiumMg = totalSodium,
            activeBurnKcal = totalActiveBurn,
            workoutTitle = topWorkoutTitle,
            weightKg = 68.4,
            weightSource = "Huawei Scale",
            smmKg = 32.8,
            bodyFatPct = 13.8,
            aptCm = 11.7,
            sleepHrvMs = 84,
            sleepScore = 85
        )

        dao.insertDailySummary(updated)
    }

    suspend fun syncWithGas(actionName: String, payload: JSONObject): SyncResult {
        val url = getGasWebhookUrl()
        return gasService.syncDataToGas(url, payload)
    }
}

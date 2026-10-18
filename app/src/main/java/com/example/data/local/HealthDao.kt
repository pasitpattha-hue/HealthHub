package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthDao {

    // 1. Nutrition Entries (Meals, Snacks, Beverages)
    @Query("SELECT * FROM nutrition_entries WHERE date = :date ORDER BY id ASC")
    fun getMealsForDate(date: String): Flow<List<NutritionEntryEntity>>

    @Query("SELECT * FROM nutrition_entries ORDER BY date DESC, id DESC")
    fun getAllNutritionEntries(): Flow<List<NutritionEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: NutritionEntryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeals(meals: List<NutritionEntryEntity>)

    @Query("DELETE FROM nutrition_entries WHERE id = :id")
    suspend fun deleteMeal(id: Long)

    @Query("SELECT * FROM nutrition_entries WHERE isSynced = 0")
    suspend fun getUnsyncedNutrition(): List<NutritionEntryEntity>

    @Query("UPDATE nutrition_entries SET isSynced = 1 WHERE id = :id")
    suspend fun markNutritionSynced(id: Long)


    // 2. Microcycle Workouts (P-01 to P-07 periodization exercises)
    @Query("SELECT * FROM microcycle_workouts ORDER BY dayCode ASC, id ASC")
    fun getAllGymExercises(): Flow<List<MicrocycleWorkoutEntity>>

    @Query("SELECT * FROM microcycle_workouts WHERE dayCode = :dayCode ORDER BY id ASC")
    fun getGymExercisesForDay(dayCode: String): Flow<List<MicrocycleWorkoutEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGymExercises(exercises: List<MicrocycleWorkoutEntity>)

    @Update
    suspend fun updateGymExercise(exercise: MicrocycleWorkoutEntity)

    @Query("SELECT * FROM microcycle_workouts WHERE isSynced = 0")
    suspend fun getUnsyncedMicrocycleWorkouts(): List<MicrocycleWorkoutEntity>


    // 3. Daily Logs (Metabolic Energy Balance, Macros & Biometrics)
    @Query("SELECT * FROM daily_logs ORDER BY date DESC LIMIT 14")
    fun getRecentDailySummaries(): Flow<List<DailyLogEntity>>

    @Query("SELECT * FROM daily_logs WHERE date = :date LIMIT 1")
    suspend fun getDailySummary(date: String): DailyLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailySummary(summary: DailyLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailySummaries(summaries: List<DailyLogEntity>)

    @Query("SELECT * FROM daily_logs WHERE isSynced = 0")
    suspend fun getUnsyncedDailyLogs(): List<DailyLogEntity>

    @Query("UPDATE daily_logs SET isSynced = 1 WHERE date = :date")
    suspend fun markDailyLogSynced(date: String)


    // Workouts (GPS runs, trail runs, aerobic sessions)
    @Query("SELECT * FROM workouts ORDER BY date DESC, id DESC")
    fun getAllWorkouts(): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workouts WHERE date = :date")
    fun getWorkoutsForDate(date: String): Flow<List<WorkoutEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkouts(workouts: List<WorkoutEntity>)


    // Body Composition (Visbody 3D, Huawei Scale, Evolt 360)
    @Query("SELECT * FROM body_comp ORDER BY date DESC, id DESC")
    fun getAllBodyComp(): Flow<List<BodyCompEntity>>

    @Query("SELECT * FROM body_comp WHERE source = :source ORDER BY date ASC")
    fun getBodyCompBySource(source: String): Flow<List<BodyCompEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBodyComp(bodyComp: BodyCompEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBodyComps(bodyComps: List<BodyCompEntity>)


    // Sleep (HRV, score, duration)
    @Query("SELECT * FROM sleep_records ORDER BY date ASC")
    fun getAllSleepRecords(): Flow<List<SleepEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleep(sleep: SleepEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleepRecords(records: List<SleepEntity>)


    // Settings
    @Query("SELECT value FROM settings WHERE `key` = :key LIMIT 1")
    suspend fun getSetting(key: String): String?

    @Query("SELECT * FROM settings")
    fun getAllSettings(): Flow<List<SettingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setSetting(setting: SettingEntity)
}

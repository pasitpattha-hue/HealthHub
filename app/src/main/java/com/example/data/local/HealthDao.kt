package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Daily Logs (`daily_logs`).
 */
@Dao
interface DailyLogDao {
    @Query("SELECT * FROM daily_logs ORDER BY date DESC LIMIT 14")
    fun getRecentDailyLogs(): Flow<List<DailyLogEntity>>

    @Query("SELECT * FROM daily_logs WHERE date = :date LIMIT 1")
    suspend fun getDailyLogByDate(date: String): DailyLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDailyLog(log: DailyLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDailyLogs(logs: List<DailyLogEntity>)

    @Query("SELECT * FROM daily_logs WHERE isSynced = 0")
    suspend fun getUnsyncedDailyLogs(): List<DailyLogEntity>

    @Query("UPDATE daily_logs SET isSynced = 1 WHERE date = :date")
    suspend fun markDailyLogSynced(date: String)
}

/**
 * DAO for Nutrition & Meal Entries (`nutrition_entries`).
 */
@Dao
interface NutritionDao {
    @Query("SELECT * FROM nutrition_entries WHERE date = :date ORDER BY timestamp DESC")
    fun getNutritionEntriesForDate(date: String): Flow<List<NutritionEntryEntity>>

    @Query("SELECT * FROM nutrition_entries ORDER BY date DESC, timestamp DESC")
    fun getAllNutritionEntries(): Flow<List<NutritionEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNutritionEntry(entry: NutritionEntryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNutritionEntries(entries: List<NutritionEntryEntity>)

    @Query("DELETE FROM nutrition_entries WHERE id = :id")
    suspend fun deleteNutritionEntryById(id: Long)

    @Query("SELECT * FROM nutrition_entries WHERE isSynced = 0")
    suspend fun getUnsyncedNutritionEntries(): List<NutritionEntryEntity>

    @Query("UPDATE nutrition_entries SET isSynced = 1 WHERE id = :id")
    suspend fun markNutritionEntrySynced(id: Long)
}

/**
 * DAO for Microcycle Workouts (`microcycle_workouts`) and Cardio/Telemetry Workouts (`workouts`).
 */
@Dao
interface WorkoutDao {
    @Query("SELECT * FROM microcycle_workouts WHERE dayCode = :dayCode ORDER BY id ASC")
    fun getMicrocycleWorkoutsForDay(dayCode: String): Flow<List<MicrocycleWorkoutEntity>>

    @Query("SELECT * FROM microcycle_workouts ORDER BY dayCode ASC, id ASC")
    fun getAllMicrocycleWorkouts(): Flow<List<MicrocycleWorkoutEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMicrocycleWorkout(workout: MicrocycleWorkoutEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMicrocycleWorkouts(workouts: List<MicrocycleWorkoutEntity>)

    @Update
    suspend fun updateMicrocycleWorkout(workout: MicrocycleWorkoutEntity)

    @Query("SELECT * FROM microcycle_workouts WHERE isSynced = 0")
    suspend fun getUnsyncedMicrocycleWorkouts(): List<MicrocycleWorkoutEntity>

    @Query("UPDATE microcycle_workouts SET isSynced = 1 WHERE id = :id")
    suspend fun markMicrocycleWorkoutSynced(id: Long)

    // Cardio / Watch Workout Telemetry
    @Query("SELECT * FROM workouts ORDER BY date DESC, id DESC")
    fun getAllWorkouts(): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workouts WHERE date = :date ORDER BY id DESC")
    fun getWorkoutsForDate(date: String): Flow<List<WorkoutEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkouts(workouts: List<WorkoutEntity>)
}

/**
 * Unified DAO for Body Composition, Sleep Telemetry, Settings, and cross-table access.
 */
@Dao
interface HealthDao {
    @Query("SELECT * FROM body_comp ORDER BY date DESC, id DESC")
    fun getAllBodyComp(): Flow<List<BodyCompEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBodyComp(entity: BodyCompEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBodyComps(entities: List<BodyCompEntity>)

    @Query("SELECT * FROM sleep_records ORDER BY date DESC, id DESC")
    fun getAllSleep(): Flow<List<SleepEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleep(entity: SleepEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleeps(entities: List<SleepEntity>)

    @Query("SELECT value FROM settings WHERE `key` = :key LIMIT 1")
    suspend fun getSetting(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setSetting(setting: SettingEntity)
}

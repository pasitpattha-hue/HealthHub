package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthDao {

    // Meals
    @Query("SELECT * FROM meals WHERE date = :date ORDER BY id ASC")
    fun getMealsForDate(date: String): Flow<List<MealEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: MealEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeals(meals: List<MealEntity>)

    @Query("DELETE FROM meals WHERE id = :id")
    suspend fun deleteMeal(id: Long)

    // Workouts
    @Query("SELECT * FROM workouts ORDER BY date DESC, id DESC")
    fun getAllWorkouts(): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workouts WHERE date = :date")
    fun getWorkoutsForDate(date: String): Flow<List<WorkoutEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkouts(workouts: List<WorkoutEntity>)

    // Body Composition
    @Query("SELECT * FROM body_comp ORDER BY date DESC, id DESC")
    fun getAllBodyComp(): Flow<List<BodyCompEntity>>

    @Query("SELECT * FROM body_comp WHERE source = :source ORDER BY date ASC")
    fun getBodyCompBySource(source: String): Flow<List<BodyCompEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBodyComp(bodyComp: BodyCompEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBodyComps(bodyComps: List<BodyCompEntity>)

    // Sleep
    @Query("SELECT * FROM sleep_records ORDER BY date ASC")
    fun getAllSleepRecords(): Flow<List<SleepEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleep(sleep: SleepEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleepRecords(records: List<SleepEntity>)

    // Gym Exercises
    @Query("SELECT * FROM gym_exercises ORDER BY dayCode ASC, id ASC")
    fun getAllGymExercises(): Flow<List<GymExerciseEntity>>

    @Query("SELECT * FROM gym_exercises WHERE dayCode = :dayCode ORDER BY id ASC")
    fun getGymExercisesForDay(dayCode: String): Flow<List<GymExerciseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGymExercises(exercises: List<GymExerciseEntity>)

    @Update
    suspend fun updateGymExercise(exercise: GymExerciseEntity)

    // Daily Summaries
    @Query("SELECT * FROM daily_summaries ORDER BY date DESC LIMIT 14")
    fun getRecentDailySummaries(): Flow<List<DailySummaryEntity>>

    @Query("SELECT * FROM daily_summaries WHERE date = :date LIMIT 1")
    suspend fun getDailySummary(date: String): DailySummaryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailySummary(summary: DailySummaryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailySummaries(summaries: List<DailySummaryEntity>)

    // Settings
    @Query("SELECT value FROM settings WHERE `key` = :key LIMIT 1")
    suspend fun getSetting(key: String): String?

    @Query("SELECT * FROM settings")
    fun getAllSettings(): Flow<List<SettingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setSetting(setting: SettingEntity)
}

package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        DailyLogEntity::class,
        NutritionEntryEntity::class,
        MicrocycleWorkoutEntity::class,
        WorkoutEntity::class,
        BodyCompEntity::class,
        SleepEntity::class,
        SettingEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun dailyLogDao(): DailyLogDao
    abstract fun nutritionDao(): NutritionDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun healthDao(): HealthDao

    companion object {
        const val DATABASE_NAME = "healthhub_database.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun buildInMemory(context: Context): AppDatabase {
            return Room.inMemoryDatabaseBuilder(
                context.applicationContext,
                AppDatabase::class.java
            )
                .allowMainThreadQueries()
                .build()
        }
    }
}

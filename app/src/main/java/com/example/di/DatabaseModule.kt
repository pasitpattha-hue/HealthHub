package com.example.di

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.local.AppDatabase
import com.example.data.local.DailyLogDao
import com.example.data.local.HealthDao
import com.example.data.local.NutritionDao
import com.example.data.local.WorkoutDao
import com.example.data.remote.GeminiService
import com.example.data.remote.GoogleAppsScriptService
import com.example.data.repository.HealthHubRepository
import com.example.ui.viewmodel.HealthHubViewModel

/**
 * Dependency Injection Module for the Room Database, domain DAOs (DailyLogDao, NutritionDao,
 * WorkoutDao, HealthDao), remote sync/AI services, and the offline-first HealthHubRepository.
 */
object DatabaseModule {

    @Volatile
    private var repositoryInstance: HealthHubRepository? = null

    fun provideDatabase(context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    fun provideDailyLogDao(database: AppDatabase): DailyLogDao {
        return database.dailyLogDao()
    }

    fun provideNutritionDao(database: AppDatabase): NutritionDao {
        return database.nutritionDao()
    }

    fun provideWorkoutDao(database: AppDatabase): WorkoutDao {
        return database.workoutDao()
    }

    fun provideHealthDao(database: AppDatabase): HealthDao {
        return database.healthDao()
    }

    fun provideGoogleAppsScriptService(): GoogleAppsScriptService {
        return GoogleAppsScriptService()
    }

    fun provideGeminiService(): GeminiService {
        return GeminiService()
    }

    fun provideHealthHubRepository(context: Context): HealthHubRepository {
        return repositoryInstance ?: synchronized(this) {
            val db = provideDatabase(context)
            val instance = HealthHubRepository(
                database = db,
                dailyLogDao = provideDailyLogDao(db),
                nutritionDao = provideNutritionDao(db),
                workoutDao = provideWorkoutDao(db),
                healthDao = provideHealthDao(db),
                gasService = provideGoogleAppsScriptService()
            )
            repositoryInstance = instance
            instance
        }
    }

    fun provideViewModelFactory(application: Application): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(HealthHubViewModel::class.java)) {
                    return HealthHubViewModel(
                        application = application,
                        repository = provideHealthHubRepository(application),
                        geminiService = provideGeminiService()
                    ) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}

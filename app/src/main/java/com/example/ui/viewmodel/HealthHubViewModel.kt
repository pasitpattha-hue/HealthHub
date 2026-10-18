package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.BodyCompEntity
import com.example.data.local.GymExerciseEntity
import com.example.data.local.MealEntity
import com.example.data.local.SleepEntity
import com.example.data.local.WorkoutEntity
import com.example.data.model.CategoryType
import com.example.data.model.DailySummary
import com.example.data.model.DeviceSource
import com.example.data.model.HealthConstants
import com.example.data.remote.AiExtractedHealthData
import com.example.data.remote.GeminiService
import com.example.data.repository.HealthHubRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class AiSnapFormState(
    val isAnalyzing: Boolean = false,
    val imageBitmap: Bitmap? = null,
    val category: CategoryType = CategoryType.NUTRITION,
    val title: String = "",
    val calories: String = "",
    val protein: String = "",
    val carbs: String = "",
    val fat: String = "",
    val sodium: String = "",
    val workoutType: String = "",
    val distanceKm: String = "",
    val durationMin: String = "",
    val activeBurnKcal: String = "",
    val avgHr: String = "",
    val elevationDPlus: String = "",
    val sourceDevice: String = "Huawei Scale",
    val weightKg: String = "",
    val bodyFatPct: String = "",
    val smmKg: String = "",
    val waterPct: String = "",
    val aptCm: String = "",
    val sleepDurationHours: String = "",
    val sleepScore: String = "",
    val sleepHrvMs: String = "",
    val notes: String = "",
    val statusMessage: String? = null
)

class HealthHubViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = HealthHubRepository(AppDatabase.getInstance(application))
    private val geminiService = GeminiService()

    private val _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex: StateFlow<Int> = _selectedTabIndex.asStateFlow()

    private val _selectedDate = MutableStateFlow("2026-10-18")
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _selectedGymDay = MutableStateFlow("P-03")
    val selectedGymDay: StateFlow<String> = _selectedGymDay.asStateFlow()

    private val _aiSnapForm = MutableStateFlow(AiSnapFormState())
    val aiSnapForm: StateFlow<AiSnapFormState> = _aiSnapForm.asStateFlow()

    private val _showSettingsDialog = MutableStateFlow(false)
    val showSettingsDialog: StateFlow<Boolean> = _showSettingsDialog.asStateFlow()

    private val _gasWebhookUrl = MutableStateFlow(HealthConstants.DEFAULT_GAS_URL)
    val gasWebhookUrl: StateFlow<String> = _gasWebhookUrl.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // Data streams from repository
    val dailySummaries: StateFlow<List<DailySummary>> = repository.getRecentSummaries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val todayMeals: StateFlow<List<MealEntity>> = _selectedDate
        .flatMapLatest { date -> repository.getMealsForDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allWorkouts: StateFlow<List<WorkoutEntity>> = repository.getAllWorkouts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBodyComp: StateFlow<List<BodyCompEntity>> = repository.getAllBodyComp()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSleep: StateFlow<List<SleepEntity>> = repository.getAllSleepRecords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val gymExercises: StateFlow<List<GymExerciseEntity>> = _selectedGymDay
        .combine(repository.getGymExercisesForDay("P-03")) { day, _ ->
            day
        }.combine(repository.getGymExercisesForDay("P-01")) { _, _ ->
            // Flow will update dynamically via query
            emptyList<GymExerciseEntity>()
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Separate direct query for gym exercises based on selected day
    private val _currentGymExercises = MutableStateFlow<List<GymExerciseEntity>>(emptyList())
    val currentGymExercises: StateFlow<List<GymExerciseEntity>> = _currentGymExercises.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeDatabaseIfEmpty()
            _gasWebhookUrl.value = repository.getGasWebhookUrl()
            loadGymExercisesForDay(_selectedGymDay.value)
        }
    }

    fun selectTab(index: Int) {
        _selectedTabIndex.value = index
    }

    fun selectDate(date: String) {
        _selectedDate.value = date
    }

    fun selectGymDay(dayCode: String) {
        _selectedGymDay.value = dayCode
        loadGymExercisesForDay(dayCode)
    }

    private fun loadGymExercisesForDay(dayCode: String) {
        viewModelScope.launch {
            repository.getGymExercisesForDay(dayCode).collect { exercises ->
                _currentGymExercises.value = exercises
            }
        }
    }

    fun toggleGymExercise(exercise: GymExerciseEntity) {
        viewModelScope.launch {
            val updated = exercise.copy(
                isCompleted = !exercise.isCompleted,
                completedSets = if (!exercise.isCompleted) exercise.totalSets else 0
            )
            repository.updateGymExercise(updated)
            loadGymExercisesForDay(_selectedGymDay.value)
        }
    }

    fun updateGymSetCount(exercise: GymExerciseEntity, newCompleted: Int) {
        viewModelScope.launch {
            val updated = exercise.copy(
                completedSets = newCompleted.coerceIn(0, exercise.totalSets),
                isCompleted = newCompleted >= exercise.totalSets
            )
            repository.updateGymExercise(updated)
            loadGymExercisesForDay(_selectedGymDay.value)
        }
    }

    fun onImageSelected(bitmap: Bitmap) {
        _aiSnapForm.value = _aiSnapForm.value.copy(
            imageBitmap = bitmap,
            statusMessage = "Image loaded. Tap 'Analyze with Gemini' to classify."
        )
    }

    fun analyzeImage() {
        val bitmap = _aiSnapForm.value.imageBitmap ?: return
        _aiSnapForm.value = _aiSnapForm.value.copy(isAnalyzing = true, statusMessage = "Gemini 2.5 Flash analyzing image...")

        viewModelScope.launch {
            val result = geminiService.analyzeHealthImage(bitmap)
            result.onSuccess { data ->
                _aiSnapForm.value = _aiSnapForm.value.copy(
                    isAnalyzing = false,
                    category = data.category,
                    title = data.title,
                    calories = if (data.calories > 0) data.calories.toString() else "",
                    protein = if (data.proteinG > 0) data.proteinG.toString() else "",
                    carbs = if (data.carbsG > 0) data.carbsG.toString() else "",
                    fat = if (data.fatG > 0) data.fatG.toString() else "",
                    sodium = if (data.sodiumMg > 0) data.sodiumMg.toString() else "",
                    workoutType = data.workoutType,
                    distanceKm = if (data.distanceKm > 0) data.distanceKm.toString() else "",
                    durationMin = if (data.durationMin > 0) data.durationMin.toString() else "",
                    activeBurnKcal = if (data.activeBurnKcal > 0) data.activeBurnKcal.toString() else "",
                    avgHr = if (data.avgHr > 0) data.avgHr.toString() else "",
                    elevationDPlus = if (data.elevationDPlus > 0) data.elevationDPlus.toString() else "",
                    sourceDevice = data.sourceDevice,
                    weightKg = if (data.weightKg > 0) data.weightKg.toString() else "68.4",
                    bodyFatPct = if (data.bodyFatPct > 0) data.bodyFatPct.toString() else "13.8",
                    smmKg = if (data.smmKg > 0) data.smmKg.toString() else "32.8",
                    waterPct = if (data.waterPct > 0) data.waterPct.toString() else "56.4",
                    aptCm = if (data.aptCm > 0) data.aptCm.toString() else "11.7",
                    sleepDurationHours = if (data.sleepDurationHours > 0) data.sleepDurationHours.toString() else "7.8",
                    sleepScore = if (data.sleepScore > 0) data.sleepScore.toString() else "85",
                    sleepHrvMs = if (data.avgSleepHrvMs > 0) data.avgSleepHrvMs.toString() else "84",
                    notes = data.notes,
                    statusMessage = "Classification complete: ${data.category.title}. You can edit fields before saving."
                )
            }.onFailure { err ->
                _aiSnapForm.value = _aiSnapForm.value.copy(
                    isAnalyzing = false,
                    statusMessage = "Analysis error: ${err.message}. Ready for manual entry."
                )
            }
        }
    }

    fun setAiCategory(category: CategoryType) {
        _aiSnapForm.value = _aiSnapForm.value.copy(category = category)
    }

    fun updateAiFormField(
        title: String? = null,
        calories: String? = null,
        protein: String? = null,
        carbs: String? = null,
        fat: String? = null,
        sodium: String? = null,
        workoutType: String? = null,
        distanceKm: String? = null,
        durationMin: String? = null,
        activeBurnKcal: String? = null,
        avgHr: String? = null,
        elevationDPlus: String? = null,
        sourceDevice: String? = null,
        weightKg: String? = null,
        bodyFatPct: String? = null,
        smmKg: String? = null,
        aptCm: String? = null,
        sleepDurationHours: String? = null,
        sleepScore: String? = null,
        sleepHrvMs: String? = null
    ) {
        _aiSnapForm.value = _aiSnapForm.value.copy(
            title = title ?: _aiSnapForm.value.title,
            calories = calories ?: _aiSnapForm.value.calories,
            protein = protein ?: _aiSnapForm.value.protein,
            carbs = carbs ?: _aiSnapForm.value.carbs,
            fat = fat ?: _aiSnapForm.value.fat,
            sodium = sodium ?: _aiSnapForm.value.sodium,
            workoutType = workoutType ?: _aiSnapForm.value.workoutType,
            distanceKm = distanceKm ?: _aiSnapForm.value.distanceKm,
            durationMin = durationMin ?: _aiSnapForm.value.durationMin,
            activeBurnKcal = activeBurnKcal ?: _aiSnapForm.value.activeBurnKcal,
            avgHr = avgHr ?: _aiSnapForm.value.avgHr,
            elevationDPlus = elevationDPlus ?: _aiSnapForm.value.elevationDPlus,
            sourceDevice = sourceDevice ?: _aiSnapForm.value.sourceDevice,
            weightKg = weightKg ?: _aiSnapForm.value.weightKg,
            bodyFatPct = bodyFatPct ?: _aiSnapForm.value.bodyFatPct,
            smmKg = smmKg ?: _aiSnapForm.value.smmKg,
            aptCm = aptCm ?: _aiSnapForm.value.aptCm,
            sleepDurationHours = sleepDurationHours ?: _aiSnapForm.value.sleepDurationHours,
            sleepScore = sleepScore ?: _aiSnapForm.value.sleepScore,
            sleepHrvMs = sleepHrvMs ?: _aiSnapForm.value.sleepHrvMs
        )
    }

    fun saveAiSnapToDatabaseAndSync() {
        val form = _aiSnapForm.value
        val today = _selectedDate.value

        viewModelScope.launch {
            when (form.category) {
                CategoryType.NUTRITION -> {
                    val meal = MealEntity(
                        date = today,
                        time = "13:00",
                        mealType = "Lunch",
                        name = form.title.ifBlank { "Scanned Nutritional Meal" },
                        calories = form.calories.toIntOrNull() ?: 500,
                        proteinG = form.protein.toDoubleOrNull() ?: 35.0,
                        carbsG = form.carbs.toDoubleOrNull() ?: 50.0,
                        fatG = form.fat.toDoubleOrNull() ?: 15.0,
                        sodiumMg = form.sodium.toIntOrNull() ?: 450
                    )
                    repository.addMeal(meal)
                }
                CategoryType.WORKOUT -> {
                    val workout = WorkoutEntity(
                        date = today,
                        title = form.title.ifBlank { form.workoutType.ifBlank { "Workout Session" } },
                        category = form.workoutType.ifBlank { "Trail Run" },
                        activeKcal = form.activeBurnKcal.toIntOrNull() ?: 550,
                        durationMin = form.durationMin.toIntOrNull() ?: 50,
                        distanceKm = form.distanceKm.toDoubleOrNull() ?: 8.0,
                        avgHr = form.avgHr.toIntOrNull() ?: 150,
                        elevationDPlus = form.elevationDPlus.toIntOrNull() ?: 200,
                        cadence = 172,
                        gctMs = 218,
                        notes = form.notes
                    )
                    repository.addWorkout(workout)
                }
                CategoryType.BODYCOMP -> {
                    val bodyComp = BodyCompEntity(
                        date = today,
                        source = form.sourceDevice,
                        weightKg = form.weightKg.toDoubleOrNull() ?: 68.4,
                        bodyFatPct = form.bodyFatPct.toDoubleOrNull() ?: 13.8,
                        smmKg = form.smmKg.toDoubleOrNull() ?: 32.8,
                        waterPct = form.waterPct.toDoubleOrNull() ?: 56.4,
                        bmrKcal = HealthConstants.BMR,
                        visceralFat = 4,
                        aptCm = form.aptCm.toDoubleOrNull() ?: 11.7,
                        shoulderImbalanceDeg = 1.2
                    )
                    repository.addBodyComp(bodyComp)
                }
                CategoryType.SLEEP -> {
                    val sleep = SleepEntity(
                        date = today,
                        durationHours = form.sleepDurationHours.toDoubleOrNull() ?: 7.8,
                        score = form.sleepScore.toIntOrNull() ?: 85,
                        deepSleepPct = 24,
                        avgHr = 48,
                        avgHrvMs = form.sleepHrvMs.toIntOrNull() ?: 84
                    )
                    repository.addSleep(sleep)
                }
            }

            _snackbarMessage.value = "Saved to Room DB & Synced with 2026_HealthHub!"
            _aiSnapForm.value = AiSnapFormState(statusMessage = "Logged successfully! Ready for next snap.")
        }
    }

    fun addQuickMeal(
        name: String,
        calories: Int,
        protein: Double,
        carbs: Double,
        fat: Double,
        sodium: Int,
        mealType: String
    ) {
        viewModelScope.launch {
            repository.addMeal(
                MealEntity(
                    date = _selectedDate.value,
                    time = "14:30",
                    mealType = mealType,
                    name = name,
                    calories = calories,
                    proteinG = protein,
                    carbsG = carbs,
                    fatG = fat,
                    sodiumMg = sodium
                )
            )
            _snackbarMessage.value = "Meal logged: +$calories kcal"
        }
    }

    fun deleteMeal(id: Long) {
        viewModelScope.launch {
            repository.deleteMeal(id, _selectedDate.value)
            _snackbarMessage.value = "Meal deleted"
        }
    }

    fun openSettingsDialog() {
        _showSettingsDialog.value = true
    }

    fun closeSettingsDialog() {
        _showSettingsDialog.value = false
    }

    fun updateGasUrl(newUrl: String) {
        viewModelScope.launch {
            repository.setGasWebhookUrl(newUrl)
            _gasWebhookUrl.value = newUrl
            _showSettingsDialog.value = false
            _snackbarMessage.value = "Google Apps Script endpoint updated!"
        }
    }

    fun showSnackbar(message: String) {
        _snackbarMessage.value = message
    }

    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }

    fun generate14DayContextMarkdown(): String {
        val summaries = dailySummaries.value
        val sb = StringBuilder()
        sb.appendLine("# 14-DAY PERFORMANCE TELEMETRY CONTEXT (2026_HealthHub)")
        sb.appendLine("Athlete: Pasit Patthanasethanon | BMR: 1608 kcal | NEAT: 400 kcal")
        sb.appendLine("Formulas: TEF = round(Intake * 0.10) | TDEE = 1608 + 400 + TEF + ActiveBurn | Net = Intake - TDEE")
        sb.appendLine()
        sb.appendLine("| Date | Intake | ActiveBurn | TDEE | Net Balance | Status | Weight | Source | SMM | APT | Sleep HRV |")
        sb.appendLine("|---|---|---|---|---|---|---|---|---|---|---|")
        for (s in summaries) {
            sb.appendLine("| ${s.date} | ${s.intakeKcal} kcal | ${s.activeBurnKcal} kcal | ${s.tdee} kcal | ${s.netBalance} kcal | ${s.status.name} | ${s.weightKg}kg | ${s.weightSource.label} | ${s.smmKg}kg | ${s.aptCm}cm | ${s.sleepHrvMs}ms |")
        }
        sb.appendLine()
        sb.appendLine("## 4-Pillar Clinical Observations:")
        sb.appendLine("1. Pilates and Deep Sleep -> HRV rebound (89-92ms vs 53-57ms).")
        sb.appendLine("2. Evening Sodium (>3200mg) -> Next-morning water retention (57.7%).")
        sb.appendLine("3. Tibialis Raises (12kg) & Eccentric Calf Raises (24kg) -> Shin splint prevention for carbon sandals on Khao Chalak trails.")
        sb.appendLine("4. Protein Intake average: ~135g/day exceeding 120g goal -> SMM preserved at 32.8-33.1kg.")
        return sb.toString()
    }
}

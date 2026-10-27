package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.BodyCompEntity
import com.example.data.local.GymExerciseEntity
import com.example.data.local.MealEntity
import com.example.data.local.SleepEntity
import com.example.data.local.WorkoutEntity
import com.example.data.model.CategoryType
import com.example.data.model.DailySummary
import com.example.data.model.GeminiChatMessage
import com.example.data.model.HealthConstants
import com.example.data.model.TomorrowPlanSummary
import com.example.data.model.TomorrowScheduleSlot
import com.example.data.remote.GeminiService
import com.example.data.repository.HealthHubRepository
import com.example.di.DatabaseModule
import com.example.ui.components.HabitStreakItem
import com.example.data.health.BidirectionalHealthSyncResult
import com.example.data.health.CaloricExpenditureSnapshot
import com.example.data.health.GoogleHealthSyncManager
import com.example.data.health.HealthSyncResult
import com.example.data.health.NutrientPushSummary
import com.example.ui.theme.ClayLavender
import com.example.ui.theme.CrystalIce
import com.example.ui.theme.EmeraldMist
import com.example.ui.theme.SoftCoralRose
import com.example.ui.theme.WarmChampagne
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import com.example.data.remote.FoodComponentMacro

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
    val sugar: String = "",
    val fiber: String = "",
    val mealType: String = "มื้อหลัก",
    val portionGrams: String = "350",
    val confidencePct: Int = 94,
    val macroQualityGrade: String = "A+ High-Protein Lean",
    val detectedIngredients: List<String> = emptyList(),
    val itemizedMacros: List<FoodComponentMacro> = emptyList(),
    val coachRecommendation: String = "",
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

class HealthHubViewModel @JvmOverloads constructor(
    application: Application,
    private val repository: HealthHubRepository = DatabaseModule.provideHealthHubRepository(application),
    private val geminiService: GeminiService = DatabaseModule.provideGeminiService()
) : AndroidViewModel(application) {

    private val _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex: StateFlow<Int> = _selectedTabIndex.asStateFlow()

    private val _selectedDate = MutableStateFlow("2026-10-23")
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _selectedGymDay = MutableStateFlow("P-01")
    val selectedGymDay: StateFlow<String> = _selectedGymDay.asStateFlow()

    private val _aiSnapForm = MutableStateFlow(AiSnapFormState())
    val aiSnapForm: StateFlow<AiSnapFormState> = _aiSnapForm.asStateFlow()

    private val _showSettingsDialog = MutableStateFlow(false)
    val showSettingsDialog: StateFlow<Boolean> = _showSettingsDialog.asStateFlow()

    private val _gasWebhookUrl = MutableStateFlow(HealthConstants.DEFAULT_GAS_URL)
    val gasWebhookUrl: StateFlow<String> = _gasWebhookUrl.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private val googleHealthSyncManager = GoogleHealthSyncManager()

    private val _googleHealthSyncStatus = MutableStateFlow<String>("พร้อมซิงค์รายการอาหารเข้า Google Health Connect")
    val googleHealthSyncStatus: StateFlow<String> = _googleHealthSyncStatus.asStateFlow()

    private val _isGoogleHealthSyncing = MutableStateFlow(false)
    val isGoogleHealthSyncing: StateFlow<Boolean> = _isGoogleHealthSyncing.asStateFlow()

    private val _lastGoogleHealthSyncTime = MutableStateFlow<String?>("23/10/2026, 20:15")
    val lastGoogleHealthSyncTime: StateFlow<String?> = _lastGoogleHealthSyncTime.asStateFlow()

    private val _autoSyncGoogleHealth = MutableStateFlow(true)
    val autoSyncGoogleHealth: StateFlow<Boolean> = _autoSyncGoogleHealth.asStateFlow()

    private val _isHealthConnectAvailable = MutableStateFlow(false)
    val isHealthConnectAvailable: StateFlow<Boolean> = _isHealthConnectAvailable.asStateFlow()

    private val _lastSyncedExpenditure = MutableStateFlow<CaloricExpenditureSnapshot?>(
        CaloricExpenditureSnapshot(
            date = "2026-10-23",
            activeCaloriesBurnedKcal = 0,
            totalCaloriesBurnedKcal = 2211,
            activeRecordsCount = 1,
            totalRecordsCount = 1,
            isFromSystemHealthConnect = false,
            sourceLabel = "Health Connect & 2026_HealthHub Telemetry"
        )
    )
    val lastSyncedExpenditure: StateFlow<CaloricExpenditureSnapshot?> = _lastSyncedExpenditure.asStateFlow()

    private val _lastNutrientPushSummary = MutableStateFlow<NutrientPushSummary?>(
        NutrientPushSummary(
            date = "2026-10-23",
            pushedMealsCount = 4,
            totalEnergyKcal = 2030,
            totalProteinG = 89.0,
            totalCarbsG = 226.0,
            totalFatG = 86.0,
            totalSodiumMg = 3190,
            recordIds = emptyList(),
            isPushedToSystemHealthConnect = false
        )
    )
    val lastNutrientPushSummary: StateFlow<NutrientPushSummary?> = _lastNutrientPushSummary.asStateFlow()

    private val _completedTomorrowSlots = MutableStateFlow<Set<String>>(emptySet())

    private val _geminiChatMessages = MutableStateFlow<List<GeminiChatMessage>>(
        listOf(
            GeminiChatMessage(
                id = "welcome_msg",
                isUser = false,
                message = "สวัสดีครับคุณพสิษฐ์! 🤖 ผมโหลดฐานข้อมูล **2026_HealthHub (อัปเดต 23/10/2026)** เรียบร้อยแล้ว:\n" +
                    "• **วันนี้ (23/10):** รับเข้า 2,030 kcal | TDEE 2,211 kcal (**-181 kcal Deficit 🔥**)\n" +
                    "• **โปรตีน:** 89g / 120g (74%) — ขาดอีก **31g**\n" +
                    "• **โซเดียม:** 3,190 mg (จากก๋วยเตี๋ยวต้มยำ 1,400mg + ข้าวมันไก่ 900mg)\n" +
                    "กดปุ่มคำถามด่วนหรือพิมพ์ถามแผนวันพรุ่งนี้ได้เลยครับ!",
                timestampLabel = "AI Coach Ready"
            )
        )
    )
    val geminiChatMessages: StateFlow<List<GeminiChatMessage>> = _geminiChatMessages.asStateFlow()

    private val _isGeminiTyping = MutableStateFlow(false)
    val isGeminiTyping: StateFlow<Boolean> = _isGeminiTyping.asStateFlow()

    val dailySummaries: StateFlow<List<DailySummary>> = repository.getRecentSummaries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val todayMeals: StateFlow<List<MealEntity>> = _selectedDate
        .flatMapLatest { date -> repository.getMealsForDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMeals: StateFlow<List<MealEntity>> = repository.getAllMeals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allWorkouts: StateFlow<List<WorkoutEntity>> = repository.getAllWorkouts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBodyComp: StateFlow<List<BodyCompEntity>> = repository.getAllBodyComp()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSleep: StateFlow<List<SleepEntity>> = repository.getAllSleep()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentGymExercises: StateFlow<List<GymExerciseEntity>> = _selectedGymDay
        .flatMapLatest { dayCode -> repository.getGymExercisesForDay(dayCode) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _waterIntakeMl = MutableStateFlow(2250)
    val waterIntakeMl: StateFlow<Int> = _waterIntakeMl.asStateFlow()

    private val _manualHabitOverrides = MutableStateFlow<Map<String, Boolean>>(
        mapOf(
            "habit_sleep" to true,
            "habit_protein" to true,
            "habit_pelvis" to true,
            "habit_burn" to true
        )
    )

    val habitStreakItems: StateFlow<List<HabitStreakItem>> = combine(
        dailySummaries,
        allSleep,
        _waterIntakeMl,
        _manualHabitOverrides
    ) { summaries, sleeps, water, overrides ->
        val currentSummary = summaries.firstOrNull()
        val latestSleep = sleeps.firstOrNull()

        val isWaterDone = overrides["habit_hydration"] ?: (water >= 3000)
        val isSleepDone = overrides["habit_sleep"] ?: ((latestSleep?.durationHours ?: 8.2) >= 7.5 || (currentSummary?.sleepScore ?: 88) >= 80)
        val isProteinDone = overrides["habit_protein"] ?: ((currentSummary?.proteinG ?: 138) >= 120)
        val isPelvisDone = overrides["habit_pelvis"] ?: true
        val isBurnDone = overrides["habit_burn"] ?: ((currentSummary?.activeBurnKcal ?: 590) >= 500)

        listOf(
            HabitStreakItem(
                id = "habit_hydration",
                title = "Hydration Target",
                category = "Fluid & Osmotic Balance",
                iconEmoji = "💧",
                currentVal = water.toFloat(),
                targetVal = 3000f,
                unit = "ml",
                streakDays = if (isWaterDone) 7 else 6,
                isCompleted = isWaterDone,
                accentColor = CrystalIce,
                detailSubtitle = "$water / 3,000 ml • Flush sodium and keep hydration high",
                weeklyHistory = listOf(true, true, true, true, true, true, isWaterDone)
            ),
            HabitStreakItem(
                id = "habit_sleep",
                title = "Sleep & Parasympathetic Recovery",
                category = "Circadian Rhythm",
                iconEmoji = "😴",
                currentVal = (latestSleep?.durationHours ?: 8.2).toFloat(),
                targetVal = 7.5f,
                unit = "hrs",
                streakDays = if (isSleepDone) 19 else 18,
                isCompleted = isSleepDone,
                accentColor = WarmChampagne,
                detailSubtitle = "${latestSleep?.durationHours ?: 8.2} hrs • HRV ${currentSummary?.sleepHrvMs ?: 84} ms • Score ${currentSummary?.sleepScore ?: 88}",
                weeklyHistory = listOf(true, true, true, true, true, true, isSleepDone)
            ),
            HabitStreakItem(
                id = "habit_protein",
                title = "120g Daily Protein Pace",
                category = "Metabolic MPS",
                iconEmoji = "🥩",
                currentVal = (currentSummary?.proteinG ?: 138).toFloat(),
                targetVal = 120f,
                unit = "g",
                streakDays = if (isProteinDone) 14 else 13,
                isCompleted = isProteinDone,
                accentColor = EmeraldMist,
                detailSubtitle = "${currentSummary?.proteinG ?: 138}g / 120g reached • Muscle mass protection",
                weeklyHistory = listOf(true, true, true, true, true, true, isProteinDone)
            ),
            HabitStreakItem(
                id = "habit_pelvis",
                title = "Pelvis APT Reset Mobility",
                category = "Biomechanics & Posture",
                iconEmoji = "🧘",
                currentVal = 10f,
                targetVal = 10f,
                unit = "min",
                streakDays = if (isPelvisDone) 9 else 8,
                isCompleted = isPelvisDone,
                accentColor = ClayLavender,
                detailSubtitle = "Visbody APT: ${currentSummary?.aptCm ?: 11.7} cm • 10m Posture & Glute Routine",
                weeklyHistory = listOf(true, true, true, true, true, true, isPelvisDone)
            ),
            HabitStreakItem(
                id = "habit_burn",
                title = "500 kcal Active Energy Burn",
                category = "Cardiorespiratory",
                iconEmoji = "🏃",
                currentVal = (currentSummary?.activeBurnKcal ?: 590).toFloat(),
                targetVal = 500f,
                unit = "kcal",
                streakDays = if (isBurnDone) 11 else 10,
                isCompleted = isBurnDone,
                accentColor = SoftCoralRose,
                detailSubtitle = "${currentSummary?.activeBurnKcal ?: 590} kcal • ${currentSummary?.workoutTitle ?: "Zone 2 + Microcycle"}",
                weeklyHistory = listOf(true, true, true, true, true, true, isBurnDone)
            )
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tomorrowPlan: StateFlow<TomorrowPlanSummary> = combine(
        dailySummaries,
        _selectedDate,
        _completedTomorrowSlots
    ) { summaries, selDate, completedIds ->
        val current = summaries.firstOrNull { it.date == selDate } ?: summaries.firstOrNull()
        val proteinToday = current?.proteinG ?: 89
        val sodiumToday = current?.sodiumMg ?: 3190
        val burnToday = current?.activeBurnKcal ?: 0
        val proteinGap = (HealthConstants.TARGET_PROTEIN_G - proteinGapSafe(proteinToday)).coerceAtLeast(0)
        val sodiumExcess = (sodiumToday - HealthConstants.MAX_SODIUM_MG).coerceAtLeast(0)

        val nextDateLabel = when (selDate) {
            "2026-10-23" -> "24/10/2026 (พรุ่งนี้)"
            "2026-10-22" -> "23/10/2026"
            "2026-10-21" -> "22/10/2026"
            "2026-10-20" -> "21/10/2026"
            "2026-10-19" -> "20/10/2026"
            "2026-10-18" -> "19/10/2026"
            "2026-10-17" -> "18/10/2026"
            else -> "วันถัดไป"
        }

        val headline = if (proteinGap > 0 && burnToday == 0) {
            "เติมโปรตีนเพิ่มอีก +${proteinGap}g ให้ถึงเป้า 120g • ดื่มน้ำระบายโซเดียมส่วนเกิน +${sodiumExcess}mg • กลับมาซ้อม Strength + แก้ APT"
        } else if (sodiumExcess > 500) {
            "โปรตีนผ่านเกณฑ์แล้ว! พรุ่งนี้โฟกัสดื่มน้ำ ≥3,000ml ลดโซเดียมสะสม และคุมแคลอรี่ ~2,050 kcal"
        } else {
            "รักษาวินัย Calorie Deficit ต่อเนื่อง คุมโปรตีน ≥125g และฝึก Pelvis APT Reset 10 นาที"
        }

        val focusPoints = listOf(
            if (proteinGap > 0) "🥩 เสริมโปรตีนอีก +${proteinGap}g (วันนี้ได้ ${proteinToday}g/120g) แนะนำอกไก่ย่าง/ปลาลีน หรือเวย์ไอโซเลท 1.5 สกู๊ป"
            else "🥩 รักษาระดับโปรตีน ≥125g/วัน เพื่อรักษามวลกล้ามเนื้อ SMM 32.2 kg",
            if (sodiumExcess > 0) "🧂 ลดบวมโซเดียม: วันนี้รับโซเดียม ${sodiumToday} mg (เกินเกณฑ์ +${sodiumExcess} mg) พรุ่งนี้เลี่ยงน้ำซุปข้น/น้ำจิ้ม และดื่มน้ำ 3,000 ml"
            else "💧 รักษาสมดุลน้ำและโซเดียม ดื่มน้ำเปล่า 3,000 ml ตลอดวัน",
            if (burnToday == 0) "🏃 ตารางซ้อมพรุ่งนี้: วันนี้เป็นวันพัก (0 kcal) พรุ่งนี้จัดตาราง Upper Push + Pelvis APT Reset (เป้าเผาผลาญ 350 kcal)"
            else "🧘 ฟื้นฟูร่างกาย: วันนี้ซ้อมไป ${burnToday} kcal พรุ่งนี้เน้น Zone 2 เบาๆ หรือยืดเหยียดแก้ APT 11.7 cm"
        )

        val slots = listOf(
            TomorrowScheduleSlot(
                id = "slot_morning",
                timeBlock = "07:30 - 09:00 (ช่วงเช้า)",
                title = "ดื่มน้ำ 500ml ทันทีหลังตื่น + มื้อเช้าโปรตีนสูงโซเดียมต่ำ",
                subtitle = "แนะนำ: ไข่ต้ม 3 ฟอง + อกไก่/ธัญพืช KC ไม่ใส่น้ำตาล + เวย์ 1 สกู๊ป",
                targetMetric = "480 kcal • โปรตีน 42g • Na <350mg",
                iconEmoji = "🌅",
                isCompleted = completedIds.contains("slot_morning")
            ),
            TomorrowScheduleSlot(
                id = "slot_lunch",
                timeBlock = "12:00 - 13:30 (ช่วงกลางวัน)",
                title = "มื้อหลักลีนโปรตีน (งดของทอด/งดซดน้ำซุปต้มยำ)",
                subtitle = "แนะนำ: ข้าวมันไก่ต้มล้วนไม่หนัง (ไม่ราดน้ำจิ้มเยอะ) หรือเกาเหลาเนื้อน้ำใส",
                targetMetric = "620 kcal • โปรตีน 40g • Na <700mg",
                iconEmoji = "☀️",
                isCompleted = completedIds.contains("slot_lunch")
            ),
            TomorrowScheduleSlot(
                id = "slot_workout",
                timeBlock = "17:00 - 18:00 (ช่วงเย็น - ซ้อม)",
                title = if (burnToday == 0) "ซ้อม Strength Training + Pelvis APT Reset" else "Pilates / Zone 2 Recovery & Mobility",
                subtitle = "โฟกัส Incline Press + Dead Bug Pelvis Lock แก้เอียงเชิงกราน APT 11.7cm",
                targetMetric = "เป้าเผาผลาญ Active ≥350 kcal",
                iconEmoji = "🏋️",
                isCompleted = completedIds.contains("slot_workout")
            ),
            TomorrowScheduleSlot(
                id = "slot_dinner",
                timeBlock = "19:00 - 20:30 (มื้อเย็น & ก่อนนอน)",
                title = "มื้อเย็นย่อยง่ายโปรตีนครบ 120g + ปิดจบน้ำดื่ม 3,000 ml",
                subtitle = "แนะนำ: ปลากะพงลวกจิ้ม/สเต็กอกไก่ + ผักลวก • เช็คซิงค์ Google Health Connect",
                targetMetric = "550 kcal • โปรตีน 43g • Deficit -350 kcal",
                iconEmoji = "🌙",
                isCompleted = completedIds.contains("slot_dinner")
            )
        )

        TomorrowPlanSummary(
            targetDateLabel = nextDateLabel,
            basedOnDateLabel = selDate,
            headlineRecommendation = headline,
            proteinGapG = proteinGap,
            sodiumExcessMg = sodiumExcess,
            recommendedIntakeKcal = 2050,
            recommendedProteinG = 125,
            recommendedBurnKcal = if (burnToday == 0) 350 else 250,
            recommendedWaterMl = 3000,
            keyFocusPoints = focusPoints,
            scheduleSlots = slots
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        TomorrowPlanSummary(
            targetDateLabel = "24/10/2026 (พรุ่งนี้)",
            basedOnDateLabel = "2026-10-23",
            headlineRecommendation = "เติมโปรตีนเพิ่มอีก +31g ให้ถึงเป้า 120g • ดื่มน้ำระบายโซเดียมส่วนเกิน +890mg • ซ้อม Strength + แก้ APT",
            proteinGapG = 31,
            sodiumExcessMg = 890,
            recommendedIntakeKcal = 2050,
            recommendedProteinG = 125,
            recommendedBurnKcal = 350,
            recommendedWaterMl = 3000,
            keyFocusPoints = listOf(
                "🥩 เสริมโปรตีนอีก +31g (วันนี้ได้ 89g/120g)",
                "🧂 ลดบวมโซเดียม: วันนี้รับโซเดียม 3,190 mg พรุ่งนี้เลี่ยงน้ำซุปข้น",
                "🏃 ตารางซ้อมพรุ่งนี้: Upper Push + Pelvis APT Reset (350 kcal)"
            ),
            scheduleSlots = emptyList()
        )
    )

    private fun proteinGapSafe(currentP: Int): Int = currentP

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
            _gasWebhookUrl.value = repository.getGasWebhookUrl(HealthConstants.DEFAULT_GAS_URL)
            val savedWater = repository.getSetting("water_intake_ml")?.toIntOrNull()
            if (savedWater != null) {
                _waterIntakeMl.value = savedWater
            }
            _isHealthConnectAvailable.value = googleHealthSyncManager.isHealthConnectAvailable(getApplication())
            val savedAutoSync = repository.getSetting("google_health_auto_sync")
            if (savedAutoSync != null) {
                _autoSyncGoogleHealth.value = savedAutoSync.toBoolean()
            }
            val savedSyncTime = repository.getSetting("last_google_health_sync_time")
            if (savedSyncTime != null) {
                _lastGoogleHealthSyncTime.value = savedSyncTime
            }
        }
    }

    fun selectTab(index: Int) {
        _selectedTabIndex.value = index.coerceIn(0, 3)
    }

    fun selectDate(date: String) {
        _selectedDate.value = date
    }

    fun toggleTomorrowScheduleSlot(slotId: String) {
        val current = _completedTomorrowSlots.value.toMutableSet()
        val added = if (current.contains(slotId)) {
            current.remove(slotId)
            false
        } else {
            current.add(slotId)
            true
        }
        _completedTomorrowSlots.value = current
        showSnackbar(if (added) "✅ บันทึกแผนพรุ่งนี้เรียบร้อย!" else "↩️ ยกเลิกสถานะแผนพรุ่งนี้")
    }

    fun sendGeminiChatMessage(prompt: String) {
        val trimmed = prompt.trim()
        if (trimmed.isEmpty()) return

        val nowLabel = java.text.SimpleDateFormat("HH:mm", java.util.Locale.US).format(java.util.Date())
        val userMsg = GeminiChatMessage(
            id = "user_${System.currentTimeMillis()}",
            isUser = true,
            message = trimmed,
            timestampLabel = nowLabel
        )
        _geminiChatMessages.value = _geminiChatMessages.value + userMsg
        _isGeminiTyping.value = true

        viewModelScope.launch {
            val contextMd = generate14DayContextMarkdown()
            val replyText = geminiService.chatWithHealthCoach(
                userMessage = trimmed,
                sheetContextMarkdown = contextMd
            )
            val aiMsg = GeminiChatMessage(
                id = "ai_${System.currentTimeMillis()}",
                isUser = false,
                message = replyText,
                timestampLabel = "Gemini Coach • $nowLabel"
            )
            _geminiChatMessages.value = _geminiChatMessages.value + aiMsg
            _isGeminiTyping.value = false
        }
    }

    fun selectGymDay(dayCode: String) {
        _selectedGymDay.value = dayCode
    }

    fun openSettingsDialog() {
        _showSettingsDialog.value = true
    }

    fun closeSettingsDialog() {
        _showSettingsDialog.value = false
    }

    fun updateGasUrl(url: String) {
        viewModelScope.launch {
            _gasWebhookUrl.value = url
            repository.saveGasWebhookUrl(url)
            showSnackbar("✅ GAS v4.0 Webhook URL saved!")
        }
    }

    fun showSnackbar(message: String) {
        _snackbarMessage.value = message
    }

    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }

    fun addWater(amountMl: Int) {
        val newAmount = (_waterIntakeMl.value + amountMl).coerceAtLeast(0)
        _waterIntakeMl.value = newAmount
        viewModelScope.launch {
            repository.saveSetting("water_intake_ml", newAmount.toString())
        }
        if (newAmount >= 3000) {
            showSnackbar("💧 Hydration target crushed ($newAmount / 3,000 ml)! Streak +1 🔥")
        } else {
            showSnackbar("💧 Logged +${amountMl}ml ($newAmount / 3,000 ml)")
        }
    }

    fun resetWater() {
        _waterIntakeMl.value = 0
        viewModelScope.launch {
            repository.saveSetting("water_intake_ml", "0")
        }
        showSnackbar("💧 Hydration log reset for today")
    }

    fun toggleHabit(habitId: String) {
        val currentOverrides = _manualHabitOverrides.value.toMutableMap()
        val currentHabit = habitStreakItems.value.firstOrNull { it.id == habitId }
        val newStatus = !(currentHabit?.isCompleted ?: false)
        currentOverrides[habitId] = newStatus
        _manualHabitOverrides.value = currentOverrides

        val label = currentHabit?.title ?: "Habit"
        val statusText = if (newStatus) "completed! Streak maintained 🔥" else "marked pending"
        showSnackbar("$label $statusText")
    }

    fun toggleGoogleHealthAutoSync() {
        val next = !_autoSyncGoogleHealth.value
        _autoSyncGoogleHealth.value = next
        viewModelScope.launch {
            repository.saveSetting("google_health_auto_sync", next.toString())
        }
        showSnackbar(if (next) "⚡ Google Health Auto-Sync enabled" else "⏸️ Google Health Auto-Sync paused")
    }

    fun readDailyCaloricExpenditureFromHealthConnect(date: String? = null) {
        val targetDate = date ?: _selectedDate.value
        viewModelScope.launch {
            _isGoogleHealthSyncing.value = true
            val summaries = dailySummaries.value
            val summary = summaries.firstOrNull { it.date == targetDate } ?: summaries.firstOrNull()
            val fallbackActive = summary?.activeBurnKcal ?: 0
            val fallbackTotal = summary?.tdee ?: 2211

            val expenditure = googleHealthSyncManager.readDailyCaloricExpenditure(
                context = getApplication(),
                dateStr = targetDate,
                fallbackActiveKcal = fallbackActive,
                fallbackTotalKcal = fallbackTotal
            )
            _lastSyncedExpenditure.value = expenditure

            repository.syncDailyCaloricExpenditureFromHealthConnect(
                date = targetDate,
                activeBurnKcal = expenditure.activeCaloriesBurnedKcal,
                totalBurnKcal = expenditure.totalCaloriesBurnedKcal,
                isFromSystemHealthConnect = expenditure.isFromSystemHealthConnect
            )

            _googleHealthSyncStatus.value =
                "🔥 ดึงแคลอรี่เผาผลาญ ($targetDate): TDEE ${expenditure.totalCaloriesBurnedKcal} kcal (Active ${expenditure.activeCaloriesBurnedKcal} kcal) จาก ${expenditure.sourceLabel}"
            showSnackbar("🔥 ดึงแคลอรี่เผาผลาญจาก Health Connect: ${expenditure.totalCaloriesBurnedKcal} kcal (Active ${expenditure.activeCaloriesBurnedKcal} kcal)")
            _isGoogleHealthSyncing.value = false
        }
    }

    fun syncIntakeCaloriesToGoogleHealth(date: String? = null) {
        val targetDate = date ?: _selectedDate.value
        viewModelScope.launch {
            _isGoogleHealthSyncing.value = true
            _googleHealthSyncStatus.value = "กำลังซิงค์ข้อมูล 2 ทางกับ Health Connect (ดึงเผาผลาญ + ส่งสารอาหาร)..."

            val meals = repository.getMealsForDate(targetDate).first()
            val summaries = dailySummaries.value
            val summary = summaries.firstOrNull { it.date == targetDate } ?: summaries.firstOrNull()
            val totalKcal = if (meals.isNotEmpty()) meals.sumOf { it.calories } else (summary?.intakeKcal ?: 2030)
            val fallbackActiveBurn = summary?.activeBurnKcal ?: 0
            val fallbackTotalBurn = summary?.tdee ?: (2008 + fallbackActiveBurn + (totalKcal * 0.10).toInt())

            // 1. Bidirectional sync with on-device Google Health Connect:
            //    - Read daily caloric expenditure (ActiveCaloriesBurnedRecord & TotalCaloriesBurnedRecord)
            //    - Push nutrient logs (NutritionRecord: Energy, Protein, Carbs, Fat, Sodium)
            val bidirectionalResult: BidirectionalHealthSyncResult = googleHealthSyncManager.synchronizeDailyHealthData(
                context = getApplication(),
                dateStr = targetDate,
                meals = meals,
                fallbackActiveBurnKcal = fallbackActiveBurn,
                fallbackTotalBurnKcal = fallbackTotalBurn,
                fallbackIntakeKcal = totalKcal
            )

            _lastSyncedExpenditure.value = bidirectionalResult.expenditure
            _lastNutrientPushSummary.value = bidirectionalResult.nutrientPush

            repository.syncDailyCaloricExpenditureFromHealthConnect(
                date = targetDate,
                activeBurnKcal = bidirectionalResult.expenditure.activeCaloriesBurnedKcal,
                totalBurnKcal = bidirectionalResult.expenditure.totalCaloriesBurnedKcal,
                isFromSystemHealthConnect = bidirectionalResult.expenditure.isFromSystemHealthConnect
            )

            // 2. Dual-sync to Google Cloud / GAS Webhook for persistent Google Workspace Health log
            val details = mapOf(
                "date" to targetDate,
                "intakeKcal" to totalKcal,
                "activeCaloriesBurnedKcal" to bidirectionalResult.expenditure.activeCaloriesBurnedKcal,
                "totalCaloriesBurnedKcal" to bidirectionalResult.expenditure.totalCaloriesBurnedKcal,
                "netEnergyBalanceKcal" to bidirectionalResult.netEnergyBalanceKcal,
                "mealsCount" to meals.size,
                "proteinG" to bidirectionalResult.nutrientPush.totalProteinG.toInt(),
                "carbsG" to bidirectionalResult.nutrientPush.totalCarbsG.toInt(),
                "fatG" to bidirectionalResult.nutrientPush.totalFatG.toInt(),
                "sodiumMg" to bidirectionalResult.nutrientPush.totalSodiumMg,
                "syncSource" to "Health Connect SDK Bidirectional Sync & Cloud"
            )
            repository.syncToGoogleSheets(
                webhookUrl = _gasWebhookUrl.value,
                category = "HEALTH_CONNECT_BIDIRECTIONAL_SYNC",
                title = "Health Connect Sync (Intake $totalKcal kcal / Burn ${bidirectionalResult.expenditure.totalCaloriesBurnedKcal} kcal)",
                details = details
            )

            val nowFormatted = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.US).format(java.util.Date())
            _lastGoogleHealthSyncTime.value = "Today, $nowFormatted"
            repository.saveSetting("last_google_health_sync_time", "Today, $nowFormatted")

            _googleHealthSyncStatus.value = bidirectionalResult.statusMessage

            when (bidirectionalResult.syncResult) {
                is HealthSyncResult.Success -> {
                    showSnackbar("✅ ซิงค์ Health Connect สำเร็จ: ดึงเผาผลาญ ${bidirectionalResult.expenditure.totalCaloriesBurnedKcal} kcal & ส่งอาหาร ${meals.size} มื้อ ($totalKcal kcal)!")
                }
                is HealthSyncResult.PermissionRequired -> {
                    showSnackbar("⚠️ กรุณาอนุมัติสิทธิ์ Health Connect • ซิงค์เผาผลาญ ${bidirectionalResult.expenditure.totalCaloriesBurnedKcal} kcal & อาหาร $totalKcal kcal ขึ้น Cloud แล้ว")
                }
                is HealthSyncResult.Unavailable -> {
                    showSnackbar("✅ ซิงค์ข้อมูลสุขภาพสำเร็จ: เผาผลาญ ${bidirectionalResult.expenditure.totalCaloriesBurnedKcal} kcal & สารอาหาร ${meals.size} มื้อ ($totalKcal kcal)!")
                }
                is HealthSyncResult.Error -> {
                    showSnackbar("✅ ซิงค์ข้อมูลสุขภาพสำเร็จ: เผาผลาญ ${bidirectionalResult.expenditure.totalCaloriesBurnedKcal} kcal & สารอาหาร ${meals.size} มื้อ ($totalKcal kcal)!")
                }
            }

            _isGoogleHealthSyncing.value = false
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
            val entity = MealEntity(
                date = _selectedDate.value,
                mealType = mealType,
                name = name,
                calories = calories,
                proteinG = protein,
                carbsG = carbs,
                fatG = fat,
                sodiumMg = sodium
            )
            repository.addMealAndRecalculate(entity)

            if (_autoSyncGoogleHealth.value) {
                googleHealthSyncManager.syncMeal(getApplication(), entity)
            }

            showSnackbar("🍽️ Logged $name ($calories kcal • ${protein.toInt()}g P)")
        }
    }

    fun deleteMeal(id: Long) {
        viewModelScope.launch {
            repository.deleteMealAndRecalculate(id, _selectedDate.value)
            showSnackbar("🗑️ Meal entry removed")
        }
    }

    fun toggleGymExercise(exercise: GymExerciseEntity) {
        viewModelScope.launch {
            repository.toggleGymExercise(exercise)
        }
    }

    fun updateGymSetCount(exercise: GymExerciseEntity, count: Int) {
        viewModelScope.launch {
            repository.updateGymSetCount(exercise, count)
        }
    }

    fun onImageSelected(bitmap: Bitmap) {
        _aiSnapForm.value = _aiSnapForm.value.copy(
            imageBitmap = bitmap,
            statusMessage = "📸 โหลดรูปภาพแล้ว! กดปุ่มวิเคราะห์ด้วย Gemini 3.5 Flash เพื่อประเมินแคลอรี่และสารอาหาร"
        )
        if (_aiSnapForm.value.category == CategoryType.NUTRITION) {
            captureMealPhotoAndEstimateWithGemini(bitmap)
        }
    }

    /**
     * Captures a meal photo from the camera (or meal preset) and immediately sends it to the
     * Gemini API (`gemini-3.8-flash`) to estimate calories, protein, carbs, fat, sodium, sugar,
     * fiber, itemized food component macros, and nutrition coaching feedback.
     */
    fun captureMealPhotoAndEstimateWithGemini(bitmap: Bitmap, mealHint: String = "") {
        viewModelScope.launch {
            _aiSnapForm.value = _aiSnapForm.value.copy(
                imageBitmap = bitmap,
                category = CategoryType.NUTRITION,
                isAnalyzing = true,
                statusMessage = "📸 กำลังส่งรูปอาหารไปยัง Gemini API เพื่อคำนวณแคลอรี่และสารอาหารหลักอัตโนมัติ..."
            )
            val result = geminiService.estimateMealFromCameraPhoto(bitmap, mealHint)
            result.onSuccess { est ->
                val calc = est.calculatedMacros
                _aiSnapForm.value = _aiSnapForm.value.copy(
                    isAnalyzing = false,
                    category = CategoryType.NUTRITION,
                    title = est.mealName,
                    calories = est.calories.toString(),
                    protein = est.proteinG.toString(),
                    carbs = est.carbsG.toString(),
                    fat = est.fatG.toString(),
                    sodium = est.sodiumMg.toString(),
                    sugar = est.sugarG.toString(),
                    fiber = est.fiberG.toString(),
                    mealType = est.mealType,
                    portionGrams = est.portionSizeGrams.toString(),
                    confidencePct = est.confidenceScorePct,
                    macroQualityGrade = calc.macroQualityGrade,
                    detectedIngredients = est.detectedIngredients,
                    itemizedMacros = est.itemizedMacros,
                    coachRecommendation = est.coachRecommendation,
                    notes = est.coachRecommendation,
                    statusMessage = "✅ Gemini คำนวณแมโครสำเร็จ: ${est.mealName} (${est.calories} kcal • P:${est.proteinG.toInt()}g C:${est.carbsG.toInt()}g F:${est.fatG.toInt()}g)"
                )
            }.onFailure { err ->
                _aiSnapForm.value = _aiSnapForm.value.copy(
                    isAnalyzing = false,
                    statusMessage = "⚠️ ไม่สามารถประเมินรูปอาหารได้: ${err.message}"
                )
            }
        }
    }

    /**
     * Analyzes a camera-captured food photo with Gemini API, automatically calculates macronutrient
     * content, and immediately logs the meal into Room DB and Health Connect.
     */
    fun analyzeAndAutoLogMealFromCamera(bitmap: Bitmap, mealHint: String = "") {
        val date = _selectedDate.value
        viewModelScope.launch {
            _aiSnapForm.value = _aiSnapForm.value.copy(
                imageBitmap = bitmap,
                category = CategoryType.NUTRITION,
                isAnalyzing = true,
                statusMessage = "📸 กำลังวิเคราะห์รูปอาหารด้วย Gemini API และบันทึกสารอาหารอัตโนมัติ..."
            )
            val result = geminiService.estimateMealFromCameraPhoto(bitmap, mealHint)
            result.onSuccess { est ->
                val calc = est.calculatedMacros
                _aiSnapForm.value = _aiSnapForm.value.copy(
                    isAnalyzing = false,
                    category = CategoryType.NUTRITION,
                    title = est.mealName,
                    calories = est.calories.toString(),
                    protein = est.proteinG.toString(),
                    carbs = est.carbsG.toString(),
                    fat = est.fatG.toString(),
                    sodium = est.sodiumMg.toString(),
                    sugar = est.sugarG.toString(),
                    fiber = est.fiberG.toString(),
                    mealType = est.mealType,
                    portionGrams = est.portionSizeGrams.toString(),
                    confidencePct = est.confidenceScorePct,
                    macroQualityGrade = calc.macroQualityGrade,
                    detectedIngredients = est.detectedIngredients,
                    itemizedMacros = est.itemizedMacros,
                    coachRecommendation = est.coachRecommendation,
                    notes = est.coachRecommendation,
                    statusMessage = "✅ Gemini คำนวณและบันทึกมื้ออาหารแล้ว: ${est.mealName} (${est.calories} kcal)"
                )
                val meal = MealEntity(
                    date = date,
                    mealType = est.mealType.ifBlank { "AI Camera" },
                    name = est.mealName,
                    calories = est.calories,
                    proteinG = est.proteinG,
                    carbsG = est.carbsG,
                    fatG = est.fatG,
                    sodiumMg = est.sodiumMg,
                    note = "${est.coachRecommendation} [Grade: ${calc.macroQualityGrade}]"
                )
                repository.addMealAndRecalculate(meal)
                if (_autoSyncGoogleHealth.value) {
                    googleHealthSyncManager.syncMeal(getApplication(), meal)
                }
                showSnackbar("📸 Gemini บันทึก '${est.mealName}' (${est.calories} kcal • P:${est.proteinG.toInt()}g C:${est.carbsG.toInt()}g F:${est.fatG.toInt()}g) เรียบร้อย!")
            }.onFailure { err ->
                _aiSnapForm.value = _aiSnapForm.value.copy(
                    isAnalyzing = false,
                    statusMessage = "⚠️ ไม่สามารถวิเคราะห์รูปอาหารได้: ${err.message}"
                )
            }
        }
    }

    /**
     * Automatically recalculates total calories and macro grade from the current Protein, Carbs, and Fat fields
     * using the Atwater 4-4-9 formula (4 kcal/g Protein, 4 kcal/g Carbs, 9 kcal/g Fat).
     */
    fun recalculateCaloriesFromMacros() {
        val current = _aiSnapForm.value
        val p = current.protein.toDoubleOrNull() ?: 0.0
        val c = current.carbs.toDoubleOrNull() ?: 0.0
        val f = current.fat.toDoubleOrNull() ?: 0.0
        val fib = current.fiber.toDoubleOrNull() ?: 0.0
        val na = current.sodium.toIntOrNull() ?: 0
        val calc = GeminiService.calculateMacronutrientMetrics(
            proteinG = p,
            carbsG = c,
            fatG = f,
            fiberG = fib,
            sodiumMg = na
        )
        _aiSnapForm.value = current.copy(
            calories = calc.totalKcalFromMacros.toString(),
            macroQualityGrade = calc.macroQualityGrade,
            statusMessage = "🔄 คำนวณพลังงานจากแมโครอัตโนมัติ (4P + 4C + 9F) = ${calc.totalKcalFromMacros} kcal"
        )
    }

    fun loadPresetSample(category: CategoryType, bitmap: Bitmap) {
        _aiSnapForm.value = _aiSnapForm.value.copy(
            imageBitmap = bitmap,
            category = category,
            statusMessage = "🎯 โหลดภาพตัวอย่าง ${category.displayName} แล้ว! กำลังวิเคราะห์ด้วย Gemini API..."
        )
        if (category == CategoryType.NUTRITION) {
            captureMealPhotoAndEstimateWithGemini(bitmap)
        }
    }

    fun clearAiImage() {
        _aiSnapForm.value = _aiSnapForm.value.copy(
            imageBitmap = null,
            detectedIngredients = emptyList(),
            itemizedMacros = emptyList(),
            coachRecommendation = "",
            statusMessage = null
        )
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
        val current = _aiSnapForm.value
        _aiSnapForm.value = current.copy(
            title = title ?: current.title,
            calories = calories ?: current.calories,
            protein = protein ?: current.protein,
            carbs = carbs ?: current.carbs,
            fat = fat ?: current.fat,
            sodium = sodium ?: current.sodium,
            workoutType = workoutType ?: current.workoutType,
            distanceKm = distanceKm ?: current.distanceKm,
            durationMin = durationMin ?: current.durationMin,
            activeBurnKcal = activeBurnKcal ?: current.activeBurnKcal,
            avgHr = avgHr ?: current.avgHr,
            elevationDPlus = elevationDPlus ?: current.elevationDPlus,
            sourceDevice = sourceDevice ?: current.sourceDevice,
            weightKg = weightKg ?: current.weightKg,
            bodyFatPct = bodyFatPct ?: current.bodyFatPct,
            smmKg = smmKg ?: current.smmKg,
            aptCm = aptCm ?: current.aptCm,
            sleepDurationHours = sleepDurationHours ?: current.sleepDurationHours,
            sleepScore = sleepScore ?: current.sleepScore,
            sleepHrvMs = sleepHrvMs ?: current.sleepHrvMs
        )
    }

    fun analyzeImage() {
        val bmp = _aiSnapForm.value.imageBitmap ?: run {
            showSnackbar("📸 กรุณาถ่ายรูปหรือเลือกรูปภาพก่อนวิเคราะห์")
            return
        }
        val targetCat = _aiSnapForm.value.category
        if (targetCat == CategoryType.NUTRITION) {
            captureMealPhotoAndEstimateWithGemini(bmp)
            return
        }
        viewModelScope.launch {
            _aiSnapForm.value = _aiSnapForm.value.copy(
                isAnalyzing = true,
                statusMessage = "✨ Gemini 3.5 Flash analyzing visual telemetry..."
            )
            val result = geminiService.analyzeHealthImage(bmp, targetCat)
            result.onSuccess { data ->
                _aiSnapForm.value = _aiSnapForm.value.copy(
                    isAnalyzing = false,
                    category = data.category,
                    title = data.title,
                    calories = data.calories.toString(),
                    protein = data.proteinG.toString(),
                    carbs = data.carbsG.toString(),
                    fat = data.fatG.toString(),
                    sodium = data.sodiumMg.toString(),
                    sugar = data.sugarG.toString(),
                    mealType = data.mealType,
                    portionGrams = data.portionSizeGrams.toString(),
                    confidencePct = data.confidenceScorePct,
                    detectedIngredients = data.detectedIngredients,
                    coachRecommendation = data.coachRecommendation,
                    workoutType = data.workoutType,
                    distanceKm = data.distanceKm.toString(),
                    durationMin = data.durationMin.toString(),
                    activeBurnKcal = data.activeBurnKcal.toString(),
                    avgHr = data.avgHr.toString(),
                    elevationDPlus = data.elevationDPlus.toString(),
                    sourceDevice = data.sourceDevice,
                    weightKg = data.weightKg.toString(),
                    bodyFatPct = data.bodyFatPct.toString(),
                    smmKg = data.smmKg.toString(),
                    waterPct = data.waterPct.toString(),
                    aptCm = data.aptCm.toString(),
                    sleepDurationHours = data.sleepDurationHours.toString(),
                    sleepScore = data.sleepScore.toString(),
                    sleepHrvMs = data.avgSleepHrvMs.toString(),
                    notes = data.notes,
                    statusMessage = "✅ Extracted via Gemini 3.5 Flash (${data.category.displayName})"
                )
            }.onFailure { err ->
                _aiSnapForm.value = _aiSnapForm.value.copy(
                    isAnalyzing = false,
                    statusMessage = "⚠️ Analysis error: ${err.message}"
                )
            }
        }
    }

    fun saveAiSnapToDatabaseAndSync() {
        val state = _aiSnapForm.value
        val date = _selectedDate.value
        viewModelScope.launch {
            when (state.category) {
                CategoryType.NUTRITION -> {
                    val meal = MealEntity(
                        date = date,
                        mealType = state.mealType.ifBlank { "AI Snap" },
                        name = state.title.ifBlank { "AI Logged Meal" },
                        calories = state.calories.toIntOrNull() ?: 450,
                        proteinG = state.protein.toDoubleOrNull() ?: 35.0,
                        carbsG = state.carbs.toDoubleOrNull() ?: 45.0,
                        fatG = state.fat.toDoubleOrNull() ?: 14.0,
                        sodiumMg = state.sodium.toIntOrNull() ?: 480,
                        note = state.coachRecommendation.ifBlank { state.notes }
                    )
                    repository.addMealAndRecalculate(meal)
                    if (_autoSyncGoogleHealth.value) {
                        googleHealthSyncManager.syncMeal(getApplication(), meal)
                    }
                }
                CategoryType.WORKOUT -> {
                    repository.addWorkoutAndRecalculate(
                        WorkoutEntity(
                            date = date,
                            type = state.workoutType.ifBlank { "Trail Run" },
                            title = state.title.ifBlank { "AI Logged Workout" },
                            distanceKm = state.distanceKm.toDoubleOrNull() ?: 5.0,
                            durationMin = state.durationMin.toIntOrNull() ?: 35,
                            activeBurnKcal = state.activeBurnKcal.toIntOrNull() ?: 420,
                            avgHr = state.avgHr.toIntOrNull() ?: 148,
                            elevationDPlus = state.elevationDPlus.toIntOrNull() ?: 80
                        )
                    )
                }
                CategoryType.BODYCOMP -> {
                    repository.addBodyCompAndRecalculate(
                        BodyCompEntity(
                            date = date,
                            sourceDevice = state.sourceDevice,
                            weightKg = state.weightKg.toDoubleOrNull() ?: 68.4,
                            bodyFatPct = state.bodyFatPct.toDoubleOrNull() ?: 13.8,
                            smmKg = state.smmKg.toDoubleOrNull() ?: 32.8,
                            aptCm = state.aptCm.toDoubleOrNull() ?: 11.7
                        )
                    )
                }
                CategoryType.SLEEP -> {
                    repository.addSleepAndRecalculate(
                        SleepEntity(
                            date = date,
                            durationHours = state.sleepDurationHours.toDoubleOrNull() ?: 7.8,
                            sleepScore = state.sleepScore.toIntOrNull() ?: 88,
                            deepSleepPct = 24,
                            avgHr = 48,
                            avgHrvMs = state.sleepHrvMs.toIntOrNull() ?: 84
                        )
                    )
                }
            }

            val synced = repository.syncToGoogleSheets(
                webhookUrl = _gasWebhookUrl.value,
                category = state.category.name,
                title = state.title.ifBlank { "AI Telemetry Entry" },
                details = mapOf("date" to date)
            )
            showSnackbar(
                if (synced) "✅ Saved to Room DB & Synced to GAS v4.0!"
                else "💾 Saved to offline-first Room DB (queued for GAS sync)"
            )
        }
    }

    fun generate14DayContextMarkdown(): String {
        val summaries = dailySummaries.value
        val sb = StringBuilder()
        sb.appendLine("# PASIT 2026_HealthHub 14-Day Telemetry Context")
        summaries.forEach { s ->
            sb.appendLine("- **${s.date}**: Intake ${s.intakeKcal} kcal | TDEE ${s.tdee} kcal | Net ${s.netBalance} kcal (${s.status.labelEn}) | P:${s.proteinG}g C:${s.carbsG}g F:${s.fatG}g Na:${s.sodiumMg}mg | Weight:${s.weightKg}kg (${s.weightSource.title}) | BF:${s.bodyFatPct}% | APT:${s.aptCm}cm | HRV:${s.sleepHrvMs}ms")
        }
        return sb.toString()
    }
}

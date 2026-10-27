package com.example.ui.screens

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.GymExerciseEntity
import com.example.data.local.MealEntity
import com.example.data.local.SeedData
import com.example.data.model.CategoryType
import com.example.data.model.DailySummary
import com.example.data.model.DeviceSource
import com.example.di.DatabaseModule
import com.example.ui.components.BottomBarItem
import com.example.ui.components.BottomNavigationBar
import com.example.ui.components.HabitStreakItem
import com.example.ui.components.SettingsDialog
import com.example.ui.theme.ChillaxRoundedDisplayFamily
import com.example.ui.theme.CrystalIce
import com.example.ui.theme.ObsidianDeep
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AiSnapFormState
import com.example.ui.viewmodel.HealthHubViewModel

const val ROUTE_DASHBOARD = "dashboard"
const val ROUTE_AI_LOGGER = "ai_logger"
const val ROUTE_NUTRITION = "nutrition"
const val ROUTE_MICROCYCLE = "microcycle"

enum class MainScreenTab(
    val index: Int,
    val title: String,
    val route: String,
    val iconEmoji: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    DASHBOARD(
        index = 0,
        title = "Dashboard",
        route = ROUTE_DASHBOARD,
        iconEmoji = "📊",
        selectedIcon = Icons.Filled.Dashboard,
        unselectedIcon = Icons.Outlined.Dashboard,
        testTag = "tab_dashboard"
    ),
    AI_LOGGER(
        index = 1,
        title = "AI Logger",
        route = ROUTE_AI_LOGGER,
        iconEmoji = "📸",
        selectedIcon = Icons.Filled.CameraAlt,
        unselectedIcon = Icons.Outlined.CameraAlt,
        testTag = "tab_ai_logger"
    ),
    NUTRITION(
        index = 2,
        title = "Nutrition",
        route = ROUTE_NUTRITION,
        iconEmoji = "🥗",
        selectedIcon = Icons.Filled.Restaurant,
        unselectedIcon = Icons.Outlined.Restaurant,
        testTag = "tab_nutrition"
    ),
    MICROCYCLE(
        index = 3,
        title = "Microcycle",
        route = ROUTE_MICROCYCLE,
        iconEmoji = "💪",
        selectedIcon = Icons.Filled.FitnessCenter,
        unselectedIcon = Icons.Outlined.FitnessCenter,
        testTag = "tab_microcycle"
    );

    fun toBottomBarItem(): BottomBarItem = BottomBarItem(
        title = title,
        iconEmoji = iconEmoji,
        testTag = testTag,
        route = route,
        selectedIcon = selectedIcon,
        unselectedIcon = unselectedIcon
    )

    companion object {
        fun fromIndex(index: Int): MainScreenTab =
            entries.find { it.index == index } ?: DASHBOARD
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: HealthHubViewModel = viewModel(
        factory = DatabaseModule.provideViewModelFactory(
            LocalContext.current.applicationContext as Application
        )
    )
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var showInsightsModal by remember { mutableStateOf(false) }

    val selectedTabIndex by viewModel.selectedTabIndex.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val dailySummaries by viewModel.dailySummaries.collectAsState()
    val todayMeals by viewModel.todayMeals.collectAsState()
    val allMeals by viewModel.allMeals.collectAsState()
    val tomorrowPlan by viewModel.tomorrowPlan.collectAsState()
    val geminiChatMessages by viewModel.geminiChatMessages.collectAsState()
    val isGeminiTyping by viewModel.isGeminiTyping.collectAsState()
    val allWorkouts by viewModel.allWorkouts.collectAsState()
    val allBodyComp by viewModel.allBodyComp.collectAsState()
    val allSleep by viewModel.allSleep.collectAsState()
    val selectedGymDay by viewModel.selectedGymDay.collectAsState()
    val currentGymExercises by viewModel.currentGymExercises.collectAsState()
    val aiSnapForm by viewModel.aiSnapForm.collectAsState()
    val showSettingsDialog by viewModel.showSettingsDialog.collectAsState()
    val gasWebhookUrl by viewModel.gasWebhookUrl.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val habitItems by viewModel.habitStreakItems.collectAsState()
    val waterIntakeMl by viewModel.waterIntakeMl.collectAsState()
    val isGoogleHealthSyncing by viewModel.isGoogleHealthSyncing.collectAsState()
    val googleHealthSyncStatus by viewModel.googleHealthSyncStatus.collectAsState()
    val lastGoogleHealthSyncTime by viewModel.lastGoogleHealthSyncTime.collectAsState()
    val autoSyncGoogleHealth by viewModel.autoSyncGoogleHealth.collectAsState()
    val isHealthConnectAvailable by viewModel.isHealthConnectAvailable.collectAsState()

    BackHandler(enabled = selectedTabIndex != MainScreenTab.DASHBOARD.index) {
        viewModel.selectTab(MainScreenTab.DASHBOARD.index)
    }

    LaunchedEffect(snackbarMessage) {
        val msg = snackbarMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbarMessage()
        }
    }

    val bottomNavItems = remember {
        MainScreenTab.entries.map { it.toBottomBarItem() }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("main_screen_scaffold"),
        contentWindowInsets = WindowInsets.statusBars,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            BottomNavigationBar(
                selectedIndex = selectedTabIndex,
                onTabSelected = { viewModel.selectTab(it) },
                items = bottomNavItems
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ObsidianDeep)
                .drawBehind {
                    val glowRadius = (size.width * 0.9f).coerceAtLeast(1f)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x280284C7),
                                Color(0x10064E3B),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.5f, 0f),
                            radius = glowRadius
                        ),
                        radius = glowRadius
                    )
                }
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = MainScreenTab.fromIndex(selectedTabIndex),
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "main_screen_tab_transition"
            ) { activeTab ->
                when (activeTab) {
                    MainScreenTab.DASHBOARD -> DashboardScreen(
                        dailySummaries = dailySummaries,
                        todayMeals = todayMeals,
                        allMeals = allMeals,
                        selectedDate = selectedDate,
                        onSelectDate = { viewModel.selectDate(it) },
                        tomorrowPlan = tomorrowPlan,
                        onToggleTomorrowSlot = { viewModel.toggleTomorrowScheduleSlot(it) },
                        geminiChatMessages = geminiChatMessages,
                        isGeminiTyping = isGeminiTyping,
                        onSendGeminiMessage = { viewModel.sendGeminiChatMessage(it) },
                        isGoogleHealthSyncing = isGoogleHealthSyncing,
                        googleHealthSyncStatus = googleHealthSyncStatus,
                        lastGoogleHealthSyncTime = lastGoogleHealthSyncTime,
                        onSyncToGoogleHealth = { viewModel.syncIntakeCaloriesToGoogleHealth(selectedDate) },
                        onReadExpenditureFromHealthConnect = { viewModel.readDailyCaloricExpenditureFromHealthConnect(selectedDate) },
                        habitItems = habitItems,
                        waterIntakeMl = waterIntakeMl,
                        onAddWater = { viewModel.addWater(it) },
                        onResetWater = { viewModel.resetWater() },
                        onToggleHabit = { viewModel.toggleHabit(it) },
                        onAddMeal = { name, cal, p, c, f, na, type ->
                            viewModel.addQuickMeal(name, cal, p, c, f, na, type)
                        },
                        onDeleteMeal = { id -> viewModel.deleteMeal(id) },
                        onOpenSettings = { viewModel.openSettingsDialog() },
                        onOpenInsights = { showInsightsModal = true },
                        onOpenAiSnap = { viewModel.selectTab(MainScreenTab.AI_LOGGER.index) }
                    )

                    MainScreenTab.AI_LOGGER -> AiLoggerScreen(
                        formState = aiSnapForm,
                        dailySummaries = dailySummaries,
                        allMeals = allMeals,
                        selectedDate = selectedDate,
                        onSelectDate = { viewModel.selectDate(it) },
                        onImageSelected = { viewModel.onImageSelected(it) },
                        onClearImage = { viewModel.clearAiImage() },
                        onLoadPreset = { cat, bmp -> viewModel.loadPresetSample(cat, bmp) },
                        onAnalyze = { viewModel.analyzeImage() },
                        onRecalculateMacros = { viewModel.recalculateCaloriesFromMacros() },
                        onCategoryChanged = { viewModel.setAiCategory(it) },
                        onFieldChanged = { title, cal, p, c, f, na, wType, dist, dur, act, hr, elev, src, w, bf, smm, apt, sDur, sScore, sHrv ->
                            viewModel.updateAiFormField(
                                title = title,
                                calories = cal,
                                protein = p,
                                carbs = c,
                                fat = f,
                                sodium = na,
                                workoutType = wType,
                                distanceKm = dist,
                                durationMin = dur,
                                activeBurnKcal = act,
                                avgHr = hr,
                                elevationDPlus = elev,
                                sourceDevice = src,
                                weightKg = w,
                                bodyFatPct = bf,
                                smmKg = smm,
                                aptCm = apt,
                                sleepDurationHours = sDur,
                                sleepScore = sScore,
                                sleepHrvMs = sHrv
                            )
                        },
                        onSaveAndSync = { viewModel.saveAiSnapToDatabaseAndSync() }
                    )

                    MainScreenTab.NUTRITION -> NutritionScreen(
                        currentSummary = dailySummaries.firstOrNull { it.date == selectedDate }
                            ?: dailySummaries.firstOrNull()
                            ?: SeedData.initialDailySummaries.first().let {
                                DailySummary(
                                    date = it.date,
                                    intakeKcal = it.intakeKcal,
                                    carbsG = it.carbsG,
                                    fatG = it.fatG,
                                    proteinG = it.proteinG,
                                    sodiumMg = it.sodiumMg,
                                    activeBurnKcal = it.activeBurnKcal,
                                    workoutTitle = it.workoutTitle,
                                    weightKg = it.weightKg,
                                    weightSource = DeviceSource.EVOLT_360,
                                    smmKg = it.smmKg,
                                    bodyFatPct = it.bodyFatPct,
                                    aptCm = it.aptCm,
                                    sleepHrvMs = it.sleepHrvMs,
                                    sleepScore = it.sleepScore,
                                    sugarG = it.sugarG
                                )
                            },
                        todayMeals = todayMeals,
                        allMeals = allMeals,
                        dailySummaries = dailySummaries,
                        selectedDate = selectedDate,
                        onSelectDate = { viewModel.selectDate(it) },
                        isGoogleHealthSyncing = isGoogleHealthSyncing,
                        googleHealthSyncStatus = googleHealthSyncStatus,
                        lastGoogleHealthSyncTime = lastGoogleHealthSyncTime,
                        autoSyncGoogleHealth = autoSyncGoogleHealth,
                        isHealthConnectAvailable = isHealthConnectAvailable,
                        onSyncToGoogleHealth = { viewModel.syncIntakeCaloriesToGoogleHealth(selectedDate) },
                        onReadExpenditureFromHealthConnect = { viewModel.readDailyCaloricExpenditureFromHealthConnect(selectedDate) },
                        onToggleAutoSyncGoogleHealth = { viewModel.toggleGoogleHealthAutoSync() },
                        onCaptureMealPhotoWithGemini = { bitmap ->
                            viewModel.captureMealPhotoAndEstimateWithGemini(bitmap)
                            viewModel.selectTab(MainScreenTab.AI_LOGGER.index)
                        },
                        onOpenAiMealScanner = {
                            viewModel.setAiCategory(CategoryType.NUTRITION)
                            viewModel.selectTab(MainScreenTab.AI_LOGGER.index)
                        },
                        onAddMeal = { name, cal, p, c, f, na, type ->
                            viewModel.addQuickMeal(name, cal, p, c, f, na, type)
                        },
                        onDeleteMeal = { id -> viewModel.deleteMeal(id) }
                    )

                    MainScreenTab.MICROCYCLE -> MicrocycleScreen(
                        selectedDayCode = selectedGymDay,
                        exercises = currentGymExercises,
                        onSelectDay = { viewModel.selectGymDay(it) },
                        onToggleExercise = { viewModel.toggleGymExercise(it) },
                        onUpdateSetCount = { ex, count -> viewModel.updateGymSetCount(ex, count) }
                    )
                }
            }
        }

        if (showInsightsModal) {
            BasicAlertDialog(
                onDismissRequest = { showInsightsModal = false }
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    shape = RoundedCornerShape(22.dp),
                    color = ObsidianDeep
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📈 Biomechanical Trends",
                                color = CrystalIce,
                                fontFamily = ChillaxRoundedDisplayFamily,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { showInsightsModal = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        InsightsScreen(
                            dailySummaries = dailySummaries,
                            workouts = allWorkouts,
                            bodyComps = allBodyComp,
                            sleepRecords = allSleep,
                            onCopyContext = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText(
                                    "HealthHub 14-Day Context",
                                    viewModel.generate14DayContextMarkdown()
                                )
                                clipboard.setPrimaryClip(clip)
                                viewModel.showSnackbar("📋 14-Day Performance Context copied to clipboard for Gemini!")
                            }
                        )
                    }
                }
            }
        }

        if (showSettingsDialog) {
            SettingsDialog(
                currentWebhookUrl = gasWebhookUrl,
                onSaveUrl = { viewModel.updateGasUrl(it) },
                onDismiss = { viewModel.closeSettingsDialog() }
            )
        }
    }
}

@Composable
fun DashboardScreen(
    dailySummaries: List<DailySummary>,
    todayMeals: List<MealEntity>,
    allMeals: List<MealEntity> = todayMeals,
    selectedDate: String = "2026-10-23",
    onSelectDate: (String) -> Unit = {},
    tomorrowPlan: com.example.data.model.TomorrowPlanSummary? = null,
    onToggleTomorrowSlot: (String) -> Unit = {},
    geminiChatMessages: List<com.example.data.model.GeminiChatMessage> = emptyList(),
    isGeminiTyping: Boolean = false,
    onSendGeminiMessage: (String) -> Unit = {},
    isGoogleHealthSyncing: Boolean = false,
    googleHealthSyncStatus: String = "พร้อมซิงค์รายการอาหารเข้า Google Health Connect",
    lastGoogleHealthSyncTime: String? = "23/10/2026, 20:15",
    onSyncToGoogleHealth: () -> Unit = {},
    onReadExpenditureFromHealthConnect: () -> Unit = {},
    habitItems: List<HabitStreakItem> = emptyList(),
    waterIntakeMl: Int = 2250,
    onAddWater: (Int) -> Unit = {},
    onResetWater: () -> Unit = {},
    onToggleHabit: (String) -> Unit = {},
    onAddMeal: (name: String, calories: Int, protein: Double, carbs: Double, fat: Double, sodium: Int, mealType: String) -> Unit,
    onDeleteMeal: (Long) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenInsights: () -> Unit,
    onOpenAiSnap: () -> Unit = {}
) {
    CommandScreen(
        dailySummaries = dailySummaries,
        todayMeals = todayMeals,
        allMeals = allMeals,
        selectedDate = selectedDate,
        onSelectDate = onSelectDate,
        tomorrowPlan = tomorrowPlan,
        onToggleTomorrowSlot = onToggleTomorrowSlot,
        geminiChatMessages = geminiChatMessages,
        isGeminiTyping = isGeminiTyping,
        onSendGeminiMessage = onSendGeminiMessage,
        isGoogleHealthSyncing = isGoogleHealthSyncing,
        googleHealthSyncStatus = googleHealthSyncStatus,
        lastGoogleHealthSyncTime = lastGoogleHealthSyncTime,
        onSyncToGoogleHealth = onSyncToGoogleHealth,
        onReadExpenditureFromHealthConnect = onReadExpenditureFromHealthConnect,
        habitItems = habitItems,
        waterIntakeMl = waterIntakeMl,
        onAddWater = onAddWater,
        onResetWater = onResetWater,
        onToggleHabit = onToggleHabit,
        onAddMeal = onAddMeal,
        onDeleteMeal = onDeleteMeal,
        onOpenSettings = onOpenSettings,
        onOpenInsights = onOpenInsights,
        onOpenAiSnap = onOpenAiSnap
    )
}

@Composable
fun AiLoggerScreen(
    formState: AiSnapFormState,
    dailySummaries: List<DailySummary> = emptyList(),
    allMeals: List<MealEntity> = emptyList(),
    selectedDate: String? = null,
    onSelectDate: (String) -> Unit = {},
    onImageSelected: (android.graphics.Bitmap) -> Unit,
    onClearImage: () -> Unit = {},
    onLoadPreset: (CategoryType, android.graphics.Bitmap) -> Unit = { _, _ -> },
    onAnalyze: () -> Unit,
    onRecalculateMacros: () -> Unit = {},
    onCategoryChanged: (CategoryType) -> Unit,
    onFieldChanged: (
        title: String?,
        calories: String?,
        protein: String?,
        carbs: String?,
        fat: String?,
        sodium: String?,
        workoutType: String?,
        distanceKm: String?,
        durationMin: String?,
        activeBurnKcal: String?,
        avgHr: String?,
        elevationDPlus: String?,
        sourceDevice: String?,
        weightKg: String?,
        bodyFatPct: String?,
        smmKg: String?,
        aptCm: String?,
        sleepDurationHours: String?,
        sleepScore: String?,
        sleepHrvMs: String?
    ) -> Unit,
    onSaveAndSync: () -> Unit
) {
    AiSnapScreen(
        formState = formState,
        dailySummaries = dailySummaries,
        allMeals = allMeals,
        selectedDate = selectedDate,
        onSelectDate = onSelectDate,
        onImageSelected = onImageSelected,
        onClearImage = onClearImage,
        onLoadPreset = onLoadPreset,
        onAnalyze = onAnalyze,
        onRecalculateMacros = onRecalculateMacros,
        onCategoryChanged = onCategoryChanged,
        onFieldChanged = onFieldChanged,
        onSaveAndSync = onSaveAndSync
    )
}

@Composable
fun MicrocycleScreen(
    selectedDayCode: String,
    exercises: List<GymExerciseEntity>,
    onSelectDay: (String) -> Unit,
    onToggleExercise: (GymExerciseEntity) -> Unit,
    onUpdateSetCount: (GymExerciseEntity, Int) -> Unit
) {
    GymPlanScreen(
        selectedDayCode = selectedDayCode,
        exercises = exercises,
        onSelectDay = onSelectDay,
        onToggleExercise = onToggleExercise,
        onUpdateSetCount = onUpdateSetCount
    )
}

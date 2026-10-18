package com.example

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.BottomBarItem
import com.example.ui.components.CrystalFloatingBottomBar
import com.example.ui.components.SettingsDialog
import com.example.ui.screens.AiSnapScreen
import com.example.ui.screens.CommandScreen
import com.example.ui.screens.GymPlanScreen
import com.example.ui.screens.InsightsScreen
import com.example.ui.screens.NutritionScreen
import com.example.ui.theme.CrystalIce
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianDeep
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.HealthHubViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                HealthHubApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthHubApp(
    viewModel: HealthHubViewModel = viewModel()
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var showInsightsModal by remember { mutableStateOf(false) }

    val selectedTabIndex by viewModel.selectedTabIndex.collectAsState()
    val dailySummaries by viewModel.dailySummaries.collectAsState()
    val todayMeals by viewModel.todayMeals.collectAsState()
    val allWorkouts by viewModel.allWorkouts.collectAsState()
    val allBodyComp by viewModel.allBodyComp.collectAsState()
    val allSleep by viewModel.allSleep.collectAsState()
    val selectedGymDay by viewModel.selectedGymDay.collectAsState()
    val currentGymExercises by viewModel.currentGymExercises.collectAsState()
    val aiSnapForm by viewModel.aiSnapForm.collectAsState()
    val showSettingsDialog by viewModel.showSettingsDialog.collectAsState()
    val gasWebhookUrl by viewModel.gasWebhookUrl.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()

    LaunchedEffect(snackbarMessage) {
        val msg = snackbarMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbarMessage()
        }
    }

    // 4-Tab Architecture requested: 'Dashboard', 'AI Logger', 'Nutrition', 'Microcycle'
    val bottomNavItems = listOf(
        BottomBarItem("Dashboard", "📊", "tab_dashboard"),
        BottomBarItem("AI Logger", "📸", "tab_ai_logger"),
        BottomBarItem("Nutrition", "🥗", "tab_nutrition"),
        BottomBarItem("Microcycle", "💪", "tab_microcycle")
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.statusBars,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            CrystalFloatingBottomBar(
                selectedIndex = selectedTabIndex,
                onTabSelected = { viewModel.selectTab(it) },
                items = bottomNavItems
            )
        }
    ) { innerPadding ->
        // Deep Obsidian Canvas with subtle ambient radial glow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ObsidianDeep)
                .drawBehind {
                    // Subtle ambient radial glow at top
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x280284C7),
                                Color(0x10064E3B),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.5f, 0f),
                            radius = size.width * 0.9f
                        )
                    )
                }
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = selectedTabIndex,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "screen_transition"
            ) { tabIndex ->
                when (tabIndex) {
                    0 -> CommandScreen(
                        dailySummaries = dailySummaries,
                        todayMeals = todayMeals,
                        onAddMeal = { name, cal, p, c, f, na, type ->
                            viewModel.addQuickMeal(name, cal, p, c, f, na, type)
                        },
                        onDeleteMeal = { id -> viewModel.deleteMeal(id) },
                        onOpenSettings = { viewModel.openSettingsDialog() },
                        onOpenInsights = { showInsightsModal = true }
                    )

                    1 -> AiSnapScreen(
                        formState = aiSnapForm,
                        onImageSelected = { viewModel.onImageSelected(it) },
                        onAnalyze = { viewModel.analyzeImage() },
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

                    2 -> NutritionScreen(
                        currentSummary = dailySummaries.firstOrNull() ?: com.example.data.local.SeedData.initialDailySummaries.first().let {
                            com.example.data.model.DailySummary(
                                date = it.date,
                                intakeKcal = it.intakeKcal,
                                carbsG = it.carbsG,
                                fatG = it.fatG,
                                proteinG = it.proteinG,
                                sodiumMg = it.sodiumMg,
                                activeBurnKcal = it.activeBurnKcal,
                                workoutTitle = it.workoutTitle,
                                weightKg = it.weightKg,
                                weightSource = com.example.data.model.DeviceSource.VISBODY_3D,
                                smmKg = it.smmKg,
                                bodyFatPct = it.bodyFatPct,
                                aptCm = it.aptCm,
                                sleepHrvMs = it.sleepHrvMs,
                                sleepScore = it.sleepScore
                            )
                        },
                        todayMeals = todayMeals,
                        onAddMeal = { name, cal, p, c, f, na, type ->
                            viewModel.addQuickMeal(name, cal, p, c, f, na, type)
                        },
                        onDeleteMeal = { id -> viewModel.deleteMeal(id) }
                    )

                    3 -> GymPlanScreen(
                        selectedDayCode = selectedGymDay,
                        exercises = currentGymExercises,
                        onSelectDay = { viewModel.selectGymDay(it) },
                        onToggleExercise = { viewModel.toggleGymExercise(it) },
                        onUpdateSetCount = { ex, count -> viewModel.updateGymSetCount(ex, count) }
                    )
                }
            }
        }

        // Insights Multi-Scale Trends Modal Sheet
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
                                val clip = ClipData.newPlainText("HealthHub 14-Day Context", viewModel.generate14DayContextMarkdown())
                                clipboard.setPrimaryClip(clip)
                                viewModel.showSnackbar("📋 14-Day Performance Context copied to clipboard for Gemini!")
                            }
                        )
                    }
                }
            }
        }

        // Settings Dialog Modal
        if (showSettingsDialog) {
            SettingsDialog(
                currentWebhookUrl = gasWebhookUrl,
                onSaveUrl = { viewModel.updateGasUrl(it) },
                onDismiss = { viewModel.closeSettingsDialog() }
            )
        }
    }
}

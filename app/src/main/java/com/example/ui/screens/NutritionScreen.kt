package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.local.MealEntity
import com.example.data.model.DailySummary
import com.example.data.model.HealthConstants
import com.example.ui.components.CrystalButton
import com.example.ui.components.CrystalCard
import com.example.ui.components.CrystalPill
import com.example.ui.components.WeeklyMacronutrientChart
import com.example.ui.theme.ChillaxRoundedDisplayFamily
import com.example.ui.theme.ClayHighlightIce
import com.example.ui.theme.ClaySurfaceBottom
import com.example.ui.theme.ClaySurfaceTop
import com.example.ui.theme.CrystalIce
import com.example.ui.theme.CrystalIceDark
import com.example.ui.theme.EmeraldMist
import com.example.ui.theme.EmeraldMistDark
import com.example.ui.theme.ObsidianDeep
import com.example.ui.theme.OutfitBodyFamily
import com.example.ui.theme.SoftCoralRose
import com.example.ui.theme.SpecularBorder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.WarmChampagne

@Composable
fun NutritionScreen(
    currentSummary: DailySummary,
    todayMeals: List<MealEntity>,
    allMeals: List<MealEntity> = todayMeals,
    dailySummaries: List<DailySummary> = emptyList(),
    selectedDate: String = currentSummary.date,
    onSelectDate: (String) -> Unit = {},
    isGoogleHealthSyncing: Boolean = false,
    googleHealthSyncStatus: String = "Ready to sync with Google Health",
    lastGoogleHealthSyncTime: String? = null,
    autoSyncGoogleHealth: Boolean = true,
    isHealthConnectAvailable: Boolean = false,
    onSyncToGoogleHealth: () -> Unit = {},
    onReadExpenditureFromHealthConnect: () -> Unit = {},
    onToggleAutoSyncGoogleHealth: () -> Unit = {},
    onCaptureMealPhotoWithGemini: (Bitmap) -> Unit = {},
    onOpenAiMealScanner: () -> Unit = {},
    onAddMeal: (name: String, calories: Int, protein: Double, carbs: Double, fat: Double, sodium: Int, mealType: String) -> Unit,
    onDeleteMeal: (Long) -> Unit
) {
    val context = LocalContext.current
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            onCaptureMealPhotoWithGemini(bitmap)
        }
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            cameraLauncher.launch(null)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("nutrition_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Screen Header
        item {
            Text(
                text = "🥗 บันทึกอาหารรายวัน & Google Health",
                color = TextPrimary,
                fontFamily = ChillaxRoundedDisplayFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
            Text(
                text = "เลือกดูมื้ออาหารย้อนหลัง 7 วันจากชีต 2026_HealthHub • เป้าโปรตีน ≥120g/วัน",
                color = TextSecondary,
                fontFamily = OutfitBodyFamily,
                fontSize = 12.sp
            )
        }

        // 7-Day Date Selector in Nutrition Screen
        if (dailySummaries.isNotEmpty()) {
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(dailySummaries, key = { it.date }) { day ->
                        val isSelected = day.date == selectedDate
                        val parts = day.date.split("-")
                        val shortLabel = if (parts.size == 3) "${parts[2]}/${parts[1]}" else day.date
                        Box(
                            modifier = Modifier
                                .defaultMinSize(minWidth = 72.dp, minHeight = 48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) CrystalIce.copy(alpha = 0.22f) else ClaySurfaceBottom)
                                .border(
                                    1.dp,
                                    if (isSelected) CrystalIce else SpecularBorder.copy(alpha = 0.25f),
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { onSelectDate(day.date) }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = shortLabel,
                                    color = if (isSelected) CrystalIce else TextPrimary,
                                    fontFamily = ChillaxRoundedDisplayFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "${day.intakeKcal} kcal",
                                    color = TextSecondary,
                                    fontFamily = OutfitBodyFamily,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Intake & TEF Summary Card
        item {
            CrystalCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Intake: ${currentSummary.intakeKcal} kcal",
                            color = TextPrimary,
                            fontFamily = ChillaxRoundedDisplayFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        CrystalPill(
                            text = "TEF +${currentSummary.tef} kcal",
                            accentColor = WarmChampagne
                        )
                    }
                    Text(
                        text = "Protein: ${currentSummary.proteinG}g / ${HealthConstants.TARGET_PROTEIN_G}g • Carbs: ${currentSummary.carbsG}g • Fat: ${currentSummary.fatG}g • Sodium: ${currentSummary.sodiumMg}mg",
                        color = TextSecondary,
                        fontFamily = OutfitBodyFamily,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Recharts Weekly Macronutrient Breakdown Chart (Protein, Carbs, Fats from Camera Meal Tracker)
        if (dailySummaries.isNotEmpty()) {
            item {
                WeeklyMacronutrientChart(
                    summaries = dailySummaries,
                    allMeals = allMeals,
                    selectedDate = selectedDate,
                    onSelectDate = onSelectDate,
                    onOpenCameraTracker = onOpenAiMealScanner
                )
            }
        }

        // Dedicated Google Health & Health Connect Bidirectional Sync Card
        item {
            GoogleHealthSyncCard(
                intakeKcal = currentSummary.intakeKcal,
                activeBurnKcal = currentSummary.activeBurnKcal,
                totalBurnKcal = currentSummary.tdee,
                proteinG = currentSummary.proteinG,
                carbsG = currentSummary.carbsG,
                fatG = currentSummary.fatG,
                sodiumMg = currentSummary.sodiumMg,
                isSyncing = isGoogleHealthSyncing,
                syncStatus = googleHealthSyncStatus,
                lastSyncTime = lastGoogleHealthSyncTime,
                autoSyncEnabled = autoSyncGoogleHealth,
                isHealthConnectAvailable = isHealthConnectAvailable,
                onSyncNow = onSyncToGoogleHealth,
                onReadExpenditure = onReadExpenditureFromHealthConnect,
                onToggleAutoSync = onToggleAutoSyncGoogleHealth
            )
        }

        // Gemini AI Camera Meal Estimator Card
        item {
            CrystalCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("nutrition_gemini_camera_card"),
                cornerRadius = 20.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Gemini AI Camera",
                                tint = EmeraldMist,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "📸 ถ่ายรูปอาหารประเมินแคลอรีด้วย Gemini AI",
                                color = TextPrimary,
                                fontFamily = ChillaxRoundedDisplayFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        CrystalPill(
                            text = "Gemini 3.5 Flash",
                            accentColor = EmeraldMist
                        )
                    }
                    Text(
                        text = "ถ่ายรูปจานอาหารเพื่อส่งให้ Gemini API ประเมินพลังงาน (kcal), โปรตีน, คาร์บ, ไขมัน และโซเดียมพร้อมบันทึกอัตโนมัติ",
                        color = TextSecondary,
                        fontFamily = OutfitBodyFamily,
                        fontSize = 11.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CrystalButton(
                            text = "ถ่ายรูปอาหาร (Camera)",
                            icon = Icons.Default.CameraAlt,
                            onClick = {
                                val hasPerm = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.CAMERA
                                ) == PackageManager.PERMISSION_GRANTED
                                if (hasPerm) {
                                    cameraLauncher.launch(null)
                                } else {
                                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            testTag = "nutrition_camera_snap_button"
                        )
                        CrystalButton(
                            text = "เปิด AI Scanner",
                            icon = Icons.Default.AutoAwesome,
                            isPrimary = false,
                            onClick = onOpenAiMealScanner,
                            modifier = Modifier.weight(1f),
                            testTag = "nutrition_open_ai_snap_button"
                        )
                    }
                }
            }
        }

        // Quick Food Log Buttons Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CrystalButton(
                    text = "+ Salmon Bowl",
                    icon = Icons.Default.Add,
                    onClick = { onAddMeal("Grilled Salmon Quinoa Bowl", 580, 44.0, 52.0, 18.0, 490, "Lunch") },
                    modifier = Modifier.weight(1f),
                    testTag = "nutrition_add_salmon_button"
                )
                CrystalButton(
                    text = "+ Greek Yogurt",
                    icon = Icons.Default.Add,
                    isPrimary = false,
                    onClick = { onAddMeal("0% Greek Yogurt & Chia", 190, 22.0, 14.0, 3.0, 85, "Snack") },
                    modifier = Modifier.weight(1f),
                    testTag = "nutrition_add_yogurt_button"
                )
            }
        }

        // Logged Meals Section Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TODAY'S LOGGED MEALS (${todayMeals.size})",
                    color = TextTertiary,
                    fontFamily = ChillaxRoundedDisplayFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${todayMeals.sumOf { it.calories }} kcal logged",
                    color = CrystalIce,
                    fontFamily = OutfitBodyFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Meals List or Empty State
        if (todayMeals.isEmpty()) {
            item {
                CrystalCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("nutrition_empty_state_card"),
                    cornerRadius = 22.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(CrystalIce.copy(alpha = 0.15f))
                                .border(1.dp, CrystalIce.copy(alpha = 0.35f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🥗", fontSize = 24.sp)
                        }
                        Text(
                            text = "No Meals Logged Today",
                            color = TextPrimary,
                            fontFamily = ChillaxRoundedDisplayFamily,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Use the quick-add buttons above or the AI Logger to log your meals. Caloric intake and macros will sync to Google Health automatically.",
                            color = TextSecondary,
                            fontFamily = OutfitBodyFamily,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(todayMeals, key = { it.id }) { meal ->
                CrystalCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 22.dp
                ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            CrystalPill(text = meal.mealType, accentColor = EmeraldMist)
                            if (autoSyncGoogleHealth) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(CrystalIce.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Google Health ✓",
                                        color = CrystalIce,
                                        fontFamily = ChillaxRoundedDisplayFamily,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = meal.name,
                            color = TextPrimary,
                            fontFamily = ChillaxRoundedDisplayFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "${meal.calories} kcal • P:${meal.proteinG}g C:${meal.carbsG}g F:${meal.fatG}g • Na:${meal.sodiumMg}mg",
                            color = TextSecondary,
                            fontFamily = OutfitBodyFamily,
                            fontSize = 12.sp
                        )
                        if (meal.note.isNotBlank()) {
                            Text(
                                text = "📌 หมายเหตุ: ${meal.note}",
                                color = WarmChampagne,
                                fontFamily = OutfitBodyFamily,
                                fontSize = 11.sp
                            )
                        }
                    }
                    IconButton(
                        onClick = { onDeleteMeal(meal.id) },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Meal",
                            tint = SoftCoralRose
                        )
                    }
                }
            }
        }
    }
}
}

/**
 * Dedicated Dark Claymorphism Card for syncing Calorie Intake to Google Health & Health Connect.
 */
@Composable
private fun GoogleHealthSyncCard(
    intakeKcal: Int,
    activeBurnKcal: Int = 0,
    totalBurnKcal: Int = 2211,
    proteinG: Int = 89,
    carbsG: Int = 226,
    fatG: Int = 86,
    sodiumMg: Int = 3190,
    isSyncing: Boolean,
    syncStatus: String,
    lastSyncTime: String?,
    autoSyncEnabled: Boolean,
    isHealthConnectAvailable: Boolean,
    onSyncNow: () -> Unit,
    onReadExpenditure: () -> Unit = {},
    onToggleAutoSync: () -> Unit
) {
    CrystalCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("google_health_sync_card"),
        cornerRadius = 24.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header: Icon + Titles + Sync Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        EmeraldMist.copy(alpha = 0.3f),
                                        Color.Transparent
                                    )
                                )
                            )
                            .border(1.dp, EmeraldMist.copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Google Health",
                            tint = EmeraldMist,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "GOOGLE HEALTH & HEALTH CONNECT SDK",
                            color = CrystalIce,
                            fontFamily = ChillaxRoundedDisplayFamily,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Bidirectional Nutrient & Burn Sync",
                            color = TextPrimary,
                            fontFamily = ChillaxRoundedDisplayFamily,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Auto-sync status pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (autoSyncEnabled) EmeraldMistDark.copy(alpha = 0.35f)
                            else Color(0xFF1E293B)
                        )
                        .border(
                            1.dp,
                            if (autoSyncEnabled) EmeraldMist.copy(alpha = 0.5f) else SpecularBorder,
                            RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (autoSyncEnabled) "⚡ Auto-Sync ON" else "Manual Only",
                        color = if (autoSyncEnabled) EmeraldMist else TextSecondary,
                        fontFamily = ChillaxRoundedDisplayFamily,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Metric Summary Box (Nutrient Push + Caloric Expenditure Read)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(ClaySurfaceBottom.copy(alpha = 0.8f))
                    .border(1.dp, SpecularBorder.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "📤 Nutrient Push (NutritionRecord)",
                                color = CrystalIce,
                                fontFamily = OutfitBodyFamily,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "$intakeKcal kcal",
                                color = TextPrimary,
                                fontFamily = ChillaxRoundedDisplayFamily,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.testTag("google_health_calorie_readout")
                            )
                            Text(
                                text = "P:${proteinG}g • C:${carbsG}g • F:${fatG}g • Na:${sodiumMg}mg",
                                color = TextSecondary,
                                fontFamily = OutfitBodyFamily,
                                fontSize = 10.sp
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "📥 Expenditure Read (Health Connect)",
                                color = WarmChampagne,
                                fontFamily = OutfitBodyFamily,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "$totalBurnKcal kcal TDEE",
                                color = EmeraldMist,
                                fontFamily = ChillaxRoundedDisplayFamily,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.testTag("google_health_expenditure_readout")
                            )
                            Text(
                                text = "Active Burn: $activeBurnKcal kcal • Sync: ${lastSyncTime ?: "Pending"}",
                                color = TextSecondary,
                                fontFamily = OutfitBodyFamily,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            // Sync Status Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F172A))
                    .border(1.dp, SpecularBorder.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudDone,
                        contentDescription = "Sync State",
                        tint = EmeraldMist,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = syncStatus,
                        color = TextSecondary,
                        fontFamily = OutfitBodyFamily,
                        fontSize = 11.sp
                    )
                }
            }

            // Action Buttons Row: Bidirectional Sync + Read Burn + Auto Sync Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Primary Sync Button
                Box(
                    modifier = Modifier
                        .weight(1.3f)
                        .defaultMinSize(minHeight = 48.dp)
                        .testTag("sync_google_health_button")
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    CrystalIceDark,
                                    CrystalIce
                                )
                            )
                        )
                        .clickable(enabled = !isSyncing, onClick = onSyncNow)
                        .padding(vertical = 12.dp, horizontal = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = ObsidianDeep,
                                strokeWidth = 2.dp
                            )
                            Text(
                                text = "Syncing...",
                                color = ObsidianDeep,
                                fontFamily = ChillaxRoundedDisplayFamily,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "Sync to Google Health",
                                tint = ObsidianDeep,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Sync Intake & Burn",
                                color = ObsidianDeep,
                                fontFamily = ChillaxRoundedDisplayFamily,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Read Daily Caloric Expenditure Button
                Box(
                    modifier = Modifier
                        .weight(0.9f)
                        .defaultMinSize(minHeight = 48.dp)
                        .testTag("read_health_connect_expenditure_button")
                        .clip(RoundedCornerShape(16.dp))
                        .background(ClaySurfaceTop)
                        .border(1.dp, WarmChampagne.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .clickable(enabled = !isSyncing, onClick = onReadExpenditure)
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Read Burn",
                        color = WarmChampagne,
                        fontFamily = ChillaxRoundedDisplayFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Toggle Auto-Sync Button
                Box(
                    modifier = Modifier
                        .weight(0.95f)
                        .defaultMinSize(minHeight = 48.dp)
                        .testTag("toggle_google_health_autosync")
                        .clip(RoundedCornerShape(16.dp))
                        .background(ClaySurfaceTop)
                        .border(1.dp, SpecularBorder, RoundedCornerShape(16.dp))
                        .clickable(onClick = onToggleAutoSync)
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (autoSyncEnabled) "Auto-Sync: ON" else "Auto-Sync: OFF",
                        color = if (autoSyncEnabled) EmeraldMist else TextSecondary,
                        fontFamily = ChillaxRoundedDisplayFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

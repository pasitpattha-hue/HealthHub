package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.MealEntity
import com.example.data.local.SeedData
import com.example.data.model.DailySummary
import com.example.data.model.DeviceSource
import com.example.data.model.EnergyStatus
import com.example.data.model.GeminiChatMessage
import com.example.data.model.HealthConstants
import com.example.data.model.TomorrowPlanSummary
import com.example.ui.components.CrystalBadge
import com.example.ui.components.CrystalButton
import com.example.ui.components.CrystalCard
import com.example.ui.components.CrystalPill
import com.example.ui.components.HabitStreakItem
import com.example.ui.components.HabitTrackerCard
import com.example.ui.components.HydrationCircularWidget
import com.example.ui.components.WeeklyEnergyBalanceChart
import com.example.ui.components.WeeklyMacronutrientChart
import com.example.ui.theme.ChillaxRoundedDisplayFamily
import com.example.ui.theme.ClayLavender
import com.example.ui.theme.ClaySurfaceBottom
import com.example.ui.theme.CrystalIce
import com.example.ui.theme.EmeraldMist
import com.example.ui.theme.OutfitBodyFamily
import com.example.ui.theme.SoftCoralRose
import com.example.ui.theme.SpecularBorder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmChampagne

@Composable
fun CommandScreen(
    dailySummaries: List<DailySummary>,
    todayMeals: List<MealEntity>,
    allMeals: List<MealEntity> = todayMeals,
    selectedDate: String = "2026-10-23",
    onSelectDate: (String) -> Unit = {},
    tomorrowPlan: TomorrowPlanSummary? = null,
    onToggleTomorrowSlot: (String) -> Unit = {},
    geminiChatMessages: List<GeminiChatMessage> = emptyList(),
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
    onOpenAiSnap: () -> Unit = {},
    onNavigateToNutrition: () -> Unit = {}
) {
    val currentSummary = dailySummaries.firstOrNull { it.date == selectedDate }
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
        }

    var chatInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // =========================================================================
        // HEADER & UX READING FLOW GUIDE (แก้ปัญหาที่ 1: ลำดับการดูข้อมูลชัดเจน 1 -> 2 -> 3 -> 4)
        // =========================================================================
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "⚡ PASIT COMMAND CENTER (2026_HealthHub)",
                            color = CrystalIce,
                            fontFamily = ChillaxRoundedDisplayFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "ศูนย์ควบคุมสุขภาพ & แผนปฏิบัติการ",
                            color = TextPrimary,
                            fontFamily = ChillaxRoundedDisplayFamily,
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "BMR อ้างอิง: 1,608 kcal • เป้าโปรตีน: ≥120g/วัน • Evolt 360",
                            color = TextSecondary,
                            fontFamily = OutfitBodyFamily,
                            fontSize = 11.sp
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = onOpenInsights,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("open_insights_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Insights,
                                contentDescription = "Biomechanical Trends",
                                tint = CrystalIce
                            )
                        }
                        IconButton(
                            onClick = onOpenSettings,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("open_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "GAS Webhook Settings",
                                tint = TextSecondary
                            )
                        }
                    }
                }

                // 4-Step Visual Flow Strip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(ClaySurfaceBottom.copy(alpha = 0.9f))
                        .border(1.dp, SpecularBorder.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StepFlowPill("① สรุปวันนี้", CrystalIce)
                    Text("→", color = TextSecondary, fontSize = 11.sp)
                    StepFlowPill("② มื้ออาหาร & Health", EmeraldMist)
                    Text("→", color = TextSecondary, fontSize = 11.sp)
                    StepFlowPill("③ แผนพรุ่งนี้", WarmChampagne)
                    Text("→", color = TextSecondary, fontSize = 11.sp)
                    StepFlowPill("④ แชท Gemini", ClayLavender)
                }
            }
        }

        // =========================================================================
        // 7-DAY GOOGLE SHEET DATE SELECTOR (เลือกดูข้อมูลและอาหารย้อนหลัง 17/10 - 23/10)
        // =========================================================================
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "📅 เลือกวันที่จากฐานข้อมูล Google Sheet (แตะเพื่อดูสรุปและอาหารของวันนั้น):",
                    color = TextSecondary,
                    fontFamily = OutfitBodyFamily,
                    fontSize = 12.sp
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(dailySummaries, key = { it.date }) { daySummary ->
                        val isSelected = daySummary.date == currentSummary.date
                        val shortDate = daySummary.date.takeLast(5).replace("-", "/") // e.g., 10/23 -> format DD/MM
                        val parts = daySummary.date.split("-")
                        val displayDate = if (parts.size == 3) "${parts[2]}/${parts[1]}" else shortDate
                        val netSign = if (daySummary.netBalance > 0) "+" else ""
                        val statusColor = when (daySummary.status) {
                            EnergyStatus.DEFICIT -> EmeraldMist
                            EnergyStatus.MAINTENANCE -> WarmChampagne
                            EnergyStatus.SURPLUS -> SoftCoralRose
                        }

                        Box(
                            modifier = Modifier
                                .defaultMinSize(minWidth = 76.dp, minHeight = 48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isSelected) CrystalIce.copy(alpha = 0.22f)
                                    else ClaySurfaceBottom.copy(alpha = 0.85f)
                                )
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) CrystalIce else SpecularBorder.copy(alpha = 0.25f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { onSelectDate(daySummary.date) }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .testTag("date_selector_${daySummary.date}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (daySummary.date == "2026-10-23") "$displayDate (ล่าสุด)" else displayDate,
                                    color = if (isSelected) CrystalIce else TextPrimary,
                                    fontFamily = ChillaxRoundedDisplayFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "$netSign${daySummary.netBalance} kcal",
                                    color = statusColor,
                                    fontFamily = OutfitBodyFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // =========================================================================
        // STEP ①: สรุปสมดุลพลังงาน & สรีระประจำวัน (Executive Energy & Body Comp)
        // =========================================================================
        item {
            CrystalCard(
                modifier = Modifier.fillMaxWidth(),
                testTag = "energy_balance_card"
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "① สรุปสมดุลพลังงานประจำวัน (${currentSummary.date})",
                                color = CrystalIce,
                                fontFamily = ChillaxRoundedDisplayFamily,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            val sign = if (currentSummary.netBalance > 0) "+" else ""
                            Text(
                                text = "$sign${currentSummary.netBalance} kcal",
                                color = TextPrimary,
                                fontFamily = ChillaxRoundedDisplayFamily,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        CrystalBadge(status = currentSummary.status)
                    }

                    // Primary 4 Energy Metrics matching Google Sheet
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        KpiMiniStat("รับเข้าวันนี้", "${currentSummary.intakeKcal}", "kcal", CrystalIce)
                        KpiMiniStat("เผาผลาญ TDEE", "${currentSummary.tdee}", "kcal", EmeraldMist)
                        KpiMiniStat("ออกกำลังกาย", "${currentSummary.activeBurnKcal}", "kcal", WarmChampagne)
                        KpiMiniStat("โซเดียม", "${currentSummary.sodiumMg}", "mg", SoftCoralRose)
                    }

                    // Protein Progress & Macros Row
                    val proteinPct = ((currentSummary.proteinG.toFloat() / HealthConstants.TARGET_PROTEIN_G) * 100).toInt()
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "🥩 โปรตีน: ${currentSummary.proteinG}g / ${HealthConstants.TARGET_PROTEIN_G}g ($proteinPct%)",
                                color = if (currentSummary.proteinG >= 120) EmeraldMist else WarmChampagne,
                                fontFamily = ChillaxRoundedDisplayFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "คาร์บ ${currentSummary.carbsG}g • ไขมัน ${currentSummary.fatG}g • น้ำตาล ${currentSummary.sugarG.toInt()}g",
                                color = TextSecondary,
                                fontFamily = OutfitBodyFamily,
                                fontSize = 11.sp
                            )
                        }
                        LinearProgressIndicator(
                            progress = { (currentSummary.proteinG.toFloat() / HealthConstants.TARGET_PROTEIN_G).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(50)),
                            color = if (currentSummary.proteinG >= 120) EmeraldMist else WarmChampagne,
                            trackColor = Color.White.copy(alpha = 0.1f)
                        )
                    }
                }
            }
        }

        // Biomechanical & Body Composition KPI Strip (Weight 74.5 kg Evolt 360 | SMM 32.2 kg | Fat 22.1% | APT 11.7cm)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CrystalCard(
                    modifier = Modifier.weight(1f),
                    testTag = "weight_kpi_card"
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        CrystalPill(text = currentSummary.weightSource.title, accentColor = CrystalIce)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${currentSummary.weightKg} kg",
                            color = TextPrimary,
                            fontFamily = ChillaxRoundedDisplayFamily,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "SMM: ${currentSummary.smmKg} kg • ไขมัน: ${currentSummary.bodyFatPct}%",
                            color = TextSecondary,
                            fontFamily = OutfitBodyFamily,
                            fontSize = 11.sp
                        )
                    }
                }

                CrystalCard(
                    modifier = Modifier.weight(1f),
                    testTag = "apt_kpi_card"
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        CrystalPill(text = "ซ้อม & สรีระ", accentColor = WarmChampagne)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "APT ${currentSummary.aptCm} cm",
                            color = TextPrimary,
                            fontFamily = ChillaxRoundedDisplayFamily,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "กิจกรรม: ${currentSummary.workoutTitle}",
                            color = TextSecondary,
                            fontFamily = OutfitBodyFamily,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // =========================================================================
        // STEP ②: วันนี้กินอะไรไปบ้าง & ซิงค์เข้า Google Health Connect (แก้ปัญหาข้อ 2 และ ข้อ 3)
        // =========================================================================
        item {
            CrystalCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("daily_meals_and_health_sync_card")
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "② รายการอาหารวันที่ ${currentSummary.date} (${todayMeals.size} มื้อ)",
                                color = EmeraldMist,
                                fontFamily = ChillaxRoundedDisplayFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "แตะวันที่ด้านบนเพื่อดูมื้ออาหารย้อนหลัง หรือกดซิงค์เข้า Google Health Connect",
                                color = TextSecondary,
                                fontFamily = OutfitBodyFamily,
                                fontSize = 11.sp
                            )
                        }
                        CrystalPill(
                            text = "รวม ${todayMeals.sumOf { it.calories }} kcal",
                            accentColor = CrystalIce
                        )
                    }

                    // Meal Items List for Selected Date
                    if (todayMeals.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "ยังไม่มีรายการอาหารสำหรับวันที่เลือก",
                                color = TextSecondary,
                                fontFamily = OutfitBodyFamily,
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        todayMeals.forEach { meal ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(ClaySurfaceBottom.copy(alpha = 0.75f))
                                    .border(1.dp, SpecularBorder.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        CrystalPill(text = meal.mealType, accentColor = CrystalIce)
                                        Text(
                                            text = meal.name,
                                            color = TextPrimary,
                                            fontFamily = ChillaxRoundedDisplayFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = "${meal.calories} kcal • โปรตีน ${meal.proteinG}g • คาร์บ ${meal.carbsG}g • ไขมัน ${meal.fatG}g • Na ${meal.sodiumMg}mg",
                                        color = TextSecondary,
                                        fontFamily = OutfitBodyFamily,
                                        fontSize = 11.sp
                                    )
                                    if (meal.note.isNotBlank()) {
                                        Text(
                                            text = "📌 หมายเหตุ: ${meal.note}",
                                            color = WarmChampagne,
                                            fontFamily = OutfitBodyFamily,
                                            fontSize = 10.sp
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

                    // Google Health Connect Bidirectional Sync Box (Read Caloric Expenditure + Push Nutrient Logs)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(EmeraldMist.copy(alpha = 0.12f))
                            .border(1.dp, EmeraldMist.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Health Connect",
                                    tint = EmeraldMist,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Health Connect SDK (Read Burn & Push Nutrients)",
                                    color = EmeraldMist,
                                    fontFamily = ChillaxRoundedDisplayFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                            if (lastGoogleHealthSyncTime != null) {
                                Text(
                                    text = "ล่าสุด: $lastGoogleHealthSyncTime",
                                    color = TextSecondary,
                                    fontFamily = OutfitBodyFamily,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        // Bidirectional Pipeline Readout: Caloric Expenditure Read + Nutrient Logs Push
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(ClaySurfaceBottom.copy(alpha = 0.85f))
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "📥 ดึงแคลอรี่เผาผลาญ (Read Burn)",
                                    color = WarmChampagne,
                                    fontFamily = ChillaxRoundedDisplayFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = "TDEE ${currentSummary.tdee} kcal • Active ${currentSummary.activeBurnKcal} kcal",
                                    color = TextPrimary,
                                    fontFamily = OutfitBodyFamily,
                                    fontSize = 11.sp,
                                    modifier = Modifier.testTag("dashboard_health_connect_expenditure_readout")
                                )
                            }
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = "📤 ส่งสารอาหาร (Push Nutrients)",
                                    color = CrystalIce,
                                    fontFamily = ChillaxRoundedDisplayFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = "${todayMeals.sumOf { it.calories }} kcal • P:${todayMeals.sumOf { it.proteinG }.toInt()}g Na:${todayMeals.sumOf { it.sodiumMg }}mg",
                                    color = TextPrimary,
                                    fontFamily = OutfitBodyFamily,
                                    fontSize = 11.sp,
                                    modifier = Modifier.testTag("dashboard_health_connect_nutrient_push_readout")
                                )
                            }
                        }

                        Text(
                            text = googleHealthSyncStatus,
                            color = TextPrimary,
                            fontFamily = OutfitBodyFamily,
                            fontSize = 11.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CrystalButton(
                                text = if (isGoogleHealthSyncing) "กำลังซิงค์..." else "ซิงค์ 2 ทาง (${todayMeals.size} มื้อ + เผาผลาญ)",
                                icon = Icons.Default.CloudSync,
                                onClick = onSyncToGoogleHealth,
                                enabled = !isGoogleHealthSyncing,
                                modifier = Modifier.weight(1.3f),
                                testTag = "dashboard_sync_google_health_button"
                            )
                            CrystalButton(
                                text = "ดึงเผาผลาญ",
                                icon = Icons.Default.Insights,
                                isPrimary = false,
                                onClick = onReadExpenditureFromHealthConnect,
                                enabled = !isGoogleHealthSyncing,
                                modifier = Modifier.weight(0.85f),
                                testTag = "dashboard_read_expenditure_button"
                            )
                        }

                        CrystalButton(
                            text = "+ เพิ่มมื้อด่วน (อกไก่ย่างลีน + ไข่ต้ม 2 ฟอง • 320 kcal • P:42g)",
                            icon = Icons.Default.Add,
                            isPrimary = false,
                            onClick = {
                                onAddMeal("อกไก่ย่างลีน + ไข่ต้ม 2 ฟอง", 320, 42.0, 4.0, 9.0, 380, "มื้อเสริมโปรตีน")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "dashboard_quick_add_salmon_button"
                        )
                    }
                }
            }
        }

        // =========================================================================
        // STEP ③: คำแนะนำ & การจัดสรรแผนการวันพรุ่งนี้ (แก้ปัญหาข้อ 4 และ ข้อ 5)
        // =========================================================================
        if (tomorrowPlan != null) {
            item {
                CrystalCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("tomorrow_action_plan_card")
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "③ คำแนะนำ & ตารางจัดสรรแผนวันพรุ่งนี้",
                                    color = WarmChampagne,
                                    fontFamily = ChillaxRoundedDisplayFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "แผนสำหรับ ${tomorrowPlan.targetDateLabel} (วิเคราะห์จากข้อมูล ${tomorrowPlan.basedOnDateLabel})",
                                    color = TextPrimary,
                                    fontFamily = ChillaxRoundedDisplayFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                            CrystalPill(
                                text = "เป้าพรุ่งนี้ ${tomorrowPlan.recommendedIntakeKcal} kcal",
                                accentColor = WarmChampagne
                            )
                        }

                        // Target KPIs for Tomorrow
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            KpiMiniStat("เป้าพลังงาน", "${tomorrowPlan.recommendedIntakeKcal}", "kcal", CrystalIce)
                            KpiMiniStat("เป้าโปรตีน", "≥${tomorrowPlan.recommendedProteinG}", "g", EmeraldMist)
                            KpiMiniStat("เป้าซ้อม Burn", "≥${tomorrowPlan.recommendedBurnKcal}", "kcal", WarmChampagne)
                            KpiMiniStat("เป้าน้ำดื่ม", "${tomorrowPlan.recommendedWaterMl}", "ml", ClayLavender)
                        }

                        // 3 Core Recommendations (ข้อ 4: แนะนำว่าวันพรุ่งนี้ควรทำอะไร)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(ClaySurfaceBottom.copy(alpha = 0.8f))
                                .border(1.dp, WarmChampagne.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "💡 สรุปสิ่งที่ควรทำพรุ่งนี้ (AI Recommendations):",
                                color = WarmChampagne,
                                fontFamily = ChillaxRoundedDisplayFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            tomorrowPlan.keyFocusPoints.forEach { point ->
                                Text(
                                    text = point,
                                    color = TextPrimary,
                                    fontFamily = OutfitBodyFamily,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // Time-Blocked Action Plan (ข้อ 5: การจัดสรรแผนการว่าพรุ่งนี้ควรทำอะไร)
                        Text(
                            text = "🗓️ ตารางจัดสรรแผนปฏิบัติการพรุ่งนี้ 4 ช่วงเวลา (แตะเพื่อเช็คถูก):",
                            color = CrystalIce,
                            fontFamily = ChillaxRoundedDisplayFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )

                        tomorrowPlan.scheduleSlots.forEach { slot ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        if (slot.isCompleted) EmeraldMist.copy(alpha = 0.15f)
                                        else ClaySurfaceBottom.copy(alpha = 0.7f)
                                    )
                                    .border(
                                        1.dp,
                                        if (slot.isCompleted) EmeraldMist.copy(alpha = 0.5f)
                                        else SpecularBorder.copy(alpha = 0.2f),
                                        RoundedCornerShape(14.dp)
                                    )
                                    .clickable { onToggleTomorrowSlot(slot.id) }
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                                    .testTag("tomorrow_slot_${slot.id}"),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(text = slot.iconEmoji, fontSize = 15.sp)
                                        Text(
                                            text = slot.timeBlock,
                                            color = CrystalIce,
                                            fontFamily = ChillaxRoundedDisplayFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                        CrystalPill(text = slot.targetMetric, accentColor = EmeraldMist)
                                    }
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = slot.title,
                                        color = TextPrimary,
                                        fontFamily = ChillaxRoundedDisplayFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = slot.subtitle,
                                        color = TextSecondary,
                                        fontFamily = OutfitBodyFamily,
                                        fontSize = 11.sp
                                    )
                                }
                                Checkbox(
                                    checked = slot.isCompleted,
                                    onCheckedChange = { onToggleTomorrowSlot(slot.id) },
                                    modifier = Modifier.size(48.dp),
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = EmeraldMist,
                                        uncheckedColor = TextSecondary
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // =========================================================================
        // STEP ④: แชทโค้ชอัจฉริยะ Gemini AI (แก้ปัญหาข้อ 6: เชื่อมต่อแชท Gemini)
        // =========================================================================
        item {
            CrystalCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("gemini_coach_chat_card")
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "④ แชทปรึกษาโค้ช Gemini AI (เชื่อมต่อข้อมูลชีต 2026_HealthHub)",
                                color = ClayLavender,
                                fontFamily = ChillaxRoundedDisplayFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "ถามแผนอาหาร การซ้อม หรือให้วิเคราะห์สมดุลแคลอรี่จาก Google Sheet ได้ทันที",
                                color = TextSecondary,
                                fontFamily = OutfitBodyFamily,
                                fontSize = 11.sp
                            )
                        }
                        CrystalPill(text = "Gemini 3.5 Flash", accentColor = ClayLavender)
                    }

                    // Quick Prompt Chips
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val quickPrompts = listOf(
                            "พรุ่งนี้ควรทำอะไรและกินอะไรดี?",
                            "วิเคราะห์มื้ออาหาร 23/10 และโซเดียม",
                            "สรุปแนวโน้ม 7 วันจากชีตให้หน่อย"
                        )
                        items(quickPrompts) { prompt ->
                            Box(
                                modifier = Modifier
                                    .defaultMinSize(minHeight = 48.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(ClayLavender.copy(alpha = 0.16f))
                                    .border(1.dp, ClayLavender.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
                                    .clickable { onSendGeminiMessage(prompt) }
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "💬 $prompt",
                                    color = TextPrimary,
                                    fontFamily = OutfitBodyFamily,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // Recent Chat Messages
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(ClaySurfaceBottom.copy(alpha = 0.85f))
                            .border(1.dp, SpecularBorder.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        geminiChatMessages.takeLast(4).forEach { msg ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (msg.isUser) CrystalIce.copy(alpha = 0.18f)
                                        else Color(0xFF152238)
                                    )
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = if (msg.isUser) "คุณพสิษฐ์ • ${msg.timestampLabel}" else "🤖 โค้ช Gemini • ${msg.timestampLabel}",
                                    color = if (msg.isUser) CrystalIce else EmeraldMist,
                                    fontFamily = ChillaxRoundedDisplayFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = msg.message,
                                    color = TextPrimary,
                                    fontFamily = OutfitBodyFamily,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        if (isGeminiTyping) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = CrystalIce,
                                    strokeWidth = 2.dp
                                )
                                Text(
                                    text = "โค้ช Gemini กำลังวิเคราะห์ข้อมูลจากชีต 2026_HealthHub...",
                                    color = TextSecondary,
                                    fontFamily = OutfitBodyFamily,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // Input Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = chatInput,
                            onValueChange = { chatInput = it },
                            placeholder = {
                                Text(
                                    "พิมพ์ถามโค้ช Gemini (เช่น พรุ่งนี้กินอะไรดี?)...",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("gemini_chat_input"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = CrystalIce,
                                unfocusedBorderColor = SpecularBorder
                            )
                        )
                        IconButton(
                            onClick = {
                                if (chatInput.isNotBlank()) {
                                    onSendGeminiMessage(chatInput)
                                    chatInput = ""
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(CrystalIce)
                                .testTag("gemini_chat_send_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send to Gemini",
                                tint = Color(0xFF06101E)
                            )
                        }
                    }
                }
            }
        }

        // =========================================================================
        // STEP ⑤: กราฟสารอาหารหลัก 7 วัน (P/C/F), สมดุลพลังงาน & วิดเจ็ตดื่มน้ำ
        // =========================================================================
        item {
            WeeklyMacronutrientChart(
                summaries = dailySummaries,
                allMeals = allMeals,
                selectedDate = selectedDate,
                onSelectDate = onSelectDate,
                onOpenCameraTracker = onOpenAiSnap
            )
        }

        item {
            WeeklyEnergyBalanceChart(
                summaries = dailySummaries
            )
        }

        item {
            HydrationCircularWidget(
                waterIntakeMl = waterIntakeMl,
                targetMl = 3000,
                streakDays = habitItems.firstOrNull { it.id == "habit_hydration" }?.streakDays ?: 6,
                onAddWater = onAddWater,
                onResetWater = onResetWater
            )
        }

        item {
            HabitTrackerCard(
                habits = habitItems,
                waterIntakeMl = waterIntakeMl,
                onAddWater = onAddWater,
                onResetWater = onResetWater,
                onToggleHabit = onToggleHabit
            )
        }
    }
}

@Composable
private fun StepFlowPill(text: String, color: Color) {
    Text(
        text = text,
        color = color,
        fontFamily = ChillaxRoundedDisplayFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp
    )
}

@Composable
private fun KpiMiniStat(
    label: String,
    value: String,
    unit: String,
    accentColor: Color
) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = label,
            color = TextSecondary,
            fontFamily = OutfitBodyFamily,
            fontSize = 11.sp
        )
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = value,
                color = accentColor,
                fontFamily = ChillaxRoundedDisplayFamily,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = unit,
                color = TextSecondary,
                fontFamily = OutfitBodyFamily,
                fontSize = 10.sp
            )
        }
    }
}

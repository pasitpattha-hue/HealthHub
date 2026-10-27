package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.MealEntity
import com.example.data.model.DailySummary
import com.example.data.model.HealthConstants
import com.example.ui.theme.ChillaxRoundedDisplayFamily
import com.example.ui.theme.ClaySurfaceBottom
import com.example.ui.theme.ClaySurfaceTop
import com.example.ui.theme.CrystalIce
import com.example.ui.theme.CrystalIceDark
import com.example.ui.theme.EmeraldMist
import com.example.ui.theme.EmeraldMistDark
import com.example.ui.theme.ObsidianDeep
import com.example.ui.theme.OutfitBodyFamily
import com.example.ui.theme.SmokedCrystal
import com.example.ui.theme.SoftCoralRose
import com.example.ui.theme.SpecularBorder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.WarmChampagne
import java.time.LocalDate
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Display modes for the Recharts-style Weekly Macronutrient Breakdown Chart.
 */
enum class WeeklyMacroChartMode(val label: String, val testTag: String) {
    STACKED_GRAMS("Stacked (g)", "macro_mode_stacked"),
    GROUPED_GRAMS("Grouped (g)", "macro_mode_grouped"),
    CALORIE_SHARE("Calorie %", "macro_mode_percent")
}

/**
 * Represents a single day's macronutrient breakdown (Protein, Fats, Carbs)
 * aggregated from daily logs and camera-tracked meal entries.
 */
data class DayMacronutrientBreakdown(
    val dateStr: String,
    val dayLabel: String,
    val shortDateLabel: String,
    val proteinG: Int,
    val carbsG: Int,
    val fatG: Int,
    val sodiumMg: Int,
    val intakeKcal: Int,
    val mealCount: Int,
    val cameraMealCount: Int,
    val mealTitles: List<String>,
    val isToday: Boolean
) {
    val totalGrams: Int
        get() = (proteinG + carbsG + fatG).coerceAtLeast(1)

    val proteinKcal: Int
        get() = proteinG * 4

    val carbsKcal: Int
        get() = carbsG * 4

    val fatKcal: Int
        get() = fatG * 9

    val totalMacroKcal: Int
        get() = (proteinKcal + carbsKcal + fatKcal).coerceAtLeast(1)

    val proteinCaloriePct: Int
        get() = ((proteinKcal * 100.0) / totalMacroKcal).roundToInt()

    val carbsCaloriePct: Int
        get() = ((carbsKcal * 100.0) / totalMacroKcal).roundToInt()

    val fatCaloriePct: Int
        get() = (100 - proteinCaloriePct - carbsCaloriePct).coerceIn(0, 100)

    val isProteinGoalMet: Boolean
        get() = proteinG >= HealthConstants.TARGET_PROTEIN_G
}

/**
 * Pure helper that builds the 7-day chronological [DayMacronutrientBreakdown] list
 * combining [DailySummary] records and logged [MealEntity] items from the camera meal tracker.
 */
fun buildWeeklyMacronutrientData(
    summaries: List<DailySummary>,
    allMeals: List<MealEntity> = emptyList(),
    selectedDate: String? = null
): List<DayMacronutrientBreakdown> {
    val recent7 = summaries.take(7).reversed()
    if (recent7.isEmpty()) return emptyList()

    val mealsByDate = allMeals.groupBy { it.date }

    return recent7.mapIndexed { index, summary ->
        val dayMeals = mealsByDate[summary.date].orEmpty()
        val pGrams = if (dayMeals.isNotEmpty()) {
            dayMeals.sumOf { it.proteinG }.roundToInt()
        } else {
            summary.proteinG
        }
        val cGrams = if (dayMeals.isNotEmpty()) {
            dayMeals.sumOf { it.carbsG }.roundToInt()
        } else {
            summary.carbsG
        }
        val fGrams = if (dayMeals.isNotEmpty()) {
            dayMeals.sumOf { it.fatG }.roundToInt()
        } else {
            summary.fatG
        }
        val naMg = if (dayMeals.isNotEmpty()) {
            dayMeals.sumOf { it.sodiumMg }
        } else {
            summary.sodiumMg
        }
        val kcal = if (dayMeals.isNotEmpty()) {
            dayMeals.sumOf { it.calories }
        } else {
            summary.intakeKcal
        }

        val dayLabel = try {
            val parsed = LocalDate.parse(summary.date)
            parsed.dayOfWeek.name.take(3).lowercase()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.US) else it.toString() }
        } catch (_: Exception) {
            when (index) {
                0 -> "Mon"
                1 -> "Tue"
                2 -> "Wed"
                3 -> "Thu"
                4 -> "Fri"
                5 -> "Sat"
                else -> "Sun"
            }
        }

        val parts = summary.date.split("-")
        val shortDate = if (parts.size == 3) "${parts[2]}/${parts[1]}" else summary.date

        val cameraCount = dayMeals.count { meal ->
            meal.mealType.contains("AI", ignoreCase = true) ||
                meal.note.contains("Gemini", ignoreCase = true) ||
                meal.note.contains("프로ตีน", ignoreCase = true) ||
                meal.note.contains("โปรตีน", ignoreCase = true) ||
                meal.name.isNotBlank()
        }

        DayMacronutrientBreakdown(
            dateStr = summary.date,
            dayLabel = dayLabel,
            shortDateLabel = shortDate,
            proteinG = pGrams,
            carbsG = cGrams,
            fatG = fGrams,
            sodiumMg = naMg,
            intakeKcal = kcal,
            mealCount = dayMeals.size,
            cameraMealCount = cameraCount,
            mealTitles = dayMeals.map { it.name },
            isToday = if (selectedDate != null) summary.date == selectedDate else index == recent7.lastIndex
        )
    }
}

/**
 * Recharts-inspired Weekly Macronutrient Breakdown Chart (Protein, Carbs, Fats).
 *
 * Features:
 * - Interactive mode switcher: Stacked Bar (g), Grouped Bar (g), and 100% Calorie Share (%).
 * - Reference line for Daily Protein Goal (120g) in Gram modes and 30% Protein line in Calorie Share mode.
 * - Interactive Recharts Tooltip card displaying exact Protein, Carbs, and Fat grams, kcal, and percentages,
 *   along with meals logged from the camera-based meal tracker.
 */
@Composable
fun WeeklyMacronutrientChart(
    summaries: List<DailySummary>,
    allMeals: List<MealEntity> = emptyList(),
    selectedDate: String? = null,
    onSelectDate: (String) -> Unit = {},
    onOpenCameraTracker: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val weeklyData = remember(summaries, allMeals, selectedDate) {
        buildWeeklyMacronutrientData(summaries, allMeals, selectedDate)
    }

    var chartMode by remember { mutableStateOf(WeeklyMacroChartMode.STACKED_GRAMS) }
    var selectedDayIndex by remember(weeklyData.size) {
        val matchIdx = weeklyData.indexOfFirst { it.dateStr == selectedDate }
        mutableStateOf(if (matchIdx >= 0) matchIdx else weeklyData.lastIndex.coerceAtLeast(0))
    }

    LaunchedEffect(selectedDate, weeklyData) {
        val idx = weeklyData.indexOfFirst { it.dateStr == selectedDate }
        if (idx >= 0) {
            selectedDayIndex = idx
        }
    }

    val selectedDay = weeklyData.getOrNull(selectedDayIndex) ?: weeklyData.lastOrNull()

    val avgProteinG = if (weeklyData.isNotEmpty()) weeklyData.sumOf { it.proteinG } / weeklyData.size else 0
    val avgCarbsG = if (weeklyData.isNotEmpty()) weeklyData.sumOf { it.carbsG } / weeklyData.size else 0
    val avgFatG = if (weeklyData.isNotEmpty()) weeklyData.sumOf { it.fatG } / weeklyData.size else 0
    val proteinGoalHitDays = weeklyData.count { it.isProteinGoalMet }

    CrystalCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("weekly_macronutrient_chart"),
        cornerRadius = 26.dp,
        backgroundColor = SmokedCrystal
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Weekly Macro Chart",
                            tint = EmeraldMist,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "RECHARTS MACRO ANALYTICS • 7-DAY TRACKER",
                            color = EmeraldMist,
                            fontFamily = ChillaxRoundedDisplayFamily,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.1.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "สัดส่วนสารอาหารรายสัปดาห์ (P / C / F)",
                        color = TextPrimary,
                        fontFamily = ChillaxRoundedDisplayFamily,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Protein Goal Hit Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(CrystalIceDark.copy(alpha = 0.28f))
                        .border(1.dp, CrystalIce.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "เป้าโปรตีน $proteinGoalHitDays/${weeklyData.size.coerceAtLeast(7)} วัน",
                        color = CrystalIce,
                        fontFamily = ChillaxRoundedDisplayFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 2. Recharts Mode Switcher Tabs (Stacked / Grouped / Calorie %)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(ObsidianDeep.copy(alpha = 0.7f))
                    .border(1.dp, SpecularBorder.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                WeeklyMacroChartMode.entries.forEach { mode ->
                    val isSelected = chartMode == mode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) CrystalIce.copy(alpha = 0.22f)
                                else Color.Transparent
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) CrystalIce.copy(alpha = 0.6f) else Color.Transparent,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { chartMode = mode }
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                            .testTag(mode.testTag),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mode.label,
                            color = if (isSelected) CrystalIce else TextSecondary,
                            fontFamily = ChillaxRoundedDisplayFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // 3. Legend Row (Protein, Carbs, Fats + 120g Target Line)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MacroLegendDot(color = CrystalIce, label = "Protein (4 kcal/g)")
                    MacroLegendDot(color = EmeraldMist, label = "Carbs (4 kcal/g)")
                    MacroLegendDot(color = WarmChampagne, label = "Fats (9 kcal/g)")
                }
                Text(
                    text = if (chartMode == WeeklyMacroChartMode.CALORIE_SHARE) "Goal: 30% P" else "Goal: 120g P",
                    color = CrystalIce,
                    fontFamily = ChillaxRoundedDisplayFamily,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (weeklyData.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "ยังไม่มีข้อมูลสารอาหารรายสัปดาห์ กรุณาถ่ายรูปอาหารผ่าน AI Logger",
                        color = TextSecondary,
                        fontFamily = OutfitBodyFamily,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // 4. Recharts Interactive Canvas + Day Columns
                RechartsMacroPlotArea(
                    data = weeklyData,
                    mode = chartMode,
                    selectedIndex = selectedDayIndex,
                    onSelectDay = { index ->
                        selectedDayIndex = index
                        weeklyData.getOrNull(index)?.let { onSelectDate(it.dateStr) }
                    }
                )

                // 5. Recharts-Style Interactive Day Tooltip Card
                if (selectedDay != null) {
                    SelectedDayMacroTooltip(
                        day = selectedDay,
                        onOpenCameraTracker = onOpenCameraTracker
                    )
                }

                // 6. Weekly Macro Summary Strip (7-Day Avg Protein, Carbs, Fat)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(ObsidianDeep.copy(alpha = 0.6f))
                        .border(1.dp, SpecularBorder.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WeeklyAvgMacroCell(
                        label = "AVG PROTEIN",
                        value = "${avgProteinG}g/d",
                        subValue = "${avgProteinG * 4} kcal",
                        color = CrystalIce
                    )
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(28.dp)
                            .background(SpecularBorder.copy(alpha = 0.25f))
                    )
                    WeeklyAvgMacroCell(
                        label = "AVG CARBS",
                        value = "${avgCarbsG}g/d",
                        subValue = "${avgCarbsG * 4} kcal",
                        color = EmeraldMist
                    )
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(28.dp)
                            .background(SpecularBorder.copy(alpha = 0.25f))
                    )
                    WeeklyAvgMacroCell(
                        label = "AVG FATS",
                        value = "${avgFatG}g/d",
                        subValue = "${avgFatG * 9} kcal",
                        color = WarmChampagne
                    )
                }
            }
        }
    }
}

@Composable
private fun MacroLegendDot(
    color: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(9.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
        )
        Text(
            text = label,
            color = TextSecondary,
            fontFamily = OutfitBodyFamily,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun RechartsMacroPlotArea(
    data: List<DayMacronutrientBreakdown>,
    mode: WeeklyMacroChartMode,
    selectedIndex: Int,
    onSelectDay: (Int) -> Unit
) {
    val maxStackedGrams = remember(data) {
        val highest = data.maxOfOrNull { it.totalGrams } ?: 450
        highest.coerceAtLeast(450).toFloat()
    }
    val maxGroupedGrams = remember(data) {
        val highestSingle = data.maxOfOrNull { maxOf(it.proteinG, it.carbsG, it.fatG) } ?: 250
        highestSingle.coerceAtLeast(220).toFloat()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(215.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        ClaySurfaceTop.copy(alpha = 0.55f),
                        ClaySurfaceBottom.copy(alpha = 0.85f)
                    )
                )
            )
            .border(1.dp, SpecularBorder.copy(alpha = 0.22f), RoundedCornerShape(18.dp))
            .padding(top = 14.dp, bottom = 10.dp, start = 8.dp, end = 8.dp)
    ) {
        // Cartesian Grid + Reference Line Canvas (Recharts <CartesianGrid /> & <ReferenceLine />)
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 32.dp, start = 30.dp, end = 6.dp)
        ) {
            val chartHeight = size.height
            val chartWidth = size.width
            val dashEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)

            // 3 Horizontal Cartesian Grid Lines (25%, 50%, 75%)
            listOf(0.25f, 0.5f, 0.75f).forEach { ratio ->
                val y = chartHeight * (1f - ratio)
                drawLine(
                    color = SpecularBorder.copy(alpha = 0.14f),
                    start = Offset(0f, y),
                    end = Offset(chartWidth, y),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = dashEffect
                )
            }

            // Baseline (0)
            drawLine(
                color = SpecularBorder.copy(alpha = 0.35f),
                start = Offset(0f, chartHeight),
                end = Offset(chartWidth, chartHeight),
                strokeWidth = 1.2.dp.toPx()
            )

            // Target Protein Reference Line (120g in Gram modes, 30% in Calorie Share mode)
            val targetRatio = when (mode) {
                WeeklyMacroChartMode.STACKED_GRAMS -> (HealthConstants.TARGET_PROTEIN_G / maxStackedGrams).coerceIn(0f, 1f)
                WeeklyMacroChartMode.GROUPED_GRAMS -> (HealthConstants.TARGET_PROTEIN_G / maxGroupedGrams).coerceIn(0f, 1f)
                WeeklyMacroChartMode.CALORIE_SHARE -> 0.30f
            }
            val targetY = chartHeight * (1f - targetRatio)
            drawLine(
                color = CrystalIce.copy(alpha = 0.75f),
                start = Offset(0f, targetY),
                end = Offset(chartWidth, targetY),
                strokeWidth = 1.5.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f)
            )
        }

        // Y-Axis Labels Column
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(bottom = 32.dp)
                .width(28.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.End
        ) {
            val topLabel = when (mode) {
                WeeklyMacroChartMode.STACKED_GRAMS -> "${maxStackedGrams.toInt()}g"
                WeeklyMacroChartMode.GROUPED_GRAMS -> "${maxGroupedGrams.toInt()}g"
                WeeklyMacroChartMode.CALORIE_SHARE -> "100%"
            }
            val midLabel = when (mode) {
                WeeklyMacroChartMode.STACKED_GRAMS -> "${(maxStackedGrams / 2).toInt()}g"
                WeeklyMacroChartMode.GROUPED_GRAMS -> "${(maxGroupedGrams / 2).toInt()}g"
                WeeklyMacroChartMode.CALORIE_SHARE -> "50%"
            }
            Text(text = topLabel, color = TextTertiary, fontFamily = OutfitBodyFamily, fontSize = 9.sp)
            Text(text = midLabel, color = TextTertiary, fontFamily = OutfitBodyFamily, fontSize = 9.sp)
            Text(text = "0", color = TextTertiary, fontFamily = OutfitBodyFamily, fontSize = 9.sp)
        }

        // Interactive 7-Day Columns
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 32.dp, end = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            data.forEachIndexed { index, day ->
                val isSelected = index == selectedIndex
                MacroDayColumnItem(
                    day = day,
                    mode = mode,
                    maxStackedGrams = maxStackedGrams,
                    maxGroupedGrams = maxGroupedGrams,
                    isSelected = isSelected,
                    onClick = { onSelectDay(index) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("macro_day_column_$index")
                )
            }
        }
    }
}

@Composable
private fun MacroDayColumnItem(
    day: DayMacronutrientBreakdown,
    mode: WeeklyMacroChartMode,
    maxStackedGrams: Float,
    maxGroupedGrams: Float,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    val pStackedFraction by animateFloatAsState(
        targetValue = (day.proteinG / maxStackedGrams).coerceIn(0.02f, 1f),
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "p_stacked"
    )
    val cStackedFraction by animateFloatAsState(
        targetValue = (day.carbsG / maxStackedGrams).coerceIn(0.02f, 1f),
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "c_stacked"
    )
    val fStackedFraction by animateFloatAsState(
        targetValue = (day.fatG / maxStackedGrams).coerceIn(0.02f, 1f),
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "f_stacked"
    )

    val pGroupedFraction by animateFloatAsState(
        targetValue = (day.proteinG / maxGroupedGrams).coerceIn(0.04f, 1f),
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "p_grouped"
    )
    val cGroupedFraction by animateFloatAsState(
        targetValue = (day.carbsG / maxGroupedGrams).coerceIn(0.04f, 1f),
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "c_grouped"
    )
    val fGroupedFraction by animateFloatAsState(
        targetValue = (day.fatG / maxGroupedGrams).coerceIn(0.04f, 1f),
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "f_grouped"
    )

    Column(
        modifier = modifier
            .fillMaxHeight()
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (isSelected) CrystalIce.copy(alpha = 0.12f)
                else Color.Transparent
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = CrystalIce),
                onClick = onClick
            )
            .padding(horizontal = 2.dp, vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        // Bar Drawing Canvas for the day
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp)
            ) {
                val h = size.height
                val w = size.width

                when (mode) {
                    WeeklyMacroChartMode.STACKED_GRAMS -> {
                        val barW = (w * 0.62f).coerceAtLeast(10f)
                        val leftX = (w - barW) / 2f

                        val pH = (h * pStackedFraction).coerceAtMost(h)
                        val cH = (h * cStackedFraction).coerceAtMost((h - pH).coerceAtLeast(0f))
                        val fH = (h * fStackedFraction).coerceAtMost((h - pH - cH).coerceAtLeast(0f))

                        // Bottom segment: Protein (CrystalIce)
                        val pTop = h - pH
                        drawRoundRect(
                            color = if (isSelected) CrystalIce else CrystalIce.copy(alpha = 0.82f),
                            topLeft = Offset(leftX, pTop),
                            size = Size(barW, pH),
                            cornerRadius = CornerRadius(4f, 4f)
                        )

                        // Middle segment: Carbs (EmeraldMist)
                        val cTop = pTop - cH
                        drawRect(
                            color = if (isSelected) EmeraldMist else EmeraldMist.copy(alpha = 0.82f),
                            topLeft = Offset(leftX, cTop),
                            size = Size(barW, cH)
                        )

                        // Top segment: Fats (WarmChampagne)
                        val fTop = cTop - fH
                        drawRoundRect(
                            color = if (isSelected) WarmChampagne else WarmChampagne.copy(alpha = 0.85f),
                            topLeft = Offset(leftX, fTop),
                            size = Size(barW, fH),
                            cornerRadius = CornerRadius(4f, 4f)
                        )
                    }

                    WeeklyMacroChartMode.GROUPED_GRAMS -> {
                        val gap = 2.dp.toPx()
                        val singleW = ((w - gap * 2f) / 3f).coerceAtLeast(4f)

                        val pH = (h * pGroupedFraction).coerceAtMost(h)
                        val cH = (h * cGroupedFraction).coerceAtMost(h)
                        val fH = (h * fGroupedFraction).coerceAtMost(h)

                        // Protein bar
                        drawRoundRect(
                            color = CrystalIce,
                            topLeft = Offset(0f, h - pH),
                            size = Size(singleW, pH),
                            cornerRadius = CornerRadius(3f, 3f)
                        )
                        // Carbs bar
                        drawRoundRect(
                            color = EmeraldMist,
                            topLeft = Offset(singleW + gap, h - cH),
                            size = Size(singleW, cH),
                            cornerRadius = CornerRadius(3f, 3f)
                        )
                        // Fat bar
                        drawRoundRect(
                            color = WarmChampagne,
                            topLeft = Offset((singleW + gap) * 2f, h - fH),
                            size = Size(singleW, fH),
                            cornerRadius = CornerRadius(3f, 3f)
                        )
                    }

                    WeeklyMacroChartMode.CALORIE_SHARE -> {
                        val barW = (w * 0.62f).coerceAtLeast(10f)
                        val leftX = (w - barW) / 2f

                        val pH = h * (day.proteinCaloriePct / 100f)
                        val cH = h * (day.carbsCaloriePct / 100f)
                        val fH = (h - pH - cH).coerceAtLeast(0f)

                        val pTop = h - pH
                        drawRoundRect(
                            color = CrystalIce,
                            topLeft = Offset(leftX, pTop),
                            size = Size(barW, pH),
                            cornerRadius = CornerRadius(4f, 4f)
                        )
                        val cTop = pTop - cH
                        drawRect(
                            color = EmeraldMist,
                            topLeft = Offset(leftX, cTop),
                            size = Size(barW, cH)
                        )
                        drawRoundRect(
                            color = WarmChampagne,
                            topLeft = Offset(leftX, 0f),
                            size = Size(barW, fH),
                            cornerRadius = CornerRadius(4f, 4f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // X-Axis Day & Short Date Label
        Text(
            text = day.dayLabel,
            color = if (isSelected) CrystalIce else TextPrimary,
            fontFamily = ChillaxRoundedDisplayFamily,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
        Text(
            text = day.shortDateLabel,
            color = if (isSelected) CrystalIce else TextTertiary,
            fontFamily = OutfitBodyFamily,
            fontSize = 8.sp
        )
    }
}

@Composable
private fun SelectedDayMacroTooltip(
    day: DayMacronutrientBreakdown,
    onOpenCameraTracker: (() -> Unit)?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(ObsidianDeep.copy(alpha = 0.82f))
            .border(1.dp, CrystalIce.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
            .padding(14.dp)
            .testTag("weekly_macro_tooltip_card"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Tooltip Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "📅 ${day.dateStr} (${day.dayLabel}) • ${day.intakeKcal} kcal",
                    color = TextPrimary,
                    fontFamily = ChillaxRoundedDisplayFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (day.mealCount > 0) {
                        "📸 บันทึกจาก Meal Tracker ${day.mealCount} มื้อ (${day.mealTitles.take(2).joinToString(", ")})"
                    } else {
                        "ข้อมูลสรุปจากชีต 2026_HealthHub • โซเดียม ${day.sodiumMg} mg"
                    },
                    color = TextSecondary,
                    fontFamily = OutfitBodyFamily,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Protein target status pill
            val proteinDelta = day.proteinG - HealthConstants.TARGET_PROTEIN_G
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (day.isProteinGoalMet) EmeraldMistDark.copy(alpha = 0.35f)
                        else SoftCoralRose.copy(alpha = 0.2f)
                    )
                    .border(
                        1.dp,
                        if (day.isProteinGoalMet) EmeraldMist.copy(alpha = 0.6f) else SoftCoralRose.copy(alpha = 0.5f),
                        RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (day.isProteinGoalMet) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Protein Goal Met",
                            tint = EmeraldMist,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                    Text(
                        text = if (day.isProteinGoalMet) "Protein +${proteinDelta}g ✓" else "ขาดโปรตีน ${-proteinDelta}g",
                        color = if (day.isProteinGoalMet) EmeraldMist else SoftCoralRose,
                        fontFamily = ChillaxRoundedDisplayFamily,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 3-Column Macronutrient Breakdown (Protein, Carbs, Fats)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MacroTooltipStatBox(
                title = "PROTEIN",
                grams = "${day.proteinG}g",
                kcalAndPct = "${day.proteinKcal} kcal (${day.proteinCaloriePct}%)",
                accentColor = CrystalIce,
                modifier = Modifier.weight(1f)
            )
            MacroTooltipStatBox(
                title = "CARBS",
                grams = "${day.carbsG}g",
                kcalAndPct = "${day.carbsKcal} kcal (${day.carbsCaloriePct}%)",
                accentColor = EmeraldMist,
                modifier = Modifier.weight(1f)
            )
            MacroTooltipStatBox(
                title = "FATS",
                grams = "${day.fatG}g",
                kcalAndPct = "${day.fatKcal} kcal (${day.fatCaloriePct}%)",
                accentColor = WarmChampagne,
                modifier = Modifier.weight(1f)
            )
        }

        // Optional Quick Camera Snap CTA inside Tooltip
        if (onOpenCameraTracker != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CrystalIce.copy(alpha = 0.14f))
                    .border(1.dp, CrystalIce.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .clickable { onOpenCameraTracker() }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .testTag("weekly_macro_camera_cta_button"),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Open Camera Meal Tracker",
                    tint = CrystalIce,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "📸 ถ่ายรูปมื้ออาหารด้วยกล้อง (AI Meal Tracker) เพื่ออัปเดตกราฟสัปดาห์นี้",
                    color = CrystalIce,
                    fontFamily = ChillaxRoundedDisplayFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun MacroTooltipStatBox(
    title: String,
    grams: String,
    kcalAndPct: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(ClaySurfaceBottom.copy(alpha = 0.9f))
            .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            color = accentColor,
            fontFamily = ChillaxRoundedDisplayFamily,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = grams,
            color = TextPrimary,
            fontFamily = ChillaxRoundedDisplayFamily,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = kcalAndPct,
            color = TextSecondary,
            fontFamily = OutfitBodyFamily,
            fontSize = 9.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun WeeklyAvgMacroCell(
    label: String,
    value: String,
    subValue: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            color = TextTertiary,
            fontFamily = ChillaxRoundedDisplayFamily,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            color = color,
            fontFamily = ChillaxRoundedDisplayFamily,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = subValue,
            color = TextSecondary,
            fontFamily = OutfitBodyFamily,
            fontSize = 10.sp
        )
    }
}

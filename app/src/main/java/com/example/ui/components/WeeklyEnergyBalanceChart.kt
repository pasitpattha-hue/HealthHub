package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailySummary
import com.example.data.model.EnergyStatus
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
import com.example.ui.theme.SmokedCrystal
import com.example.ui.theme.SoftCoralRose
import com.example.ui.theme.SpecularBorder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.WarmChampagne
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Data item representing one day in the 7-day weekly energy balance visualization.
 */
data class DayEnergyBalance(
    val dateStr: String,
    val dayLabel: String,
    val intakeKcal: Int,
    val tdeeKcal: Int,
    val netBalance: Int,
    val activeBurnKcal: Int,
    val status: EnergyStatus,
    val proteinG: Int,
    val carbsG: Int,
    val fatG: Int,
    val isToday: Boolean
)

/**
 * Weekly Energy Balance Visualization for the Dashboard.
 * Inspired by D3.js and Recharts:
 * - Clustered bar chart comparing Caloric Intake vs Estimated Total Expenditure (TDEE = BMR + Burn + TEF).
 * - Interactive tap-to-inspect Tooltip showing net deficit/surplus delta, macro composition, and exercise burn.
 * - Horizontal reference guide lines (1k, 2k, 3k kcal).
 * - Weekly cumulative energy balance summary & estimated fat loss pace.
 * - Dark Claymorphism aesthetic adhering to M3 and app theme.
 */
@Composable
fun WeeklyEnergyBalanceChart(
    summaries: List<DailySummary>,
    modifier: Modifier = Modifier
) {
    // Take the 7 most recent summaries and order chronologically (oldest to newest)
    val weeklyData = remember(summaries) {
        val recent7 = summaries.take(7).reversed()
        if (recent7.isEmpty()) {
            emptyList()
        } else {
            recent7.mapIndexed { index, summary ->
                val dayLabel = try {
                    val date = LocalDate.parse(summary.date)
                    date.dayOfWeek.name.take(3).lowercase()
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.US) else it.toString() }
                } catch (e: Exception) {
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
                DayEnergyBalance(
                    dateStr = summary.date,
                    dayLabel = dayLabel,
                    intakeKcal = summary.intakeKcal,
                    tdeeKcal = summary.tdee,
                    netBalance = summary.netBalance,
                    activeBurnKcal = summary.activeBurnKcal,
                    status = summary.status,
                    proteinG = summary.proteinG,
                    carbsG = summary.carbsG,
                    fatG = summary.fatG,
                    isToday = index == recent7.lastIndex
                )
            }
        }
    }

    var selectedDayIndex by remember { mutableStateOf(weeklyData.lastIndex.coerceAtLeast(0)) }
    val selectedDay = weeklyData.getOrNull(selectedDayIndex) ?: weeklyData.lastOrNull()

    // Calculate weekly metrics
    val totalIntake = weeklyData.sumOf { it.intakeKcal }
    val totalTdee = weeklyData.sumOf { it.tdeeKcal }
    val cumulativeNet = totalIntake - totalTdee
    val avgIntake = if (weeklyData.isNotEmpty()) totalIntake / weeklyData.size else 0
    val avgTdee = if (weeklyData.isNotEmpty()) totalTdee / weeklyData.size else 0
    // 7,700 kcal is approximately 1kg of fat tissue
    val estimatedFatChangeKg = cumulativeNet / 7700.0

    CrystalCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("weekly_energy_balance_chart"),
        cornerRadius = 28.dp,
        backgroundColor = SmokedCrystal
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header: Category Title + Net Weekly Delta Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "📊", fontSize = 16.sp)
                        Text(
                            text = "WEEKLY ENERGY BALANCE",
                            color = CrystalIce,
                            fontFamily = ChillaxRoundedDisplayFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Intake vs Expenditure",
                        color = TextPrimary,
                        fontFamily = ChillaxRoundedDisplayFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Cumulative Weekly Delta Capsule
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (cumulativeNet < 0) EmeraldMistDark.copy(alpha = 0.35f)
                            else WarmChampagne.copy(alpha = 0.25f)
                        )
                        .border(
                            1.dp,
                            if (cumulativeNet < 0) EmeraldMist.copy(alpha = 0.5f) else WarmChampagne.copy(alpha = 0.5f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (cumulativeNet < 0) Icons.Default.TrendingDown else Icons.Default.TrendingUp,
                            contentDescription = "Trend",
                            tint = if (cumulativeNet < 0) EmeraldMist else WarmChampagne,
                            modifier = Modifier.size(14.dp)
                        )
                        val sign = if (cumulativeNet > 0) "+" else ""
                        Text(
                            text = "$sign$cumulativeNet kcal/wk",
                            color = if (cumulativeNet < 0) EmeraldMist else WarmChampagne,
                            fontFamily = ChillaxRoundedDisplayFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (weeklyData.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                        .testTag("energy_balance_empty_state"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "📈", fontSize = 28.sp)
                        Text(
                            text = "No Weekly Telemetry Yet",
                            color = TextPrimary,
                            fontFamily = ChillaxRoundedDisplayFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Log food intake or workouts to activate your 7-day energy balance telemetry.",
                            color = TextSecondary,
                            fontFamily = OutfitBodyFamily,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            } else {
                // Legend Row (D3/Recharts style)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LegendIndicator(
                        label = "Intake (Food)",
                        color = CrystalIce
                    )
                    LegendIndicator(
                        label = "TDEE (BMR+Burn+TEF)",
                        color = EmeraldMist
                    )
                    LegendIndicator(
                        label = "Net Deficit / Surplus",
                        color = SoftCoralRose
                    )
                }

                // Clustered Bar Visualization Area
                ClusteredEnergyBarsCanvas(
                    days = weeklyData,
                    selectedIndex = selectedDayIndex,
                    onSelectDay = { selectedDayIndex = it }
                )

                // Interactive Tooltip / Detail Inspector (D3/Recharts Hover Card)
                if (selectedDay != null) {
                    EnergyBalanceTooltipCard(
                        day = selectedDay,
                        modifier = Modifier.testTag("energy_balance_tooltip")
                    )
                }

                // Weekly Summary Analytics Strip
                WeeklySummaryMetricsStrip(
                    avgIntake = avgIntake,
                    avgTdee = avgTdee,
                    cumulativeNet = cumulativeNet,
                    estimatedFatChangeKg = estimatedFatChangeKg
                )
            }
        }
    }
}

/**
 * Custom Canvas Clustered Bar Chart comparing Intake and TDEE side-by-side for each of the 7 days.
 */
@Composable
private fun ClusteredEnergyBarsCanvas(
    days: List<DayEnergyBalance>,
    selectedIndex: Int,
    onSelectDay: (Int) -> Unit
) {
    val maxKcal = remember(days) {
        val highest = days.maxOfOrNull { maxOf(it.intakeKcal, it.tdeeKcal) } ?: 3000
        ((highest / 500) + 1) * 500
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(ClaySurfaceBottom.copy(alpha = 0.85f))
            .border(1.dp, SpecularBorder.copy(alpha = 0.2f), RoundedCornerShape(18.dp))
            .padding(top = 16.dp, bottom = 10.dp, start = 12.dp, end = 12.dp)
    ) {
        // Grid reference lines & Bars drawn via Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val chartWidth = size.width
            val chartHeight = size.height - 24.dp.toPx() // space for day labels

            // Horizontal grid lines at 1000, 2000, 3000 kcal
            val gridStep = 1000
            var currentGridKcal = gridStep
            val pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)

            while (currentGridKcal < maxKcal) {
                val y = chartHeight - (currentGridKcal.toFloat() / maxKcal * chartHeight)
                drawLine(
                    color = Color.White.copy(alpha = 0.08f),
                    start = Offset(0f, y),
                    end = Offset(chartWidth, y),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = pathEffect
                )
                currentGridKcal += gridStep
            }

            // Baseline at y = chartHeight
            drawLine(
                color = SpecularBorder.copy(alpha = 0.3f),
                start = Offset(0f, chartHeight),
                end = Offset(chartWidth, chartHeight),
                strokeWidth = 1.5.dp.toPx()
            )

            // Render Clustered Bars for each day
            val dayCount = days.size.coerceAtLeast(1)
            val slotWidth = chartWidth / dayCount
            val barWidth = (slotWidth * 0.32f).coerceAtMost(16.dp.toPx())
            val barGap = 3.dp.toPx()

            days.forEachIndexed { index, day ->
                val slotCenterX = index * slotWidth + (slotWidth / 2f)
                val isSelected = index == selectedIndex

                // Intake Bar (Left)
                val intakeHeight = (day.intakeKcal.toFloat() / maxKcal * chartHeight).coerceAtLeast(4.dp.toPx())
                val intakeLeft = slotCenterX - barWidth - (barGap / 2f)
                val intakeTop = chartHeight - intakeHeight

                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            if (isSelected) CrystalIce else CrystalIce.copy(alpha = 0.85f),
                            if (isSelected) Color(0xFF0284C7) else Color(0xFF0369A1)
                        ),
                        startY = intakeTop,
                        endY = chartHeight
                    ),
                    topLeft = Offset(intakeLeft, intakeTop),
                    size = Size(barWidth, intakeHeight),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )

                // Top highlight line for intake bar
                drawLine(
                    color = Color.White.copy(alpha = 0.5f),
                    start = Offset(intakeLeft, intakeTop),
                    end = Offset(intakeLeft + barWidth, intakeTop),
                    strokeWidth = 1.5.dp.toPx()
                )

                // TDEE Bar (Right)
                val tdeeHeight = (day.tdeeKcal.toFloat() / maxKcal * chartHeight).coerceAtLeast(4.dp.toPx())
                val tdeeLeft = slotCenterX + (barGap / 2f)
                val tdeeTop = chartHeight - tdeeHeight

                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            if (isSelected) EmeraldMist else EmeraldMist.copy(alpha = 0.85f),
                            if (isSelected) EmeraldMistDark else Color(0xFF047857)
                        ),
                        startY = tdeeTop,
                        endY = chartHeight
                    ),
                    topLeft = Offset(tdeeLeft, tdeeTop),
                    size = Size(barWidth, tdeeHeight),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )

                // Top highlight line for TDEE bar
                drawLine(
                    color = Color.White.copy(alpha = 0.5f),
                    start = Offset(tdeeLeft, tdeeTop),
                    end = Offset(tdeeLeft + barWidth, tdeeTop),
                    strokeWidth = 1.5.dp.toPx()
                )

                // Net Delta Indicator Dot at top
                val deltaY = minOf(intakeTop, tdeeTop) - 6.dp.toPx()
                val deltaColor = if (day.netBalance < 0) EmeraldMist else SoftCoralRose
                drawCircle(
                    color = deltaColor,
                    radius = if (isSelected) 3.5.dp.toPx() else 2.5.dp.toPx(),
                    center = Offset(slotCenterX, deltaY.coerceAtLeast(4.dp.toPx()))
                )

                // Selection highlight pillar if active
                if (isSelected) {
                    drawRoundRect(
                        color = CrystalIce.copy(alpha = 0.08f),
                        topLeft = Offset(index * slotWidth + 2.dp.toPx(), 0f),
                        size = Size(slotWidth - 4.dp.toPx(), chartHeight + 20.dp.toPx()),
                        cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                    )
                }
            }
        }

        // Overlay Interactive Click Row for each Day
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            days.forEachIndexed { index, day ->
                val isSelected = index == selectedIndex
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onSelectDay(index) }
                        )
                        .testTag("energy_bar_day_$index"),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        text = day.dayLabel,
                        color = if (isSelected) CrystalIce else (if (day.isToday) TextPrimary else TextSecondary),
                        fontFamily = ChillaxRoundedDisplayFamily,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected || day.isToday) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

/**
 * Interactive D3/Recharts-Style Tooltip Inspector displaying the selected day's exact metrics.
 */
@Composable
private fun EnergyBalanceTooltipCard(
    day: DayEnergyBalance,
    modifier: Modifier = Modifier
) {
    val isDeficit = day.netBalance < 0
    val deltaSign = if (day.netBalance > 0) "+" else ""

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        ClaySurfaceTop,
                        Color(0xFF131D31)
                    )
                )
            )
            .border(1.dp, if (isDeficit) EmeraldMist.copy(alpha = 0.45f) else SoftCoralRose.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Header: Date & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "📅 ${day.dateStr}",
                        color = TextPrimary,
                        fontFamily = ChillaxRoundedDisplayFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (day.isToday) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CrystalIce.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Today",
                                color = CrystalIce,
                                fontFamily = ChillaxRoundedDisplayFamily,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Status Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isDeficit) EmeraldMistDark.copy(alpha = 0.3f)
                            else SoftCoralRose.copy(alpha = 0.2f)
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "$deltaSign${day.netBalance} kcal (${if (isDeficit) "Deficit" else "Surplus"})",
                        color = if (isDeficit) EmeraldMist else SoftCoralRose,
                        fontFamily = ChillaxRoundedDisplayFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Stats Comparison Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TooltipMetric(
                    title = "Intake",
                    value = "${day.intakeKcal} kcal",
                    accentColor = CrystalIce
                )
                TooltipMetric(
                    title = "Estimated TDEE",
                    value = "${day.tdeeKcal} kcal",
                    accentColor = EmeraldMist
                )
                TooltipMetric(
                    title = "Active Burn",
                    value = "${day.activeBurnKcal} kcal",
                    accentColor = WarmChampagne
                )
                TooltipMetric(
                    title = "Protein",
                    value = "${day.proteinG}g",
                    accentColor = TextPrimary
                )
            }
        }
    }
}

@Composable
private fun TooltipMetric(
    title: String,
    value: String,
    accentColor: Color
) {
    Column {
        Text(
            text = title,
            color = TextSecondary,
            fontFamily = OutfitBodyFamily,
            fontSize = 10.sp
        )
        Text(
            text = value,
            color = accentColor,
            fontFamily = ChillaxRoundedDisplayFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Weekly Summary Metrics Strip showing average intake vs TDEE and estimated rate of fat loss/gain.
 */
@Composable
private fun WeeklySummaryMetricsStrip(
    avgIntake: Int,
    avgTdee: Int,
    cumulativeNet: Int,
    estimatedFatChangeKg: Double
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF0D1424))
            .border(1.dp, SpecularBorder.copy(alpha = 0.15f), RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "7-DAY ROLLING AVERAGE",
                    color = TextTertiary,
                    fontFamily = ChillaxRoundedDisplayFamily,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Avg Intake $avgIntake vs TDEE $avgTdee kcal",
                    color = TextPrimary,
                    fontFamily = OutfitBodyFamily,
                    fontSize = 11.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "EST. WEEKLY FAT PACE",
                    color = TextTertiary,
                    fontFamily = ChillaxRoundedDisplayFamily,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                val paceText = String.format(Locale.US, "%.2f kg/wk", estimatedFatChangeKg)
                Text(
                    text = if (cumulativeNet < 0) "$paceText loss" else "+$paceText surplus",
                    color = if (cumulativeNet < 0) EmeraldMist else WarmChampagne,
                    fontFamily = ChillaxRoundedDisplayFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun LegendIndicator(
    label: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            color = TextSecondary,
            fontFamily = OutfitBodyFamily,
            fontSize = 10.sp
        )
    }
}

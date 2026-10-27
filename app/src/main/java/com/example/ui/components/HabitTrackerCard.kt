package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ChillaxRoundedDisplayFamily
import com.example.ui.theme.ClayHighlightIce
import com.example.ui.theme.ClayHighlightLight
import com.example.ui.theme.ClayLavender
import com.example.ui.theme.ClayPeach
import com.example.ui.theme.ClayShadowDark
import com.example.ui.theme.ClaySurfaceBottom
import com.example.ui.theme.ClaySurfaceTop
import com.example.ui.theme.CrystalIce
import com.example.ui.theme.CrystalIceDark
import com.example.ui.theme.EmeraldMist
import com.example.ui.theme.EmeraldMistDark
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianDeep
import com.example.ui.theme.OutfitBodyFamily
import com.example.ui.theme.SmokedCrystal
import com.example.ui.theme.SoftCoralRose
import com.example.ui.theme.SpecularBorder
import com.example.ui.theme.SpecularHighlight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.WarmChampagne

/**
 * Model representing a Daily Habit tracked in the Dark Claymorphism dashboard.
 */
data class HabitStreakItem(
    val id: String,
    val title: String,
    val category: String,
    val iconEmoji: String,
    val currentVal: Float,
    val targetVal: Float,
    val unit: String,
    val streakDays: Int,
    val isCompleted: Boolean,
    val accentColor: Color,
    val detailSubtitle: String,
    val weeklyHistory: List<Boolean> = listOf(true, true, true, true, true, true, false)
)

/**
 * Main Habit Tracker Card for the Dashboard with puffy Dark Claymorphism styling,
 * weekly streak matrix, and dedicated daily health goals (Hydration, Sleep, Protein, APT, Active Burn).
 */
@Composable
fun HabitTrackerCard(
    habits: List<HabitStreakItem>,
    waterIntakeMl: Int,
    waterTargetMl: Int = 3000,
    onAddWater: (Int) -> Unit,
    onResetWater: () -> Unit,
    onToggleHabit: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val completedCount = habits.count { it.isCompleted }
    val totalCount = habits.size
    val completionRatio = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f
    val bestStreak = habits.maxOfOrNull { it.streakDays } ?: 0

    CrystalCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("habit_tracker_card"),
        cornerRadius = 30.dp,
        backgroundColor = SmokedCrystal
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Row: Title + Progress Dial Pill
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
                        Text(
                            text = "🔥",
                            fontSize = 16.sp
                        )
                        Text(
                            text = "HABIT STREAK ENGINE",
                            color = CrystalIce,
                            fontFamily = ChillaxRoundedDisplayFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Daily Health Goals",
                        color = TextPrimary,
                        fontFamily = ChillaxRoundedDisplayFamily,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Clay Puffy Score Capsule
                ClayPuffyCapsuleScore(
                    completed = completedCount,
                    total = totalCount,
                    ratio = completionRatio
                )
            }

            // Streak Momentum Banner (Best streak celebration)
            StreakMomentumBanner(
                bestStreakDays = bestStreak,
                completedToday = completedCount == totalCount && totalCount > 0
            )

            // 7-Day Completion Momentum Strip (Weekly Dots)
            WeeklyStreakMomentumStrip(
                modifier = Modifier.fillMaxWidth()
            )

            // Dedicated Hydration Tracker (Prominent Clay Card)
            ClayHydrationTrackerItem(
                currentMl = waterIntakeMl,
                targetMl = waterTargetMl,
                streakDays = habits.firstOrNull { it.id == "habit_hydration" }?.streakDays ?: 6,
                onAddWater = onAddWater,
                onResetWater = onResetWater
            )

            // List of other Health Habits (Sleep, Protein, APT, Movement)
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                habits.filter { it.id != "habit_hydration" }.forEach { habit ->
                    ClayHabitRowItem(
                        habit = habit,
                        onToggle = { onToggleHabit(habit.id) }
                    )
                }
            }
        }
    }
}

/**
 * Puffy Dark Claymorphism Score Capsule with circular progress and gradient highlight.
 */
@Composable
private fun ClayPuffyCapsuleScore(
    completed: Int,
    total: Int,
    ratio: Float
) {
    val animatedRatio by animateFloatAsState(
        targetValue = ratio,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "score_ratio"
    )

    Box(
        modifier = Modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black,
                spotColor = EmeraldMist.copy(alpha = 0.35f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        ClaySurfaceTop.copy(alpha = 0.95f),
                        ClaySurfaceBottom.copy(alpha = 0.95f)
                    )
                )
            )
            .border(1.dp, SpecularBorder, RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Circular Progress Gauge
            Box(
                modifier = Modifier.size(28.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxWidth()) {
                    val strokeWidth = 3.dp.toPx()
                    // Track
                    drawCircle(
                        color = Color.White.copy(alpha = 0.10f),
                        style = Stroke(width = strokeWidth)
                    )
                    // Progress arc
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(EmeraldMist, CrystalIce, EmeraldMist)
                        ),
                        startAngle = -90f,
                        sweepAngle = animatedRatio * 360f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
                Text(
                    text = "${(animatedRatio * 100).toInt()}%",
                    color = TextPrimary,
                    fontFamily = ChillaxRoundedDisplayFamily,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = "$completed / $total Done",
                    color = TextPrimary,
                    fontFamily = ChillaxRoundedDisplayFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (completed == total) "All Met! 🎉" else "In Progress",
                    color = if (completed == total) EmeraldMist else TextSecondary,
                    fontFamily = OutfitBodyFamily,
                    fontSize = 10.sp
                )
            }
        }
    }
}

/**
 * Streak Momentum Banner highlighting consecutive days consistency.
 */
@Composable
private fun StreakMomentumBanner(
    bestStreakDays: Int,
    completedToday: Boolean
) {
    val bgColor = if (completedToday) EmeraldMistDark.copy(alpha = 0.35f) else Color(0xFF1E293B)
    val accentBorder = if (completedToday) EmeraldMist.copy(alpha = 0.5f) else SpecularBorder

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        bgColor,
                        ClaySurfaceBottom
                    )
                )
            )
            .border(1.dp, accentBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
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
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(WarmChampagne.copy(alpha = 0.3f), Color.Transparent)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "⚡", fontSize = 16.sp)
                }
                Column {
                    Text(
                        text = if (completedToday) "All Targets Cleared Today!" else "Current Streak Momentum",
                        color = TextPrimary,
                        fontFamily = ChillaxRoundedDisplayFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (completedToday) "Great discipline! 100% adherence score." else "Consistent logging builds metabolic resilience.",
                        color = TextSecondary,
                        fontFamily = OutfitBodyFamily,
                        fontSize = 10.sp
                    )
                }
            }

            // Streak Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF2E2619))
                    .border(1.dp, WarmChampagne.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "🔥 $bestStreakDays Days",
                    color = WarmChampagne,
                    fontFamily = ChillaxRoundedDisplayFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * 7-Day Weekly Streak Matrix showing historical momentum (Mon to Sun).
 */
@Composable
private fun WeeklyStreakMomentumStrip(
    modifier: Modifier = Modifier
) {
    val days = listOf(
        Triple("Mon", true, "100%"),
        Triple("Tue", true, "100%"),
        Triple("Wed", true, "80%"),
        Triple("Thu", true, "100%"),
        Triple("Fri", true, "100%"),
        Triple("Sat", true, "100%"),
        Triple("Sun", false, "Today")
    )

    Column(
        modifier = modifier
            .testTag("habit_weekly_streak_strip")
            .clip(RoundedCornerShape(18.dp))
            .background(ClaySurfaceBottom.copy(alpha = 0.7f))
            .border(1.dp, SpecularBorder.copy(alpha = 0.15f), RoundedCornerShape(18.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "WEEKLY ADHERENCE (7-DAY TRAIL)",
                color = TextTertiary,
                fontFamily = ChillaxRoundedDisplayFamily,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = "6/7 Completed",
                color = EmeraldMist,
                fontFamily = OutfitBodyFamily,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            days.forEachIndexed { index, (dayLabel, isDone, score) ->
                val isToday = index == days.lastIndex

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = dayLabel,
                        color = if (isToday) CrystalIce else TextSecondary,
                        fontFamily = ChillaxRoundedDisplayFamily,
                        fontSize = 10.sp,
                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                    )

                    // Puffy Clay 3D Disc
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .shadow(
                                elevation = if (isDone || isToday) 6.dp else 2.dp,
                                shape = CircleShape,
                                ambientColor = Color.Black,
                                spotColor = if (isDone) EmeraldMist.copy(alpha = 0.4f) else CrystalIce.copy(alpha = 0.3f)
                            )
                            .clip(CircleShape)
                            .background(
                                Brush.verticalGradient(
                                    colors = when {
                                        isDone -> listOf(EmeraldMistDark, Color(0xFF064E3B))
                                        isToday -> listOf(ClaySurfaceTop, ClaySurfaceBottom)
                                        else -> listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                                    }
                                )
                            )
                            .border(
                                width = if (isToday) 1.5.dp else 1.dp,
                                color = when {
                                    isDone -> EmeraldMist.copy(alpha = 0.7f)
                                    isToday -> CrystalIce
                                    else -> SpecularBorder.copy(alpha = 0.2f)
                                },
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isDone) {
                            Text(text = "🔥", fontSize = 14.sp)
                        } else if (isToday) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(CrystalIce)
                            )
                        } else {
                            Text(text = "•", color = TextTertiary, fontSize = 12.sp)
                        }
                    }

                    Text(
                        text = score,
                        color = if (isDone) EmeraldMist else TextTertiary,
                        fontFamily = OutfitBodyFamily,
                        fontSize = 8.sp
                    )
                }
            }
        }
    }
}

/**
 * Dedicated Dark Claymorphism Hydration Tracker with water gauge,
 * tactile +250ml / +500ml quick-add buttons, and streak chip.
 */
@Composable
private fun ClayHydrationTrackerItem(
    currentMl: Int,
    targetMl: Int,
    streakDays: Int,
    onAddWater: (Int) -> Unit,
    onResetWater: () -> Unit
) {
    val progress = (currentMl.toFloat() / targetMl).coerceIn(0f, 1.25f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "water_progress"
    )
    val isGoalMet = currentMl >= targetMl

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("habit_item_hydration")
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(22.dp),
                ambientColor = Color.Black,
                spotColor = CrystalIce.copy(alpha = 0.25f)
            )
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        ClaySurfaceTop,
                        ClaySurfaceBottom
                    )
                )
            )
            .border(1.5.dp, if (isGoalMet) EmeraldMist.copy(alpha = 0.6f) else SpecularBorder, RoundedCornerShape(22.dp))
            .drawWithContent {
                drawContent()
                // Specular top rim
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            ClayHighlightIce.copy(alpha = 0.35f),
                            Color.Transparent
                        )
                    ),
                    cornerRadius = CornerRadius(22.dp.toPx(), 22.dp.toPx()),
                    style = Stroke(width = 2.dp.toPx())
                )
            }
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Header Row: Emoji, Title, Streak & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Puffy Water Icon Box
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .shadow(4.dp, RoundedCornerShape(14.dp), spotColor = CrystalIce)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFF0369A1), Color(0xFF0C4A6E))
                                )
                            )
                            .border(1.dp, CrystalIce.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "💧", fontSize = 20.sp)
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Hydration Target",
                                color = TextPrimary,
                                fontFamily = ChillaxRoundedDisplayFamily,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (isGoalMet) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(EmeraldMist.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Goal Met! ✓",
                                        color = EmeraldMist,
                                        fontFamily = ChillaxRoundedDisplayFamily,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Text(
                            text = "3,000 ml Flush • Sodium & Renal Balance",
                            color = TextSecondary,
                            fontFamily = OutfitBodyFamily,
                            fontSize = 11.sp
                        )
                    }
                }

                // Flame streak badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF2A1C16))
                        .border(1.dp, SoftCoralRose.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "🔥 $streakDays d",
                        color = SoftCoralRose,
                        fontFamily = ChillaxRoundedDisplayFamily,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Water Volume Bar & Telemetry Label
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "$currentMl",
                            color = CrystalIce,
                            fontFamily = ChillaxRoundedDisplayFamily,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "/ $targetMl ml",
                            color = TextSecondary,
                            fontFamily = ChillaxRoundedDisplayFamily,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                    Text(
                        text = "${(animatedProgress * 100).toInt()}%",
                        color = if (isGoalMet) EmeraldMist else TextPrimary,
                        fontFamily = ChillaxRoundedDisplayFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Tactile Clay Pill Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, SpecularBorder.copy(alpha = 0.3f), RoundedCornerShape(7.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = (animatedProgress).coerceIn(0f, 1f))
                            .height(14.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = if (isGoalMet) {
                                        listOf(EmeraldMistDark, EmeraldMist, CrystalIce)
                                    } else {
                                        listOf(Color(0xFF0284C7), CrystalIce, Color(0xFF7DD3FC))
                                    }
                                )
                            )
                            .drawWithContent {
                                drawContent()
                                // Specular top line in the water bar
                                drawLine(
                                    color = Color.White.copy(alpha = 0.45f),
                                    start = Offset(0f, 2.dp.toPx()),
                                    end = Offset(size.width, 2.dp.toPx()),
                                    strokeWidth = 1.5.dp.toPx()
                                )
                            }
                    )
                }
            }

            // Tactile Quick-Add Clay Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TactileClayAddButton(
                    label = "+250 ml (Glass)",
                    onClick = { onAddWater(250) },
                    accentColor = CrystalIce,
                    modifier = Modifier.weight(1f),
                    testTag = "habit_water_add_250"
                )

                TactileClayAddButton(
                    label = "+500 ml (Bottle)",
                    onClick = { onAddWater(500) },
                    accentColor = EmeraldMist,
                    modifier = Modifier.weight(1f),
                    testTag = "habit_water_add_500"
                )

                TactileClayIconButton(
                    icon = Icons.Default.Refresh,
                    onClick = onResetWater,
                    contentDescription = "Reset Water",
                    testTag = "habit_water_reset"
                )
            }
        }
    }
}

/**
 * Generic Dark Claymorphism Habit Row with tactile check-off toggle,
 * streak flame badge, and visual metrics.
 */
@Composable
private fun ClayHabitRowItem(
    habit: HabitStreakItem,
    onToggle: () -> Unit
) {
    val isCompleted = habit.isCompleted
    val progress = (habit.currentVal / habit.targetVal).coerceIn(0f, 1f)

    var isPressed by remember { mutableStateOf(false) }
    val scaleAnim by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "press_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(habit.id)
            .scale(scaleAnim)
            .shadow(
                elevation = if (isCompleted) 8.dp else 4.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black,
                spotColor = habit.accentColor.copy(alpha = if (isCompleted) 0.35f else 0.15f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    colors = if (isCompleted) {
                        listOf(
                            ClaySurfaceTop.copy(alpha = 0.98f),
                            habit.accentColor.copy(alpha = 0.12f)
                        )
                    } else {
                        listOf(
                            ClaySurfaceTop.copy(alpha = 0.85f),
                            ClaySurfaceBottom.copy(alpha = 0.85f)
                        )
                    }
                )
            )
            .border(
                width = 1.2.dp,
                color = if (isCompleted) habit.accentColor.copy(alpha = 0.6f) else SpecularBorder,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = habit.accentColor),
                onClick = {
                    onToggle()
                }
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Icon + Title + Detail
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Tactile Clay Emoji Badge
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .shadow(3.dp, RoundedCornerShape(12.dp), spotColor = habit.accentColor)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    habit.accentColor.copy(alpha = 0.25f),
                                    Color(0xFF0F172A)
                                )
                            )
                        )
                        .border(1.dp, habit.accentColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = habit.iconEmoji, fontSize = 20.sp)
                }

                Column(modifier = Modifier.padding(end = 8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = habit.title,
                            color = TextPrimary,
                            fontFamily = ChillaxRoundedDisplayFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        // Flame Streak Tag
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF281C14))
                                .border(1.dp, WarmChampagne.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "🔥 ${habit.streakDays}d",
                                color = WarmChampagne,
                                fontFamily = ChillaxRoundedDisplayFamily,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = habit.detailSubtitle,
                        color = TextSecondary,
                        fontFamily = OutfitBodyFamily,
                        fontSize = 11.sp
                    )

                    if (habit.targetVal > 0f) {
                        Spacer(modifier = Modifier.height(6.dp))
                        // Mini progress bar
                        Box(
                            modifier = Modifier
                                .width(120.dp)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFF0F172A))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction = progress)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(habit.accentColor)
                            )
                        }
                    }
                }
            }

            // Right: Interactive Tactile Checkbox Button
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .shadow(
                        elevation = if (isCompleted) 8.dp else 2.dp,
                        shape = CircleShape,
                        spotColor = if (isCompleted) habit.accentColor else Color.Black
                    )
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            colors = if (isCompleted) {
                                listOf(habit.accentColor, habit.accentColor.copy(alpha = 0.7f))
                            } else {
                                listOf(ClaySurfaceTop, ClaySurfaceBottom)
                            }
                        )
                    )
                    .border(
                        width = 1.5.dp,
                        color = if (isCompleted) Color.White.copy(alpha = 0.8f) else SpecularBorder,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = Color(0xFF0B0F17),
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    )
                }
            }
        }
    }
}

/**
 * Tactile Clay button with puffy inner gradient and specular rim for quick logging.
 */
@Composable
private fun TactileClayAddButton(
    label: String,
    onClick: () -> Unit,
    accentColor: Color,
    modifier: Modifier = Modifier,
    testTag: String? = null
) {
    Box(
        modifier = modifier
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
            .shadow(4.dp, RoundedCornerShape(14.dp), spotColor = accentColor.copy(alpha = 0.2f))
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        ClaySurfaceTop.copy(alpha = 0.95f),
                        ClaySurfaceBottom.copy(alpha = 0.95f)
                    )
                )
            )
            .border(1.dp, SpecularBorder, RoundedCornerShape(14.dp))
            .drawWithContent {
                drawContent()
                // Top specular highlight
                drawLine(
                    color = SpecularHighlight,
                    start = Offset(0f, 1.dp.toPx()),
                    end = Offset(size.width, 1.dp.toPx()),
                    strokeWidth = 1.5.dp.toPx()
                )
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = accentColor),
                onClick = onClick
            )
            .padding(vertical = 10.dp, horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = TextPrimary,
            fontFamily = ChillaxRoundedDisplayFamily,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Tactile small clay icon button (e.g. for reset).
 */
@Composable
private fun TactileClayIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    contentDescription: String,
    testTag: String? = null
) {
    Box(
        modifier = Modifier
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
            .size(38.dp)
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(ClaySurfaceBottom)
            .border(1.dp, SpecularBorder, RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = SoftCoralRose),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = TextSecondary,
            modifier = Modifier.size(16.dp)
        )
    }
}

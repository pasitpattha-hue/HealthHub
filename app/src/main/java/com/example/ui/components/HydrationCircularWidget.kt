package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ChillaxRoundedDisplayFamily
import com.example.ui.theme.ClayHighlightIce
import com.example.ui.theme.ClayShadowDark
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
import kotlin.math.cos
import kotlin.math.sin

/**
 * Common glass / vessel size configuration for the quick-add buttons.
 */
data class GlassPreset(
    val amountMl: Int,
    val name: String,
    val vesselType: String,
    val iconEmoji: String,
    val testTag: String
)

val DEFAULT_GLASS_PRESETS = listOf(
    GlassPreset(
        amountMl = 150,
        name = "+150 ml",
        vesselType = "Cup / Sip",
        iconEmoji = "☕",
        testTag = "quick_add_150"
    ),
    GlassPreset(
        amountMl = 250,
        name = "+250 ml",
        vesselType = "Glass",
        iconEmoji = "🥛",
        testTag = "quick_add_250"
    ),
    GlassPreset(
        amountMl = 500,
        name = "+500 ml",
        vesselType = "Shaker",
        iconEmoji = "🍶",
        testTag = "quick_add_500"
    ),
    GlassPreset(
        amountMl = 750,
        name = "+750 ml",
        vesselType = "Bottle",
        iconEmoji = "🧴",
        testTag = "quick_add_750"
    )
)

/**
 * Dedicated Circular Progress Indicator Hydration Widget for the Dashboard.
 * Features:
 * - Fluid circular gauge with animated arc sweep, glow bead, and milestone ticks
 * - Large typography with instant volume readout and remaining target calculation
 * - Quick-add button cluster for common glass sizes (150ml, 250ml, 500ml, 750ml)
 * - Fine-tune volume adjusters (+100ml / -100ml) and daily reset capability
 * - Dark Claymorphism styling adhering to M3 and app theme
 */
@Composable
fun HydrationCircularWidget(
    waterIntakeMl: Int,
    targetMl: Int = 3000,
    streakDays: Int = 6,
    onAddWater: (Int) -> Unit,
    onResetWater: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = if (targetMl > 0) (waterIntakeMl.toFloat() / targetMl).coerceIn(0f, 1.5f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "circular_water_progress"
    )

    val isGoalMet = waterIntakeMl >= targetMl
    val remainingMl = (targetMl - waterIntakeMl).coerceAtLeast(0)
    val percentageInt = ((waterIntakeMl.toFloat() / targetMl) * 100).toInt()

    var showResetConfirmDialog by remember { mutableStateOf(false) }

    if (showResetConfirmDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = {
                Text(
                    text = "Reset Today's Water Intake?",
                    color = TextPrimary,
                    fontFamily = ChillaxRoundedDisplayFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to reset your water intake log to 0 ml? Your current logged amount ($waterIntakeMl ml) will be cleared.",
                    color = TextSecondary,
                    fontFamily = OutfitBodyFamily,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                CrystalButton(
                    text = "Reset to 0 ml",
                    onClick = {
                        showResetConfirmDialog = false
                        onResetWater()
                    },
                    isPrimary = true,
                    testTag = "confirm_reset_water_button"
                )
            },
            dismissButton = {
                CrystalButton(
                    text = "Cancel",
                    onClick = { showResetConfirmDialog = false },
                    isPrimary = false,
                    testTag = "cancel_reset_water_button"
                )
            },
            containerColor = Color(0xFF0F172A),
            shape = RoundedCornerShape(24.dp)
        )
    }

    CrystalCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("hydration_circular_widget"),
        cornerRadius = 30.dp,
        backgroundColor = SmokedCrystal
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Header: Category Title + Streak Badge + Reset Button
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
                        Text(text = "💧", fontSize = 16.sp)
                        Text(
                            text = "DAILY HYDRATION MATRIX",
                            color = CrystalIce,
                            fontFamily = ChillaxRoundedDisplayFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Water Intake & Cellular Flush",
                        color = TextPrimary,
                        fontFamily = ChillaxRoundedDisplayFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Streak Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF231F17))
                            .border(1.dp, WarmChampagne.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "🔥 $streakDays d",
                            color = WarmChampagne,
                            fontFamily = ChillaxRoundedDisplayFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Reset Button (with 48dp touch target)
                    IconButton(
                        onClick = { showResetConfirmDialog = true },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("reset_water_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Water Intake",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Central Circular Progress Indicator Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                HydrationCircularGauge(
                    currentMl = waterIntakeMl,
                    targetMl = targetMl,
                    progress = animatedProgress,
                    percentage = percentageInt,
                    isGoalMet = isGoalMet,
                    remainingMl = remainingMl
                )
            }

            // Milestone Target Strip (Morning, Afternoon, Evening)
            HydrationMilestonePills(
                currentMl = waterIntakeMl,
                targetMl = targetMl
            )

            // Goal Achieved Celebration Banner
            AnimatedVisibility(
                visible = isGoalMet,
                enter = fadeIn() + androidx.compose.animation.expandVertically(),
                exit = fadeOut() + androidx.compose.animation.shrinkVertically()
            ) {
                GoalAchievedBanner(currentMl = waterIntakeMl, targetMl = targetMl)
            }

            // Quick-Add Glass Sizes Section
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "QUICK-ADD GLASS SIZES",
                        color = TextTertiary,
                        fontFamily = ChillaxRoundedDisplayFamily,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    // Fine-Tune Steppers
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Fine tune:",
                            color = TextSecondary,
                            fontFamily = OutfitBodyFamily,
                            fontSize = 10.sp
                        )
                        FineTuneButton(
                            label = "-100",
                            onClick = { onAddWater(-100) },
                            testTag = "fine_tune_minus_100"
                        )
                        FineTuneButton(
                            label = "+100",
                            onClick = { onAddWater(100) },
                            testTag = "fine_tune_add_100"
                        )
                    }
                }

                // 4-Button Cluster for Standard Glass Sizes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DEFAULT_GLASS_PRESETS.forEach { preset ->
                        TactileGlassQuickAddButton(
                            preset = preset,
                            onClick = { onAddWater(preset.amountMl) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Circular progress gauge drawn with custom Canvas, gradients, and central stat readout.
 */
@Composable
private fun HydrationCircularGauge(
    currentMl: Int,
    targetMl: Int,
    progress: Float,
    percentage: Int,
    isGoalMet: Boolean,
    remainingMl: Int
) {
    val gaugeSize = 210.dp
    val strokeWidthDp = 16.dp

    val trackColor = Color(0xFF0D1527)
    val trackBorderColor = Color(0xFF1E293B)

    val activeGradientColors = if (isGoalMet) {
        listOf(
            EmeraldMistDark,
            EmeraldMist,
            CrystalIce,
            EmeraldMist
        )
    } else {
        listOf(
            Color(0xFF0369A1),
            CrystalIceDark,
            CrystalIce,
            Color(0xFF7DD3FC)
        )
    }

    Box(
        modifier = Modifier
            .size(gaugeSize)
            .testTag("hydration_circular_gauge"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidthPx = strokeWidthDp.toPx()
            val radius = (size.minDimension - strokeWidthPx) / 2f
            val centerOffset = Offset(size.width / 2f, size.height / 2f)

            // Outer subtle diffuse glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        (if (isGoalMet) EmeraldMist else CrystalIce).copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    center = centerOffset,
                    radius = radius + strokeWidthPx * 1.5f
                ),
                radius = radius + strokeWidthPx,
                center = centerOffset
            )

            // Recessed Dark Circular Track
            drawCircle(
                color = trackColor,
                radius = radius,
                center = centerOffset,
                style = Stroke(width = strokeWidthPx)
            )

            // Track inner & outer subtle border rims
            drawCircle(
                color = trackBorderColor.copy(alpha = 0.7f),
                radius = radius + (strokeWidthPx / 2f),
                center = centerOffset,
                style = Stroke(width = 1.dp.toPx())
            )
            drawCircle(
                color = trackBorderColor.copy(alpha = 0.7f),
                radius = (radius - (strokeWidthPx / 2f)).coerceAtLeast(0f),
                center = centerOffset,
                style = Stroke(width = 1.dp.toPx())
            )

            // Milestone tick marks at 25%, 50%, 75%, 100%
            listOf(0.25f, 0.5f, 0.75f, 1.0f).forEach { fraction ->
                val angleDeg = -90f + fraction * 360f
                val angleRad = Math.toRadians(angleDeg.toDouble())
                val innerR = radius - (strokeWidthPx / 2f) + 1.dp.toPx()
                val outerR = radius + (strokeWidthPx / 2f) - 1.dp.toPx()

                val startX = centerOffset.x + (innerR * cos(angleRad)).toFloat()
                val startY = centerOffset.y + (innerR * sin(angleRad)).toFloat()
                val endX = centerOffset.x + (outerR * cos(angleRad)).toFloat()
                val endY = centerOffset.y + (outerR * sin(angleRad)).toFloat()

                drawLine(
                    color = Color.White.copy(alpha = 0.25f),
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 1.5.dp.toPx()
                )
            }

            // Active Arc Sweep
            val sweepAngle = (progress * 360f).coerceIn(0f, 360f)
            if (sweepAngle > 0f) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = activeGradientColors,
                        center = centerOffset
                    ),
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round),
                    size = Size(radius * 2f, radius * 2f),
                    topLeft = Offset(centerOffset.x - radius, centerOffset.y - radius)
                )

                // Leading glowing head bead
                val endAngleRad = Math.toRadians((-90f + sweepAngle).toDouble())
                val beadX = centerOffset.x + (radius * cos(endAngleRad)).toFloat()
                val beadY = centerOffset.y + (radius * sin(endAngleRad)).toFloat()

                drawCircle(
                    color = Color.White,
                    radius = (strokeWidthPx / 2f) - 2.dp.toPx(),
                    center = Offset(beadX, beadY)
                )
                drawCircle(
                    color = if (isGoalMet) EmeraldMist else CrystalIce,
                    radius = (strokeWidthPx / 2f),
                    center = Offset(beadX, beadY),
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }

        // Center Readout Display
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Icon Pill
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                (if (isGoalMet) EmeraldMist else CrystalIce).copy(alpha = 0.25f),
                                Color.Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isGoalMet) "🏆" else "💧",
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Current Volume Readout
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$currentMl",
                    color = TextPrimary,
                    fontFamily = ChillaxRoundedDisplayFamily,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag("hydration_current_text")
                )
                Text(
                    text = " ml",
                    color = CrystalIce,
                    fontFamily = ChillaxRoundedDisplayFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }

            // Target % badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isGoalMet) EmeraldMist.copy(alpha = 0.2f)
                        else CrystalIce.copy(alpha = 0.15f)
                    )
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "$percentage% OF TARGET",
                    color = if (isGoalMet) EmeraldMist else CrystalIce,
                    fontFamily = ChillaxRoundedDisplayFamily,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Subtitle status
            Text(
                text = if (isGoalMet) "Goal Crushed! 🎉" else "$remainingMl ml to 3.0L goal",
                color = if (isGoalMet) EmeraldMist else TextSecondary,
                fontFamily = OutfitBodyFamily,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Mini milestone progress capsules for Morning (1000ml), Midday (2000ml), and Goal (3000ml).
 */
@Composable
private fun HydrationMilestonePills(
    currentMl: Int,
    targetMl: Int
) {
    val milestones = listOf(
        Triple("🌅 Morning", 1000, currentMl >= 1000),
        Triple("☀️ Midday", 2000, currentMl >= 2000),
        Triple("🌙 3L Goal", targetMl, currentMl >= targetMl)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        milestones.forEach { (label, mlTarget, reached) ->
            val bg = if (reached) EmeraldMistDark.copy(alpha = 0.25f) else Color(0xFF0F172A)
            val border = if (reached) EmeraldMist.copy(alpha = 0.5f) else SpecularBorder.copy(alpha = 0.2f)
            val textColor = if (reached) EmeraldMist else TextSecondary

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(bg)
                    .border(1.dp, border, RoundedCornerShape(12.dp))
                    .padding(vertical = 8.dp, horizontal = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = label,
                        color = textColor,
                        fontFamily = ChillaxRoundedDisplayFamily,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (reached) "✓ Done" else "$mlTarget ml",
                        color = if (reached) EmeraldMist else TextTertiary,
                        fontFamily = OutfitBodyFamily,
                        fontSize = 9.sp
                    )
                }
            }
        }
    }
}

/**
 * Tactile Quick-Add Button for a single glass size preset.
 * Built with Dark Claymorphism highlights, specular rim, and accessible touch target >= 48dp.
 */
@Composable
private fun TactileGlassQuickAddButton(
    preset: GlassPreset,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .testTag(preset.testTag)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = Color.Black,
                spotColor = CrystalIce.copy(alpha = 0.25f)
            )
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        ClaySurfaceTop,
                        ClaySurfaceBottom
                    )
                )
            )
            .border(1.dp, SpecularBorder, RoundedCornerShape(18.dp))
            .drawWithContent {
                drawContent()
                // Top specular reflection line
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            ClayHighlightIce.copy(alpha = 0.45f),
                            Color.Transparent
                        )
                    ),
                    cornerRadius = CornerRadius(18.dp.toPx(), 18.dp.toPx()),
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = CrystalIce),
                onClick = onClick
            )
            .padding(horizontal = 6.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Icon
            Text(
                text = preset.iconEmoji,
                fontSize = 20.sp
            )

            // Amount Label
            Text(
                text = preset.name,
                color = CrystalIce,
                fontFamily = ChillaxRoundedDisplayFamily,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            // Vessel Subtitle
            Text(
                text = preset.vesselType,
                color = TextSecondary,
                fontFamily = OutfitBodyFamily,
                fontSize = 9.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Fine tune button (-100 or +100 ml).
 */
@Composable
private fun FineTuneButton(
    label: String,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .testTag(testTag)
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF1E293B))
            .border(1.dp, SpecularBorder.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = CrystalIce,
            fontFamily = ChillaxRoundedDisplayFamily,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Celebratory banner shown when daily hydration target is achieved.
 */
@Composable
private fun GoalAchievedBanner(
    currentMl: Int,
    targetMl: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        EmeraldMistDark.copy(alpha = 0.35f),
                        ClaySurfaceBottom
                    )
                )
            )
            .border(1.dp, EmeraldMist.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(EmeraldMist.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🎉", fontSize = 18.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Daily Hydration Cleared!",
                    color = EmeraldMist,
                    fontFamily = ChillaxRoundedDisplayFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Logged $currentMl ml against your $targetMl ml target. Optimal blood viscosity and electrolyte transport preserved.",
                    color = TextPrimary,
                    fontFamily = OutfitBodyFamily,
                    fontSize = 11.sp
                )
            }
        }
    }
}

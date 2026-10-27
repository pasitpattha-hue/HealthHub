package com.example.ui.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ChillaxRoundedDisplayFamily
import com.example.ui.theme.ClayLavender
import com.example.ui.theme.ClayPeach
import com.example.ui.theme.ClayShadowDark
import com.example.ui.theme.ClaySurfaceBottom
import com.example.ui.theme.ClaySurfaceTop
import com.example.ui.theme.CrystalIce
import com.example.ui.theme.EmeraldMist
import com.example.ui.theme.ObsidianDeep
import com.example.ui.theme.OutfitBodyFamily
import com.example.ui.theme.SoftCoralRose
import com.example.ui.theme.SpecularBorder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.WarmChampagne

data class BoopHabitItem(
    val id: String,
    val emoji: String,
    val title: String,
    val subtitle: String,
    val streakDays: Int,
    val accentColor: Color,
    val isBooped: Boolean
)

@Composable
fun BoopClayLandingSection(
    onOpenAiSnap: () -> Unit,
    onQuickLogMeal: () -> Unit,
    onOpenTrends: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val habits = remember {
        mutableStateListOf(
            BoopHabitItem(
                id = "habit_protein",
                emoji = "🥩",
                title = "120g Protein Pace",
                subtitle = "103g logged • 17g to go",
                streakDays = 14,
                accentColor = EmeraldMist,
                isBooped = true
            ),
            BoopHabitItem(
                id = "habit_pelvis",
                emoji = "🧘",
                title = "Pelvis APT Reset",
                subtitle = "Visbody 11.7cm • 10m Pilates",
                streakDays = 9,
                accentColor = CrystalIce,
                isBooped = true
            ),
            BoopHabitItem(
                id = "habit_hydration",
                emoji = "💧",
                title = "Sodium Balance Flush",
                subtitle = "Keep < 2,300mg + 3L Water",
                streakDays = 6,
                accentColor = ClayLavender,
                isBooped = false
            ),
            BoopHabitItem(
                id = "habit_sleep",
                emoji = "😴",
                title = "84ms HRV Recovery",
                subtitle = "In bed by 22:30 • Deep sleep",
                streakDays = 19,
                accentColor = WarmChampagne,
                isBooped = true
            )
        )
    }

    val boopedCount = habits.count { it.isBooped }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CrystalCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 32.dp,
            testTag = "boop_hero_card"
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                FloatingClayOrb(
                    baseColor = CrystalIce,
                    sizeDp = 58.dp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-14).dp, y = 16.dp)
                )
                FloatingClayOrb(
                    baseColor = ClayLavender,
                    sizeDp = 38.dp,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = 18.dp, y = 88.dp)
                )
                FloatingClayOrb(
                    baseColor = EmeraldMist,
                    sizeDp = 44.dp,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-22).dp, y = (-40).dp)
                )
                FloatingClayOrb(
                    baseColor = SoftCoralRose,
                    sizeDp = 28.dp,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset(x = 12.dp, y = 40.dp)
                )

                Canvas(
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.CenterEnd)
                        .offset(x = (-8).dp, y = (-60).dp)
                ) {
                    if (size.minDimension <= 0f) return@Canvas
                    drawCircle(
                        brush = Brush.linearGradient(
                            colors = listOf(WarmChampagne, SoftCoralRose)
                        ),
                        radius = size.minDimension * 0.36f,
                        style = Stroke(width = 11.dp.toPx())
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.45f),
                        radius = size.minDimension * 0.36f,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CrystalPill(
                        text = "🫧 BOOP × HEALTHHUB • DARK CLAY",
                        accentColor = CrystalIce
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Boop your daily habits into peak performance.",
                        color = TextPrimary,
                        fontFamily = ChillaxRoundedDisplayFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp,
                        lineHeight = 31.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Friendly, tactile habit streaks fused with executive metabolic & biomechanical telemetry.",
                        color = TextSecondary,
                        fontFamily = OutfitBodyFamily,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    ClayPhoneMockup(
                        habits = habits,
                        boopedCount = boopedCount,
                        onToggleHabit = { index ->
                            val item = habits[index]
                            val nextBooped = !item.isBooped
                            habits[index] = item.copy(
                                isBooped = nextBooped,
                                streakDays = if (nextBooped) item.streakDays + 1 else (item.streakDays - 1).coerceAtLeast(1)
                            )
                        }
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("boop_three_features_section"),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "WHY BOOP WORKS",
                    color = CrystalIce,
                    fontFamily = ChillaxRoundedDisplayFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.1.sp
                )
                CrystalPill(
                    text = "3 Core Pillars",
                    accentColor = ClayLavender
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PuffyFeatureMiniCard(
                    emoji = "📸",
                    title = "AI Snap Boop",
                    desc = "1-tap Gemini vision for meals, watch runs & 3D body scans.",
                    accent = CrystalIce,
                    onClick = onOpenAiSnap,
                    modifier = Modifier.weight(1f),
                    testTag = "feature_card_ai_snap"
                )
                PuffyFeatureMiniCard(
                    emoji = "🔥",
                    title = "GAS v4.0 Math",
                    desc = "Live TDEE, 10% TEF & net calorie balance without guesswork.",
                    accent = EmeraldMist,
                    onClick = { onOpenTrends?.invoke() },
                    modifier = Modifier.weight(1f),
                    testTag = "feature_card_energy_math"
                )
                PuffyFeatureMiniCard(
                    emoji = "💪",
                    title = "Clay Streaks",
                    desc = "P-01 to P-07 gym & posture habits that feel good to tap.",
                    accent = ClayLavender,
                    onClick = onQuickLogMeal,
                    modifier = Modifier.weight(1f),
                    testTag = "feature_card_clay_streaks"
                )
            }
        }

        BrightClayCtaBand(
            boopedCount = boopedCount,
            totalHabits = habits.size,
            onSnapClick = onOpenAiSnap,
            onQuickMealClick = onQuickLogMeal
        )
    }
}

@Composable
private fun ClayPhoneMockup(
    habits: List<BoopHabitItem>,
    boopedCount: Int,
    onToggleHabit: (Int) -> Unit
) {
    val phoneShape = RoundedCornerShape(36.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth(0.94f)
            .testTag("boop_phone_mockup")
            .shadow(
                elevation = 22.dp,
                shape = phoneShape,
                ambientColor = Color.Black,
                spotColor = CrystalIce.copy(alpha = 0.45f)
            )
            .clip(phoneShape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF27385B),
                        Color(0xFF101829),
                        Color(0xFF0A0F1C)
                    )
                )
            )
            .border(3.dp, Color.White.copy(alpha = 0.22f), phoneShape)
            .drawWithContent {
                drawContent()
                val r = 36.dp.toPx()
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.35f), Color.Transparent),
                        startY = 0f,
                        endY = (size.height * 0.35f).coerceAtLeast(1f)
                    ),
                    cornerRadius = CornerRadius(r, r),
                    style = Stroke(width = 3.dp.toPx())
                )
            }
            .padding(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(ObsidianDeep)
                .border(1.dp, SpecularBorder, RoundedCornerShape(26.dp))
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .width(76.dp)
                    .height(18.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFF04060C))
                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(50)),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(EmeraldMist)
                    )
                    Text(
                        text = "boop live",
                        color = TextSecondary,
                        fontFamily = ChillaxRoundedDisplayFamily,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Today's Boop Board",
                        color = TextPrimary,
                        fontFamily = ChillaxRoundedDisplayFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Tap any clay pill to boop your streak",
                        color = TextTertiary,
                        fontFamily = OutfitBodyFamily,
                        fontSize = 11.sp
                    )
                }
                CrystalPill(
                    text = "$boopedCount/${habits.size} Booped ✨",
                    accentColor = if (boopedCount == habits.size) EmeraldMist else CrystalIce
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            habits.forEachIndexed { index, habit ->
                val scale by animateFloatAsState(
                    targetValue = if (habit.isBooped) 1.0f else 0.98f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "habit_scale"
                )
                val rowBg by animateColorAsState(
                    targetValue = if (habit.isBooped) habit.accentColor.copy(alpha = 0.16f) else Color(0xFF131C2E),
                    label = "habit_bg"
                )
                val borderCol by animateColorAsState(
                    targetValue = if (habit.isBooped) habit.accentColor.copy(alpha = 0.55f) else SpecularBorder,
                    label = "habit_border"
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .scale(scale)
                        .testTag(habit.id)
                        .shadow(6.dp, RoundedCornerShape(20.dp), spotColor = habit.accentColor.copy(alpha = 0.25f))
                        .clip(RoundedCornerShape(20.dp))
                        .background(rowBg)
                        .border(1.2.dp, borderCol, RoundedCornerShape(20.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = habit.accentColor),
                            onClick = { onToggleHabit(index) }
                        )
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(13.dp))
                                .background(habit.accentColor.copy(alpha = 0.22f))
                                .border(1.dp, habit.accentColor.copy(alpha = 0.45f), RoundedCornerShape(13.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = habit.emoji, fontSize = 17.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = habit.title,
                                color = TextPrimary,
                                fontFamily = ChillaxRoundedDisplayFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = habit.subtitle,
                                color = TextSecondary,
                                fontFamily = OutfitBodyFamily,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(
                                if (habit.isBooped) {
                                    Brush.verticalGradient(
                                        listOf(habit.accentColor, habit.accentColor.copy(alpha = 0.75f))
                                    )
                                } else {
                                    Brush.verticalGradient(
                                        listOf(ClaySurfaceTop, ClaySurfaceBottom)
                                    )
                                }
                            )
                            .border(
                                1.dp,
                                if (habit.isBooped) Color.White.copy(alpha = 0.6f) else SpecularBorder,
                                RoundedCornerShape(50)
                            )
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (habit.isBooped) "✓ ${habit.streakDays}d" else "Boop +1",
                            color = if (habit.isBooped) Color(0xFF06101E) else TextSecondary,
                            fontFamily = ChillaxRoundedDisplayFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PuffyFeatureMiniCard(
    emoji: String,
    title: String,
    desc: String,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    CrystalCard(
        modifier = modifier,
        cornerRadius = 26.dp,
        borderColor = accent.copy(alpha = 0.38f),
        topSpecularColor = accent.copy(alpha = 0.45f),
        onClick = onClick,
        testTag = testTag
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .shadow(6.dp, RoundedCornerShape(14.dp), spotColor = accent)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                accent.copy(alpha = 0.35f),
                                accent.copy(alpha = 0.14f)
                            )
                        )
                    )
                    .border(1.dp, accent.copy(alpha = 0.55f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                color = TextPrimary,
                fontFamily = ChillaxRoundedDisplayFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = desc,
                color = TextSecondary,
                fontFamily = OutfitBodyFamily,
                fontSize = 10.sp,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
private fun BrightClayCtaBand(
    boopedCount: Int,
    totalHabits: Int,
    onSnapClick: () -> Unit,
    onQuickMealClick: () -> Unit
) {
    val shape = RoundedCornerShape(30.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("boop_bright_cta_band")
            .shadow(
                elevation = 18.dp,
                shape = shape,
                ambientColor = CrystalIce.copy(alpha = 0.4f),
                spotColor = ClayLavender.copy(alpha = 0.5f)
            )
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF1D4ED8),
                        Color(0xFF0284C7),
                        Color(0xFF0D9488)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(900f, 400f)
                )
            )
            .border(2.dp, Color.White.copy(alpha = 0.42f), shape)
            .drawWithContent {
                drawContent()
                val r = 30.dp.toPx()
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.45f), Color.Transparent),
                        startY = 0f,
                        endY = (size.height * 0.45f).coerceAtLeast(1f)
                    ),
                    cornerRadius = CornerRadius(r, r),
                    style = Stroke(width = 3.dp.toPx())
                )
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        listOf(Color.Transparent, ClayShadowDark.copy(alpha = 0.6f)),
                        startY = size.height * 0.55f,
                        endY = size.height.coerceAtLeast(1f)
                    ),
                    cornerRadius = CornerRadius(r, r),
                    style = Stroke(width = 3.dp.toPx())
                )
            }
            .padding(18.dp)
    ) {
        FloatingClayOrb(
            baseColor = ClayPeach,
            sizeDp = 34.dp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-6).dp, y = 4.dp)
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CrystalPill(
                    text = "⚡ DAILY STREAK BOOST",
                    accentColor = Color.White
                )
                Text(
                    text = "$boopedCount/$totalHabits Habits Complete",
                    color = Color.White.copy(alpha = 0.9f),
                    fontFamily = ChillaxRoundedDisplayFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Keep the momentum rolling — Boop your next meal or scan!",
                color = Color.White,
                fontFamily = ChillaxRoundedDisplayFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                lineHeight = 23.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Snap food, Huawei Watch workouts, or Visbody 3D scans with 1-tap Gemini AI.",
                color = Color.White.copy(alpha = 0.88f),
                fontFamily = OutfitBodyFamily,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CrystalButton(
                    text = "📸 Boop AI Snap",
                    onClick = onSnapClick,
                    isPrimary = true,
                    modifier = Modifier.weight(1f),
                    testTag = "cta_boop_ai_snap_button"
                )
                CrystalButton(
                    text = "🍽️ Quick Log",
                    onClick = onQuickMealClick,
                    isPrimary = false,
                    modifier = Modifier.weight(1f),
                    testTag = "cta_quick_log_button"
                )
            }
        }
    }
}

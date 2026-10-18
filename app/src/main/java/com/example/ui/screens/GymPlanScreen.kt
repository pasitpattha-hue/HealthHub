package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.GymExerciseEntity
import com.example.ui.components.CrystalCard
import com.example.ui.components.CrystalPill
import com.example.ui.theme.CrystalIce
import com.example.ui.theme.EmeraldMist
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.SmokedCrystal
import com.example.ui.theme.SmokedCrystalElevated
import com.example.ui.theme.SpecularBorder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.WarmChampagne

data class MicrocycleDay(
    val code: String,
    val dayName: String,
    val focus: String,
    val icon: String
)

val MICROCYCLE_DAYS = listOf(
    MicrocycleDay("P-01", "Sun", "Recovery & Mobility", "🧘"),
    MicrocycleDay("P-02", "Mon", "Pilates for APT", "🤸"),
    MicrocycleDay("P-03", "Tue", "Heavy Legs & Shin", "🏋️"),
    MicrocycleDay("P-04", "Wed", "Jetts Upper Body", "💪"),
    MicrocycleDay("P-05", "Thu", "Tempo Repeats", "⚡"),
    MicrocycleDay("P-06", "Fri", "Zone 2 Flush", "🏃"),
    MicrocycleDay("P-07", "Sat", "Trail Long Run", "⛰️")
)

@Composable
fun GymPlanScreen(
    selectedDayCode: String,
    exercises: List<GymExerciseEntity>,
    onSelectDay: (String) -> Unit,
    onToggleExercise: (GymExerciseEntity) -> Unit,
    onUpdateSetCount: (GymExerciseEntity, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentDay = MICROCYCLE_DAYS.find { it.code == selectedDayCode } ?: MICROCYCLE_DAYS[2]

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("gym_plan_screen")
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "7-DAY MICROCYCLE MASTER PLAN",
                color = CrystalIce,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )
            Text(
                text = "Gym & Periodization Hub",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Progressive set logging from warm-up to top set with strict coach cues for APT reduction & shin splint resilience.",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        // Microcycle Day Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SmokedCrystalElevated)
                    .border(1.dp, SpecularBorder, RoundedCornerShape(16.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MICROCYCLE_DAYS.forEach { day ->
                    val isSelected = day.code == selectedDayCode
                    val activeBg by animateColorAsState(
                        targetValue = if (isSelected) CrystalIce.copy(alpha = 0.25f) else Color.Transparent,
                        label = "day_tab_bg"
                    )
                    val textCol = if (isSelected) CrystalIce else TextSecondary

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(activeBg)
                            .clickable { onSelectDay(day.code) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(day.icon, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = day.dayName,
                                color = textCol,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Session Overview Banner
        item {
            CrystalCard(
                modifier = Modifier.fillMaxWidth(),
                testTag = "session_banner_card"
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CrystalPill(currentDay.code, CrystalIce)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = currentDay.focus,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = when (currentDay.code) {
                                "P-02" -> "Target: Anterior Pelvic Tilt (APT) from 13.2cm down to 11.7cm"
                                "P-03" -> "Top Set: Squat 121kg x 5 | Leg Press 375.2kg (includes 75.2kg sled)"
                                "P-07" -> "Bang Phra / Khao Chalak loop | 16km + 650m D+ technical elevation"
                                else -> "Structured microcycle progression for Pasit 2026"
                            },
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    val completedCount = exercises.count { it.isCompleted }
                    val totalCount = exercises.size
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(EmeraldMist.copy(alpha = 0.15f))
                            .border(1.dp, EmeraldMist.copy(alpha = 0.4f), CircleShape)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$completedCount/$totalCount Done",
                            color = EmeraldMist,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Exercises Checklist
        items(exercises) { ex ->
            CrystalCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = if (ex.isCompleted) EmeraldMist.copy(alpha = 0.4f) else SpecularBorder,
                testTag = "exercise_card_${ex.id}"
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Checkbox(
                                checked = ex.isCompleted,
                                onCheckedChange = { onToggleExercise(ex) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = EmeraldMist,
                                    uncheckedColor = TextSecondary,
                                    checkmarkColor = Color.Black
                                ),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = ex.exerciseName,
                                    color = if (ex.isCompleted) TextSecondary else TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                if (ex.topWeightKg > 0.0) {
                                    Text(
                                        text = "Top Weight: ${ex.topWeightKg} kg",
                                        color = CrystalIce,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        // Set Counter Controls
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SmokedCrystalElevated)
                                .border(1.dp, SpecularBorder, RoundedCornerShape(8.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            IconButton(
                                onClick = { onUpdateSetCount(ex, ex.completedSets - 1) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrement", tint = TextSecondary, modifier = Modifier.size(14.dp))
                            }
                            Text(
                                text = "${ex.completedSets}/${ex.totalSets}",
                                color = if (ex.isCompleted) EmeraldMist else TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )
                            IconButton(
                                onClick = { onUpdateSetCount(ex, ex.completedSets + 1) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Increment", tint = TextSecondary, modifier = Modifier.size(14.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Warm-up and Working Sets breakdown
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF090D18))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "🔄 Warm-up: ${ex.warmUpSets}",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "⚡ Working: ${ex.workingSets}",
                            color = CrystalIce,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Coach Cue
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(WarmChampagne.copy(alpha = 0.08f))
                            .border(1.dp, WarmChampagne.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🎯 Cue: ", color = WarmChampagne, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = ex.coachCue,
                            color = TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}

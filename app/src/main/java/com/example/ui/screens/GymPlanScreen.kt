package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.GymExerciseEntity
import com.example.ui.components.CrystalButton
import com.example.ui.components.CrystalCard
import com.example.ui.components.CrystalPill
import com.example.ui.theme.ChillaxRoundedDisplayFamily
import com.example.ui.theme.CrystalIce
import com.example.ui.theme.EmeraldMist
import com.example.ui.theme.OutfitBodyFamily
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmChampagne

@Composable
fun GymPlanScreen(
    selectedDayCode: String,
    exercises: List<GymExerciseEntity>,
    onSelectDay: (String) -> Unit,
    onToggleExercise: (GymExerciseEntity) -> Unit,
    onUpdateSetCount: (GymExerciseEntity, Int) -> Unit
) {
    val days = listOf("P-01", "P-02", "P-03", "P-04", "P-05", "P-06", "P-07")
    val sessionTitle = exercises.firstOrNull()?.sessionTitle ?: "Microcycle Session"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("microcycle_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "💪 7-Day Microcycle & Posture",
                color = TextPrimary,
                fontFamily = ChillaxRoundedDisplayFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            Text(
                text = "Hypertrophy + Anterior Pelvic Tilt (APT) Corrective Biomechanics",
                color = TextSecondary,
                fontFamily = OutfitBodyFamily,
                fontSize = 13.sp
            )
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(days) { code ->
                    CrystalButton(
                        text = code,
                        isPrimary = code == selectedDayCode,
                        onClick = { onSelectDay(code) },
                        testTag = "day_selector_$code"
                    )
                }
            }
        }

        item {
            CrystalCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        CrystalPill(text = selectedDayCode, accentColor = CrystalIce)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = sessionTitle,
                            color = TextPrimary,
                            fontFamily = ChillaxRoundedDisplayFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                    val done = exercises.count { it.isCompleted }
                    CrystalPill(
                        text = "$done/${exercises.size} Done",
                        accentColor = EmeraldMist
                    )
                }
            }
        }

        if (exercises.isEmpty()) {
            item {
                CrystalCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("gym_empty_state_card"),
                    cornerRadius = 24.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(text = "🏋️", fontSize = 28.sp)
                        Text(
                            text = "No Workout Scheduled",
                            color = TextPrimary,
                            fontFamily = ChillaxRoundedDisplayFamily,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "No exercises found for microcycle day $selectedDayCode. Select another day code (P-01 to P-07) from the strip above.",
                            color = TextSecondary,
                            fontFamily = OutfitBodyFamily,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(exercises, key = { it.id }) { ex ->
                CrystalCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 24.dp,
                    testTag = "exercise_card_${ex.id}"
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = ex.exerciseName,
                                    color = TextPrimary,
                                    fontFamily = ChillaxRoundedDisplayFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "${ex.setsReps} • Top: ${ex.topWeightKg}kg • RPE ${ex.targetRpe} • Rest ${ex.restSeconds}s",
                                    color = TextSecondary,
                                    fontFamily = OutfitBodyFamily,
                                    fontSize = 12.sp
                                )
                            }
                            Checkbox(
                                checked = ex.isCompleted,
                                onCheckedChange = { onToggleExercise(ex) },
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("exercise_checkbox_${ex.id}"),
                                colors = CheckboxDefaults.colors(
                                    checkedColor = EmeraldMist,
                                    uncheckedColor = TextSecondary
                                )
                            )
                        }

                        // Set logging controls & completion progress
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CrystalPill(
                                text = "Sets: ${ex.completedSets}/${ex.totalSets} ${if (ex.isCompleted) "✓ Completed" else "In Progress"}",
                                accentColor = if (ex.isCompleted) EmeraldMist else WarmChampagne
                            )

                            // [-] and [+] Stepper for sets
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                androidx.compose.material3.IconButton(
                                    onClick = { onUpdateSetCount(ex, ex.completedSets - 1) },
                                    enabled = ex.completedSets > 0,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .testTag("exercise_minus_set_${ex.id}")
                                ) {
                                    Text(
                                        text = "−1",
                                        color = if (ex.completedSets > 0) CrystalIce else TextSecondary.copy(alpha = 0.4f),
                                        fontFamily = ChillaxRoundedDisplayFamily,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                androidx.compose.material3.IconButton(
                                    onClick = { onUpdateSetCount(ex, ex.completedSets + 1) },
                                    enabled = ex.completedSets < ex.totalSets,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .testTag("exercise_plus_set_${ex.id}")
                                ) {
                                    Text(
                                        text = "+1 Set",
                                        color = if (ex.completedSets < ex.totalSets) EmeraldMist else TextSecondary.copy(alpha = 0.4f),
                                        fontFamily = ChillaxRoundedDisplayFamily,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

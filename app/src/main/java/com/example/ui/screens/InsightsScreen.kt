package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BodyCompEntity
import com.example.data.local.SleepEntity
import com.example.data.local.WorkoutEntity
import com.example.data.model.DailySummary
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
fun InsightsScreen(
    dailySummaries: List<DailySummary>,
    workouts: List<WorkoutEntity>,
    bodyComps: List<BodyCompEntity>,
    sleepRecords: List<SleepEntity>,
    onCopyContext: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CrystalCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Multi-Scale Body Comp & Pelvis APT",
                    color = TextPrimary,
                    fontFamily = ChillaxRoundedDisplayFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                bodyComps.take(3).forEach { bc ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${bc.date} • ${bc.sourceDevice}",
                            color = TextSecondary,
                            fontFamily = OutfitBodyFamily,
                            fontSize = 12.sp
                        )
                        CrystalPill(
                            text = "${bc.weightKg}kg • BF ${bc.bodyFatPct}% • APT ${bc.aptCm}cm",
                            accentColor = CrystalIce
                        )
                    }
                }
            }
        }

        CrystalCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Trail & Running Cadence Telemetry",
                    color = TextPrimary,
                    fontFamily = ChillaxRoundedDisplayFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                workouts.take(2).forEach { w ->
                    Text(
                        text = "${w.date}: ${w.title} (${w.distanceKm}km • D+${w.elevationDPlus}m • ${w.cadence} spm)",
                        color = EmeraldMist,
                        fontFamily = OutfitBodyFamily,
                        fontSize = 12.sp
                    )
                }
            }
        }

        CrystalButton(
            text = "📋 Copy 14-Day Context for Gemini",
            icon = Icons.Default.ContentCopy,
            onClick = onCopyContext,
            modifier = Modifier.fillMaxWidth(),
            testTag = "copy_14day_context_button"
        )
    }
}

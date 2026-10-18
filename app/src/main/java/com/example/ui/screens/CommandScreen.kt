package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.MealEntity
import com.example.data.model.DailySummary
import com.example.data.model.EnergyStatus
import com.example.data.model.HealthConstants
import com.example.ui.components.CrystalBadge
import com.example.ui.components.CrystalButton
import com.example.ui.components.CrystalCard
import com.example.ui.components.CrystalPill
import com.example.ui.theme.CrystalIce
import com.example.ui.theme.EmeraldMist
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.SmokedCrystal
import com.example.ui.theme.SmokedCrystalElevated
import com.example.ui.theme.SoftCoralRose
import com.example.ui.theme.SpecularBorder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.WarmChampagne

@Composable
fun CommandScreen(
    dailySummaries: List<DailySummary>,
    todayMeals: List<MealEntity>,
    onAddMeal: (name: String, calories: Int, protein: Double, carbs: Double, fat: Double, sodium: Int, mealType: String) -> Unit,
    onDeleteMeal: (id: Long) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenInsights: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val currentSummary = dailySummaries.firstOrNull() ?: DailySummary(
        date = "2026-10-18",
        intakeKcal = 2420,
        carbsG = 242,
        fatG = 113,
        proteinG = 103,
        sodiumMg = 3640,
        activeBurnKcal = 0,
        workoutTitle = "พัก (Rest Day)",
        weightKg = 74.6,
        weightSource = com.example.data.model.DeviceSource.VISBODY_3D,
        smmKg = 26.7,
        bodyFatPct = 35.4,
        aptCm = 11.7,
        sleepHrvMs = 84,
        sleepScore = 84
    )

    var showAddMealDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("command_center_screen")
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PASIT HEALTH & PERFORMANCE",
                        color = CrystalIce,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "Command Center 2026",
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (onOpenInsights != null) {
                        CrystalButton(
                            text = "📈 Trends",
                            onClick = onOpenInsights,
                            isPrimary = false,
                            modifier = Modifier
                                .height(34.dp)
                                .padding(end = 6.dp),
                            testTag = "open_insights_button"
                        )
                    }
                    CrystalPill(
                        text = "GAS v4.0",
                        accentColor = EmeraldMist,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier
                            .testTag("settings_icon_button")
                            .size(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = TextSecondary
                        )
                    }
                }
            }
        }

        // 7 Executive KPI Cards Grid
        item {
            Text(
                text = "EXECUTIVE TELEMETRY (TODAY)",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Row 1: Intake & TDEE
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Card 1: 🍽️ Intake
                CrystalCard(
                    modifier = Modifier.weight(1f),
                    testTag = "kpi_intake_card"
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("🍽️ รับเข้า", color = TextSecondary, fontSize = 12.sp)
                            CrystalPill("TEF: +${currentSummary.tef}", CrystalIce)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${currentSummary.intakeKcal}",
                            color = TextPrimary,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "kcal (C:${currentSummary.carbsG}g / F:${currentSummary.fatG}g)",
                            color = TextTertiary,
                            fontSize = 11.sp
                        )
                    }
                }

                // Card 2: 🔥 TDEE
                CrystalCard(
                    modifier = Modifier.weight(1f),
                    testTag = "kpi_tdee_card"
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("🔥 เผาผลาญรวม", color = TextSecondary, fontSize = 12.sp)
                            Text("BMR: 1608", color = TextTertiary, fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${currentSummary.tdee}",
                            color = TextPrimary,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "1608+400+${currentSummary.tef}+${currentSummary.activeBurnKcal}",
                            color = TextTertiary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Card 3: ⚖️ Net Energy Balance (Executive Hero Card)
            CrystalCard(
                modifier = Modifier.fillMaxWidth(),
                testTag = "kpi_net_balance_card"
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "⚖️ สมดุลแคลอรี่ Net Balance",
                                color = TextSecondary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            val netPrefix = if (currentSummary.netBalance > 0) "+" else ""
                            Text(
                                text = "$netPrefix${currentSummary.netBalance} kcal",
                                color = currentSummary.status.color,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        CrystalBadge(status = currentSummary.status)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Formula: Intake (${currentSummary.intakeKcal}) - TDEE (${currentSummary.tdee})",
                        color = TextTertiary,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Row 2: Protein & Active Workout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Card 4: 🥩 Protein & Sodium
                CrystalCard(
                    modifier = Modifier.weight(1f),
                    testTag = "kpi_protein_card"
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("🥩 โปรตีน (เป้า 120g)", color = TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${currentSummary.proteinG}g",
                                color = EmeraldMist,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "(${currentSummary.proteinPct}%)",
                                color = TextTertiary,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Sodium: ${currentSummary.sodiumMg} / 2300mg",
                            color = if (currentSummary.sodiumMg > 2300) SoftCoralRose else TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }

                // Card 5: 🏃 Active Workout
                CrystalCard(
                    modifier = Modifier.weight(1f),
                    testTag = "kpi_workout_card"
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("🏃 ออกกำลังกาย", color = TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "+${currentSummary.activeBurnKcal} kcal",
                            color = CrystalIce,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentSummary.workoutTitle,
                            color = TextSecondary,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Row 3: Weight & Physique/Recovery
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Card 6: 🧍 Weight
                CrystalCard(
                    modifier = Modifier.weight(1f),
                    testTag = "kpi_weight_card"
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🧍 น้ำหนัก", color = TextSecondary, fontSize = 12.sp)
                            CrystalPill(currentSummary.weightSource.label, CrystalIce)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${currentSummary.weightKg} kg",
                            color = TextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "BF: ${currentSummary.bodyFatPct}% | SMM: ${currentSummary.smmKg}kg",
                            color = TextTertiary,
                            fontSize = 10.sp
                        )
                    }
                }

                // Card 7: 📐 Biomechanics & Sleep HRV
                CrystalCard(
                    modifier = Modifier.weight(1f),
                    testTag = "kpi_recovery_card"
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("📐 สรีระ & ฟื้นฟู", color = TextSecondary, fontSize = 12.sp)
                            CrystalPill("HRV ${currentSummary.sleepHrvMs}ms", EmeraldMist)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "APT ${currentSummary.aptCm} cm",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Visbody 3D APT | Sleep: ${currentSummary.sleepScore}pts",
                            color = TextTertiary,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // Today's Meals Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TODAY'S MEALS BREAKDOWN",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
                CrystalButton(
                    text = "+ Log Meal",
                    onClick = { showAddMealDialog = true },
                    isPrimary = false,
                    modifier = Modifier.height(36.dp),
                    testTag = "add_meal_button"
                )
            }
        }

        items(todayMeals) { meal ->
            CrystalCard(
                modifier = Modifier.fillMaxWidth(),
                testTag = "meal_item_${meal.id}"
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CrystalPill(meal.mealType, CrystalIce)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(meal.time, color = TextTertiary, fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = meal.name,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "P: ${meal.proteinG}g | C: ${meal.carbsG}g | F: ${meal.fatG}g | Na: ${meal.sodiumMg}mg",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${meal.calories} kcal",
                            color = CrystalIce,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        IconButton(
                            onClick = { onDeleteMeal(meal.id) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = TextTertiary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // 7-Day Energy Balance Summary Table
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "7-DAY ENERGY BALANCE SUMMARY (GAS v4.0)",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            CrystalCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Table Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, SpecularBorder, RoundedCornerShape(8.dp))
                            .background(SmokedCrystalElevated)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Date", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f))
                        Text("Intake", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Text("TDEE", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Text("Net", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Text("Status", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.5f))
                    }

                    dailySummaries.forEach { item ->
                        val netSign = if (item.netBalance > 0) "+" else ""
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(item.date.substring(5), color = TextPrimary, fontSize = 11.sp, modifier = Modifier.weight(1.2f))
                            Text("${item.intakeKcal}", color = TextPrimary, fontSize = 11.sp, modifier = Modifier.weight(1f))
                            Text("${item.tdee}", color = TextSecondary, fontSize = 11.sp, modifier = Modifier.weight(1f))
                            Text(
                                "$netSign${item.netBalance}",
                                color = item.status.color,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Box(modifier = Modifier.weight(1.5f)) {
                                Text(
                                    text = item.status.badgeIcon + " " + item.status.labelEn,
                                    color = item.status.color,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(90.dp)) // Clearance for floating bottom bar
        }
    }

    // Quick Add Meal Dialog
    if (showAddMealDialog) {
        var mealName by remember { mutableStateOf("") }
        var mealCalories by remember { mutableStateOf("450") }
        var mealProtein by remember { mutableStateOf("35") }
        var mealCarbs by remember { mutableStateOf("40") }
        var mealFat by remember { mutableStateOf("12") }
        var mealSodium by remember { mutableStateOf("450") }
        var mealType by remember { mutableStateOf("Snack") }

        AlertDialog(
            onDismissRequest = { showAddMealDialog = false },
            title = {
                Text("Log Meal Manually", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = mealName,
                        onValueChange = { mealName = it },
                        label = { Text("Meal Name / Food") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CrystalIce,
                            unfocusedBorderColor = SpecularBorder
                        )
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = mealCalories,
                            onValueChange = { mealCalories = it },
                            label = { Text("Calories kcal") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CrystalIce,
                                unfocusedBorderColor = SpecularBorder
                            )
                        )
                        OutlinedTextField(
                            value = mealProtein,
                            onValueChange = { mealProtein = it },
                            label = { Text("Protein g") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CrystalIce,
                                unfocusedBorderColor = SpecularBorder
                            )
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = mealCarbs,
                            onValueChange = { mealCarbs = it },
                            label = { Text("Carbs g") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CrystalIce,
                                unfocusedBorderColor = SpecularBorder
                            )
                        )
                        OutlinedTextField(
                            value = mealFat,
                            onValueChange = { mealFat = it },
                            label = { Text("Fat g") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CrystalIce,
                                unfocusedBorderColor = SpecularBorder
                            )
                        )
                    }
                    OutlinedTextField(
                        value = mealSodium,
                        onValueChange = { mealSodium = it },
                        label = { Text("Sodium mg") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CrystalIce,
                            unfocusedBorderColor = SpecularBorder
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onAddMeal(
                            mealName.ifBlank { "Quick Logged Meal" },
                            mealCalories.toIntOrNull() ?: 450,
                            mealProtein.toDoubleOrNull() ?: 35.0,
                            mealCarbs.toDoubleOrNull() ?: 40.0,
                            mealFat.toDoubleOrNull() ?: 12.0,
                            mealSodium.toIntOrNull() ?: 450,
                            mealType
                        )
                        showAddMealDialog = false
                    }
                ) {
                    Text("Save Meal", color = CrystalIce, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddMealDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = ObsidianCard
        )
    }
}

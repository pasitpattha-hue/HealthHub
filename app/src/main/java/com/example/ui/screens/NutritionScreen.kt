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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.data.model.HealthConstants
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
fun NutritionScreen(
    currentSummary: DailySummary,
    todayMeals: List<MealEntity>,
    onAddMeal: (name: String, calories: Int, protein: Double, carbs: Double, fat: Double, sodium: Int, mealType: String) -> Unit,
    onDeleteMeal: (id: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddMealDialog by remember { mutableStateOf(false) }

    val totalCalories = todayMeals.sumOf { it.calories }
    val totalProtein = todayMeals.sumOf { it.proteinG }
    val totalCarbs = todayMeals.sumOf { it.carbsG }
    val totalFat = todayMeals.sumOf { it.fatG }
    val totalSodium = todayMeals.sumOf { it.sodiumMg }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("nutrition_screen")
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PRECISION NUTRITION & MACROS",
                        color = CrystalIce,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "Nutrition Command",
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                CrystalButton(
                    text = "+ Log Food",
                    icon = Icons.Default.Add,
                    onClick = { showAddMealDialog = true },
                    isPrimary = true,
                    modifier = Modifier.height(40.dp),
                    testTag = "log_food_button"
                )
            }
        }

        // Daily Calorie & TEF Hero Card
        item {
            CrystalCard(
                modifier = Modifier.fillMaxWidth(),
                testTag = "nutrition_hero_card"
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("พลังงานรับเข้าทั้งหมด (Intake)", color = TextSecondary, fontSize = 12.sp)
                            Text(
                                text = "$totalCalories kcal",
                                color = TextPrimary,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            CrystalPill("TEF: +${Math.round(totalCalories * 0.10f)} kcal", CrystalIce)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "10% Diet Induced Thermogenesis",
                                color = TextTertiary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Macro Distribution Ratio Bar
                    Text("Macro Caloric Ratio", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))

                    val proteinCals = (totalProtein * 4).toFloat()
                    val carbsCals = (totalCarbs * 4).toFloat()
                    val fatCals = (totalFat * 9).toFloat()
                    val sumCals = (proteinCals + carbsCals + fatCals).coerceAtLeast(1f)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .weight((carbsCals / sumCals).coerceAtLeast(0.05f))
                                .fillMaxSize()
                                .background(CrystalIce)
                        )
                        Box(
                            modifier = Modifier
                                .weight((proteinCals / sumCals).coerceAtLeast(0.05f))
                                .fillMaxSize()
                                .background(EmeraldMist)
                        )
                        Box(
                            modifier = Modifier
                                .weight((fatCals / sumCals).coerceAtLeast(0.05f))
                                .fillMaxSize()
                                .background(WarmChampagne)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(2.dp)).background(CrystalIce))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Carbs: ${Math.round(totalCarbs)}g", color = TextSecondary, fontSize = 11.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(2.dp)).background(EmeraldMist))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Protein: ${Math.round(totalProtein)}g", color = TextSecondary, fontSize = 11.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(2.dp)).background(WarmChampagne))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Fat: ${Math.round(totalFat)}g", color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // 4 Macro & Electrolyte Target Cards
        item {
            Text(
                text = "TARGET ADHERENCE & LIMITS",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Card 1: Protein Goal
                CrystalCard(
                    modifier = Modifier.weight(1f),
                    testTag = "protein_target_card"
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🥩 โปรตีน", color = TextSecondary, fontSize = 12.sp)
                            Text("เป้า ≥120g", color = TextTertiary, fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${Math.round(totalProtein)} g",
                            color = EmeraldMist,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        val proteinProgress = (totalProtein / HealthConstants.PROTEIN_GOAL_G).toFloat().coerceIn(0f, 1f)
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { proteinProgress },
                            color = EmeraldMist,
                            trackColor = SmokedCrystalElevated,
                            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp))
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${Math.round(proteinProgress * 100)}% of goal",
                            color = TextTertiary,
                            fontSize = 10.sp
                        )
                    }
                }

                // Card 2: Sodium Limit
                CrystalCard(
                    modifier = Modifier.weight(1f),
                    testTag = "sodium_limit_card"
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🧂 โซเดียม", color = TextSecondary, fontSize = 12.sp)
                            Text("ลิมิต 2300mg", color = TextTertiary, fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        val isOverSodium = totalSodium > HealthConstants.SODIUM_LIMIT_MG
                        Text(
                            text = "$totalSodium mg",
                            color = if (isOverSodium) SoftCoralRose else TextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        val sodiumProgress = (totalSodium / HealthConstants.SODIUM_LIMIT_MG).toFloat().coerceIn(0f, 1.5f)
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { (sodiumProgress / 1.5f).coerceIn(0f, 1f) },
                            color = if (isOverSodium) SoftCoralRose else CrystalIce,
                            trackColor = SmokedCrystalElevated,
                            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp))
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isOverSodium) "⚠️ High Water Retention Spike" else "Normal Hydration",
                            color = if (isOverSodium) SoftCoralRose else EmeraldMist,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Today's Meal Timeline
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TODAY'S LOGGED MEALS (${todayMeals.size})",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "18/10/2026",
                    color = CrystalIce,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        items(todayMeals) { meal ->
            CrystalCard(
                modifier = Modifier.fillMaxWidth(),
                testTag = "nutrition_meal_item_${meal.id}"
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
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "โปรตีน ${meal.proteinG}g | คาร์บ ${meal.carbsG}g | ไขมัน ${meal.fatG}g | โซเดียม ${meal.sodiumMg}mg",
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
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(90.dp))
        }
    }

    // Add Meal Dialog
    if (showAddMealDialog) {
        var mealName by remember { mutableStateOf("") }
        var mealCalories by remember { mutableStateOf("650") }
        var mealProtein by remember { mutableStateOf("32") }
        var mealCarbs by remember { mutableStateOf("68") }
        var mealFat by remember { mutableStateOf("27") }
        var mealSodium by remember { mutableStateOf("950") }
        var mealType by remember { mutableStateOf("อาหารจานหลัก") }

        AlertDialog(
            onDismissRequest = { showAddMealDialog = false },
            title = {
                Text("Log Food Item", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = mealName,
                        onValueChange = { mealName = it },
                        label = { Text("Food / Menu Name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CrystalIce,
                            unfocusedBorderColor = SpecularBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
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
                                unfocusedBorderColor = SpecularBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
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
                                unfocusedBorderColor = SpecularBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
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
                                unfocusedBorderColor = SpecularBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
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
                                unfocusedBorderColor = SpecularBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
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
                            unfocusedBorderColor = SpecularBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onAddMeal(
                            mealName.ifBlank { "Logged Food Item" },
                            mealCalories.toIntOrNull() ?: 650,
                            mealProtein.toDoubleOrNull() ?: 32.0,
                            mealCarbs.toDoubleOrNull() ?: 68.0,
                            mealFat.toDoubleOrNull() ?: 27.0,
                            mealSodium.toIntOrNull() ?: 950,
                            mealType
                        )
                        showAddMealDialog = false
                    }
                ) {
                    Text("Save to Log", color = CrystalIce, fontWeight = FontWeight.Bold)
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

package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CategoryType
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
import com.example.ui.viewmodel.AiSnapFormState

@Composable
fun AiSnapScreen(
    formState: AiSnapFormState,
    onImageSelected: (Bitmap) -> Unit,
    onAnalyze: () -> Unit,
    onCategoryChanged: (CategoryType) -> Unit,
    onFieldChanged: (
        title: String?,
        calories: String?,
        protein: String?,
        carbs: String?,
        fat: String?,
        sodium: String?,
        workoutType: String?,
        distanceKm: String?,
        durationMin: String?,
        activeBurnKcal: String?,
        avgHr: String?,
        elevationDPlus: String?,
        sourceDevice: String?,
        weightKg: String?,
        bodyFatPct: String?,
        smmKg: String?,
        aptCm: String?,
        sleepDurationHours: String?,
        sleepScore: String?,
        sleepHrvMs: String?
    ) -> Unit,
    onSaveAndSync: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Zero-permission Android Photo Picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
                if (bitmap != null) {
                    onImageSelected(bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("ai_snap_screen")
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "MULTIMODAL AI TELEMETRY SCANNER",
                color = CrystalIce,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )
            Text(
                text = "AI Snap & Auto-Extract",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Powered by gemini-2.5-flash with automatic category classification for Nutrition, Workouts, BodyComp & Sleep.",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        // Upload / Camera / Sample presets
        item {
            CrystalCard(
                modifier = Modifier.fillMaxWidth(),
                testTag = "upload_area_card"
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (formState.imageBitmap != null) {
                        Image(
                            bitmap = formState.imageBitmap.asImageBitmap(),
                            contentDescription = "Selected image preview",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .border(1.dp, CrystalIce.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(SmokedCrystalElevated)
                                .border(1.dp, SpecularBorder, RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.CloudUpload,
                                    contentDescription = "Upload",
                                    tint = CrystalIce,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Select Meal, Watch, or Visbody 3D Scan", color = TextPrimary, fontSize = 13.sp)
                                Text("JPEG / PNG from photo picker or samples below", color = TextTertiary, fontSize = 11.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CrystalButton(
                            text = "Pick Photo",
                            icon = Icons.Default.Image,
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            isPrimary = false,
                            modifier = Modifier.weight(1f),
                            testTag = "pick_photo_button"
                        )

                        CrystalButton(
                            text = if (formState.isAnalyzing) "Analyzing..." else "Scan with AI",
                            icon = Icons.Default.AutoAwesome,
                            onClick = onAnalyze,
                            enabled = formState.imageBitmap != null && !formState.isAnalyzing,
                            isPrimary = true,
                            modifier = Modifier.weight(1f),
                            testTag = "analyze_ai_button"
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Preset Demo Quick Loaders
                    Text("Instant Test Samples (Emulator Ready):", color = TextTertiary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        DemoSampleChip("🥗 Food Snap", modifier = Modifier.weight(1f)) {
                            onImageSelected(generateDemoBitmap("Salmon Rice Bowl\n42g Protein", Color(0xFF0F766E)))
                        }
                        DemoSampleChip("🏃 Watch Run", modifier = Modifier.weight(1f)) {
                            onImageSelected(generateDemoBitmap("Huawei Watch Run\n12.4km | 480m D+", Color(0xFF1E3A8A)))
                        }
                        DemoSampleChip("⚖️ Visbody 3D", modifier = Modifier.weight(1f)) {
                            onImageSelected(generateDemoBitmap("Visbody 3D Scan\nAPT: 11.7cm", Color(0xFF581C87)))
                        }
                    }
                }
            }
        }

        // Analysis status alert
        if (formState.isAnalyzing) {
            item {
                CrystalCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = CrystalIce
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = CrystalIce,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.5.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = formState.statusMessage ?: "Gemini 2.5 Flash analyzing image...",
                            color = CrystalIce,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        } else if (formState.statusMessage != null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(EmeraldMist.copy(alpha = 0.12f))
                        .border(1.dp, EmeraldMist.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = formState.statusMessage,
                        color = EmeraldMist,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Category Selector Tabs
        item {
            Text(
                text = "LOG CATEGORY (AUTO-DETECTED BY AI)",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CategoryType.values().forEach { cat ->
                    val isSelected = formState.category == cat
                    val bg = if (isSelected) CrystalIce.copy(alpha = 0.25f) else SmokedCrystal
                    val border = if (isSelected) CrystalIce else SpecularBorder
                    val textColor = if (isSelected) CrystalIce else TextSecondary

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(bg)
                            .border(1.dp, border, RoundedCornerShape(12.dp))
                            .clickable { onCategoryChanged(cat) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(cat.icon, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = cat.name,
                                color = textColor,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // Editable Verification Form based on Category
        item {
            CrystalCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "VERIFY & EDIT BEFORE SAVING",
                        color = CrystalIce,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = formState.title,
                        onValueChange = { onFieldChanged(it, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null) },
                        label = { Text("Log Title / Name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = crystalTextFieldColors()
                    )

                    when (formState.category) {
                        CategoryType.NUTRITION -> {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = formState.calories,
                                    onValueChange = { onFieldChanged(null, it, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null) },
                                    label = { Text("Calories (kcal)") },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = crystalTextFieldColors()
                                )
                                OutlinedTextField(
                                    value = formState.protein,
                                    onValueChange = { onFieldChanged(null, null, it, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null) },
                                    label = { Text("Protein (g)") },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = crystalTextFieldColors()
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = formState.carbs,
                                    onValueChange = { onFieldChanged(null, null, null, it, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null) },
                                    label = { Text("Carbs (g)") },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = crystalTextFieldColors()
                                )
                                OutlinedTextField(
                                    value = formState.fat,
                                    onValueChange = { onFieldChanged(null, null, null, null, it, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null) },
                                    label = { Text("Fat (g)") },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = crystalTextFieldColors()
                                )
                            }
                            OutlinedTextField(
                                value = formState.sodium,
                                onValueChange = { onFieldChanged(null, null, null, null, null, it, null, null, null, null, null, null, null, null, null, null, null, null, null, null) },
                                label = { Text("Sodium (mg) [Limit: 2300mg]") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = crystalTextFieldColors()
                            )
                        }

                        CategoryType.WORKOUT -> {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = formState.workoutType,
                                    onValueChange = { onFieldChanged(null, null, null, null, null, null, it, null, null, null, null, null, null, null, null, null, null, null, null, null) },
                                    label = { Text("Workout Type") },
                                    modifier = Modifier.weight(1f),
                                    colors = crystalTextFieldColors()
                                )
                                OutlinedTextField(
                                    value = formState.activeBurnKcal,
                                    onValueChange = { onFieldChanged(null, null, null, null, null, null, null, null, null, it, null, null, null, null, null, null, null, null, null, null) },
                                    label = { Text("Active kcal") },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = crystalTextFieldColors()
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = formState.distanceKm,
                                    onValueChange = { onFieldChanged(null, null, null, null, null, null, null, it, null, null, null, null, null, null, null, null, null, null, null, null) },
                                    label = { Text("Distance (km)") },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = crystalTextFieldColors()
                                )
                                OutlinedTextField(
                                    value = formState.durationMin,
                                    onValueChange = { onFieldChanged(null, null, null, null, null, null, null, null, it, null, null, null, null, null, null, null, null, null, null, null) },
                                    label = { Text("Duration (min)") },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = crystalTextFieldColors()
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = formState.avgHr,
                                    onValueChange = { onFieldChanged(null, null, null, null, null, null, null, null, null, null, it, null, null, null, null, null, null, null, null, null) },
                                    label = { Text("Avg HR (bpm)") },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = crystalTextFieldColors()
                                )
                                OutlinedTextField(
                                    value = formState.elevationDPlus,
                                    onValueChange = { onFieldChanged(null, null, null, null, null, null, null, null, null, null, null, it, null, null, null, null, null, null, null, null) },
                                    label = { Text("Elevation D+ (m)") },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = crystalTextFieldColors()
                                )
                            }
                        }

                        CategoryType.BODYCOMP -> {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = formState.sourceDevice,
                                    onValueChange = { onFieldChanged(null, null, null, null, null, null, null, null, null, null, null, null, it, null, null, null, null, null, null, null) },
                                    label = { Text("Device Source") },
                                    modifier = Modifier.weight(1f),
                                    colors = crystalTextFieldColors()
                                )
                                OutlinedTextField(
                                    value = formState.weightKg,
                                    onValueChange = { onFieldChanged(null, null, null, null, null, null, null, null, null, null, null, null, null, it, null, null, null, null, null, null) },
                                    label = { Text("Weight (kg)") },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = crystalTextFieldColors()
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = formState.bodyFatPct,
                                    onValueChange = { onFieldChanged(null, null, null, null, null, null, null, null, null, null, null, null, null, null, it, null, null, null, null, null) },
                                    label = { Text("Body Fat %") },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = crystalTextFieldColors()
                                )
                                OutlinedTextField(
                                    value = formState.smmKg,
                                    onValueChange = { onFieldChanged(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, it, null, null, null, null) },
                                    label = { Text("SMM Muscle (kg)") },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = crystalTextFieldColors()
                                )
                            }
                            OutlinedTextField(
                                value = formState.aptCm,
                                onValueChange = { onFieldChanged(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, it, null, null, null) },
                                label = { Text("Visbody APT Anterior Pelvic Tilt (cm)") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = crystalTextFieldColors()
                            )
                        }

                        CategoryType.SLEEP -> {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = formState.sleepDurationHours,
                                    onValueChange = { onFieldChanged(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, it, null, null) },
                                    label = { Text("Duration (hours)") },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = crystalTextFieldColors()
                                )
                                OutlinedTextField(
                                    value = formState.sleepScore,
                                    onValueChange = { onFieldChanged(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, it, null) },
                                    label = { Text("Sleep Score (0-100)") },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = crystalTextFieldColors()
                                )
                            }
                            OutlinedTextField(
                                value = formState.sleepHrvMs,
                                onValueChange = { onFieldChanged(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, it) },
                                label = { Text("Avg Sleep HRV (ms)") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = crystalTextFieldColors()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    CrystalButton(
                        text = "💾 Save to Room & Sync 2026_HealthHub",
                        onClick = onSaveAndSync,
                        isPrimary = true,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "save_and_sync_button"
                    )
                }
            }
            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}

@Composable
private fun DemoSampleChip(label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SmokedCrystalElevated)
            .border(1.dp, SpecularBorder, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = CrystalIce, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

private fun generateDemoBitmap(text: String, bgColor: Color): Bitmap {
    val bitmap = Bitmap.createBitmap(400, 240, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paintBg = Paint().apply { color = bgColor.value.toInt() }
    canvas.drawRect(0f, 0f, 400f, 240f, paintBg)

    val paintText = Paint().apply {
        color = android.graphics.Color.WHITE
        textSize = 28f
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
    }

    val lines = text.split("\n")
    var y = 100f
    for (line in lines) {
        canvas.drawText(line, 200f, y, paintText)
        y += 40f
    }
    return bitmap
}

@Composable
private fun crystalTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = CrystalIce,
    unfocusedBorderColor = SpecularBorder,
    focusedLabelColor = CrystalIce,
    unfocusedLabelColor = TextSecondary,
    cursorColor = CrystalIce,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary
)

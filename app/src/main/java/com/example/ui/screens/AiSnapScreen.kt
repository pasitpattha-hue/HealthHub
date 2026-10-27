package com.example.ui.screens

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.MealEntity
import com.example.data.model.CategoryType
import com.example.data.model.DailySummary
import com.example.ui.components.CrystalButton
import com.example.ui.components.CrystalCard
import com.example.ui.components.CrystalPill
import com.example.ui.components.WeeklyMacronutrientChart
import com.example.ui.theme.ChillaxRoundedDisplayFamily
import com.example.ui.theme.ClayHighlightLight
import com.example.ui.theme.ClayShadowDark
import com.example.ui.theme.ClaySurfaceBottom
import com.example.ui.theme.ClaySurfaceTop
import com.example.ui.theme.CrystalIce
import com.example.ui.theme.EmeraldMist
import com.example.ui.theme.SoftCoralRose
import com.example.ui.theme.SpecularBorder
import com.example.ui.theme.SpecularHighlight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmChampagne
import com.example.ui.viewmodel.AiSnapFormState

@Composable
fun AiSnapScreen(
    formState: AiSnapFormState,
    dailySummaries: List<DailySummary> = emptyList(),
    allMeals: List<MealEntity> = emptyList(),
    selectedDate: String? = null,
    onSelectDate: (String) -> Unit = {},
    onImageSelected: (Bitmap) -> Unit,
    onClearImage: () -> Unit = {},
    onLoadPreset: (CategoryType, Bitmap) -> Unit = { _, _ -> },
    onAnalyze: () -> Unit,
    onRecalculateMacros: () -> Unit = {},
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
    onSaveAndSync: () -> Unit
) {
    val context = LocalContext.current
    var permissionDeniedNotice by remember { mutableStateOf<String?>(null) }

    // Direct camera capture launcher
    val cameraPreviewLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bmp ->
        if (bmp != null) {
            onImageSelected(bmp)
        }
    }

    // Camera runtime permission launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            permissionDeniedNotice = null
            cameraPreviewLauncher.launch(null)
        } else {
            permissionDeniedNotice = "Camera permission is required to capture food or workout telemetry."
        }
    }

    // Photo picker launcher (zero-permission)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val bmp = BitmapFactory.decodeStream(stream)
                    if (bmp != null) {
                        onImageSelected(bmp)
                    }
                }
            } catch (e: Exception) {
                // If decoding fails, fall back to sample
                onImageSelected(SampleTelemetryGenerators.generateSample(formState.category))
            }
        }
    }

    // Scanline animation when analyzing
    val infiniteTransition = rememberInfiniteTransition(label = "scan_laser")
    val laserOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_y"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("ai_logger_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Column(modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📸 AI Camera Meal & Telemetry",
                        color = TextPrimary,
                        fontFamily = ChillaxRoundedDisplayFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 21.sp
                    )
                    CrystalPill(
                        text = "Gemini 3.5 Flash",
                        accentColor = CrystalIce
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "ถ่ายรูปจานอาหารด้วยกล้องเพื่อส่งให้ Gemini 3.5 Flash ประเมินแคลอรี่ โปรตีน คาร์บ ไขมัน โซเดียม และวัตถุดิบอัตโนมัติ (รองรับสมาร์ทวอทช์ & Visbody 3D)",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
        }

        // Camera & Photo Controls Card
        item {
            CrystalCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CrystalButton(
                            text = "Camera Snap",
                            icon = Icons.Default.CameraAlt,
                            onClick = {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            },
                            modifier = Modifier.weight(1f),
                            testTag = "camera_snap_button"
                        )
                        CrystalButton(
                            text = "Photo Gallery",
                            icon = Icons.Default.PhotoLibrary,
                            isPrimary = false,
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier.weight(1f),
                            testTag = "photo_picker_button"
                        )
                    }

                    if (permissionDeniedNotice != null) {
                        Text(
                            text = "⚠️ $permissionDeniedNotice",
                            color = SoftCoralRose,
                            fontSize = 11.sp
                        )
                    }

                    // Quick Telemetry Presets (Crucial for test emulator without physical camera)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0x200284C7))
                            .border(1.dp, Color(0x350284C7), RoundedCornerShape(16.dp))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "🍽️ ตัวอย่างภาพถ่ายจานอาหาร & เทเลเมทรี (แตะเพื่อส่งให้ Gemini ประเมินทันที):",
                            color = CrystalIce,
                            fontFamily = ChillaxRoundedDisplayFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            PresetPill(
                                title = "🥗 Salmon Bowl",
                                onClick = {
                                    val bmp = SampleTelemetryGenerators.generateNutritionSample()
                                    onLoadPreset(CategoryType.NUTRITION, bmp)
                                },
                                modifier = Modifier.weight(1f),
                                testTag = "preset_food_btn"
                            )
                            PresetPill(
                                title = "🍗 ข้าวมันไก่ต้มลีน",
                                onClick = {
                                    val bmp = SampleTelemetryGenerators.generateThaiChickenRiceSample()
                                    onLoadPreset(CategoryType.NUTRITION, bmp)
                                },
                                modifier = Modifier.weight(1f),
                                testTag = "preset_chicken_rice_btn"
                            )
                            PresetPill(
                                title = "🍳 กะเพราอกไก่",
                                onClick = {
                                    val bmp = SampleTelemetryGenerators.generateThaiKapraoChickenSample()
                                    onLoadPreset(CategoryType.NUTRITION, bmp)
                                },
                                modifier = Modifier.weight(1f),
                                testTag = "preset_kaprao_btn"
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            PresetPill(
                                title = "🏃 Huawei Run",
                                onClick = {
                                    val bmp = SampleTelemetryGenerators.generateWorkoutSample()
                                    onLoadPreset(CategoryType.WORKOUT, bmp)
                                },
                                modifier = Modifier.weight(1f),
                                testTag = "preset_run_btn"
                            )
                            PresetPill(
                                title = "⚖️ Visbody 3D",
                                onClick = {
                                    val bmp = SampleTelemetryGenerators.generateBodyCompSample()
                                    onLoadPreset(CategoryType.BODYCOMP, bmp)
                                },
                                modifier = Modifier.weight(1f),
                                testTag = "preset_visbody_btn"
                            )
                            PresetPill(
                                title = "😴 Sleep TruSleep",
                                onClick = {
                                    val bmp = SampleTelemetryGenerators.generateSleepSample()
                                    onLoadPreset(CategoryType.SLEEP, bmp)
                                },
                                modifier = Modifier.weight(1f),
                                testTag = "preset_sleep_btn"
                            )
                        }
                    }
                }
            }
        }

        // Live Telemetry Viewfinder / Preview Card
        item {
            val bmp = formState.imageBitmap
            CrystalCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (bmp != null) EmeraldMist else WarmChampagne)
                            )
                            Text(
                                text = if (bmp != null) "LIVE TELEMETRY TARGET LOADED" else "TARGET VIEWFINDER (STANDBY)",
                                color = if (bmp != null) EmeraldMist else TextSecondary,
                                fontFamily = ChillaxRoundedDisplayFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }

                        if (bmp != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "${bmp.width}×${bmp.height}",
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                                IconButton(
                                    onClick = onClearImage,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .testTag("clear_image_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear Target Image",
                                        tint = TextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Viewfinder Screen Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9.5f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF070B14))
                            .border(1.5.dp, if (bmp != null) SpecularBorder else Color(0x3038BDF8), RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (bmp != null) {
                            Image(
                                bitmap = bmp.asImageBitmap(),
                                contentDescription = "Captured Telemetry Visual",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            // Animated Laser Scanner Line when analyzing
                            if (formState.isAnalyzing) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color(0x400284C7))
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(3.dp)
                                        .align(Alignment.TopCenter)
                                        .padding(top = (laserOffset * 180).dp)
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(
                                                    Color.Transparent,
                                                    Color(0xFF38BDF8),
                                                    Color.White,
                                                    Color(0xFF38BDF8),
                                                    Color.Transparent
                                                )
                                            )
                                        )
                                )
                            }
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = CrystalIce.copy(alpha = 0.6f),
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Ready to Capture or Load Telemetry",
                                    color = TextPrimary,
                                    fontFamily = ChillaxRoundedDisplayFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "Use Camera Snap or tap any preset above",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // Analyze Action CTA
                    CrystalButton(
                        text = if (formState.isAnalyzing) "✨ Gemini 3.5 Flash กำลังประเมินแคลอรี่ & แมโคร..." else "✨ ประเมินแคลอรี่ & แมโครด้วย Gemini 3.5 Flash",
                        icon = if (formState.isAnalyzing) null else Icons.Default.AutoAwesome,
                        enabled = !formState.isAnalyzing,
                        onClick = {
                            if (formState.imageBitmap == null) {
                                onLoadPreset(formState.category, SampleTelemetryGenerators.generateSample(formState.category))
                            } else {
                                onAnalyze()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "analyze_gemini_button"
                    )

                    // Status Message Pill
                    AnimatedVisibility(
                        visible = formState.statusMessage != null,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        formState.statusMessage?.let { msg ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (msg.contains("error", ignoreCase = true) || msg.contains("⚠️"))
                                            Color(0x25F43F5E)
                                        else Color(0x2510B981)
                                    )
                                    .border(
                                        1.dp,
                                        if (msg.contains("error", ignoreCase = true) || msg.contains("⚠️"))
                                            SoftCoralRose.copy(alpha = 0.4f)
                                        else EmeraldMist.copy(alpha = 0.4f),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (msg.contains("error", ignoreCase = true)) Icons.Default.Close else Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = if (msg.contains("error", ignoreCase = true)) SoftCoralRose else EmeraldMist,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = msg,
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Category Classification Selector
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "CLASSIFICATION DOMAIN",
                    color = TextSecondary,
                    fontFamily = ChillaxRoundedDisplayFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CategoryType.entries.forEach { cat ->
                        val isSelected = formState.category == cat
                        CategoryChip(
                            category = cat,
                            isSelected = isSelected,
                            onClick = { onCategoryChanged(cat) },
                            modifier = Modifier.weight(1f),
                            testTag = "cat_btn_${cat.name.lowercase()}"
                        )
                    }
                }
            }
        }

        // Editable Telemetry Details & Instant Calculations
        item {
            CrystalCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PARSED TELEMETRY ATTRIBUTES",
                            color = CrystalIce,
                            fontFamily = ChillaxRoundedDisplayFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Edit values prior to saving",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }

                    TelemetryField(
                        label = "Entry Title / Meal / Workout Name",
                        value = formState.title,
                        onValueChange = {
                            onFieldChanged(it, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null)
                        },
                        testTag = "ai_title_input"
                    )

                    when (formState.category) {
                        CategoryType.NUTRITION -> {
                            NutritionFormSection(
                                formState = formState,
                                onRecalculateMacros = onRecalculateMacros,
                                onFieldChanged = onFieldChanged
                            )
                        }
                        CategoryType.WORKOUT -> {
                            WorkoutFormSection(formState, onFieldChanged)
                        }
                        CategoryType.BODYCOMP -> {
                            BodyCompFormSection(formState, onFieldChanged)
                        }
                        CategoryType.SLEEP -> {
                            SleepFormSection(formState, onFieldChanged)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Final Save Button
                    CrystalButton(
                        text = "💾 Save to Room DB & Sync GAS v4.0",
                        icon = Icons.Default.CloudUpload,
                        onClick = onSaveAndSync,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "save_and_sync_button"
                    )
                }
            }
        }

        // Weekly Macronutrient Breakdown Chart for Camera Meal Tracker
        if (formState.category == CategoryType.NUTRITION && dailySummaries.isNotEmpty()) {
            item {
                WeeklyMacronutrientChart(
                    summaries = dailySummaries,
                    allMeals = allMeals,
                    selectedDate = selectedDate,
                    onSelectDate = onSelectDate
                )
            }
        }
    }
}

@Composable
private fun CategoryChip(
    category: CategoryType,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    val bgBrush = if (isSelected) {
        Brush.verticalGradient(
            listOf(
                Color(0xFF38BDF8).copy(alpha = 0.35f),
                Color(0xFF0284C7).copy(alpha = 0.18f)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                ClaySurfaceTop.copy(alpha = 0.6f),
                ClaySurfaceBottom.copy(alpha = 0.5f)
            )
        )
    }

    val borderColor = if (isSelected) CrystalIce else SpecularBorder

    Box(
        modifier = modifier
            .testTag(testTag)
            .height(44.dp)
            .shadow(if (isSelected) 8.dp else 2.dp, RoundedCornerShape(18.dp), spotColor = CrystalIce.copy(alpha = 0.3f))
            .clip(RoundedCornerShape(18.dp))
            .background(bgBrush)
            .border(1.5.dp, borderColor, RoundedCornerShape(18.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = CrystalIce),
                onClick = onClick
            )
            .padding(horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${category.iconEmoji} ${category.displayName}",
                color = if (isSelected) CrystalIce else TextSecondary,
                fontFamily = ChillaxRoundedDisplayFamily,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 11.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun PresetPill(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Box(
        modifier = modifier
            .testTag(testTag)
            .height(34.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF16233B))
            .border(1.dp, Color(0xFF283B60), RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = CrystalIce),
                onClick = onClick
            )
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = Color(0xFFBAE6FD),
            fontFamily = ChillaxRoundedDisplayFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            maxLines = 1
        )
    }
}

@Composable
private fun NutritionFormSection(
    formState: AiSnapFormState,
    onRecalculateMacros: () -> Unit = {},
    onFieldChanged: (
        title: String?, calories: String?, protein: String?, carbs: String?, fat: String?, sodium: String?,
        workoutType: String?, distanceKm: String?, durationMin: String?, activeBurnKcal: String?,
        avgHr: String?, elevationDPlus: String?, sourceDevice: String?, weightKg: String?,
        bodyFatPct: String?, smmKg: String?, aptCm: String?, sleepDurationHours: String?,
        sleepScore: String?, sleepHrvMs: String?
    ) -> Unit
) {
    val cal = formState.calories.toIntOrNull() ?: 0
    val pGrams = formState.protein.toDoubleOrNull() ?: 0.0
    val cGrams = formState.carbs.toDoubleOrNull() ?: 0.0
    val fGrams = formState.fat.toDoubleOrNull() ?: 0.0
    val naMg = formState.sodium.toIntOrNull() ?: 0
    val sugarGrams = formState.sugar.toDoubleOrNull() ?: 0.0
    val fiberGrams = formState.fiber.toDoubleOrNull() ?: 0.0

    val calcSummary = com.example.data.remote.GeminiService.calculateMacronutrientMetrics(
        proteinG = pGrams,
        carbsG = cGrams,
        fatG = fGrams,
        fiberG = fiberGrams,
        sodiumMg = naMg,
        declaredCalories = cal
    )
    val proteinPct = calcSummary.proteinCaloriePct
    val carbsPct = calcSummary.carbsCaloriePct
    val fatPct = calcSummary.fatCaloriePct
    val tef = calcSummary.tefKcal

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Visual AI Macronutrient & Calorie Estimation Summary Card
        if (cal > 0) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_macro_estimation_card")
                    .clip(RoundedCornerShape(16.dp))
                    .background(ClaySurfaceBottom.copy(alpha = 0.85f))
                    .border(1.dp, CrystalIce.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "🤖 GEMINI 3.8 FLASH AUTO-MACRO ENGINE",
                            color = CrystalIce,
                            fontFamily = ChillaxRoundedDisplayFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = "$cal kcal",
                            color = TextPrimary,
                            fontFamily = ChillaxRoundedDisplayFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 26.sp,
                            modifier = Modifier.testTag("ai_estimated_calories_readout")
                        )
                        Text(
                            text = "4P (${calcSummary.proteinKcal}) + 4C (${calcSummary.carbsKcal}) + 9F (${calcSummary.fatKcal}) = ${calcSummary.totalKcalFromMacros} kcal",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        CrystalPill(
                            text = formState.macroQualityGrade.ifBlank { calcSummary.macroQualityGrade },
                            accentColor = EmeraldMist
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "AI ${formState.confidencePct}% • ~${formState.portionGrams.ifBlank { "350" }}g • ${formState.mealType}",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                // Macronutrient Share Bar (Protein / Carbs / Fat)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = 0.08f))
                ) {
                    if (proteinPct > 0) {
                        Box(
                            modifier = Modifier
                                .weight(proteinPct.toFloat().coerceAtLeast(1f))
                                .fillMaxSize()
                                .background(EmeraldMist)
                        )
                    }
                    if (carbsPct > 0) {
                        Box(
                            modifier = Modifier
                                .weight(carbsPct.toFloat().coerceAtLeast(1f))
                                .fillMaxSize()
                                .background(CrystalIce)
                        )
                    }
                    if (fatPct > 0) {
                        Box(
                            modifier = Modifier
                                .weight(fatPct.toFloat().coerceAtLeast(1f))
                                .fillMaxSize()
                                .background(WarmChampagne)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "🥩 โปรตีน ${pGrams.toInt()}g ($proteinPct%)",
                        color = EmeraldMist,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "🍚 คาร์บ ${cGrams.toInt()}g ($carbsPct%)",
                        color = CrystalIce,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "🥑 ไขมัน ${fGrams.toInt()}g ($fatPct%)",
                        color = WarmChampagne,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "🧂 โซเดียม: $naMg mg • 🌾 ไฟเบอร์: ${fiberGrams.toInt()}g (Net C: ${calcSummary.netCarbsG.toInt()}g)",
                        color = if (naMg > 800) SoftCoralRose else TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "🍬 น้ำตาล: ${sugarGrams.toInt()}g • 🔥 TEF +$tef",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }

                // Itemized Food Component Macros Breakdown from Camera Photo
                if (formState.itemizedMacros.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ai_itemized_macros_list")
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0A1220))
                            .border(1.dp, SpecularBorder.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "🍽️ สัดส่วนสารอาหารรายองค์ประกอบในจาน (Itemized Macro Calculation):",
                            color = CrystalIce,
                            fontFamily = ChillaxRoundedDisplayFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        formState.itemizedMacros.forEach { comp ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "• ${comp.name} (${comp.portionGrams}g)",
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "${comp.calories} kcal | P:${comp.proteinG.toInt()}g C:${comp.carbsG.toInt()}g F:${comp.fatG.toInt()}g",
                                    color = EmeraldMist,
                                    fontFamily = ChillaxRoundedDisplayFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                } else if (formState.detectedIngredients.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "🔍 วัตถุดิบที่ Gemini ตรวจพบในจาน:",
                            color = CrystalIce,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                        formState.detectedIngredients.forEach { ingredient ->
                            Text(
                                text = "• $ingredient",
                                color = TextPrimary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                if (formState.coachRecommendation.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(EmeraldMist.copy(alpha = 0.14f))
                            .border(1.dp, EmeraldMist.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "💡 คำแนะนำโค้ช: ${formState.coachRecommendation}",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TelemetryField(
                label = "Calories (kcal)",
                value = formState.calories,
                onValueChange = { onFieldChanged(null, it, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null) },
                modifier = Modifier.weight(1f),
                testTag = "ai_calories_input"
            )
            TelemetryField(
                label = "Protein (g)",
                value = formState.protein,
                onValueChange = { onFieldChanged(null, null, it, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null) },
                modifier = Modifier.weight(1f),
                testTag = "ai_protein_input"
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TelemetryField(
                label = "Carbs (g)",
                value = formState.carbs,
                onValueChange = { onFieldChanged(null, null, null, it, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null) },
                modifier = Modifier.weight(1f),
                testTag = "ai_carbs_input"
            )
            TelemetryField(
                label = "Fat (g)",
                value = formState.fat,
                onValueChange = { onFieldChanged(null, null, null, null, it, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null) },
                modifier = Modifier.weight(1f),
                testTag = "ai_fat_input"
            )
            TelemetryField(
                label = "Sodium (mg)",
                value = formState.sodium,
                onValueChange = { onFieldChanged(null, null, null, null, null, it, null, null, null, null, null, null, null, null, null, null, null, null, null, null) },
                modifier = Modifier.weight(1f),
                testTag = "ai_sodium_input"
            )
        }

        // Auto-Recalculate Calories from Macros (4P + 4C + 9F) Button
        CrystalButton(
            text = "🔄 คำนวณแคลอรี่อัตโนมัติจากแมโคร (4P + 4C + 9F = ${calcSummary.totalKcalFromMacros} kcal)",
            icon = Icons.Default.AutoAwesome,
            isPrimary = false,
            onClick = onRecalculateMacros,
            modifier = Modifier.fillMaxWidth(),
            testTag = "recalculate_macro_kcal_button"
        )

        // Live GAS v4.0 impact snippet
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x1810B981))
                .border(1.dp, Color(0x3510B981), RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⚡ GAS v4.0 TEF Engine Impact:",
                    color = EmeraldMist,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                )
                Text(
                    text = "+$tef kcal TEF (10% thermic burn)",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun WorkoutFormSection(
    formState: AiSnapFormState,
    onFieldChanged: (
        title: String?, calories: String?, protein: String?, carbs: String?, fat: String?, sodium: String?,
        workoutType: String?, distanceKm: String?, durationMin: String?, activeBurnKcal: String?,
        avgHr: String?, elevationDPlus: String?, sourceDevice: String?, weightKg: String?,
        bodyFatPct: String?, smmKg: String?, aptCm: String?, sleepDurationHours: String?,
        sleepScore: String?, sleepHrvMs: String?
    ) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TelemetryField(
                label = "Workout Type",
                value = formState.workoutType,
                onValueChange = { onFieldChanged(null, null, null, null, null, null, it, null, null, null, null, null, null, null, null, null, null, null, null, null) },
                modifier = Modifier.weight(1.2f),
                testTag = "ai_workout_type_input"
            )
            TelemetryField(
                label = "Distance (km)",
                value = formState.distanceKm,
                onValueChange = { onFieldChanged(null, null, null, null, null, null, null, it, null, null, null, null, null, null, null, null, null, null, null, null) },
                modifier = Modifier.weight(1f),
                testTag = "ai_distance_input"
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TelemetryField(
                label = "Duration (min)",
                value = formState.durationMin,
                onValueChange = { onFieldChanged(null, null, null, null, null, null, null, null, it, null, null, null, null, null, null, null, null, null, null, null) },
                modifier = Modifier.weight(1f),
                testTag = "ai_duration_input"
            )
            TelemetryField(
                label = "Active Burn (kcal)",
                value = formState.activeBurnKcal,
                onValueChange = { onFieldChanged(null, null, null, null, null, null, null, null, null, it, null, null, null, null, null, null, null, null, null, null) },
                modifier = Modifier.weight(1f),
                testTag = "ai_burn_input"
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TelemetryField(
                label = "Avg HR (bpm)",
                value = formState.avgHr,
                onValueChange = { onFieldChanged(null, null, null, null, null, null, null, null, null, null, it, null, null, null, null, null, null, null, null, null) },
                modifier = Modifier.weight(1f),
                testTag = "ai_avg_hr_input"
            )
            TelemetryField(
                label = "Elevation D+ (m)",
                value = formState.elevationDPlus,
                onValueChange = { onFieldChanged(null, null, null, null, null, null, null, null, null, null, null, it, null, null, null, null, null, null, null, null) },
                modifier = Modifier.weight(1f),
                testTag = "ai_elevation_input"
            )
        }

        val dist = formState.distanceKm.toDoubleOrNull() ?: 0.0
        val dur = formState.durationMin.toIntOrNull() ?: 0
        if (dist > 0.0 && dur > 0) {
            val paceMinPerKm = dur / dist
            val paceM = paceMinPerKm.toInt()
            val paceS = ((paceMinPerKm - paceM) * 60).toInt()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x180284C7))
                    .border(1.dp, Color(0x350284C7), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🏃 Running Cadence / Pace:",
                        color = CrystalIce,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "Est. Pace: $paceM'${paceS.toString().padStart(2, '0')}\" /km",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun BodyCompFormSection(
    formState: AiSnapFormState,
    onFieldChanged: (
        title: String?, calories: String?, protein: String?, carbs: String?, fat: String?, sodium: String?,
        workoutType: String?, distanceKm: String?, durationMin: String?, activeBurnKcal: String?,
        avgHr: String?, elevationDPlus: String?, sourceDevice: String?, weightKg: String?,
        bodyFatPct: String?, smmKg: String?, aptCm: String?, sleepDurationHours: String?,
        sleepScore: String?, sleepHrvMs: String?
    ) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TelemetryField(
                label = "Source Device (Huawei / Visbody)",
                value = formState.sourceDevice,
                onValueChange = { onFieldChanged(null, null, null, null, null, null, null, null, null, null, null, null, it, null, null, null, null, null, null, null) },
                modifier = Modifier.weight(1.2f),
                testTag = "ai_source_device_input"
            )
            TelemetryField(
                label = "Weight (kg)",
                value = formState.weightKg,
                onValueChange = { onFieldChanged(null, null, null, null, null, null, null, null, null, null, null, null, null, it, null, null, null, null, null, null) },
                modifier = Modifier.weight(1f),
                testTag = "ai_weight_input"
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TelemetryField(
                label = "Body Fat (%)",
                value = formState.bodyFatPct,
                onValueChange = { onFieldChanged(null, null, null, null, null, null, null, null, null, null, null, null, null, null, it, null, null, null, null, null) },
                modifier = Modifier.weight(1f),
                testTag = "ai_bf_input"
            )
            TelemetryField(
                label = "SMM (kg)",
                value = formState.smmKg,
                onValueChange = { onFieldChanged(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, it, null, null, null, null) },
                modifier = Modifier.weight(1f),
                testTag = "ai_smm_input"
            )
            TelemetryField(
                label = "APT Pelvis (cm)",
                value = formState.aptCm,
                onValueChange = { onFieldChanged(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, it, null, null, null) },
                modifier = Modifier.weight(1f),
                testTag = "ai_apt_input"
            )
        }

        val apt = formState.aptCm.toDoubleOrNull() ?: 0.0
        if (apt > 10.0) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x25F43F5E))
                    .border(1.dp, Color(0x45F43F5E), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "⚠️ Anterior Pelvic Tilt ($apt cm) is above 10.0cm threshold. Core/glute stabilization recommended in Microcycle gym plan.",
                    color = SoftCoralRose,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
private fun SleepFormSection(
    formState: AiSnapFormState,
    onFieldChanged: (
        title: String?, calories: String?, protein: String?, carbs: String?, fat: String?, sodium: String?,
        workoutType: String?, distanceKm: String?, durationMin: String?, activeBurnKcal: String?,
        avgHr: String?, elevationDPlus: String?, sourceDevice: String?, weightKg: String?,
        bodyFatPct: String?, smmKg: String?, aptCm: String?, sleepDurationHours: String?,
        sleepScore: String?, sleepHrvMs: String?
    ) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TelemetryField(
                label = "Sleep Duration (hrs)",
                value = formState.sleepDurationHours,
                onValueChange = { onFieldChanged(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, it, null, null) },
                modifier = Modifier.weight(1f),
                testTag = "ai_sleep_dur_input"
            )
            TelemetryField(
                label = "Sleep Score (1-100)",
                value = formState.sleepScore,
                onValueChange = { onFieldChanged(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, it, null) },
                modifier = Modifier.weight(1f),
                testTag = "ai_sleep_score_input"
            )
            TelemetryField(
                label = "HRV (ms)",
                value = formState.sleepHrvMs,
                onValueChange = { onFieldChanged(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, it) },
                modifier = Modifier.weight(1f),
                testTag = "ai_hrv_input"
            )
        }

        val hrv = formState.sleepHrvMs.toIntOrNull() ?: 0
        if (hrv > 0) {
            val isHighRecovery = hrv >= 75
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isHighRecovery) Color(0x1810B981) else Color(0x18F59E0B))
                    .border(1.dp, if (isHighRecovery) Color(0x3510B981) else Color(0x35F59E0B), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = if (isHighRecovery)
                        "⚡ Parasympathetic Recovery: Optimal ($hrv ms HRV). Body is ready for high intensity training."
                    else "💤 Moderate Recovery ($hrv ms HRV). Prioritize Zone 2 endurance or mobility work today.",
                    color = if (isHighRecovery) EmeraldMist else WarmChampagne,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
private fun TelemetryField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = TextSecondary, fontSize = 11.sp) },
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedBorderColor = CrystalIce,
            unfocusedBorderColor = TextSecondary.copy(alpha = 0.5f),
            focusedContainerColor = Color(0x200B0F17),
            unfocusedContainerColor = Color(0x150B0F17)
        )
    )
}

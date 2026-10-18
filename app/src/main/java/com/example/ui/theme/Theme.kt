package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CrystalDarkColorScheme = darkColorScheme(
    primary = CrystalIce,
    onPrimary = Color(0xFF032840),
    primaryContainer = Color(0xFF0C4A6E),
    onPrimaryContainer = Color(0xFFBAE6FD),
    secondary = EmeraldMist,
    onSecondary = Color(0xFF022C1D),
    secondaryContainer = Color(0xFF065F46),
    onSecondaryContainer = Color(0xFFA7F3D0),
    tertiary = WarmChampagne,
    onTertiary = Color(0xFF332300),
    background = ObsidianDeep,
    onBackground = TextPrimary,
    surface = ObsidianCard,
    onSurface = TextPrimary,
    surfaceVariant = SmokedCrystal,
    onSurfaceVariant = TextSecondary,
    outline = SpecularBorder,
    error = SoftCoralRose,
    onError = Color(0xFF4C0519)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false, // Enforce our custom Crystal Dark aesthetic
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CrystalDarkColorScheme,
        typography = Typography,
        content = content
    )
}

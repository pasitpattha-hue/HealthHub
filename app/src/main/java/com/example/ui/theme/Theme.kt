package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkClayColorScheme = darkColorScheme(
    primary = CrystalIce,
    onPrimary = ObsidianDeep,
    secondary = EmeraldMist,
    onSecondary = ObsidianDeep,
    tertiary = WarmChampagne,
    background = ObsidianDeep,
    onBackground = TextPrimary,
    surface = ObsidianCard,
    onSurface = TextPrimary,
    surfaceVariant = ClaySurfaceTop,
    onSurfaceVariant = TextSecondary,
    error = SoftCoralRose
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkClayColorScheme,
        typography = Typography,
        content = content
    )
}

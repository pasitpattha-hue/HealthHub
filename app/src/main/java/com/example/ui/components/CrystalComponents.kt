package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EnergyStatus
import com.example.ui.theme.ChillaxRoundedDisplayFamily
import com.example.ui.theme.ClayHighlightIce
import com.example.ui.theme.ClayHighlightLight
import com.example.ui.theme.ClayShadowDark
import com.example.ui.theme.ClaySurfaceBottom
import com.example.ui.theme.ClaySurfaceTop
import com.example.ui.theme.CrystalIce
import com.example.ui.theme.CrystalIceDark
import com.example.ui.theme.EmeraldMist
import com.example.ui.theme.SmokedCrystal
import com.example.ui.theme.SmokedCrystalElevated
import com.example.ui.theme.SoftCoralRose
import com.example.ui.theme.SpecularBorder
import com.example.ui.theme.SpecularHighlight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmChampagne

@Composable
fun CrystalCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 28.dp,
    backgroundColor: Color = SmokedCrystal,
    borderColor: Color = SpecularBorder,
    topSpecularColor: Color = SpecularHighlight,
    onClick: (() -> Unit)? = null,
    testTag: String? = null,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val topGrad = if (backgroundColor == SmokedCrystal) ClaySurfaceTop else backgroundColor.copy(alpha = 0.95f)
    val bottomGrad = if (backgroundColor == SmokedCrystal) ClaySurfaceBottom else backgroundColor.copy(alpha = 0.82f)

    val cardModifier = modifier
        .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
        .shadow(
            elevation = 14.dp,
            shape = shape,
            ambientColor = Color.Black,
            spotColor = CrystalIce.copy(alpha = 0.28f)
        )
        .clip(shape)
        .background(
            Brush.verticalGradient(
                colors = listOf(topGrad, bottomGrad)
            )
        )
        .border(1.5.dp, borderColor, shape)
        .drawWithContent {
            drawContent()
            val radiusPx = cornerRadius.toPx()
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        ClayShadowDark.copy(alpha = 0.55f)
                    ),
                    startY = size.height * 0.5f,
                    endY = size.height.coerceAtLeast(1f)
                ),
                cornerRadius = CornerRadius(radiusPx, radiusPx),
                style = Stroke(width = 3.dp.toPx())
            )
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        topSpecularColor,
                        ClayHighlightIce.copy(alpha = 0.25f),
                        Color.Transparent
                    ),
                    start = Offset(0f, 0f),
                    end = Offset((size.width * 0.7f).coerceAtLeast(1f), (size.height * 0.5f).coerceAtLeast(1f))
                ),
                cornerRadius = CornerRadius(radiusPx, radiusPx),
                style = Stroke(width = 2.dp.toPx())
            )
        }
        .then(
            if (onClick != null) {
                Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(color = CrystalIce),
                    onClick = onClick
                )
            } else Modifier
        )

    Box(modifier = cardModifier) {
        content()
    }
}

@Composable
fun CrystalBadge(
    status: EnergyStatus,
    modifier: Modifier = Modifier
) {
    val (bgGradient, textCol) = when (status) {
        EnergyStatus.DEFICIT -> Pair(
            listOf(EmeraldMist.copy(alpha = 0.34f), EmeraldMist.copy(alpha = 0.14f)),
            EmeraldMist
        )
        EnergyStatus.MAINTENANCE -> Pair(
            listOf(WarmChampagne.copy(alpha = 0.34f), WarmChampagne.copy(alpha = 0.14f)),
            WarmChampagne
        )
        EnergyStatus.SURPLUS -> Pair(
            listOf(SoftCoralRose.copy(alpha = 0.34f), SoftCoralRose.copy(alpha = 0.14f)),
            SoftCoralRose
        )
    }

    Row(
        modifier = modifier
            .shadow(6.dp, RoundedCornerShape(50), spotColor = textCol.copy(alpha = 0.4f))
            .clip(RoundedCornerShape(50))
            .background(Brush.verticalGradient(bgGradient))
            .border(1.5.dp, textCol.copy(alpha = 0.5f), RoundedCornerShape(50))
            .padding(horizontal = 14.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = status.badgeIcon,
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = status.labelTh,
            color = textCol,
            fontFamily = ChillaxRoundedDisplayFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}

@Composable
fun CrystalPill(
    text: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(
                Brush.verticalGradient(
                    listOf(
                        accentColor.copy(alpha = 0.26f),
                        accentColor.copy(alpha = 0.12f)
                    )
                )
            )
            .border(1.dp, accentColor.copy(alpha = 0.45f), RoundedCornerShape(50))
            .padding(horizontal = 11.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = accentColor,
            fontFamily = ChillaxRoundedDisplayFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
        )
    }
}

@Composable
fun CrystalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    isPrimary: Boolean = true,
    enabled: Boolean = true,
    testTag: String = "crystal_button"
) {
    val shape = RoundedCornerShape(22.dp)
    val bgBrush = if (isPrimary) {
        Brush.verticalGradient(
            listOf(
                Color(0xFF93E2FE),
                CrystalIce,
                CrystalIceDark
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                SmokedCrystalElevated,
                ClaySurfaceBottom
            )
        )
    }

    val contentColor = if (isPrimary) Color(0xFF06101E) else TextPrimary
    val borderCol = if (isPrimary) Color.White.copy(alpha = 0.55f) else SpecularBorder

    Box(
        modifier = modifier
            .testTag(testTag)
            .height(48.dp)
            .shadow(
                elevation = if (isPrimary) 10.dp else 6.dp,
                shape = shape,
                ambientColor = Color.Black,
                spotColor = if (isPrimary) CrystalIce else Color.Black
            )
            .clip(shape)
            .background(bgBrush)
            .border(1.5.dp, borderCol, shape)
            .drawWithContent {
                drawContent()
                val r = 22.dp.toPx()
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        listOf(ClayHighlightLight, Color.Transparent),
                        startY = 0f,
                        endY = (size.height * 0.45f).coerceAtLeast(1f)
                    ),
                    cornerRadius = CornerRadius(r, r),
                    style = Stroke(width = 2.dp.toPx())
                )
            }
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = if (isPrimary) Color.Black else CrystalIce),
                onClick = onClick
            )
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = contentColor,
                fontFamily = ChillaxRoundedDisplayFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun FloatingClayOrb(
    baseColor: Color,
    sizeDp: Dp,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(sizeDp)) {
        if (size.minDimension <= 0f) return@Canvas
        val radius = (size.minDimension / 2f).coerceAtLeast(1f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.85f),
                    baseColor,
                    baseColor.copy(alpha = 0.7f),
                    Color(0xFF060A14)
                ),
                center = Offset(size.width * 0.32f, size.height * 0.28f),
                radius = (radius * 1.35f).coerceAtLeast(1f)
            ),
            radius = radius
        )
        val gleamRadius = (radius * 0.45f).coerceAtLeast(1f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.65f),
                    Color.Transparent
                ),
                center = Offset(size.width * 0.30f, size.height * 0.26f),
                radius = gleamRadius
            ),
            radius = gleamRadius,
            center = Offset(size.width * 0.30f, size.height * 0.26f)
        )
    }
}

data class BottomBarItem(
    val title: String,
    val iconEmoji: String,
    val testTag: String,
    val route: String = testTag,
    val selectedIcon: ImageVector? = null,
    val unselectedIcon: ImageVector? = null
)

@Composable
fun BottomNavigationBar(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    items: List<BottomBarItem>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag("bottom_navigation_bar"),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 18.dp,
                    shape = RoundedCornerShape(34.dp),
                    ambientColor = Color.Black,
                    spotColor = CrystalIce.copy(alpha = 0.35f)
                )
                .clip(RoundedCornerShape(34.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xF21E2B48),
                            Color(0xF50F172A)
                        )
                    )
                )
                .border(1.5.dp, SpecularBorder, RoundedCornerShape(34.dp))
                .drawWithContent {
                    drawContent()
                    val r = 34.dp.toPx()
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            listOf(ClayHighlightLight, Color.Transparent),
                            startY = 0f,
                            endY = (size.height * 0.4f).coerceAtLeast(1f)
                        ),
                        cornerRadius = CornerRadius(r, r),
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = index == selectedIndex
                val activeBgColor by animateColorAsState(
                    targetValue = if (isSelected) CrystalIce.copy(alpha = 0.24f) else Color.Transparent,
                    animationSpec = tween(250), label = "tab_bg"
                )
                val activeTextColor by animateColorAsState(
                    targetValue = if (isSelected) CrystalIce else TextSecondary,
                    animationSpec = tween(250), label = "tab_text"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 2.dp)
                        .testTag(item.testTag)
                        .clip(RoundedCornerShape(24.dp))
                        .background(activeBgColor)
                        .then(
                            if (isSelected) {
                                Modifier.border(
                                    1.dp,
                                    CrystalIce.copy(alpha = 0.45f),
                                    RoundedCornerShape(24.dp)
                                )
                            } else Modifier
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = CrystalIce),
                            onClick = { onTabSelected(index) }
                        )
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        val vectorIcon = if (isSelected) item.selectedIcon else (item.unselectedIcon ?: item.selectedIcon)
                        if (vectorIcon != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = vectorIcon,
                                    contentDescription = item.title,
                                    tint = activeTextColor,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = item.iconEmoji,
                                    fontSize = 13.sp
                                )
                            }
                        } else {
                            Text(
                                text = item.iconEmoji,
                                fontSize = 18.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = item.title,
                            color = activeTextColor,
                            fontFamily = ChillaxRoundedDisplayFamily,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CrystalFloatingBottomBar(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    items: List<BottomBarItem>,
    modifier: Modifier = Modifier
) {
    BottomNavigationBar(
        selectedIndex = selectedIndex,
        onTabSelected = onTabSelected,
        items = items,
        modifier = modifier
    )
}

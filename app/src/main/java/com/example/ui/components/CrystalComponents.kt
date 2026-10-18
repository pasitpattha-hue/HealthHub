package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EnergyStatus
import com.example.ui.theme.CrystalIce
import com.example.ui.theme.EmeraldMist
import com.example.ui.theme.ObsidianCard
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
    cornerRadius: Dp = 22.dp,
    backgroundColor: Color = SmokedCrystal,
    borderColor: Color = SpecularBorder,
    topSpecularColor: Color = SpecularHighlight,
    onClick: (() -> Unit)? = null,
    testTag: String? = null,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val cardModifier = modifier
        .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
        .clip(shape)
        .background(backgroundColor)
        .border(1.dp, borderColor, shape)
        .drawBehind {
            // Draw specular top border reflection
            drawLine(
                brush = Brush.horizontalGradient(
                    listOf(
                        Color.Transparent,
                        topSpecularColor,
                        Color.Transparent
                    )
                ),
                start = Offset(size.width * 0.1f, 1f),
                end = Offset(size.width * 0.9f, 1f),
                strokeWidth = 1.5f
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
            listOf(EmeraldMist.copy(alpha = 0.25f), EmeraldMist.copy(alpha = 0.08f)),
            EmeraldMist
        )
        EnergyStatus.MAINTENANCE -> Pair(
            listOf(WarmChampagne.copy(alpha = 0.25f), WarmChampagne.copy(alpha = 0.08f)),
            WarmChampagne
        )
        EnergyStatus.SURPLUS -> Pair(
            listOf(SoftCoralRose.copy(alpha = 0.25f), SoftCoralRose.copy(alpha = 0.08f)),
            SoftCoralRose
        )
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(30.dp))
            .background(Brush.horizontalGradient(bgGradient))
            .border(1.dp, textCol.copy(alpha = 0.4f), RoundedCornerShape(30.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
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
            .clip(RoundedCornerShape(20.dp))
            .background(accentColor.copy(alpha = 0.15f))
            .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = accentColor,
            fontWeight = FontWeight.SemiBold,
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
    val bgBrush = if (isPrimary) {
        Brush.horizontalGradient(listOf(CrystalIce, Color(0xFF38BDF8)))
    } else {
        Brush.horizontalGradient(listOf(SmokedCrystalElevated, SmokedCrystal))
    }

    val contentColor = if (isPrimary) Color(0xFF070A12) else TextPrimary
    val borderCol = if (isPrimary) CrystalIce.copy(alpha = 0.6f) else SpecularBorder

    Box(
        modifier = modifier
            .testTag(testTag)
            .height(48.dp) // Minimum 48.dp interactive target
            .clip(RoundedCornerShape(14.dp))
            .background(bgBrush)
            .border(1.dp, borderCol, RoundedCornerShape(14.dp))
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
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

data class BottomBarItem(
    val title: String,
    val iconEmoji: String,
    val testTag: String
)

@Composable
fun CrystalFloatingBottomBar(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    items: List<BottomBarItem>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        // Smoked Crystal Floating Pill
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(28.dp))
                .background(Color(0xE60F172A))
                .border(1.dp, SpecularBorder, RoundedCornerShape(28.dp))
                .drawBehind {
                    // Top specular glow
                    drawLine(
                        brush = Brush.horizontalGradient(
                            listOf(Color.Transparent, CrystalIce.copy(alpha = 0.3f), Color.Transparent)
                        ),
                        start = Offset(size.width * 0.15f, 0f),
                        end = Offset(size.width * 0.85f, 0f),
                        strokeWidth = 2f
                    )
                }
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = index == selectedIndex
                val activeBgColor by animateColorAsState(
                    targetValue = if (isSelected) CrystalIce.copy(alpha = 0.2f) else Color.Transparent,
                    animationSpec = tween(250), label = "tab_bg"
                )
                val activeTextColor by animateColorAsState(
                    targetValue = if (isSelected) CrystalIce else TextSecondary,
                    animationSpec = tween(250), label = "tab_text"
                )

                Box(
                    modifier = Modifier
                        .testTag(item.testTag)
                        .clip(RoundedCornerShape(20.dp))
                        .background(activeBgColor)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = CrystalIce),
                            onClick = { onTabSelected(index) }
                        )
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = item.iconEmoji,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.title,
                            color = activeTextColor,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

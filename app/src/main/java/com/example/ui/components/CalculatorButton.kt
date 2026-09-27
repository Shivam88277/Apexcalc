package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalCalculatorColors

enum class ButtonType {
    NUMBER,
    OPERATOR,
    FUNCTION,
    EQUALS,
    CLEAR,
    MEMORY
}

@Composable
fun CalculatorButton(
    text: String,
    type: ButtonType,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 22.sp,
    hapticsEnabled: Boolean = true,
    testTag: String = "btn_$text",
    onLongClick: (() -> Unit)? = null,
    onClick: () -> Unit
) {
    val colors = LocalCalculatorColors.current
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.93f else 1f,
        animationSpec = tween(durationMillis = 80),
        label = "button_scale"
    )

    val (bg, textColor) = when (type) {
        ButtonType.NUMBER -> colors.numberKeyBg to colors.numberKeyText
        ButtonType.OPERATOR -> colors.operatorKeyBg to colors.operatorKeyText
        ButtonType.FUNCTION -> colors.functionKeyBg to colors.functionKeyText
        ButtonType.EQUALS -> colors.equalsKeyBg to colors.equalsKeyText
        ButtonType.CLEAR -> colors.clearKeyBg to colors.clearKeyText
        ButtonType.MEMORY -> colors.memoryKeyBg to colors.memoryKeyText
    }

    Box(
        modifier = modifier
            .padding(3.dp)
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    if (hapticsEnabled) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }
                    onClick()
                }
            )
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = fontSize,
            fontWeight = if (type == ButtonType.EQUALS || type == ButtonType.OPERATOR || type == ButtonType.CLEAR) FontWeight.Bold else FontWeight.Medium,
            color = textColor,
            maxLines = 1
        )
    }
}

package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.AngleUnit
import com.example.ui.theme.LocalCalculatorColors

@Composable
fun CalculatorDisplay(
    expression: String,
    result: String,
    livePreview: String?,
    angleUnit: AngleUnit,
    isSecondFunction: Boolean,
    isHyp: Boolean,
    memoryValue: Double?,
    onToggleAngleUnit: () -> Unit,
    onPaste: (String) -> Unit,
    onShare: () -> Unit,
    onRecallMemory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalCalculatorColors.current
    val context = LocalContext.current
    val exprScrollState = rememberScrollState()

    // Auto-scroll to end of expression when it changes
    LaunchedEffect(expression) {
        exprScrollState.animateScrollTo(exprScrollState.maxValue)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(colors.displayBackground)
            .padding(16.dp)
            .testTag("calculator_display")
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Indicator & Quick Action Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mode Badges
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // DEG / RAD Toggle Pill
                    Surface(
                        color = colors.surfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .clickable { onToggleAngleUnit() }
                            .testTag("toggle_angle_unit")
                    ) {
                        Text(
                            text = angleUnit.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.accent,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // 2nd Indicator
                    if (isSecondFunction) {
                        Surface(
                            color = colors.accent.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "2nd",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.accent,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // HYP Indicator
                    if (isHyp) {
                        Surface(
                            color = colors.functionKeyText.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "HYP",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.functionKeyText,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Memory Indicator
                    if (memoryValue != null) {
                        Surface(
                            color = colors.memoryKeyBg,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .clickable { onRecallMemory() }
                                .testTag("memory_active_pill")
                        ) {
                            Text(
                                text = "M",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.memoryKeyText,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Copy / Paste / Share Quick Actions
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Quick Paste
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = clipboard.primaryClip
                            if (clip != null && clip.itemCount > 0) {
                                val text = clip.getItemAt(0).text?.toString() ?: ""
                                if (text.isNotBlank()) {
                                    onPaste(text)
                                    Toast.makeText(context, "Pasted from clipboard", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Clipboard empty", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "Clipboard empty", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("quick_paste_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentPaste,
                            contentDescription = "Paste from clipboard",
                            tint = colors.displayText.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Quick Copy Result
                    IconButton(
                        onClick = {
                            val copyTarget = if (result.isNotBlank()) result else (livePreview ?: expression)
                            if (copyTarget.isNotBlank()) {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Calculator Result", copyTarget)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Copied: $copyTarget", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("quick_copy_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy result to clipboard",
                            tint = colors.displayText.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Share
                    IconButton(
                        onClick = onShare,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("quick_share_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share calculation",
                            tint = colors.displayText.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Expression Input
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(exprScrollState),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = if (expression.isEmpty()) "0" else expression,
                    fontSize = if (expression.length > 16) 28.sp else 36.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Normal,
                    color = if (expression.isEmpty()) colors.displayText.copy(alpha = 0.4f) else colors.displayText,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    modifier = Modifier.testTag("display_expression_text")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Result / Live Preview Display
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                // Evaluated Result (when equals pressed or final)
                AnimatedVisibility(
                    visible = result.isNotBlank(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        text = "= $result",
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = colors.displaySecondaryText,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .clickable {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Calculator Result", result)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Copied result: $result", Toast.LENGTH_SHORT).show()
                            }
                            .testTag("display_result_text")
                    )
                }

                // Live Preview (while typing)
                AnimatedVisibility(
                    visible = result.isBlank() && livePreview != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        text = "= ${livePreview ?: ""}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.Monospace,
                        color = colors.displayText.copy(alpha = 0.5f),
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag("display_live_preview_text")
                    )
                }
            }
        }
    }
}

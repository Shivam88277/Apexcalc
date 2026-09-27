package com.example.ui.theme_settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.preferences.AppThemePreset
import com.example.ui.theme.LocalCalculatorColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeDialog(
    currentTheme: AppThemePreset,
    hapticsEnabled: Boolean,
    onSelectTheme: (AppThemePreset) -> Unit,
    onToggleHaptics: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = LocalCalculatorColors.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = colors.surface,
        contentColor = colors.displayText,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = Modifier.testTag("theme_selection_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = null,
                    tint = colors.accent
                )
                Text(
                    text = "Dark Mode & Themes",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.displayText
                )
            }

            Text(
                text = "Select your preferred aesthetic. High-contrast OLED dark themes conserve battery and reduce eye fatigue.",
                fontSize = 13.sp,
                color = colors.displayText.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            // Haptics switch row
            Surface(
                color = colors.surfaceVariant,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Vibration,
                            contentDescription = null,
                            tint = colors.accent
                        )
                        Column {
                            Text(
                                text = "Haptic Vibration",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = colors.displayText
                            )
                            Text(
                                text = "Tactile click on button press",
                                fontSize = 12.sp,
                                color = colors.displayText.copy(alpha = 0.6f)
                            )
                        }
                    }

                    Switch(
                        checked = hapticsEnabled,
                        onCheckedChange = onToggleHaptics,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = colors.accent,
                            checkedTrackColor = colors.accent.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier.testTag("switch_haptics")
                    )
                }
            }

            Text(
                text = "THEME PRESETS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = colors.accent,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Theme Preset List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .testTag("theme_preset_list"),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(AppThemePreset.entries) { preset ->
                    val isSelected = preset == currentTheme

                    Surface(
                        color = if (isSelected) colors.surfaceVariant else colors.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) colors.accent else Color.Transparent,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { onSelectTheme(preset) }
                            .testTag("theme_preset_${preset.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Mini color palette preview dot
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(preset.previewBackground))
                                        .border(2.dp, Color(preset.previewPrimary), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clip(CircleShape)
                                            .background(Color(preset.previewPrimary))
                                    )
                                }

                                Column {
                                    Text(
                                        text = preset.displayName,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.displayText
                                    )
                                    Text(
                                        text = preset.description,
                                        fontSize = 12.sp,
                                        color = colors.displayText.copy(alpha = 0.6f)
                                    )
                                }
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = colors.accent,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

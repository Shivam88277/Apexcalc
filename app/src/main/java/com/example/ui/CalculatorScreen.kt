package com.example.ui

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CalculatorDisplay
import com.example.ui.components.CalculatorKeypad
import com.example.ui.history.HistorySheet
import com.example.ui.theme.LocalCalculatorColors
import com.example.ui.theme_settings.ThemeDialog
import com.example.ui.viewmodel.CalculatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val colors = LocalCalculatorColors.current
    val context = LocalContext.current

    val expression by viewModel.expression.collectAsStateWithLifecycle()
    val result by viewModel.result.collectAsStateWithLifecycle()
    val livePreview by viewModel.livePreview.collectAsStateWithLifecycle()
    val angleUnit by viewModel.angleUnit.collectAsStateWithLifecycle()
    val isSecondFunction by viewModel.isSecondFunction.collectAsStateWithLifecycle()
    val isHyp by viewModel.isHyp.collectAsStateWithLifecycle()
    val isScientificExpanded by viewModel.isScientificExpanded.collectAsStateWithLifecycle()
    val memoryValue by viewModel.memoryValue.collectAsStateWithLifecycle()
    val currentTheme by viewModel.currentTheme.collectAsStateWithLifecycle()
    val hapticsEnabled by viewModel.hapticsEnabled.collectAsStateWithLifecycle()
    val historyList by viewModel.historyList.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val showOnlyBookmarked by viewModel.showOnlyBookmarked.collectAsStateWithLifecycle()

    var showHistorySheet by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "ApexCalc",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.displayText
                        )
                        Surface(
                            color = colors.accent.copy(alpha = 0.2f),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = "PRO",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = colors.accent,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                },
                actions = {
                    // History Button
                    IconButton(
                        onClick = { showHistorySheet = true },
                        modifier = Modifier.testTag("appbar_history_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (historyList.isNotEmpty()) {
                                    Badge(
                                        containerColor = colors.accent,
                                        contentColor = colors.equalsKeyText
                                    ) {
                                        Text(text = if (historyList.size > 99) "99+" else historyList.size.toString())
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "Calculation History",
                                tint = colors.displayText
                            )
                        }
                    }

                    // Theme Customizer Button
                    IconButton(
                        onClick = { showThemeDialog = true },
                        modifier = Modifier.testTag("appbar_theme_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Customize Theme & Dark Mode",
                            tint = colors.accent
                        )
                    }

                    // Quick Share Button
                    IconButton(
                        onClick = {
                            val shareContent = if (result.isNotBlank()) {
                                "$expression = $result"
                            } else if (expression.isNotBlank()) {
                                expression
                            } else {
                                null
                            }
                            if (shareContent != null) {
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, shareContent)
                                }
                                context.startActivity(Intent.createChooser(intent, "Share Calculation"))
                            } else {
                                Toast.makeText(context, "Nothing to share yet", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.testTag("appbar_share_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share current calculation",
                            tint = colors.displayText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.background,
                    titleContentColor = colors.displayText
                )
            )
        },
        containerColor = colors.background,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Display Area
            CalculatorDisplay(
                expression = expression,
                result = result,
                livePreview = livePreview,
                angleUnit = angleUnit,
                isSecondFunction = isSecondFunction,
                isHyp = isHyp,
                memoryValue = memoryValue,
                onToggleAngleUnit = viewModel::toggleAngleUnit,
                onPaste = viewModel::onPaste,
                onShare = {
                    val shareContent = if (result.isNotBlank()) {
                        "$expression = $result"
                    } else if (expression.isNotBlank()) {
                        expression
                    } else null

                    if (shareContent != null) {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareContent)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share Calculation"))
                    } else {
                        Toast.makeText(context, "Nothing to share yet", Toast.LENGTH_SHORT).show()
                    }
                },
                onRecallMemory = { viewModel.onMemoryAction("MR") },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Keypad Area
            CalculatorKeypad(
                isScientificExpanded = isScientificExpanded,
                onToggleScientific = viewModel::toggleScientific,
                isSecondFunction = isSecondFunction,
                onToggleSecond = viewModel::toggleSecond,
                isHyp = isHyp,
                onToggleHyp = viewModel::toggleHyp,
                hapticsEnabled = hapticsEnabled,
                onDigit = viewModel::onDigit,
                onOperator = viewModel::onOperator,
                onFunction = viewModel::onFunction,
                onEquals = viewModel::onEquals,
                onClear = viewModel::onClear,
                onDelete = viewModel::onDelete,
                onPlusMinus = viewModel::onPlusMinus,
                onPercent = viewModel::onPercent,
                onParenthesis = viewModel::onParenthesis,
                onConstant = viewModel::onConstant,
                onMemoryAction = viewModel::onMemoryAction,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    // History Bottom Sheet
    if (showHistorySheet) {
        HistorySheet(
            historyList = historyList,
            searchQuery = searchQuery,
            onSearchQueryChange = viewModel::setSearchQuery,
            showOnlyBookmarked = showOnlyBookmarked,
            onToggleShowOnlyBookmarked = viewModel::toggleShowOnlyBookmarked,
            onUseResult = { res ->
                viewModel.useResult(res)
                showHistorySheet = false
            },
            onUseExpression = { expr ->
                viewModel.useExpression(expr)
                showHistorySheet = false
            },
            onToggleBookmark = viewModel::toggleBookmark,
            onUpdateNote = viewModel::updateNote,
            onDeleteHistoryItem = viewModel::deleteHistoryItem,
            onClearAllHistory = viewModel::clearAllHistory,
            onDismiss = { showHistorySheet = false }
        )
    }

    // Theme Customizer Dialog
    if (showThemeDialog) {
        ThemeDialog(
            currentTheme = currentTheme,
            hapticsEnabled = hapticsEnabled,
            onSelectTheme = viewModel::setTheme,
            onToggleHaptics = viewModel::setHapticsEnabled,
            onDismiss = { showThemeDialog = false }
        )
    }
}

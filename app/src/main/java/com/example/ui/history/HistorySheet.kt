package com.example.ui.history

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CalculationHistoryEntity
import com.example.ui.theme.LocalCalculatorColors
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorySheet(
    historyList: List<CalculationHistoryEntity>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    showOnlyBookmarked: Boolean,
    onToggleShowOnlyBookmarked: () -> Unit,
    onUseResult: (String) -> Unit,
    onUseExpression: (String) -> Unit,
    onToggleBookmark: (Long, Boolean) -> Unit,
    onUpdateNote: (Long, String?) -> Unit,
    onDeleteHistoryItem: (Long) -> Unit,
    onClearAllHistory: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalCalculatorColors.current
    val context = LocalContext.current
    var showClearConfirm by remember { mutableStateOf(false) }
    var editingNoteItem by remember { mutableStateOf<CalculationHistoryEntity?>(null) }
    var noteInputText by remember { mutableStateOf("") }
    var showExportMenu by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = colors.surface,
        contentColor = colors.displayText,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = modifier.testTag("history_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(horizontal = 16.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = colors.accent
                    )
                    Text(
                        text = "Calculation History",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.displayText
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Export / Sync Button
                    Box {
                        IconButton(
                            onClick = { showExportMenu = true },
                            modifier = Modifier.testTag("history_export_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Export & Sync calculations",
                                tint = colors.accent
                            )
                        }

                        DropdownMenu(
                            expanded = showExportMenu,
                            onDismissRequest = { showExportMenu = false },
                            modifier = Modifier.background(colors.surfaceVariant)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Share as Text Report", color = colors.displayText) },
                                leadingIcon = { Icon(Icons.Default.Send, null, tint = colors.accent) },
                                onClick = {
                                    showExportMenu = false
                                    shareHistoryAsText(context, historyList)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Export as CSV (Spreadsheet)", color = colors.displayText) },
                                leadingIcon = { Icon(Icons.Default.TableChart, null, tint = colors.accent) },
                                onClick = {
                                    showExportMenu = false
                                    exportHistoryAsCsv(context, historyList)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Export as JSON", color = colors.displayText) },
                                leadingIcon = { Icon(Icons.Default.Code, null, tint = colors.accent) },
                                onClick = {
                                    showExportMenu = false
                                    exportHistoryAsJson(context, historyList)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Copy All to Clipboard", color = colors.displayText) },
                                leadingIcon = { Icon(Icons.Default.ContentCopy, null, tint = colors.accent) },
                                onClick = {
                                    showExportMenu = false
                                    copyAllHistoryToClipboard(context, historyList)
                                }
                            )
                        }
                    }

                    // Clear All
                    if (historyList.isNotEmpty()) {
                        IconButton(
                            onClick = { showClearConfirm = true },
                            modifier = Modifier.testTag("history_clear_all_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Clear all history",
                                tint = colors.clearKeyText
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar & Filter Chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search calculations or notes...", color = colors.displayText.copy(alpha = 0.5f)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = colors.displayText.copy(alpha = 0.6f)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(Icons.Default.Clear, "Clear search", tint = colors.displayText.copy(alpha = 0.6f))
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.accent,
                        unfocusedBorderColor = colors.surfaceVariant,
                        focusedTextColor = colors.displayText,
                        unfocusedTextColor = colors.displayText
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("history_search_input")
                )

                FilterChip(
                    selected = showOnlyBookmarked,
                    onClick = onToggleShowOnlyBookmarked,
                    label = {
                        Icon(
                            imageVector = if (showOnlyBookmarked) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Starred filter",
                            tint = if (showOnlyBookmarked) Color(0xFFFFD700) else colors.displayText
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = colors.surfaceVariant,
                        containerColor = Color.Transparent
                    ),
                    modifier = Modifier.testTag("history_filter_starred")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // History List
            if (historyList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = null,
                            tint = colors.displayText.copy(alpha = 0.3f),
                            modifier = Modifier.size(56.dp)
                        )
                        Text(
                            text = if (searchQuery.isNotBlank() || showOnlyBookmarked) "No matching calculations found" else "No calculation history yet",
                            color = colors.displayText.copy(alpha = 0.6f),
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Calculate something to see it logged here.",
                            color = colors.displayText.copy(alpha = 0.4f),
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("history_list"),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(historyList, key = { it.id }) { item ->
                        HistoryItemCard(
                            item = item,
                            onUseResult = { onUseResult(item.result) },
                            onUseExpression = { onUseExpression(item.expression) },
                            onToggleBookmark = { onToggleBookmark(item.id, item.isBookmarked) },
                            onEditNote = {
                                editingNoteItem = item
                                noteInputText = item.note ?: ""
                            },
                            onDelete = { onDeleteHistoryItem(item.id) }
                        )
                    }
                }
            }
        }
    }

    // Confirm Clear All Dialog
    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("Clear All History?") },
            text = { Text("This will permanently remove all previous calculations from your history log.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearAllHistory()
                        showClearConfirm = false
                        Toast.makeText(context, "History cleared", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Clear All", color = colors.clearKeyText)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text("Cancel", color = colors.displayText)
                }
            },
            containerColor = colors.surfaceVariant,
            titleContentColor = colors.displayText,
            textContentColor = colors.displayText.copy(alpha = 0.8f)
        )
    }

    // Edit Note Dialog
    if (editingNoteItem != null) {
        AlertDialog(
            onDismissRequest = { editingNoteItem = null },
            title = { Text("Add Label / Note") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "${editingNoteItem?.expression} = ${editingNoteItem?.result}",
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        color = colors.accent
                    )
                    OutlinedTextField(
                        value = noteInputText,
                        onValueChange = { noteInputText = it },
                        placeholder = { Text("e.g. Tax deduction, Physics HW #4, Monthly budget") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        editingNoteItem?.let {
                            onUpdateNote(it.id, noteInputText.trim().ifEmpty { null })
                        }
                        editingNoteItem = null
                    }
                ) {
                    Text("Save", color = colors.accent)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingNoteItem = null }) {
                    Text("Cancel", color = colors.displayText)
                }
            },
            containerColor = colors.surfaceVariant,
            titleContentColor = colors.displayText,
            textContentColor = colors.displayText.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun HistoryItemCard(
    item: CalculationHistoryEntity,
    onUseResult: () -> Unit,
    onUseExpression: () -> Unit,
    onToggleBookmark: () -> Unit,
    onEditNote: () -> Unit,
    onDelete: () -> Unit
) {
    val colors = LocalCalculatorColors.current
    val context = LocalContext.current
    val timeFormatted = remember(item.timestamp) {
        val sdf = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())
        sdf.format(Date(item.timestamp))
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.surfaceVariant.copy(alpha = 0.6f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("history_item_${item.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Meta Row: Time, Angle Unit, Note, Star
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = timeFormatted,
                        fontSize = 11.sp,
                        color = colors.displayText.copy(alpha = 0.5f)
                    )
                    Surface(
                        color = colors.background,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = item.angleUnit,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.accent,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (item.isBookmarked) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Bookmark",
                            tint = if (item.isBookmarked) Color(0xFFFFD700) else colors.displayText.copy(alpha = 0.5f),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onEditNote,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = "Edit Note",
                            tint = if (item.note != null) colors.accent else colors.displayText.copy(alpha = 0.5f),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete",
                            tint = colors.clearKeyText.copy(alpha = 0.8f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Note tag if present
            if (!item.note.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = colors.accent.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Text(
                        text = "🏷 ${item.note}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.accent,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Expression
            Text(
                text = item.expression,
                fontSize = 15.sp,
                fontFamily = FontFamily.Monospace,
                color = colors.displayText.copy(alpha = 0.7f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Result
            Text(
                text = "= ${item.result}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = colors.accent,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Action Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onUseResult,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Use Result", fontSize = 12.sp, color = colors.displayText)
                }

                OutlinedButton(
                    onClick = onUseExpression,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Use Expr", fontSize = 12.sp, color = colors.displayText)
                }

                OutlinedButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Calculation", "${item.expression} = ${item.result}")
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                    },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        modifier = Modifier.size(14.dp),
                        tint = colors.displayText
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy", fontSize = 12.sp, color = colors.displayText)
                }
            }
        }
    }
}

// Export & Synchronization Helpers
private fun shareHistoryAsText(context: Context, history: List<CalculationHistoryEntity>) {
    if (history.isEmpty()) {
        Toast.makeText(context, "No history to export", Toast.LENGTH_SHORT).show()
        return
    }
    val sb = StringBuilder()
    sb.append("=== ApexCalc Calculation Report ===\n")
    sb.append("Generated on: ${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())}\n\n")

    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    for (item in history) {
        val noteStr = if (!item.note.isNullOrBlank()) " [${item.note}]" else ""
        sb.append("${sdf.format(Date(item.timestamp))}: ${item.expression} = ${item.result} (${item.angleUnit})$noteStr\n")
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "ApexCalc Calculations")
        putExtra(Intent.EXTRA_TEXT, sb.toString())
    }
    context.startActivity(Intent.createChooser(intent, "Share Calculations"))
}

private fun exportHistoryAsCsv(context: Context, history: List<CalculationHistoryEntity>) {
    if (history.isEmpty()) {
        Toast.makeText(context, "No history to export", Toast.LENGTH_SHORT).show()
        return
    }
    val sb = StringBuilder()
    sb.append("Timestamp,Date,Expression,Result,AngleUnit,Bookmarked,Note\n")
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    for (item in history) {
        val safeExpr = "\"${item.expression.replace("\"", "\"\"")}\""
        val safeRes = "\"${item.result.replace("\"", "\"\"")}\""
        val safeNote = "\"${(item.note ?: "").replace("\"", "\"\"")}\""
        sb.append("${item.timestamp},\"${sdf.format(Date(item.timestamp))}\",$safeExpr,$safeRes,${item.angleUnit},${item.isBookmarked},$safeNote\n")
    }

    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("ApexCalc CSV Export", sb.toString())
    clipboard.setPrimaryClip(clip)

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/comma-separated-values"
        putExtra(Intent.EXTRA_SUBJECT, "ApexCalc History CSV")
        putExtra(Intent.EXTRA_TEXT, sb.toString())
    }
    context.startActivity(Intent.createChooser(intent, "Export History CSV"))
    Toast.makeText(context, "CSV copied to clipboard & shared", Toast.LENGTH_SHORT).show()
}

private fun exportHistoryAsJson(context: Context, history: List<CalculationHistoryEntity>) {
    if (history.isEmpty()) {
        Toast.makeText(context, "No history to export", Toast.LENGTH_SHORT).show()
        return
    }
    val sb = StringBuilder()
    sb.append("[\n")
    for (i in history.indices) {
        val item = history[i]
        val safeExpr = item.expression.replace("\\", "\\\\").replace("\"", "\\\"")
        val safeRes = item.result.replace("\\", "\\\\").replace("\"", "\\\"")
        val safeNote = item.note?.replace("\\", "\\\\")?.replace("\"", "\\\"")
        sb.append("  {\n")
        sb.append("    \"id\": ${item.id},\n")
        sb.append("    \"timestamp\": ${item.timestamp},\n")
        sb.append("    \"expression\": \"$safeExpr\",\n")
        sb.append("    \"result\": \"$safeRes\",\n")
        sb.append("    \"angleUnit\": \"${item.angleUnit}\",\n")
        sb.append("    \"bookmarked\": ${item.isBookmarked},\n")
        sb.append("    \"note\": ${if (safeNote != null) "\"$safeNote\"" else "null"}\n")
        sb.append("  }${if (i < history.size - 1) "," else ""}\n")
    }
    sb.append("]")

    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("ApexCalc JSON Export", sb.toString())
    clipboard.setPrimaryClip(clip)

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/json"
        putExtra(Intent.EXTRA_SUBJECT, "ApexCalc History JSON")
        putExtra(Intent.EXTRA_TEXT, sb.toString())
    }
    context.startActivity(Intent.createChooser(intent, "Export History JSON"))
    Toast.makeText(context, "JSON copied to clipboard & shared", Toast.LENGTH_SHORT).show()
}

private fun copyAllHistoryToClipboard(context: Context, history: List<CalculationHistoryEntity>) {
    if (history.isEmpty()) {
        Toast.makeText(context, "No history to copy", Toast.LENGTH_SHORT).show()
        return
    }
    val sb = StringBuilder()
    for (item in history) {
        sb.append("${item.expression} = ${item.result}\n")
    }
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("All Calculations", sb.toString())
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "Copied ${history.size} calculations to clipboard", Toast.LENGTH_SHORT).show()
}

package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalCalculatorColors

@Composable
fun CalculatorKeypad(
    isScientificExpanded: Boolean,
    onToggleScientific: () -> Unit,
    isSecondFunction: Boolean,
    onToggleSecond: () -> Unit,
    isHyp: Boolean,
    onToggleHyp: () -> Unit,
    hapticsEnabled: Boolean,
    onDigit: (String) -> Unit,
    onOperator: (String) -> Unit,
    onFunction: (String) -> Unit,
    onEquals: () -> Unit,
    onClear: () -> Unit,
    onDelete: () -> Unit,
    onPlusMinus: () -> Unit,
    onPercent: () -> Unit,
    onParenthesis: (String) -> Unit,
    onConstant: (String) -> Unit,
    onMemoryAction: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalCalculatorColors.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Memory & Scientific Toggle Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Memory operations
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf("MC", "MR", "M+", "M-", "MS").forEach { memOp ->
                    CalculatorButton(
                        text = memOp,
                        type = ButtonType.MEMORY,
                        fontSize = 13.sp,
                        hapticsEnabled = hapticsEnabled,
                        testTag = "btn_mem_$memOp",
                        modifier = Modifier
                            .height(36.dp)
                            .width(52.dp),
                        onClick = { onMemoryAction(memOp) }
                    )
                }
            }

            // Scientific Expand/Collapse Button
            FilledTonalButton(
                onClick = onToggleScientific,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = if (isScientificExpanded) colors.accent.copy(alpha = 0.2f) else colors.surfaceVariant,
                    contentColor = if (isScientificExpanded) colors.accent else colors.displayText
                ),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier
                    .height(36.dp)
                    .testTag("toggle_scientific_button")
            ) {
                Text(
                    text = if (isScientificExpanded) "Sci ▲" else "Sci ▼",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Scientific Panel (Expandable)
        AnimatedVisibility(
            visible = isScientificExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.surfaceVariant.copy(alpha = 0.4f))
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // Row 1: 2nd, HYP, sin/asin, cos/acos, tan/atan
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CalculatorButton(
                        text = "2nd",
                        type = if (isSecondFunction) ButtonType.EQUALS else ButtonType.FUNCTION,
                        fontSize = 14.sp,
                        hapticsEnabled = hapticsEnabled,
                        modifier = Modifier.weight(1f).height(44.dp),
                        onClick = onToggleSecond
                    )
                    CalculatorButton(
                        text = "hyp",
                        type = if (isHyp) ButtonType.EQUALS else ButtonType.FUNCTION,
                        fontSize = 14.sp,
                        hapticsEnabled = hapticsEnabled,
                        modifier = Modifier.weight(1f).height(44.dp),
                        onClick = onToggleHyp
                    )
                    val sinLabel = if (isHyp) (if (isSecondFunction) "asinh" else "sinh") else (if (isSecondFunction) "sin⁻¹" else "sin")
                    val sinFunc = if (isHyp) (if (isSecondFunction) "asinh" else "sinh") else (if (isSecondFunction) "asin" else "sin")
                    CalculatorButton(
                        text = sinLabel,
                        type = ButtonType.FUNCTION,
                        fontSize = 14.sp,
                        hapticsEnabled = hapticsEnabled,
                        modifier = Modifier.weight(1f).height(44.dp),
                        onClick = { onFunction(sinFunc) }
                    )
                    val cosLabel = if (isHyp) (if (isSecondFunction) "acosh" else "cosh") else (if (isSecondFunction) "cos⁻¹" else "cos")
                    val cosFunc = if (isHyp) (if (isSecondFunction) "acosh" else "cosh") else (if (isSecondFunction) "acos" else "cos")
                    CalculatorButton(
                        text = cosLabel,
                        type = ButtonType.FUNCTION,
                        fontSize = 14.sp,
                        hapticsEnabled = hapticsEnabled,
                        modifier = Modifier.weight(1f).height(44.dp),
                        onClick = { onFunction(cosFunc) }
                    )
                    val tanLabel = if (isHyp) (if (isSecondFunction) "atanh" else "tanh") else (if (isSecondFunction) "tan⁻¹" else "tan")
                    val tanFunc = if (isHyp) (if (isSecondFunction) "atanh" else "tanh") else (if (isSecondFunction) "atan" else "tan")
                    CalculatorButton(
                        text = tanLabel,
                        type = ButtonType.FUNCTION,
                        fontSize = 14.sp,
                        hapticsEnabled = hapticsEnabled,
                        modifier = Modifier.weight(1f).height(44.dp),
                        onClick = { onFunction(tanFunc) }
                    )
                }

                // Row 2: ln/e^x, log/10^x, log2, x!, 1/x
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val lnLabel = if (isSecondFunction) "eˣ" else "ln"
                    val lnFunc = if (isSecondFunction) "exp" else "ln"
                    CalculatorButton(
                        text = lnLabel,
                        type = ButtonType.FUNCTION,
                        fontSize = 14.sp,
                        hapticsEnabled = hapticsEnabled,
                        modifier = Modifier.weight(1f).height(44.dp),
                        onClick = { onFunction(lnFunc) }
                    )

                    val logLabel = if (isSecondFunction) "10ˣ" else "log"
                    val logFunc = if (isSecondFunction) "10^" else "log"
                    CalculatorButton(
                        text = logLabel,
                        type = ButtonType.FUNCTION,
                        fontSize = 14.sp,
                        hapticsEnabled = hapticsEnabled,
                        modifier = Modifier.weight(1f).height(44.dp),
                        onClick = {
                            if (isSecondFunction) onOperator("10^") else onFunction("log")
                        }
                    )

                    CalculatorButton(
                        text = "log₂",
                        type = ButtonType.FUNCTION,
                        fontSize = 14.sp,
                        hapticsEnabled = hapticsEnabled,
                        modifier = Modifier.weight(1f).height(44.dp),
                        onClick = { onFunction("log2") }
                    )

                    CalculatorButton(
                        text = "x!",
                        type = ButtonType.FUNCTION,
                        fontSize = 14.sp,
                        hapticsEnabled = hapticsEnabled,
                        modifier = Modifier.weight(1f).height(44.dp),
                        onClick = { onOperator("!") }
                    )

                    CalculatorButton(
                        text = "1/x",
                        type = ButtonType.FUNCTION,
                        fontSize = 14.sp,
                        hapticsEnabled = hapticsEnabled,
                        modifier = Modifier.weight(1f).height(44.dp),
                        onClick = { onFunction("inv") }
                    )
                }

                // Row 3: √/x², ∛/x³, xʸ, |x|, constants (π, e, φ)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val sqrtLabel = if (isSecondFunction) "x²" else "√"
                    CalculatorButton(
                        text = sqrtLabel,
                        type = ButtonType.FUNCTION,
                        fontSize = 15.sp,
                        hapticsEnabled = hapticsEnabled,
                        modifier = Modifier.weight(1f).height(44.dp),
                        onClick = {
                            if (isSecondFunction) onOperator("^2") else onFunction("√")
                        }
                    )

                    val cbrtLabel = if (isSecondFunction) "x³" else "∛"
                    CalculatorButton(
                        text = cbrtLabel,
                        type = ButtonType.FUNCTION,
                        fontSize = 15.sp,
                        hapticsEnabled = hapticsEnabled,
                        modifier = Modifier.weight(1f).height(44.dp),
                        onClick = {
                            if (isSecondFunction) onOperator("^3") else onFunction("∛")
                        }
                    )

                    CalculatorButton(
                        text = "xʸ",
                        type = ButtonType.FUNCTION,
                        fontSize = 15.sp,
                        hapticsEnabled = hapticsEnabled,
                        modifier = Modifier.weight(1f).height(44.dp),
                        onClick = { onOperator("^") }
                    )

                    CalculatorButton(
                        text = "|x|",
                        type = ButtonType.FUNCTION,
                        fontSize = 14.sp,
                        hapticsEnabled = hapticsEnabled,
                        modifier = Modifier.weight(1f).height(44.dp),
                        onClick = { onFunction("abs") }
                    )

                    val constLabel = if (isSecondFunction) "φ" else "π"
                    CalculatorButton(
                        text = constLabel,
                        type = ButtonType.FUNCTION,
                        fontSize = 15.sp,
                        hapticsEnabled = hapticsEnabled,
                        modifier = Modifier.weight(1f).height(44.dp),
                        onClick = { onConstant(constLabel) }
                    )
                }
            }
        }

        // Standard 4x5 Keypad
        // Row 1: AC, DEL, %, ÷
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CalculatorButton(
                text = "AC",
                type = ButtonType.CLEAR,
                fontSize = 20.sp,
                hapticsEnabled = hapticsEnabled,
                testTag = "btn_clear",
                modifier = Modifier.weight(1f).height(58.dp),
                onClick = onClear
            )
            CalculatorButton(
                text = "DEL",
                type = ButtonType.CLEAR,
                fontSize = 18.sp,
                hapticsEnabled = hapticsEnabled,
                testTag = "btn_del",
                modifier = Modifier.weight(1f).height(58.dp),
                onClick = onDelete
            )
            CalculatorButton(
                text = "%",
                type = ButtonType.OPERATOR,
                fontSize = 22.sp,
                hapticsEnabled = hapticsEnabled,
                testTag = "btn_percent",
                modifier = Modifier.weight(1f).height(58.dp),
                onClick = onPercent
            )
            CalculatorButton(
                text = "÷",
                type = ButtonType.OPERATOR,
                fontSize = 24.sp,
                hapticsEnabled = hapticsEnabled,
                testTag = "btn_divide",
                modifier = Modifier.weight(1f).height(58.dp),
                onClick = { onOperator("÷") }
            )
        }

        // Row 2: 7, 8, 9, ×
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CalculatorButton(
                text = "7",
                type = ButtonType.NUMBER,
                hapticsEnabled = hapticsEnabled,
                modifier = Modifier.weight(1f).height(58.dp),
                onClick = { onDigit("7") }
            )
            CalculatorButton(
                text = "8",
                type = ButtonType.NUMBER,
                hapticsEnabled = hapticsEnabled,
                modifier = Modifier.weight(1f).height(58.dp),
                onClick = { onDigit("8") }
            )
            CalculatorButton(
                text = "9",
                type = ButtonType.NUMBER,
                hapticsEnabled = hapticsEnabled,
                modifier = Modifier.weight(1f).height(58.dp),
                onClick = { onDigit("9") }
            )
            CalculatorButton(
                text = "×",
                type = ButtonType.OPERATOR,
                fontSize = 24.sp,
                hapticsEnabled = hapticsEnabled,
                testTag = "btn_multiply",
                modifier = Modifier.weight(1f).height(58.dp),
                onClick = { onOperator("×") }
            )
        }

        // Row 3: 4, 5, 6, −
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CalculatorButton(
                text = "4",
                type = ButtonType.NUMBER,
                hapticsEnabled = hapticsEnabled,
                modifier = Modifier.weight(1f).height(58.dp),
                onClick = { onDigit("4") }
            )
            CalculatorButton(
                text = "5",
                type = ButtonType.NUMBER,
                hapticsEnabled = hapticsEnabled,
                modifier = Modifier.weight(1f).height(58.dp),
                onClick = { onDigit("5") }
            )
            CalculatorButton(
                text = "6",
                type = ButtonType.NUMBER,
                hapticsEnabled = hapticsEnabled,
                modifier = Modifier.weight(1f).height(58.dp),
                onClick = { onDigit("6") }
            )
            CalculatorButton(
                text = "−",
                type = ButtonType.OPERATOR,
                fontSize = 24.sp,
                hapticsEnabled = hapticsEnabled,
                testTag = "btn_minus",
                modifier = Modifier.weight(1f).height(58.dp),
                onClick = { onOperator("−") }
            )
        }

        // Row 4: 1, 2, 3, +
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CalculatorButton(
                text = "1",
                type = ButtonType.NUMBER,
                hapticsEnabled = hapticsEnabled,
                modifier = Modifier.weight(1f).height(58.dp),
                onClick = { onDigit("1") }
            )
            CalculatorButton(
                text = "2",
                type = ButtonType.NUMBER,
                hapticsEnabled = hapticsEnabled,
                modifier = Modifier.weight(1f).height(58.dp),
                onClick = { onDigit("2") }
            )
            CalculatorButton(
                text = "3",
                type = ButtonType.NUMBER,
                hapticsEnabled = hapticsEnabled,
                modifier = Modifier.weight(1f).height(58.dp),
                onClick = { onDigit("3") }
            )
            CalculatorButton(
                text = "+",
                type = ButtonType.OPERATOR,
                fontSize = 24.sp,
                hapticsEnabled = hapticsEnabled,
                testTag = "btn_plus",
                modifier = Modifier.weight(1f).height(58.dp),
                onClick = { onOperator("+") }
            )
        }

        // Row 5: ±, 0, ., =
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CalculatorButton(
                text = "±",
                type = ButtonType.NUMBER,
                fontSize = 20.sp,
                hapticsEnabled = hapticsEnabled,
                testTag = "btn_plus_minus",
                modifier = Modifier.weight(1f).height(58.dp),
                onClick = onPlusMinus
            )
            CalculatorButton(
                text = "0",
                type = ButtonType.NUMBER,
                hapticsEnabled = hapticsEnabled,
                modifier = Modifier.weight(1f).height(58.dp),
                onClick = { onDigit("0") }
            )
            CalculatorButton(
                text = ".",
                type = ButtonType.NUMBER,
                fontSize = 24.sp,
                hapticsEnabled = hapticsEnabled,
                testTag = "btn_dot",
                modifier = Modifier.weight(1f).height(58.dp),
                onClick = { onDigit(".") }
            )
            CalculatorButton(
                text = "=",
                type = ButtonType.EQUALS,
                fontSize = 26.sp,
                hapticsEnabled = hapticsEnabled,
                testTag = "btn_equals",
                modifier = Modifier.weight(1f).height(58.dp),
                onClick = onEquals
            )
        }

        // Additional row for Parentheses & e when scientific is collapsed
        if (!isScientificExpanded) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CalculatorButton(
                    text = "(",
                    type = ButtonType.FUNCTION,
                    fontSize = 18.sp,
                    hapticsEnabled = hapticsEnabled,
                    modifier = Modifier.weight(1f).height(44.dp),
                    onClick = { onParenthesis("(") }
                )
                CalculatorButton(
                    text = ")",
                    type = ButtonType.FUNCTION,
                    fontSize = 18.sp,
                    hapticsEnabled = hapticsEnabled,
                    modifier = Modifier.weight(1f).height(44.dp),
                    onClick = { onParenthesis(")") }
                )
                CalculatorButton(
                    text = "π",
                    type = ButtonType.FUNCTION,
                    fontSize = 18.sp,
                    hapticsEnabled = hapticsEnabled,
                    modifier = Modifier.weight(1f).height(44.dp),
                    onClick = { onConstant("π") }
                )
                CalculatorButton(
                    text = "√",
                    type = ButtonType.FUNCTION,
                    fontSize = 18.sp,
                    hapticsEnabled = hapticsEnabled,
                    modifier = Modifier.weight(1f).height(44.dp),
                    onClick = { onFunction("√") }
                )
            }
        } else {
            // In expanded mode, parentheses row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CalculatorButton(
                    text = "(",
                    type = ButtonType.FUNCTION,
                    fontSize = 18.sp,
                    hapticsEnabled = hapticsEnabled,
                    modifier = Modifier.weight(1f).height(42.dp),
                    onClick = { onParenthesis("(") }
                )
                CalculatorButton(
                    text = ")",
                    type = ButtonType.FUNCTION,
                    fontSize = 18.sp,
                    hapticsEnabled = hapticsEnabled,
                    modifier = Modifier.weight(1f).height(42.dp),
                    onClick = { onParenthesis(")") }
                )
                CalculatorButton(
                    text = "e",
                    type = ButtonType.FUNCTION,
                    fontSize = 18.sp,
                    hapticsEnabled = hapticsEnabled,
                    modifier = Modifier.weight(1f).height(42.dp),
                    onClick = { onConstant("e") }
                )
                CalculatorButton(
                    text = "π",
                    type = ButtonType.FUNCTION,
                    fontSize = 18.sp,
                    hapticsEnabled = hapticsEnabled,
                    modifier = Modifier.weight(1f).height(42.dp),
                    onClick = { onConstant("π") }
                )
            }
        }
    }
}

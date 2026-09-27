package com.example.engine

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.*

enum class AngleUnit {
    DEG, RAD
}

object CalculatorEngine {

    val PI_VAL = Math.PI
    val E_VAL = Math.E
    val PHI_VAL = (1.0 + sqrt(5.0)) / 2.0

    /**
     * Evaluates a mathematical expression string.
     * Returns a [CalculationResult] which is either Success or Error.
     */
    fun evaluate(expression: String, angleUnit: AngleUnit = AngleUnit.DEG): CalculationResult {
        if (expression.isBlank()) {
            return CalculationResult.Error("Empty expression")
        }

        return try {
            val normalized = normalizeExpression(expression)
            val tokens = tokenize(normalized)
            val rpn = infixToRpn(tokens)
            val result = evaluateRpn(rpn, angleUnit)
            if (result.isNaN()) {
                CalculationResult.Error("Undefined result")
            } else if (result.isInfinite()) {
                CalculationResult.Error("Overflow / Division by 0")
            } else {
                CalculationResult.Success(result, formatResult(result))
            }
        } catch (e: ArithmeticException) {
            CalculationResult.Error(e.message ?: "Arithmetic error")
        } catch (e: IllegalArgumentException) {
            CalculationResult.Error(e.message ?: "Invalid syntax")
        } catch (e: Exception) {
            CalculationResult.Error("Syntax error")
        }
    }

    /**
     * Silent evaluation for live preview while typing.
     * Returns formatted string if valid, or null if expression is partial/invalid.
     */
    fun evaluatePreview(expression: String, angleUnit: AngleUnit = AngleUnit.DEG): String? {
        if (expression.isBlank()) return null

        val trimmed = expression.trim()
        val lastChar = trimmed.last()
        // If ends with an operator, don't show preview
        if (lastChar in "+−-*/×÷^%(") return null

        // Try auto-balancing parentheses for preview
        var exprToTry = trimmed
        val openCount = exprToTry.count { it == '(' }
        val closeCount = exprToTry.count { it == ')' }
        if (openCount > closeCount) {
            exprToTry += ")".repeat(openCount - closeCount)
        }

        return when (val res = evaluate(exprToTry, angleUnit)) {
            is CalculationResult.Success -> {
                // If it's identical to the raw input number, no preview needed
                if (res.formatted == trimmed) null else res.formatted
            }
            is CalculationResult.Error -> null
        }
    }

    private fun normalizeExpression(expr: String): String {
        return expr
            .replace('×', '*')
            .replace('÷', '/')
            .replace('−', '-')
            .replace("π", "PI")
            .replace("φ", "PHI")
            .replace("√", "sqrt")
            .replace("∛", "cbrt")
            .replace(" ", "")
    }

    private sealed class Token {
        data class Number(val value: Double) : Token()
        data class Operator(val symbol: Char, val precedence: Int, val rightAssociative: Boolean = false) : Token()
        data class Function(val name: String) : Token()
        object LeftParen : Token()
        object RightParen : Token()
        object Factorial : Token()
        object Percent : Token()
        object UnaryMinus : Token()
    }

    private fun tokenize(expr: String): List<Token> {
        val tokens = mutableListOf<Token>()
        var i = 0
        var canBeUnary = true

        while (i < expr.length) {
            val c = expr[i]

            when {
                c.isDigit() || c == '.' -> {
                    val sb = StringBuilder()
                    while (i < expr.length && (expr[i].isDigit() || expr[i] == '.' || expr[i] == 'E' || expr[i] == 'e')) {
                        if ((expr[i] == 'E' || expr[i] == 'e') && i + 1 < expr.length && (expr[i + 1] == '+' || expr[i + 1] == '-')) {
                            sb.append(expr[i])
                            i++
                            sb.append(expr[i])
                            i++
                        } else {
                            sb.append(expr[i])
                            i++
                        }
                    }
                    val num = sb.toString().toDoubleOrNull() ?: throw IllegalArgumentException("Invalid number: $sb")
                    tokens.add(Token.Number(num))
                    canBeUnary = false
                    continue
                }

                c.isLetter() -> {
                    val sb = StringBuilder()
                    while (i < expr.length && expr[i].isLetter()) {
                        sb.append(expr[i])
                        i++
                    }
                    val word = sb.toString()
                    when (word) {
                        "PI" -> {
                            insertImplicitMultIfNeeded(tokens)
                            tokens.add(Token.Number(PI_VAL))
                            canBeUnary = false
                        }
                        "e" -> {
                            insertImplicitMultIfNeeded(tokens)
                            tokens.add(Token.Number(E_VAL))
                            canBeUnary = false
                        }
                        "PHI" -> {
                            insertImplicitMultIfNeeded(tokens)
                            tokens.add(Token.Number(PHI_VAL))
                            canBeUnary = false
                        }
                        else -> {
                            insertImplicitMultIfNeeded(tokens)
                            tokens.add(Token.Function(word))
                            canBeUnary = false
                        }
                    }
                    continue
                }

                c == '(' -> {
                    insertImplicitMultIfNeeded(tokens)
                    tokens.add(Token.LeftParen)
                    canBeUnary = true
                    i++
                }

                c == ')' -> {
                    tokens.add(Token.RightParen)
                    canBeUnary = false
                    i++
                }

                c == '!' -> {
                    tokens.add(Token.Factorial)
                    canBeUnary = false
                    i++
                }

                c == '%' -> {
                    tokens.add(Token.Percent)
                    canBeUnary = false
                    i++
                }

                c == '+' -> {
                    if (canBeUnary) {
                        // ignore unary plus
                    } else {
                        tokens.add(Token.Operator('+', 1))
                        canBeUnary = true
                    }
                    i++
                }

                c == '-' -> {
                    if (canBeUnary) {
                        tokens.add(Token.UnaryMinus)
                    } else {
                        tokens.add(Token.Operator('-', 1))
                        canBeUnary = true
                    }
                    i++
                }

                c == '*' -> {
                    tokens.add(Token.Operator('*', 2))
                    canBeUnary = true
                    i++
                }

                c == '/' -> {
                    tokens.add(Token.Operator('/', 2))
                    canBeUnary = true
                    i++
                }

                c == '^' -> {
                    tokens.add(Token.Operator('^', 4, rightAssociative = true))
                    canBeUnary = true
                    i++
                }

                else -> {
                    i++ // Skip unrecognized
                }
            }
        }

        return tokens
    }

    private fun insertImplicitMultIfNeeded(tokens: MutableList<Token>) {
        if (tokens.isNotEmpty()) {
            val last = tokens.last()
            if (last is Token.Number || last is Token.RightParen || last is Token.Factorial || last is Token.Percent) {
                tokens.add(Token.Operator('*', 2))
            }
        }
    }

    private fun infixToRpn(tokens: List<Token>): List<Token> {
        val output = mutableListOf<Token>()
        val stack = ArrayDeque<Token>()

        // Auto close unclosed parens
        var parenBalance = 0
        for (t in tokens) {
            if (t is Token.LeftParen) parenBalance++
            if (t is Token.RightParen) parenBalance--
        }
        val fullTokens = tokens.toMutableList()
        while (parenBalance > 0) {
            fullTokens.add(Token.RightParen)
            parenBalance--
        }

        for (token in fullTokens) {
            when (token) {
                is Token.Number -> output.add(token)
                is Token.Function -> stack.addLast(token)
                is Token.Factorial -> output.add(token)
                is Token.Percent -> output.add(token)
                is Token.UnaryMinus -> stack.addLast(token)

                is Token.Operator -> {
                    while (stack.isNotEmpty()) {
                        val top = stack.last()
                        if (top is Token.UnaryMinus) {
                            output.add(stack.removeLast())
                            continue
                        }
                        if (top is Token.Function) {
                            output.add(stack.removeLast())
                            continue
                        }
                        if (top is Token.Operator) {
                            if ((!token.rightAssociative && token.precedence <= top.precedence) ||
                                (token.rightAssociative && token.precedence < top.precedence)
                            ) {
                                output.add(stack.removeLast())
                                continue
                            }
                        }
                        break
                    }
                    stack.addLast(token)
                }

                is Token.LeftParen -> stack.addLast(token)

                is Token.RightParen -> {
                    while (stack.isNotEmpty() && stack.last() !is Token.LeftParen) {
                        output.add(stack.removeLast())
                    }
                    if (stack.isNotEmpty() && stack.last() is Token.LeftParen) {
                        stack.removeLast()
                    }
                    if (stack.isNotEmpty() && stack.last() is Token.Function) {
                        output.add(stack.removeLast())
                    }
                }
            }
        }

        while (stack.isNotEmpty()) {
            val t = stack.removeLast()
            if (t !is Token.LeftParen && t !is Token.RightParen) {
                output.add(t)
            }
        }

        return output
    }

    private fun evaluateRpn(rpn: List<Token>, angleUnit: AngleUnit): Double {
        val stack = ArrayDeque<Double>()

        for (token in rpn) {
            when (token) {
                is Token.Number -> stack.addLast(token.value)

                is Token.UnaryMinus -> {
                    if (stack.isEmpty()) throw IllegalArgumentException("Missing operand for negative")
                    val a = stack.removeLast()
                    stack.addLast(-a)
                }

                is Token.Factorial -> {
                    if (stack.isEmpty()) throw IllegalArgumentException("Missing operand for factorial")
                    val a = stack.removeLast()
                    stack.addLast(calcFactorial(a))
                }

                is Token.Percent -> {
                    if (stack.isEmpty()) throw IllegalArgumentException("Missing operand for %")
                    val a = stack.removeLast()
                    stack.addLast(a / 100.0)
                }

                is Token.Operator -> {
                    if (stack.size < 2) throw IllegalArgumentException("Missing operands for ${token.symbol}")
                    val b = stack.removeLast()
                    val a = stack.removeLast()
                    val res = when (token.symbol) {
                        '+' -> a + b
                        '-' -> a - b
                        '*' -> a * b
                        '/' -> {
                            if (b == 0.0) throw ArithmeticException("Cannot divide by 0")
                            a / b
                        }
                        '^' -> a.pow(b)
                        else -> throw IllegalArgumentException("Unknown operator ${token.symbol}")
                    }
                    stack.addLast(res)
                }

                is Token.Function -> {
                    if (stack.isEmpty()) throw IllegalArgumentException("Missing argument for ${token.name}")
                    val a = stack.removeLast()
                    val res = evaluateFunction(token.name, a, angleUnit)
                    stack.addLast(res)
                }

                else -> {}
            }
        }

        if (stack.size != 1) {
            throw IllegalArgumentException("Malformed expression")
        }

        return stack.removeLast()
    }

    private fun evaluateFunction(name: String, arg: Double, angleUnit: AngleUnit): Double {
        val rad = if (angleUnit == AngleUnit.DEG) Math.toRadians(arg) else arg

        return when (name.lowercase()) {
            "sin" -> {
                if (angleUnit == AngleUnit.DEG) {
                    val degNorm = (arg % 360.0 + 360.0) % 360.0
                    when {
                        degNorm == 0.0 || degNorm == 180.0 -> 0.0
                        degNorm == 30.0 || degNorm == 150.0 -> 0.5
                        degNorm == 90.0 -> 1.0
                        degNorm == 270.0 -> -1.0
                        degNorm == 210.0 || degNorm == 330.0 -> -0.5
                        else -> sin(rad)
                    }
                } else sin(rad)
            }
            "cos" -> {
                if (angleUnit == AngleUnit.DEG) {
                    val degNorm = (arg % 360.0 + 360.0) % 360.0
                    when {
                        degNorm == 90.0 || degNorm == 270.0 -> 0.0
                        degNorm == 0.0 -> 1.0
                        degNorm == 180.0 -> -1.0
                        degNorm == 60.0 || degNorm == 300.0 -> 0.5
                        degNorm == 120.0 || degNorm == 240.0 -> -0.5
                        else -> cos(rad)
                    }
                } else cos(rad)
            }
            "tan" -> {
                if (angleUnit == AngleUnit.DEG) {
                    val degNorm = (arg % 180.0 + 180.0) % 180.0
                    if (degNorm == 90.0) throw ArithmeticException("tan(90°) is undefined")
                    if (degNorm == 0.0) 0.0
                    else if (degNorm == 45.0) 1.0
                    else if (degNorm == 135.0) -1.0
                    else tan(rad)
                } else {
                    tan(rad)
                }
            }
            "asin", "arcsin" -> {
                if (arg < -1.0 || arg > 1.0) throw ArithmeticException("Domain error: asin argument must be in [-1, 1]")
                val resRad = asin(arg)
                if (angleUnit == AngleUnit.DEG) Math.toDegrees(resRad) else resRad
            }
            "acos", "arccos" -> {
                if (arg < -1.0 || arg > 1.0) throw ArithmeticException("Domain error: acos argument must be in [-1, 1]")
                val resRad = acos(arg)
                if (angleUnit == AngleUnit.DEG) Math.toDegrees(resRad) else resRad
            }
            "atan", "arctan" -> {
                val resRad = atan(arg)
                if (angleUnit == AngleUnit.DEG) Math.toDegrees(resRad) else resRad
            }
            "sinh" -> sinh(arg)
            "cosh" -> cosh(arg)
            "tanh" -> tanh(arg)
            "asinh" -> ln(arg + sqrt(arg * arg + 1.0))
            "acosh" -> {
                if (arg < 1.0) throw ArithmeticException("Domain error: acosh(x) for x >= 1")
                ln(arg + sqrt(arg * arg - 1.0))
            }
            "atanh" -> {
                if (arg <= -1.0 || arg >= 1.0) throw ArithmeticException("Domain error: atanh(x) for -1 < x < 1")
                0.5 * ln((1.0 + arg) / (1.0 - arg))
            }
            "ln" -> {
                if (arg <= 0.0) throw ArithmeticException("Domain error: ln(x) for x > 0")
                ln(arg)
            }
            "log", "log10" -> {
                if (arg <= 0.0) throw ArithmeticException("Domain error: log(x) for x > 0")
                log10(arg)
            }
            "log2" -> {
                if (arg <= 0.0) throw ArithmeticException("Domain error: log2(x) for x > 0")
                ln(arg) / ln(2.0)
            }
            "sqrt" -> {
                if (arg < 0.0) throw ArithmeticException("Domain error: sqrt of negative")
                sqrt(arg)
            }
            "cbrt" -> cbrt(arg)
            "abs" -> abs(arg)
            "exp" -> exp(arg)
            "inv" -> {
                if (arg == 0.0) throw ArithmeticException("Cannot divide by 0")
                1.0 / arg
            }
            "sqr" -> arg * arg
            "cube" -> arg * arg * arg
            else -> throw IllegalArgumentException("Unknown function: $name")
        }
    }

    private fun calcFactorial(n: Double): Double {
        if (n < 0) throw ArithmeticException("Factorial of negative number")
        if (n > 170) throw ArithmeticException("Factorial overflow")
        val floorVal = floor(n)
        if (n == floorVal) {
            val intVal = n.toLong()
            var acc = 1.0
            for (k in 2..intVal) {
                acc *= k
            }
            return acc
        }
        // Lanczos Gamma approximation for non-integers
        return gammaLanczos(n + 1.0)
    }

    private fun gammaLanczos(z: Double): Double {
        val p = doubleArrayOf(
            676.5203681218851, -1259.1392167224028,
            771.32342877765313, -176.61502916214059,
            12.507343278686905, -0.13857109526572012,
            9.9843695780195716e-6, 1.5056327351493116e-7
        )
        val g = 7
        if (z < 0.5) return Math.PI / (sin(Math.PI * z) * gammaLanczos(1.0 - z))
        var zMod = z - 1.0
        var x = 0.99999999999980993
        for (i in p.indices) {
            x += p[i] / (zMod + i + 1)
        }
        val t = zMod + g + 0.5
        return sqrt(2.0 * Math.PI) * t.pow(zMod + 0.5) * exp(-t) * x
    }

    fun formatResult(value: Double): String {
        if (value.isNaN()) return "NaN"
        if (value.isInfinite()) return if (value > 0) "Infinity" else "-Infinity"

        // Handle exact 0
        if (abs(value) < 1e-15) return "0"

        val absVal = abs(value)
        // Check if scientific notation is needed
        if (absVal >= 1e12 || absVal < 1e-9) {
            val df = DecimalFormat("0.######E0", DecimalFormatSymbols(Locale.US))
            return df.format(value).replace("E", "e")
        }

        // Clean precision formatting
        val bd = BigDecimal(value.toString(), MathContext(12, RoundingMode.HALF_UP))
            .stripTrailingZeros()

        return bd.toPlainString()
    }
}

sealed class CalculationResult {
    data class Success(val value: Double, val formatted: String) : CalculationResult()
    data class Error(val message: String) : CalculationResult()
}

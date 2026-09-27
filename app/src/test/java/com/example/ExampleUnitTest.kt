package com.example

import com.example.engine.AngleUnit
import com.example.engine.CalculationResult
import com.example.engine.CalculatorEngine
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testBasicArithmetic() {
        val res = CalculatorEngine.evaluate("2 + 3 × 4")
        assertTrue(res is CalculationResult.Success)
        assertEquals("14", (res as CalculationResult.Success).formatted)
    }

    @Test
    fun testPrecedenceAndParentheses() {
        val res = CalculatorEngine.evaluate("(2 + 3) × 4")
        assertTrue(res is CalculationResult.Success)
        assertEquals("20", (res as CalculationResult.Success).formatted)
    }

    @Test
    fun testScientificFunctionsDeg() {
        val sin30 = CalculatorEngine.evaluate("sin(30)", AngleUnit.DEG)
        assertTrue(sin30 is CalculationResult.Success)
        assertEquals("0.5", (sin30 as CalculationResult.Success).formatted)

        val cos60 = CalculatorEngine.evaluate("cos(60)", AngleUnit.DEG)
        assertTrue(cos60 is CalculationResult.Success)
        assertEquals("0.5", (cos60 as CalculationResult.Success).formatted)

        val tan45 = CalculatorEngine.evaluate("tan(45)", AngleUnit.DEG)
        assertTrue(tan45 is CalculationResult.Success)
        assertEquals("1", (tan45 as CalculationResult.Success).formatted)
    }

    @Test
    fun testScientificFunctionsRad() {
        val sinPiOver2 = CalculatorEngine.evaluate("sin(π / 2)", AngleUnit.RAD)
        assertTrue(sinPiOver2 is CalculationResult.Success)
        assertEquals("1", (sinPiOver2 as CalculationResult.Success).formatted)
    }

    @Test
    fun testFactorialAndPowers() {
        val fact5 = CalculatorEngine.evaluate("5!")
        assertTrue(fact5 is CalculationResult.Success)
        assertEquals("120", (fact5 as CalculationResult.Success).formatted)

        val pow = CalculatorEngine.evaluate("2^3")
        assertTrue(pow is CalculationResult.Success)
        assertEquals("8", (pow as CalculationResult.Success).formatted)
    }

    @Test
    fun testRootsAndConstants() {
        val sqrt = CalculatorEngine.evaluate("√(16)")
        assertTrue(sqrt is CalculationResult.Success)
        assertEquals("4", (sqrt as CalculationResult.Success).formatted)

        val cbrt = CalculatorEngine.evaluate("∛(27)")
        assertTrue(cbrt is CalculationResult.Success)
        assertEquals("3", (cbrt as CalculationResult.Success).formatted)
    }

    @Test
    fun testImplicitMultiplication() {
        val res = CalculatorEngine.evaluate("2(3 + 4)")
        assertTrue(res is CalculationResult.Success)
        assertEquals("14", (res as CalculationResult.Success).formatted)

        val res2 = CalculatorEngine.evaluate("3π")
        assertTrue(res2 is CalculationResult.Success)
        assertTrue((res2 as CalculationResult.Success).value > 9.42 && res2.value < 9.43)
    }

    @Test
    fun testDivisionByZero() {
        val res = CalculatorEngine.evaluate("10 ÷ 0")
        assertTrue(res is CalculationResult.Error)
    }
}

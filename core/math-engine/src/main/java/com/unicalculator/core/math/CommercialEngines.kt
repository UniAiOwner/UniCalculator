package com.unicalculator.core.math

import com.unicalculator.core.model.LoanEmiResult
import com.unicalculator.core.model.MarginMarkupResult
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.util.ArrayDeque

object ShuntingYardEvaluator {
    private val HUNDRED = BigDecimal("100")

    fun evaluate(expression: String): BigDecimal {
        val sanitized = expression
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace(" ", "")

        if (sanitized.isEmpty()) return BigDecimal.ZERO

        val expanded = preprocessPercentages(sanitized)
        val tokens = tokenize(expanded)
        val rpn = toRPN(tokens)
        return evaluateRPN(rpn)
    }

    /**
     * Preprocesses commercial percentage syntax:
     * - Commercial Cumulative Running Total: A + B - C% => ((A + B) - ((A + B) * C / 100))
     *   e.g. 900 + 100 - 10% => ((900+100)-((900+100)*10/100)) = 900
     * - A * B% => A * (B / 100)
     * - A / B% => A / (B / 100)
     * - Standalone B% => (B / 100)
     */
    fun preprocessPercentages(expr: String): String {
        if (!expr.contains("%")) return expr

        var current = expr

        while (current.contains("%")) {
            val pctIndex = current.indexOf('%')

            // Extract the number immediately preceding '%'
            var numStart = pctIndex - 1
            while (numStart >= 0 && (current[numStart].isDigit() || current[numStart] == '.')) {
                numStart--
            }
            numStart++

            val pctNum = current.substring(numStart, pctIndex)
            if (pctNum.isEmpty()) {
                current = current.removeRange(pctIndex, pctIndex + 1)
                continue
            }

            val opIndex = numStart - 1
            if (opIndex >= 0 && (current[opIndex] == '+' || current[opIndex] == '-')) {
                val op = current[opIndex]
                var baseStart = opIndex - 1
                var parenDepth = 0
                while (baseStart >= 0) {
                    val c = current[baseStart]
                    if (c == ')') parenDepth++
                    else if (c == '(') {
                        if (parenDepth == 0) {
                            baseStart++
                            break
                        }
                        parenDepth--
                    }
                    baseStart--
                }
                if (baseStart < 0) baseStart = 0

                val base = current.substring(baseStart, opIndex).trim()
                if (base.isNotEmpty()) {
                    val replacement = "(($base)$op(($base)*$pctNum/100))"
                    current = current.substring(0, baseStart) + replacement + current.substring(pctIndex + 1)
                    continue
                }
            } else if (opIndex >= 0 && (current[opIndex] == '*' || current[opIndex] == '/')) {
                val replacement = "($pctNum/100)"
                current = current.substring(0, numStart) + replacement + current.substring(pctIndex + 1)
                continue
            }

            // Standalone %
            val replacement = "($pctNum/100)"
            current = current.substring(0, numStart) + replacement + current.substring(pctIndex + 1)
        }

        return current
    }

    private fun tokenize(expr: String): List<String> {
        val tokens = mutableListOf<String>()
        val currentNum = StringBuilder()

        var i = 0
        while (i < expr.length) {
            val c = expr[i]
            if (c.isDigit() || c == '.') {
                currentNum.append(c)
            } else {
                if (currentNum.isNotEmpty()) {
                    tokens.add(currentNum.toString())
                    currentNum.clear()
                }
                if (c == '-' && (tokens.isEmpty() || tokens.last() in listOf("+", "-", "*", "/", "("))) {
                    currentNum.append(c)
                } else {
                    tokens.add(c.toString())
                }
            }
            i++
        }
        if (currentNum.isNotEmpty()) {
            tokens.add(currentNum.toString())
        }
        return tokens
    }

    private fun precedence(op: String): Int = when (op) {
        "+", "-" -> 1
        "*", "/" -> 2
        "%" -> 3
        else -> 0
    }

    private fun toRPN(tokens: List<String>): List<String> {
        val output = mutableListOf<String>()
        val ops = ArrayDeque<String>()

        for (token in tokens) {
            if (token.toBigDecimalOrNull() != null) {
                output.add(token)
            } else if (token == "(") {
                ops.push(token)
            } else if (token == ")") {
                while (ops.isNotEmpty() && ops.peek() != "(") {
                    output.add(ops.pop())
                }
                if (ops.isNotEmpty() && ops.peek() == "(") {
                    ops.pop()
                }
            } else {
                while (ops.isNotEmpty() && precedence(ops.peek() ?: "") >= precedence(token)) {
                    ops.pop()?.let { output.add(it) }
                }
                ops.push(token)
            }
        }
        while (ops.isNotEmpty()) {
            output.add(ops.pop())
        }
        return output
    }

    private fun evaluateRPN(rpn: List<String>): BigDecimal {
        val stack = ArrayDeque<BigDecimal>()

        for (token in rpn) {
            val num = token.toBigDecimalOrNull()
            if (num != null) {
                stack.push(num)
            } else if (token == "%") {
                if (stack.isNotEmpty()) {
                    val a = stack.pop()
                    stack.push(a.divide(HUNDRED, 10, RoundingMode.HALF_EVEN))
                }
            } else if (stack.size >= 2) {
                val b = stack.pop()
                val a = stack.pop()
                val res = when (token) {
                    "+" -> a.add(b)
                    "-" -> a.subtract(b)
                    "*" -> a.multiply(b)
                    "/" -> {
                        if (b.compareTo(BigDecimal.ZERO) == 0) {
                            throw ArithmeticException("Cannot divide by zero")
                        } else {
                            a.divide(b, 10, RoundingMode.HALF_EVEN)
                        }
                    }
                    else -> BigDecimal.ZERO
                }
                stack.push(res)
            }
        }
        return if (stack.isNotEmpty()) stack.pop().setScale(2, RoundingMode.HALF_EVEN) else BigDecimal.ZERO
    }
}

object CommercialCalculatorEngine {
    private val HUNDRED = BigDecimal("100")
    private val TWELVE = BigDecimal("12")

    fun calculateMarginMarkup(costPrice: BigDecimal, sellingPrice: BigDecimal): MarginMarkupResult {
        val grossProfit = sellingPrice.subtract(costPrice)
        val margin = if (sellingPrice.compareTo(BigDecimal.ZERO) != 0) {
            grossProfit.multiply(HUNDRED).divide(sellingPrice, 2, RoundingMode.HALF_EVEN)
        } else BigDecimal.ZERO

        val markup = if (costPrice.compareTo(BigDecimal.ZERO) != 0) {
            grossProfit.multiply(HUNDRED).divide(costPrice, 2, RoundingMode.HALF_EVEN)
        } else BigDecimal.ZERO

        return MarginMarkupResult(
            costPrice = costPrice,
            sellingPrice = sellingPrice,
            grossProfit = grossProfit,
            profitMarginPercent = margin,
            markupPercent = markup
        )
    }

    /**
     * Casio MU (Mark-Up) Key Parity: Calculate Target Selling Price from Cost Price and Desired Margin %
     * Target SP = CP / (1 - Margin% / 100)
     */
    fun calculateTargetSellingPrice(costPrice: BigDecimal, desiredMarginPercent: BigDecimal): BigDecimal {
        if (costPrice.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO
        val marginFraction = desiredMarginPercent.divide(HUNDRED, 10, RoundingMode.HALF_EVEN)
        val denominator = BigDecimal.ONE.subtract(marginFraction)
        if (denominator.compareTo(BigDecimal.ZERO) <= 0) return costPrice
        return costPrice.divide(denominator, 2, RoundingMode.HALF_UP)
    }

    /**
     * Successive / Double Discount Solver (e.g. 50% + 20% festive sale)
     * Returns Pair(FinalPrice, TotalDiscountAmount)
     */
    fun calculateSuccessiveDiscount(originalPrice: BigDecimal, discounts: List<BigDecimal>): Pair<BigDecimal, BigDecimal> {
        if (originalPrice.compareTo(BigDecimal.ZERO) <= 0 || discounts.isEmpty()) {
            return Pair(originalPrice, BigDecimal.ZERO)
        }
        var runningPrice = originalPrice
        for (d in discounts) {
            val discountFraction = d.divide(HUNDRED, 10, RoundingMode.HALF_EVEN)
            val factor = BigDecimal.ONE.subtract(discountFraction).coerceAtLeast(BigDecimal.ZERO)
            runningPrice = runningPrice.multiply(factor)
        }
        val finalPrice = runningPrice.setScale(2, RoundingMode.HALF_UP)
        val savings = originalPrice.subtract(finalPrice)
        return Pair(finalPrice, savings)
    }

    fun calculateLoanEmi(principal: BigDecimal, annualInterestRate: BigDecimal, tenureMonths: Int): LoanEmiResult {
        if (principal.compareTo(BigDecimal.ZERO) <= 0 || tenureMonths <= 0) {
            return LoanEmiResult(principal, annualInterestRate, tenureMonths, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO)
        }

        val monthlyRate = annualInterestRate.divide(HUNDRED, 10, RoundingMode.HALF_EVEN)
            .divide(TWELVE, 10, RoundingMode.HALF_EVEN)

        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            val emi = principal.divide(BigDecimal(tenureMonths), 2, RoundingMode.HALF_EVEN)
            return LoanEmiResult(principal, annualInterestRate, tenureMonths, emi, BigDecimal.ZERO, principal)
        }

        val onePlusRPowerN = (BigDecimal.ONE.add(monthlyRate)).pow(tenureMonths, MathContext.DECIMAL64)
        val numerator = principal.multiply(monthlyRate).multiply(onePlusRPowerN)
        val denominator = onePlusRPowerN.subtract(BigDecimal.ONE)
        val emi = numerator.divide(denominator, 2, RoundingMode.HALF_EVEN)

        val totalPayment = emi.multiply(BigDecimal(tenureMonths))
        val totalInterest = totalPayment.subtract(principal)

        return LoanEmiResult(
            principalAmount = principal,
            annualInterestRate = annualInterestRate,
            tenureMonths = tenureMonths,
            monthlyEmi = emi,
            totalInterest = totalInterest,
            totalPayment = totalPayment
        )
    }
}

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
            .replace(" ", "")

        if (sanitized.isEmpty()) return BigDecimal.ZERO

        val tokens = tokenize(sanitized)
        val rpn = toRPN(tokens)
        return evaluateRPN(rpn)
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
                            BigDecimal.ZERO
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

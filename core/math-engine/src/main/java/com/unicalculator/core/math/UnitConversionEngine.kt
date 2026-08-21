package com.unicalculator.core.math

import java.math.BigDecimal
import java.math.RoundingMode

object UnitConversionEngine {

    // --- 1. LENGTH CONVERSIONS (Base: Meter) ---
    private val LENGTH_TO_METERS = mapOf(
        "Meter (m)" to BigDecimal("1.0"),
        "Kilometer (km)" to BigDecimal("1000.0"),
        "Centimeter (cm)" to BigDecimal("0.01"),
        "Millimeter (mm)" to BigDecimal("0.001"),
        "Foot (ft)" to BigDecimal("0.3048"),
        "Inch (in)" to BigDecimal("0.0254"),
        "Yard (yd)" to BigDecimal("0.9144"),
        "Mile (mi)" to BigDecimal("1609.344")
    )

    fun convertLength(value: BigDecimal, fromUnit: String, toUnit: String): BigDecimal {
        val fromFactor = LENGTH_TO_METERS[fromUnit] ?: BigDecimal.ONE
        val toFactor = LENGTH_TO_METERS[toUnit] ?: BigDecimal.ONE
        val inMeters = value.multiply(fromFactor)
        return inMeters.divide(toFactor, 6, RoundingMode.HALF_EVEN).stripTrailingZeros()
    }

    // --- 2. MASS & WEIGHT CONVERSIONS (Base: Kilogram) ---
    private val MASS_TO_KG = mapOf(
        "Kilogram (kg)" to BigDecimal("1.0"),
        "Gram (g)" to BigDecimal("0.001"),
        "Milligram (mg)" to BigDecimal("0.000001"),
        "Quintal (q)" to BigDecimal("100.0"), // 100 kg (Mandi standard)
        "Maund / Mann" to BigDecimal("40.0"), // 40 kg (Mandi standard)
        "Tonne (t)" to BigDecimal("1000.0"),
        "Tola (Vedic 11.66g)" to BigDecimal("0.0116638"), // 11.6638 grams
        "Metric Tola (10g)" to BigDecimal("0.010"), // 10.0 grams
        "Sovereign / Pavan" to BigDecimal("0.008"), // 8.0 grams
        "Masha" to BigDecimal("0.000972"), // 0.972 grams
        "Ratti" to BigDecimal("0.0001215"), // 0.1215 grams
        "Carat (ct)" to BigDecimal("0.0002"), // 200 mg
        "Pound (lb)" to BigDecimal("0.45359237"),
        "Ounce (oz)" to BigDecimal("0.028349523125")
    )

    fun convertMass(value: BigDecimal, fromUnit: String, toUnit: String): BigDecimal {
        val fromFactor = MASS_TO_KG[fromUnit] ?: BigDecimal.ONE
        val toFactor = MASS_TO_KG[toUnit] ?: BigDecimal.ONE
        val inKg = value.multiply(fromFactor)
        return inKg.divide(toFactor, 6, RoundingMode.HALF_EVEN).stripTrailingZeros()
    }

    // --- 3. AREA CONVERSIONS (Base: Square Meter) ---
    private val AREA_TO_SQ_METERS = mapOf(
        "Square Meter (sq m)" to BigDecimal("1.0"),
        "Square Foot (sq ft)" to BigDecimal("0.09290304"),
        "Square Yard (sq yd)" to BigDecimal("0.83612736"),
        "Acre" to BigDecimal("4046.8564224"),
        "Hectare" to BigDecimal("10000.0"),
        "Bigha (Standard)" to BigDecimal("2529.285264"), // ~27,000 sq ft
        "Guntha" to BigDecimal("101.17141"), // ~1,089 sq ft
        "Cent" to BigDecimal("40.468564224"), // 435.6 sq ft
        "Ground" to BigDecimal("222.967296"), // 2,400 sq ft
        "Marla" to BigDecimal("25.29285264"), // 272.25 sq ft
        "Kanal" to BigDecimal("505.8570528"), // 5,445 sq ft
        "Biswa / Katha" to BigDecimal("125.419069") // 1,350 sq ft
    )

    fun convertArea(value: BigDecimal, fromUnit: String, toUnit: String): BigDecimal {
        val fromFactor = AREA_TO_SQ_METERS[fromUnit] ?: BigDecimal.ONE
        val toFactor = AREA_TO_SQ_METERS[toUnit] ?: BigDecimal.ONE
        val inSqM = value.multiply(fromFactor)
        return inSqM.divide(toFactor, 6, RoundingMode.HALF_EVEN).stripTrailingZeros()
    }

    // --- 4. VOLUME CONVERSIONS (Base: Liter) ---
    private val VOLUME_TO_LITERS = mapOf(
        "Liter (L)" to BigDecimal("1.0"),
        "Milliliter (mL)" to BigDecimal("0.001"),
        "Gallon (US gal)" to BigDecimal("3.785411784"),
        "Cubic Meter (m³)" to BigDecimal("1000.0"),
        "Cubic Foot (ft³)" to BigDecimal("28.316846592")
    )

    fun convertVolume(value: BigDecimal, fromUnit: String, toUnit: String): BigDecimal {
        val fromFactor = VOLUME_TO_LITERS[fromUnit] ?: BigDecimal.ONE
        val toFactor = VOLUME_TO_LITERS[toUnit] ?: BigDecimal.ONE
        val inLiters = value.multiply(fromFactor)
        return inLiters.divide(toFactor, 6, RoundingMode.HALF_EVEN).stripTrailingZeros()
    }

    // --- 5. TEMPERATURE CONVERSIONS ---
    fun convertTemperature(value: BigDecimal, fromUnit: String, toUnit: String): BigDecimal {
        if (fromUnit == toUnit) return value
        val doubleVal = value.toDouble()
        val inCelsius = when (fromUnit) {
            "Celsius (°C)" -> doubleVal
            "Fahrenheit (°F)" -> (doubleVal - 32.0) * (5.0 / 9.0)
            "Kelvin (K)" -> doubleVal - 273.15
            else -> doubleVal
        }

        val result = when (toUnit) {
            "Celsius (°C)" -> inCelsius
            "Fahrenheit (°F)" -> (inCelsius * 9.0 / 5.0) + 32.0
            "Kelvin (K)" -> inCelsius + 273.15
            else -> inCelsius
        }
        return BigDecimal(result).setScale(2, RoundingMode.HALF_EVEN).stripTrailingZeros()
    }

    // --- 6. SPEED CONVERSIONS (Base: km/h) ---
    private val SPEED_TO_KMH = mapOf(
        "Kilometer/hour (km/h)" to BigDecimal("1.0"),
        "Mile/hour (mph)" to BigDecimal("1.609344"),
        "Meter/second (m/s)" to BigDecimal("3.6"),
        "Knot (kn)" to BigDecimal("1.852")
    )

    fun convertSpeed(value: BigDecimal, fromUnit: String, toUnit: String): BigDecimal {
        val fromFactor = SPEED_TO_KMH[fromUnit] ?: BigDecimal.ONE
        val toFactor = SPEED_TO_KMH[toUnit] ?: BigDecimal.ONE
        val inKmh = value.multiply(fromFactor)
        return inKmh.divide(toFactor, 4, RoundingMode.HALF_EVEN).stripTrailingZeros()
    }

    // --- 7. DATA STORAGE CONVERSIONS (Base: Byte) ---
    private val DATA_TO_BYTES = mapOf(
        "Byte (B)" to BigDecimal("1.0"),
        "Kilobyte (KB)" to BigDecimal("1024.0"),
        "Megabyte (MB)" to BigDecimal("1048576.0"),
        "Gigabyte (GB)" to BigDecimal("1073741824.0"),
        "Terabyte (TB)" to BigDecimal("1099511627776.0"),
        "Petabyte (PB)" to BigDecimal("1125899906842624.0")
    )

    fun convertData(value: BigDecimal, fromUnit: String, toUnit: String): BigDecimal {
        val fromFactor = DATA_TO_BYTES[fromUnit] ?: BigDecimal.ONE
        val toFactor = DATA_TO_BYTES[toUnit] ?: BigDecimal.ONE
        val inBytes = value.multiply(fromFactor)
        return inBytes.divide(toFactor, 6, RoundingMode.HALF_EVEN).stripTrailingZeros()
    }

    // --- 8. TIME CONVERSIONS (Base: Seconds) ---
    private val TIME_TO_SECONDS = mapOf(
        "Seconds (s)" to BigDecimal("1.0"),
        "Minutes (min)" to BigDecimal("60.0"),
        "Hours (hr)" to BigDecimal("3600.0"),
        "Days (d)" to BigDecimal("86400.0"),
        "Weeks (wk)" to BigDecimal("604800.0"),
        "Months (~30d)" to BigDecimal("2592000.0"),
        "Years (365d)" to BigDecimal("31536000.0")
    )

    fun convertTime(value: BigDecimal, fromUnit: String, toUnit: String): BigDecimal {
        val fromFactor = TIME_TO_SECONDS[fromUnit] ?: BigDecimal.ONE
        val toFactor = TIME_TO_SECONDS[toUnit] ?: BigDecimal.ONE
        val inSec = value.multiply(fromFactor)
        return inSec.divide(toFactor, 6, RoundingMode.HALF_EVEN).stripTrailingZeros()
    }

    // --- 9. CURRENCY CONVERSIONS (Base: Indian Rupee INR) ---
    val DEFAULT_CURRENCY_TO_INR = mapOf(
        "Indian Rupee (INR ₹)" to BigDecimal("1.0"),
        "US Dollar (USD $)" to BigDecimal("87.50"),
        "Euro (EUR €)" to BigDecimal("95.20"),
        "British Pound (GBP £)" to BigDecimal("111.40"),
        "UAE Dirham (AED)" to BigDecimal("23.82"),
        "Saudi Riyal (SAR)" to BigDecimal("23.30"),
        "Kuwaiti Dinar (KWD)" to BigDecimal("284.50"),
        "Qatari Riyal (QAR)" to BigDecimal("24.00"),
        "Omani Rial (OMR)" to BigDecimal("227.30"),
        "Canadian Dollar (CAD C$)" to BigDecimal("63.50"),
        "Australian Dollar (AUD A$)" to BigDecimal("56.80"),
        "Singapore Dollar (SGD S$)" to BigDecimal("66.20"),
        "Japanese Yen (JPY ¥)" to BigDecimal("0.58"),
        "Swiss Franc (CHF)" to BigDecimal("98.60"),
        "Chinese Yuan (CNY ¥)" to BigDecimal("12.10")
    )

    private val dynamicCurrencyRates = java.util.concurrent.ConcurrentHashMap<String, BigDecimal>(DEFAULT_CURRENCY_TO_INR)

    @Volatile
    var lastForexSyncTime: Long = 0L

    @Volatile
    var isLiveForexFeed: Boolean = false

    fun updateLiveRates(rates: Map<String, BigDecimal>) {
        dynamicCurrencyRates.putAll(rates)
        lastForexSyncTime = System.currentTimeMillis()
        isLiveForexFeed = true
    }

    fun getCurrencyRate(currency: String): BigDecimal {
        return dynamicCurrencyRates[currency] ?: DEFAULT_CURRENCY_TO_INR[currency] ?: BigDecimal.ONE
    }

    fun convertCurrency(value: BigDecimal, fromCurrency: String, toCurrency: String): BigDecimal {
        val fromFactor = getCurrencyRate(fromCurrency)
        val toFactor = getCurrencyRate(toCurrency)
        val inInr = value.multiply(fromFactor)
        return inInr.divide(toFactor, 4, RoundingMode.HALF_EVEN).stripTrailingZeros()
    }

    // --- 10. NUMERAL SYSTEM CONVERSIONS ---
    fun convertNumeral(valueStr: String, fromBase: Int, toBase: Int): String {
        return try {
            val decimal = valueStr.trim().toLong(fromBase)
            when (toBase) {
                2 -> java.lang.Long.toBinaryString(decimal)
                8 -> java.lang.Long.toOctalString(decimal)
                10 -> decimal.toString()
                16 -> java.lang.Long.toHexString(decimal).uppercase()
                else -> decimal.toString()
            }
        } catch (_: Exception) {
            "Invalid input"
        }
    }

    // --- 10. BMI CALCULATOR ---
    data class BmiResult(
        val bmiScore: BigDecimal,
        val category: String,
        val healthyWeightRangeKg: String
    )

    fun calculateBmi(weightKg: BigDecimal, heightCm: BigDecimal): BmiResult {
        if (weightKg <= BigDecimal.ZERO || heightCm <= BigDecimal.ZERO) {
            return BmiResult(BigDecimal.ZERO, "Enter valid weight & height", "N/A")
        }
        val heightM = heightCm.divide(BigDecimal("100"), 4, RoundingMode.HALF_EVEN)
        val heightSquared = heightM.multiply(heightM)
        val bmi = weightKg.divide(heightSquared, 2, RoundingMode.HALF_EVEN)

        val category = when {
            bmi < BigDecimal("18.5") -> "Underweight"
            bmi < BigDecimal("24.9") -> "Normal (Healthy Weight)"
            bmi < BigDecimal("29.9") -> "Overweight"
            else -> "Obese"
        }

        val minHealthy = BigDecimal("18.5").multiply(heightSquared).setScale(1, RoundingMode.HALF_EVEN)
        val maxHealthy = BigDecimal("24.9").multiply(heightSquared).setScale(1, RoundingMode.HALF_EVEN)

        return BmiResult(
            bmiScore = bmi,
            category = category,
            healthyWeightRangeKg = "$minHealthy kg – $maxHealthy kg"
        )
    }

    // --- 11. DISCOUNT SOLVER ---
    data class DiscountResult(
        val finalPrice: BigDecimal,
        val totalSavings: BigDecimal
    )

    fun calculateDiscount(originalPrice: BigDecimal, discountPercent: BigDecimal): DiscountResult {
        if (originalPrice <= BigDecimal.ZERO || discountPercent < BigDecimal.ZERO) {
            return DiscountResult(originalPrice, BigDecimal.ZERO)
        }
        val savings = originalPrice.multiply(discountPercent).divide(BigDecimal("100"), 2, RoundingMode.HALF_EVEN)
        val finalPrice = originalPrice.subtract(savings).max(BigDecimal.ZERO)
        return DiscountResult(finalPrice, savings)
    }
}

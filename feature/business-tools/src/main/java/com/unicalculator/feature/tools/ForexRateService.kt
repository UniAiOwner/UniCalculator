package com.unicalculator.feature.tools

import com.unicalculator.core.math.UnitConversionEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.math.BigDecimal
import java.math.RoundingMode
import java.net.HttpURLConnection
import java.net.URL

object ForexRateService {

    private const val API_ENDPOINT = "https://open.er-api.com/v6/latest/USD"
    private const val TIMEOUT_MS = 6000

    private val CURRENCY_MAPPINGS = mapOf(
        "Indian Rupee (INR ₹)" to "INR",
        "US Dollar (USD $)" to "USD",
        "Euro (EUR €)" to "EUR",
        "British Pound (GBP £)" to "GBP",
        "UAE Dirham (AED)" to "AED",
        "Saudi Riyal (SAR)" to "SAR",
        "Kuwaiti Dinar (KWD)" to "KWD",
        "Qatari Riyal (QAR)" to "QAR",
        "Omani Rial (OMR)" to "OMR",
        "Canadian Dollar (CAD C$)" to "CAD",
        "Australian Dollar (AUD A$)" to "AUD",
        "Singapore Dollar (SGD S$)" to "SGD",
        "Japanese Yen (JPY ¥)" to "JPY",
        "Swiss Franc (CHF)" to "CHF",
        "Chinese Yuan (CNY ¥)" to "CNY"
    )

    suspend fun fetchAndSyncLiveRates(): Result<Int> = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            val url = URL(API_ENDPOINT)
            connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = TIMEOUT_MS
                readTimeout = TIMEOUT_MS
                setRequestProperty("User-Agent", "UniCalculator-Android/1.0")
                setRequestProperty("Accept", "application/json")
            }

            val responseCode = connection.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return@withContext Result.failure(Exception("HTTP error $responseCode from Forex API"))
            }

            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = reader.use { it.readText() }
            val json = JSONObject(response)
            val ratesJson = json.optJSONObject("rates") ?: return@withContext Result.failure(Exception("Missing rates object"))

            val usdToInr = BigDecimal(ratesJson.optDouble("INR", 87.50).toString())
            val updatedRates = mutableMapOf<String, BigDecimal>()

            for ((fullName, code) in CURRENCY_MAPPINGS) {
                if (code == "INR") {
                    updatedRates[fullName] = BigDecimal.ONE
                } else if (code == "USD") {
                    updatedRates[fullName] = usdToInr
                } else {
                    val rateToUsd = ratesJson.optDouble(code, -1.0)
                    if (rateToUsd > 0.0) {
                        // 1 Foreign Unit in INR = USD_TO_INR / RATE_TO_USD
                        val foreignRate = BigDecimal(rateToUsd.toString())
                        val rateInInr = usdToInr.divide(foreignRate, 4, RoundingMode.HALF_EVEN)
                        updatedRates[fullName] = rateInInr
                    }
                }
            }

            UnitConversionEngine.updateLiveRates(updatedRates)
            Result.success(updatedRates.size)
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            connection?.disconnect()
        }
    }
}

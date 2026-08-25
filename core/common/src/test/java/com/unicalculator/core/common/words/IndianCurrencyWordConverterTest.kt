package com.unicalculator.core.common.words

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class IndianCurrencyWordConverterTest {

    @Test
    fun testPureMathematicalNumbersEnglish() {
        assertEquals("Zero", IndianCurrencyWordConverter.convert(BigDecimal.ZERO, WordsLanguage.ENGLISH, includeRupeesSuffix = false))
        assertEquals("One Hundred", IndianCurrencyWordConverter.convert(BigDecimal("100"), WordsLanguage.ENGLISH, includeRupeesSuffix = false))
        assertEquals("Twelve Thousand Three Hundred Forty Five", IndianCurrencyWordConverter.convert(BigDecimal("12345"), WordsLanguage.ENGLISH, includeRupeesSuffix = false))
        assertEquals("One Lakh Twenty Five Thousand", IndianCurrencyWordConverter.convert(BigDecimal("125000"), WordsLanguage.ENGLISH, includeRupeesSuffix = false))
        assertEquals("One Crore", IndianCurrencyWordConverter.convert(BigDecimal("10000000"), WordsLanguage.ENGLISH, includeRupeesSuffix = false))
        assertEquals("One Hundred Point Fifty", IndianCurrencyWordConverter.convert(BigDecimal("100.50"), WordsLanguage.ENGLISH, includeRupeesSuffix = false))
        assertEquals("Minus Five Hundred", IndianCurrencyWordConverter.convert(BigDecimal("-500"), WordsLanguage.ENGLISH, includeRupeesSuffix = false))
    }

    @Test
    fun testPureMathematicalNumbersHindi() {
        assertEquals("शून्य", IndianCurrencyWordConverter.convert(BigDecimal.ZERO, WordsLanguage.HINDI, includeRupeesSuffix = false))
        assertEquals("एक सौ", IndianCurrencyWordConverter.convert(BigDecimal("100"), WordsLanguage.HINDI, includeRupeesSuffix = false))
        assertEquals("बारह हज़ार तीन सौ पैंतालीस", IndianCurrencyWordConverter.convert(BigDecimal("12345"), WordsLanguage.HINDI, includeRupeesSuffix = false))
        assertEquals("एक लाख पच्चीस हज़ार", IndianCurrencyWordConverter.convert(BigDecimal("125000"), WordsLanguage.HINDI, includeRupeesSuffix = false))
        assertEquals("एक सौ दशमलव पचास", IndianCurrencyWordConverter.convert(BigDecimal("100.50"), WordsLanguage.HINDI, includeRupeesSuffix = false))
        assertEquals("ऋण पाँच सौ", IndianCurrencyWordConverter.convert(BigDecimal("-500"), WordsLanguage.HINDI, includeRupeesSuffix = false))
    }

    @Test
    fun testCurrencyModeEnglish() {
        assertEquals("Zero Rupees Only", IndianCurrencyWordConverter.convert(BigDecimal.ZERO, WordsLanguage.ENGLISH, includeRupeesSuffix = true))
        assertEquals("One Hundred Rupees Only", IndianCurrencyWordConverter.convert(BigDecimal("100"), WordsLanguage.ENGLISH, includeRupeesSuffix = true))
        assertEquals("One Hundred Rupees and Fifty Paise Only", IndianCurrencyWordConverter.convert(BigDecimal("100.50"), WordsLanguage.ENGLISH, includeRupeesSuffix = true))
        assertEquals("Fifty Paise Only", IndianCurrencyWordConverter.convert(BigDecimal("0.50"), WordsLanguage.ENGLISH, includeRupeesSuffix = true))
    }

    @Test
    fun testCurrencyModeHindi() {
        assertEquals("शून्य रुपये मात्र", IndianCurrencyWordConverter.convert(BigDecimal.ZERO, WordsLanguage.HINDI, includeRupeesSuffix = true))
        assertEquals("एक सौ रुपये मात्र", IndianCurrencyWordConverter.convert(BigDecimal("100"), WordsLanguage.HINDI, includeRupeesSuffix = true))
        assertEquals("एक सौ रुपये पचास पैसे मात्र", IndianCurrencyWordConverter.convert(BigDecimal("100.50"), WordsLanguage.HINDI, includeRupeesSuffix = true))
    }

    @Test
    fun testBothLanguageMode() {
        val result = IndianCurrencyWordConverter.convert(BigDecimal("100"), WordsLanguage.BOTH, includeRupeesSuffix = false)
        assertEquals("One Hundred • एक सौ", result)

        val currResult = IndianCurrencyWordConverter.convert(BigDecimal("100"), WordsLanguage.BOTH, includeRupeesSuffix = true)
        assertEquals("One Hundred Rupees Only • एक सौ रुपये मात्र", currResult)
    }

    @Test
    fun testOffLanguageMode() {
        val result = IndianCurrencyWordConverter.convert(BigDecimal("100"), WordsLanguage.OFF, includeRupeesSuffix = false)
        assertEquals("", result)
    }
}

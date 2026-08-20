package com.unicalculator.core.common.words

import java.math.BigDecimal

object IndianCurrencyWordConverter {
    private val englishUnits = arrayOf(
        "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
        "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
        "Seventeen", "Eighteen", "Nineteen"
    )
    private val englishTens = arrayOf(
        "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
    )

    private val hindiNumbers = mapOf(
        1 to "एक", 2 to "दो", 3 to "तीन", 4 to "चार", 5 to "पाँच",
        6 to "छह", 7 to "सात", 8 to "आठ", 9 to "नौ", 10 to "दस",
        11 to "ग्यारह", 12 to "बारह", 13 to "तेरह", 14 to "चौदह", 15 to "पंद्रह",
        16 to "सोलह", 17 to "सत्रह", 18 to "अठारह", 19 to "उन्नीस", 20 to "बीस",
        21 to "इक्कीस", 22 to "बाईस", 23 to "तेईस", 24 to "चौबीस", 25 to "पच्चीस",
        26 to "छब्बीस", 27 to "सत्ताईस", 28 to "अट्ठाईस", 29 to "उनतीस", 30 to "तीस",
        31 to "इकतीस", 32 to "बत्तीस", 33 to "तैंतीस", 34 to "चौंतीस", 35 to "पैंतीस",
        36 to "छत्तीस", 37 to "सैंतीस", 38 to "अड़तीस", 39 to "उनतालीस", 40 to "चालीस",
        41 to "इकतालीस", 42 to "बयालीस", 43 to "तैंतालीस", 44 to "चवालीस", 45 to "पैंतालीस",
        46 to "छियालीस", 47 to "सैंतालीस", 48 to "अड़तालीस", 49 to "उनचास", 50 to "पचास",
        51 to "इक्यावन", 52 to "बावन", 53 to "तिरेपन", 54 to "चौवन", 55 to "पचपन",
        56 to "छप्पन", 57 to "सत्तावन", 58 to "अट्ठावन", 59 to "उनसठ", 60 to "साठ",
        61 to "इकसठ", 62 to "बासठ", 63 to "तिरेसठ", 64 to "चौंसठ", 65 to "पैंसठ",
        66 to "छियासठ", 67 to "सड़सठ", 68 to "अड़सठ", 69 to "उनहत्तर", 70 to "सत्तर",
        71 to "इकहत्तर", 72 to "बहत्तर", 73 to "तिहत्तर", 74 to "चौहत्तर", 75 to "पचहत्तर",
        76 to "छिहत्तर", 77 to "सतहत्तर", 78 to "अठहत्तर", 79 to "उनासी", 80 to "अस्सी",
        81 to "इक्यासी", 82 to "बयासी", 83 to "तिरासी", 84 to "चौरासी", 85 to "पचासी",
        86 to "छियासी", 87 to "सतासी", 88 to "अठासी", 89 to "नवासी", 90 to "नब्बे",
        91 to "इक्यानवे", 92 to "बानवे", 93 to "तिरानवे", 94 to "चौरानवे", 95 to "पंचानवे",
        96 to "छियानवे", 97 to "सत्तानवे", 98 to "अट्ठानवे", 99 to "निन्यानवे"
    )

    fun convertToWords(
        amount: BigDecimal,
        inHindi: Boolean = false,
        includeRupeesSuffix: Boolean = true
    ): String {
        val absAmount = amount.abs()
        val rupees = absAmount.setScale(0, java.math.RoundingMode.DOWN).toLong()
        val paise = absAmount.remainder(BigDecimal.ONE).multiply(BigDecimal("100")).setScale(0, java.math.RoundingMode.HALF_UP).toInt()

        if (rupees == 0L && paise == 0) {
            return if (inHindi) {
                if (includeRupeesSuffix) "शून्य रुपये मात्र" else "शून्य"
            } else {
                if (includeRupeesSuffix) "Zero Rupees Only" else "Zero"
            }
        }

        return if (inHindi) {
            val rupeesPart = if (rupees > 0) convertToHindiWords(rupees) else ""
            val paisePart = if (paise > 0) "${hindiNumbers[paise] ?: paise.toString()} पैसे" else ""
            val fullText = when {
                rupees > 0 && paise > 0 -> "$rupeesPart रुपये $paisePart"
                rupees > 0 -> "$rupeesPart रुपये"
                else -> paisePart
            }
            if (includeRupeesSuffix) "$fullText मात्र".trim() else fullText.trim()
        } else {
            val rupeesPart = if (rupees > 0) convertToEnglishWords(rupees) else ""
            val paisePart = if (paise > 0) "${convertEnglishLessThanThousand(paise)} Paise" else ""
            val fullText = when {
                rupees > 0 && paise > 0 -> "$rupeesPart Rupees and $paisePart"
                rupees > 0 -> "$rupeesPart Rupees"
                else -> paisePart
            }
            if (includeRupeesSuffix) "$fullText Only".trim() else fullText.trim()
        }
    }

    private fun convertToEnglishWords(amount: Long): String {
        var n = amount
        val sb = StringBuilder()

        if (n >= 100000000000L) { // Kharab (10^11)
            sb.append(convertEnglishLessThanThousand((n / 100000000000L).toInt())).append(" Kharab ")
            n %= 100000000000L
        }
        if (n >= 1000000000L) { // Arab (10^9)
            sb.append(convertEnglishLessThanThousand((n / 1000000000L).toInt())).append(" Arab ")
            n %= 1000000000L
        }
        if (n >= 10000000L) { // Crore (10^7)
            sb.append(convertEnglishLessThanThousand((n / 10000000L).toInt())).append(" Crore ")
            n %= 10000000L
        }
        if (n >= 100000L) { // Lakh (10^5)
            sb.append(convertEnglishLessThanThousand((n / 100000L).toInt())).append(" Lakh ")
            n %= 100000L
        }
        if (n >= 1000L) { // Thousand (10^3)
            sb.append(convertEnglishLessThanThousand((n / 1000L).toInt())).append(" Thousand ")
            n %= 1000L
        }
        if (n >= 100L) { // Hundred (10^2)
            sb.append(convertEnglishLessThanThousand((n / 100L).toInt())).append(" Hundred ")
            n %= 100L
        }
        if (n > 0) {
            sb.append(convertEnglishLessThanThousand(n.toInt())).append(" ")
        }

        return sb.toString().trim().replace("\\s+".toRegex(), " ")
    }

    private fun convertEnglishLessThanThousand(number: Int): String {
        var num = number
        var result = ""
        if (num >= 100) {
            val h = num / 100
            if (h in 1..19) {
                result += englishUnits[h] + " Hundred "
            }
            num %= 100
        }
        if (num in 1..19) {
            result += englishUnits[num]
        } else if (num >= 20) {
            result += englishTens[num / 10] + if (num % 10 != 0) " " + englishUnits[num % 10] else ""
        }
        return result.trim()
    }

    private fun convertToHindiWords(amount: Long): String {
        var n = amount
        val sb = StringBuilder()

        if (n >= 100000000000L) { // Kharab
            val kh = (n / 100000000000L).toInt()
            sb.append(hindiNumbers[kh] ?: kh.toString()).append(" खरब ")
            n %= 100000000000L
        }
        if (n >= 1000000000L) { // Arab
            val ar = (n / 1000000000L).toInt()
            sb.append(hindiNumbers[ar] ?: ar.toString()).append(" अरब ")
            n %= 1000000000L
        }
        if (n >= 10000000L) { // Crore
            val cr = (n / 10000000L).toInt()
            sb.append(hindiNumbers[cr] ?: cr.toString()).append(" करोड़ ")
            n %= 10000000L
        }
        if (n >= 100000L) { // Lakh
            val lk = (n / 100000L).toInt()
            sb.append(hindiNumbers[lk] ?: lk.toString()).append(" लाख ")
            n %= 100000L
        }
        if (n >= 1000L) { // Thousand
            val th = (n / 1000L).toInt()
            sb.append(hindiNumbers[th] ?: th.toString()).append(" हज़ार ")
            n %= 1000L
        }
        if (n >= 100L) { // Hundred
            val hd = (n / 100L).toInt()
            sb.append(hindiNumbers[hd] ?: hd.toString()).append(" सौ ")
            n %= 100L
        }
        if (n > 0) {
            val rem = n.toInt()
            sb.append(hindiNumbers[rem] ?: rem.toString()).append(" ")
        }

        return sb.toString().trim().replace("\\s+".toRegex(), " ")
    }
}

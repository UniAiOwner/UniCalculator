package com.unicalculator.core.common.prefs

import android.content.Context
import android.content.SharedPreferences
import com.unicalculator.core.model.ProPlanType
import com.unicalculator.core.model.SubscriptionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class NumberFormatStyle {
    INDIAN_VEDIC, // 12,34,567.00
    INTERNATIONAL_WESTERN // 1,234,567.00
}

enum class HapticIntensity {
    OFF,
    SOFT,
    MEDIUM,
    STRONG
}

class UniCalculatorPreferences private constructor(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // --- Trial & Pro Subscription State ---
    private val _firstLaunchTimestamp: Long
    private var _lastKnownTimestamp: Long
    private val _subscriptionStatus = MutableStateFlow<SubscriptionStatus>(SubscriptionStatus.TrialExpired("Calculating..."))
    val subscriptionStatus: StateFlow<SubscriptionStatus> = _subscriptionStatus.asStateFlow()

    private val _trialDaysRemaining = MutableStateFlow(30)
    val trialDaysRemaining: StateFlow<Int> = _trialDaysRemaining.asStateFlow()

    private val _isProOrTrialActive = MutableStateFlow(true)
    val isProOrTrialActive: StateFlow<Boolean> = _isProOrTrialActive.asStateFlow()

    init {
        val now = System.currentTimeMillis()
        val storedFirstLaunch = prefs.getLong(KEY_FIRST_LAUNCH_TIMESTAMP, 0L)
        if (storedFirstLaunch == 0L) {
            _firstLaunchTimestamp = now
            prefs.edit().putLong(KEY_FIRST_LAUNCH_TIMESTAMP, now).apply()
        } else {
            _firstLaunchTimestamp = storedFirstLaunch
        }

        _lastKnownTimestamp = maxOf(now, prefs.getLong(KEY_LAST_KNOWN_TIMESTAMP, _firstLaunchTimestamp))
        prefs.edit().putLong(KEY_LAST_KNOWN_TIMESTAMP, _lastKnownTimestamp).apply()

        evaluateSubscriptionStatus()
    }

    /**
     * Monotonically checks and updates device timestamp to prevent clock rollback attacks
     */
    fun refreshClockAndEvaluate() {
        val currentNow = System.currentTimeMillis()
        _lastKnownTimestamp = maxOf(_lastKnownTimestamp, currentNow)
        prefs.edit().putLong(KEY_LAST_KNOWN_TIMESTAMP, _lastKnownTimestamp).apply()
        evaluateSubscriptionStatus()
    }

    private fun evaluateSubscriptionStatus() {
        val isProUser = prefs.getBoolean(KEY_IS_PRO_USER, false)
        val planTypeName = prefs.getString(KEY_PRO_PLAN_TYPE, null)
        val proExpiryTimestamp = prefs.getLong(KEY_PRO_EXPIRY_TIMESTAMP, 0L)
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

        if (isProUser && planTypeName != null) {
            val plan = try { ProPlanType.valueOf(planTypeName) } catch (e: Exception) { ProPlanType.LIFETIME }
            if (plan == ProPlanType.LIFETIME) {
                val activationDate = dateFormat.format(Date(_firstLaunchTimestamp))
                _subscriptionStatus.value = SubscriptionStatus.LifetimePro(activationDate)
                _isProOrTrialActive.value = true
                return
            } else if (proExpiryTimestamp > _lastKnownTimestamp) {
                val expiryDate = dateFormat.format(Date(proExpiryTimestamp))
                _subscriptionStatus.value = SubscriptionStatus.Subscribed(plan, expiryDate)
                _isProOrTrialActive.value = true
                return
            }
        }

        // Evaluate 30-day Free Trial
        val trialExpiryTimestamp = _firstLaunchTimestamp + TRIAL_DURATION_MILLIS
        val effectiveCurrentTime = maxOf(System.currentTimeMillis(), _lastKnownTimestamp)
        val millisLeft = trialExpiryTimestamp - effectiveCurrentTime

        if (millisLeft > 0) {
            val days = ((millisLeft / (24 * 60 * 60 * 1000L)) + 1).toInt().coerceIn(1, 30)
            val expiryDate = dateFormat.format(Date(trialExpiryTimestamp))
            _trialDaysRemaining.value = days
            _subscriptionStatus.value = SubscriptionStatus.TrialActive(days, expiryDate)
            _isProOrTrialActive.value = true
        } else {
            val expiredDate = dateFormat.format(Date(trialExpiryTimestamp))
            _trialDaysRemaining.value = 0
            _subscriptionStatus.value = SubscriptionStatus.TrialExpired(expiredDate)
            _isProOrTrialActive.value = false
        }
    }

    fun activateProPlan(plan: ProPlanType) {
        val now = maxOf(System.currentTimeMillis(), _lastKnownTimestamp)
        val expiry = when (plan) {
            ProPlanType.MONTHLY -> now + (30L * 24 * 60 * 60 * 1000L)
            ProPlanType.ANNUAL -> now + (365L * 24 * 60 * 60 * 1000L)
            ProPlanType.LIFETIME -> Long.MAX_VALUE
        }
        prefs.edit()
            .putBoolean(KEY_IS_PRO_USER, true)
            .putString(KEY_PRO_PLAN_TYPE, plan.name)
            .putLong(KEY_PRO_EXPIRY_TIMESTAMP, expiry)
            .apply()
        evaluateSubscriptionStatus()
    }

    fun restorePurchases(onResult: (Boolean, String) -> Unit) {
        evaluateSubscriptionStatus()
        if (_isProOrTrialActive.value) {
            onResult(true, "Pro subscription status restored successfully!")
        } else {
            onResult(false, "No active subscription found on this device.")
        }
    }

    // --- Standard Settings State ---
    private val _decimalPrecision = MutableStateFlow(prefs.getInt(KEY_DECIMAL_PRECISION, -1)) // -1 means auto
    val decimalPrecision: StateFlow<Int> = _decimalPrecision.asStateFlow()

    private val _numberFormat = MutableStateFlow(
        NumberFormatStyle.valueOf(prefs.getString(KEY_NUMBER_FORMAT, NumberFormatStyle.INDIAN_VEDIC.name) ?: NumberFormatStyle.INDIAN_VEDIC.name)
    )
    val numberFormat: StateFlow<NumberFormatStyle> = _numberFormat.asStateFlow()

    private val _hapticIntensity = MutableStateFlow(
        HapticIntensity.valueOf(prefs.getString(KEY_HAPTIC_INTENSITY, HapticIntensity.MEDIUM.name) ?: HapticIntensity.MEDIUM.name)
    )
    val hapticIntensity: StateFlow<HapticIntensity> = _hapticIntensity.asStateFlow()

    private val _soundClickEnabled = MutableStateFlow(prefs.getBoolean(KEY_SOUND_CLICK, false))
    val soundClickEnabled: StateFlow<Boolean> = _soundClickEnabled.asStateFlow()

    private val _keepScreenAwake = MutableStateFlow(prefs.getBoolean(KEY_KEEP_SCREEN_AWAKE, false))
    val keepScreenAwake: StateFlow<Boolean> = _keepScreenAwake.asStateFlow()

    private val _showCurrencySymbol = MutableStateFlow(prefs.getBoolean(KEY_SHOW_CURRENCY_SYMBOL, false))
    val showCurrencySymbol: StateFlow<Boolean> = _showCurrencySymbol.asStateFlow()

    private val _wordsLanguage = MutableStateFlow(
        com.unicalculator.core.common.words.WordsLanguage.valueOf(
            prefs.getString(KEY_WORDS_LANGUAGE, com.unicalculator.core.common.words.WordsLanguage.ENGLISH.name) ?: com.unicalculator.core.common.words.WordsLanguage.ENGLISH.name
        )
    )
    val wordsLanguage: StateFlow<com.unicalculator.core.common.words.WordsLanguage> = _wordsLanguage.asStateFlow()

    private val _isDarkMode = MutableStateFlow(prefs.getBoolean(KEY_IS_DARK_MODE, false))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // --- GST Pro Settings State ---
    private val _defaultGstRate = MutableStateFlow(prefs.getInt(KEY_DEFAULT_GST_RATE, 18))
    val defaultGstRate: StateFlow<Int> = _defaultGstRate.asStateFlow()

    private val _isInterStateDefault = MutableStateFlow(prefs.getBoolean(KEY_IS_INTER_STATE, false))
    val isInterStateDefault: StateFlow<Boolean> = _isInterStateDefault.asStateFlow()

    private val _isBankersRounding = MutableStateFlow(prefs.getBoolean(KEY_BANKERS_ROUNDING, true))
    val isBankersRounding: StateFlow<Boolean> = _isBankersRounding.asStateFlow()

    // --- Cash Tally Settings State ---
    private val _show2000Note = MutableStateFlow(prefs.getBoolean(KEY_SHOW_2000_NOTE, false))
    val show2000Note: StateFlow<Boolean> = _show2000Note.asStateFlow()

    private val _show2Note = MutableStateFlow(prefs.getBoolean(KEY_SHOW_2_NOTE, true))
    val show2Note: StateFlow<Boolean> = _show2Note.asStateFlow()

    private val _show1Note = MutableStateFlow(prefs.getBoolean(KEY_SHOW_1_NOTE, true))
    val show1Note: StateFlow<Boolean> = _show1Note.asStateFlow()

    private val _firmName = MutableStateFlow(prefs.getString(KEY_FIRM_NAME, "UniAi Retail Store") ?: "UniAi Retail Store")
    val firmName: StateFlow<String> = _firmName.asStateFlow()

    private val _cashierName = MutableStateFlow(prefs.getString(KEY_CASHIER_NAME, "") ?: "")
    val cashierName: StateFlow<String> = _cashierName.asStateFlow()

    private val _autoCopySlip = MutableStateFlow(prefs.getBoolean(KEY_AUTO_COPY_SLIP, true))
    val autoCopySlip: StateFlow<Boolean> = _autoCopySlip.asStateFlow()

    // --- Tools & Converters Settings State ---
    private val _autoSaveToolsHistory = MutableStateFlow(prefs.getBoolean(KEY_AUTO_SAVE_TOOLS, true))
    val autoSaveToolsHistory: StateFlow<Boolean> = _autoSaveToolsHistory.asStateFlow()

    private val _defaultCurrency = MutableStateFlow(prefs.getString(KEY_DEFAULT_CURRENCY, "INR (₹)") ?: "INR (₹)")
    val defaultCurrency: StateFlow<String> = _defaultCurrency.asStateFlow()

    // Setters
    fun setIsDarkMode(darkMode: Boolean) {
        prefs.edit().putBoolean(KEY_IS_DARK_MODE, darkMode).apply()
        _isDarkMode.value = darkMode
    }

    fun setDecimalPrecision(precision: Int) {
        prefs.edit().putInt(KEY_DECIMAL_PRECISION, precision).apply()
        _decimalPrecision.value = precision
    }

    fun setNumberFormat(format: NumberFormatStyle) {
        prefs.edit().putString(KEY_NUMBER_FORMAT, format.name).apply()
        _numberFormat.value = format
    }

    fun setHapticIntensity(intensity: HapticIntensity) {
        prefs.edit().putString(KEY_HAPTIC_INTENSITY, intensity.name).apply()
        _hapticIntensity.value = intensity
    }

    fun setSoundClickEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SOUND_CLICK, enabled).apply()
        _soundClickEnabled.value = enabled
    }

    fun setKeepScreenAwake(awake: Boolean) {
        prefs.edit().putBoolean(KEY_KEEP_SCREEN_AWAKE, awake).apply()
        _keepScreenAwake.value = awake
    }

    fun setShowCurrencySymbol(show: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_CURRENCY_SYMBOL, show).apply()
        _showCurrencySymbol.value = show
    }

    fun setDefaultGstRate(rate: Int) {
        prefs.edit().putInt(KEY_DEFAULT_GST_RATE, rate).apply()
        _defaultGstRate.value = rate
    }

    fun setIsInterStateDefault(interState: Boolean) {
        prefs.edit().putBoolean(KEY_IS_INTER_STATE, interState).apply()
        _isInterStateDefault.value = interState
    }

    fun setIsBankersRounding(bankers: Boolean) {
        prefs.edit().putBoolean(KEY_BANKERS_ROUNDING, bankers).apply()
        _isBankersRounding.value = bankers
    }

    fun setShow2000Note(show: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_2000_NOTE, show).apply()
        _show2000Note.value = show
    }

    fun setShow2Note(show: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_2_NOTE, show).apply()
        _show2Note.value = show
    }

    fun setShow1Note(show: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_1_NOTE, show).apply()
        _show1Note.value = show
    }

    fun setFirmName(name: String) {
        prefs.edit().putString(KEY_FIRM_NAME, name).apply()
        _firmName.value = name
    }

    fun setCashierName(name: String) {
        prefs.edit().putString(KEY_CASHIER_NAME, name).apply()
        _cashierName.value = name
    }

    fun setAutoCopySlip(autoCopy: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_COPY_SLIP, autoCopy).apply()
        _autoCopySlip.value = autoCopy
    }

    fun setAutoSaveToolsHistory(autoSave: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_SAVE_TOOLS, autoSave).apply()
        _autoSaveToolsHistory.value = autoSave
    }

    fun setDefaultCurrency(currency: String) {
        prefs.edit().putString(KEY_DEFAULT_CURRENCY, currency).apply()
        _defaultCurrency.value = currency
    }

    fun setWordsLanguage(language: com.unicalculator.core.common.words.WordsLanguage) {
        prefs.edit().putString(KEY_WORDS_LANGUAGE, language.name).apply()
        _wordsLanguage.value = language
    }

    companion object {
        private const val PREFS_NAME = "unicalculator_user_prefs"
        private const val KEY_FIRST_LAUNCH_TIMESTAMP = "first_launch_timestamp"
        private const val KEY_LAST_KNOWN_TIMESTAMP = "last_known_timestamp"
        private const val KEY_IS_PRO_USER = "is_pro_user"
        private const val KEY_PRO_PLAN_TYPE = "pro_plan_type"
        private const val KEY_PRO_EXPIRY_TIMESTAMP = "pro_expiry_timestamp"
        private const val TRIAL_DURATION_MILLIS = 30L * 24L * 60L * 60L * 1000L // 30 Days

        private const val KEY_IS_DARK_MODE = "is_dark_mode"
        private const val KEY_DECIMAL_PRECISION = "decimal_precision"
        private const val KEY_NUMBER_FORMAT = "number_format"
        private const val KEY_HAPTIC_INTENSITY = "haptic_intensity"
        private const val KEY_SOUND_CLICK = "sound_click"
        private const val KEY_KEEP_SCREEN_AWAKE = "keep_screen_awake"
        private const val KEY_SHOW_CURRENCY_SYMBOL = "show_currency_symbol"
        private const val KEY_WORDS_LANGUAGE = "words_language"
        private const val KEY_DEFAULT_GST_RATE = "default_gst_rate"
        private const val KEY_IS_INTER_STATE = "is_inter_state"
        private const val KEY_BANKERS_ROUNDING = "bankers_rounding"
        private const val KEY_SHOW_2000_NOTE = "show_2000_note"
        private const val KEY_SHOW_2_NOTE = "show_2_note"
        private const val KEY_SHOW_1_NOTE = "show_1_note"
        private const val KEY_FIRM_NAME = "firm_name"
        private const val KEY_CASHIER_NAME = "cashier_name"
        private const val KEY_AUTO_COPY_SLIP = "auto_copy_slip"
        private const val KEY_AUTO_SAVE_TOOLS = "auto_save_tools"
        private const val KEY_DEFAULT_CURRENCY = "default_currency"

        @Volatile
        private var INSTANCE: UniCalculatorPreferences? = null

        fun getInstance(context: Context): UniCalculatorPreferences {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UniCalculatorPreferences(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}

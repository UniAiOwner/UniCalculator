package com.unicalculator.core.common.prefs

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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

    companion object {
        private const val PREFS_NAME = "unicalculator_user_prefs"
        private const val KEY_IS_DARK_MODE = "is_dark_mode"
        private const val KEY_DECIMAL_PRECISION = "decimal_precision"
        private const val KEY_NUMBER_FORMAT = "number_format"
        private const val KEY_HAPTIC_INTENSITY = "haptic_intensity"
        private const val KEY_SOUND_CLICK = "sound_click"
        private const val KEY_KEEP_SCREEN_AWAKE = "keep_screen_awake"
        private const val KEY_SHOW_CURRENCY_SYMBOL = "show_currency_symbol"
        private const val KEY_DEFAULT_GST_RATE = "default_gst_rate"
        private const val KEY_IS_INTER_STATE = "is_inter_state"
        private const val KEY_BANKERS_ROUNDING = "bankers_rounding"
        private const val KEY_SHOW_2000_NOTE = "show_2000_note"
        private const val KEY_SHOW_2_NOTE = "show_2_note"
        private const val KEY_SHOW_1_NOTE = "show_1_note"
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

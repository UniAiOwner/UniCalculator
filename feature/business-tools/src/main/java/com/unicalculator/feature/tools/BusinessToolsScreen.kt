package com.unicalculator.feature.tools

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CropSquare
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.DataUsage
import androidx.compose.material.icons.outlined.Discount
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Height
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Pin
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Scale
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.ViewInAr
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicalculator.core.common.format.IndianVedicFormatter
import com.unicalculator.core.database.LocalCalculationHistoryRepository
import com.unicalculator.core.designsystem.component.NeumorphicButton
import com.unicalculator.core.designsystem.component.NeumorphicIconButton
import com.unicalculator.core.designsystem.component.NeumorphicPlate
import com.unicalculator.core.designsystem.modifier.NeumorphicShape
import com.unicalculator.core.designsystem.modifier.neumorphic
import com.unicalculator.core.designsystem.theme.LocalNeumorphicColors
import com.unicalculator.core.designsystem.theme.RupeeEmeraldGreen
import com.unicalculator.core.designsystem.theme.withNeonGlow
import com.unicalculator.core.math.CommercialCalculatorEngine
import com.unicalculator.core.math.UnitConversionEngine
import com.unicalculator.core.model.CalculationHistoryItem
import com.unicalculator.core.model.CalculationType
import java.math.BigDecimal
import java.math.RoundingMode

enum class ToolCategory {
    DAILY_UTILITY,
    UNIT_CONVERTER,
    FINANCIAL_BUSINESS
}

data class ToolItem(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val category: ToolCategory
)

@Composable
fun BusinessToolsScreen(
    onNavigateToHistory: (() -> Unit)? = null,
    onToggleTheme: (() -> Unit)? = null,
    onOpenSettings: (() -> Unit)? = null,
    onNavigateToGstPro: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val colors = LocalNeumorphicColors.current
    val context = LocalContext.current
    var activeToolId by remember { mutableStateOf<String?>(null) }
    var showSettingsSheet by remember { mutableStateOf(false) }

    if (showSettingsSheet) {
        ToolsSettingsSheet(onDismiss = { showSettingsSheet = false })
    }

    val allTools = remember {
        listOf(
            // 1. Daily Utility & Health (TOP)
            ToolItem("date_age", "Date & Age", Icons.Outlined.CalendarMonth, ToolCategory.DAILY_UTILITY),
            ToolItem("time", "Time", Icons.Outlined.Widgets, ToolCategory.DAILY_UTILITY),
            ToolItem("data", "Data", Icons.Outlined.DataUsage, ToolCategory.DAILY_UTILITY),
            ToolItem("numeral", "Numeral", Icons.Outlined.Pin, ToolCategory.DAILY_UTILITY),
            ToolItem("bmi", "BMI", Icons.Outlined.FitnessCenter, ToolCategory.DAILY_UTILITY),
            ToolItem("speed", "Speed", Icons.Outlined.Speed, ToolCategory.DAILY_UTILITY),

            // 2. Unit Converters (MIDDLE)
            ToolItem("length", "Length", Icons.Outlined.Height, ToolCategory.UNIT_CONVERTER),
            ToolItem("mass", "Mass", Icons.Outlined.Scale, ToolCategory.UNIT_CONVERTER),
            ToolItem("area", "Area", Icons.Outlined.CropSquare, ToolCategory.UNIT_CONVERTER),
            ToolItem("volume", "Volume", Icons.Outlined.ViewInAr, ToolCategory.UNIT_CONVERTER),
            ToolItem("temp", "Temperature", Icons.Outlined.Thermostat, ToolCategory.UNIT_CONVERTER),
            ToolItem("currency", "Currency", Icons.Outlined.AccountBalance, ToolCategory.UNIT_CONVERTER),

            // 3. Financial & Business (BOTTOM)
            ToolItem("loan_emi", "Finance / EMI", Icons.Outlined.AccountBalance, ToolCategory.FINANCIAL_BUSINESS),
            ToolItem("discount", "Discount", Icons.Outlined.Discount, ToolCategory.FINANCIAL_BUSINESS),
            ToolItem("margin", "Margin & Markup", Icons.AutoMirrored.Outlined.TrendingUp, ToolCategory.FINANCIAL_BUSINESS),
            ToolItem("gst_pro", "GST Pro", Icons.Outlined.Receipt, ToolCategory.FINANCIAL_BUSINESS)
        )
    }

    if (activeToolId != null) {
        // Active Sub-Tool Detail Screen
        ToolDetailHost(
            toolId = activeToolId!!,
            onBack = { activeToolId = null },
            onNavigateToGstPro = onNavigateToGstPro,
            modifier = modifier
        )
    } else {
        // Main Tools Super Hub Screen
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(colors.background)
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            // 0. Top Action Bar: Title (Left) + 3 Neumorphic Buttons (Right)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp, top = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Converters & Tools",
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.5.sp,
                        color = colors.textPrimary,
                        letterSpacing = 0.2.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NeumorphicIconButton(
                        icon = Icons.Outlined.History,
                        contentDescription = "History",
                        onClick = { onNavigateToHistory?.invoke() },
                        size = 38.dp,
                        iconSize = 18.dp,
                        iconTint = colors.accentEmerald
                    )

                    NeumorphicIconButton(
                        icon = Icons.Outlined.DarkMode,
                        contentDescription = "Toggle Theme",
                        onClick = { onToggleTheme?.invoke() },
                        size = 38.dp,
                        iconSize = 18.dp,
                        iconTint = colors.textSecondary
                    )

                    NeumorphicIconButton(
                        icon = Icons.Outlined.Settings,
                        contentDescription = "Settings",
                        onClick = {
                            if (onOpenSettings != null) onOpenSettings.invoke() else showSettingsSheet = true
                        },
                        size = 38.dp,
                        iconSize = 18.dp,
                        iconTint = colors.textSecondary
                    )
                }
            }

            // --- SECTION 1: DAILY UTILITY & HEALTH ---
            SectionHeader(title = "⚡ Daily Utilities & Health", count = 6)
            ToolGrid(
                items = allTools.filter { it.category == ToolCategory.DAILY_UTILITY },
                onItemClick = { activeToolId = it.id }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // --- SECTION 2: UNIT CONVERTERS ---
            SectionHeader(title = "📏 Unit Converters", count = 6)
            ToolGrid(
                items = allTools.filter { it.category == ToolCategory.UNIT_CONVERTER },
                onItemClick = { activeToolId = it.id }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // --- SECTION 3: FINANCIAL & BUSINESS ---
            SectionHeader(title = "📊 Financial & Business", count = 4)
            ToolGrid(
                items = allTools.filter { it.category == ToolCategory.FINANCIAL_BUSINESS },
                onItemClick = { tool ->
                    if (tool.id == "gst_pro") {
                        onNavigateToGstPro?.invoke()
                    } else {
                        activeToolId = tool.id
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun SectionHeader(title: String, count: Int) {
    val colors = LocalNeumorphicColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = colors.accentEmerald,
                letterSpacing = 0.5.sp
            )
        )
        Text(
            text = "$count tools",
            style = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = colors.textSecondary
            )
        )
    }
}

@Composable
fun ToolGrid(
    items: List<ToolItem>,
    onItemClick: (ToolItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        val chunked = items.chunked(3)
        chunked.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowItems.forEach { tool ->
                    ToolTile(
                        tool = tool,
                        onClick = { onItemClick(tool) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill remainder of row if less than 3
                repeat(3 - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun ToolTile(
    tool: ToolItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalNeumorphicColors.current
    Box(
        modifier = modifier
            .height(95.dp)
            .neumorphic(
                shape = NeumorphicShape.CONVEX,
                cornerRadius = 16.dp,
                elevation = 4.dp,
                lightShadowColor = colors.lightHighlight,
                darkShadowColor = colors.darkShadow,
                backgroundColor = colors.background
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = tool.icon,
                contentDescription = tool.title,
                tint = colors.textPrimary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = tool.title,
                style = TextStyle(
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center
                ),
                maxLines = 1
            )
        }
    }
}

@Composable
fun ToolDetailHost(
    toolId: String,
    onBack: () -> Unit,
    onNavigateToGstPro: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val colors = LocalNeumorphicColors.current
    BackHandler(onBack = onBack)
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar with Back Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NeumorphicIconButton(
                icon = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Back",
                onClick = onBack,
                iconTint = colors.textPrimary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = when (toolId) {
                    "date_age" -> "Date & Age Calculator"
                    "time" -> "Time Converter"
                    "data" -> "Data Storage Converter"
                    "numeral" -> "Numeral System Base"
                    "bmi" -> "BMI Health Calculator"
                    "speed" -> "Speed Converter"
                    "length" -> "Length Converter"
                    "mass" -> "Mass & Weight Converter"
                    "area" -> "Area Converter"
                    "volume" -> "Volume Converter"
                    "temp" -> "Temperature Converter"
                    "currency" -> "Currency Converter"
                    "loan_emi" -> "Loan EMI Calculator"
                    "discount" -> "Discount Solver"
                    "margin" -> "Margin & Markup Solver"
                    else -> "Tool"
                },
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = colors.textPrimary
                )
            )
        }

        when (toolId) {
            "length" -> GenericUnitConverterScreen(
                toolName = "Length Converter",
                units = listOf("Meter (m)", "Kilometer (km)", "Centimeter (cm)", "Millimeter (mm)", "Foot (ft)", "Inch (in)", "Yard (yd)", "Mile (mi)"),
                convertFn = { v, f, t -> UnitConversionEngine.convertLength(v, f, t) }
            )
            "mass" -> GenericUnitConverterScreen(
                toolName = "Mass & Weight Converter",
                units = listOf(
                    "Kilogram (kg)",
                    "Gram (g)",
                    "Milligram (mg)",
                    "Quintal (q)",
                    "Maund / Mann",
                    "Tonne (t)",
                    "Tola (Vedic 11.66g)",
                    "Metric Tola (10g)",
                    "Sovereign / Pavan",
                    "Masha",
                    "Ratti",
                    "Carat (ct)",
                    "Pound (lb)",
                    "Ounce (oz)"
                ),
                convertFn = { v, f, t -> UnitConversionEngine.convertMass(v, f, t) }
            )
            "area" -> GenericUnitConverterScreen(
                toolName = "Area Converter",
                units = listOf(
                    "Square Foot (sq ft)",
                    "Square Meter (sq m)",
                    "Square Yard (sq yd)",
                    "Acre",
                    "Hectare",
                    "Bigha (Standard)",
                    "Guntha",
                    "Cent",
                    "Ground",
                    "Marla",
                    "Kanal",
                    "Biswa / Katha"
                ),
                convertFn = { v, f, t -> UnitConversionEngine.convertArea(v, f, t) }
            )
            "volume" -> GenericUnitConverterScreen(
                toolName = "Volume Converter",
                units = listOf("Liter (L)", "Milliliter (mL)", "Gallon (US gal)", "Cubic Meter (m³)", "Cubic Foot (ft³)"),
                convertFn = { v, f, t -> UnitConversionEngine.convertVolume(v, f, t) }
            )
            "temp" -> GenericUnitConverterScreen(
                toolName = "Temperature Converter",
                units = listOf("Celsius (°C)", "Fahrenheit (°F)", "Kelvin (K)"),
                convertFn = { v, f, t -> UnitConversionEngine.convertTemperature(v, f, t) }
            )
            "speed" -> GenericUnitConverterScreen(
                toolName = "Speed Converter",
                units = listOf("Kilometer/hour (km/h)", "Mile/hour (mph)", "Meter/second (m/s)", "Knot (kn)"),
                convertFn = { v, f, t -> UnitConversionEngine.convertSpeed(v, f, t) }
            )
            "time" -> GenericUnitConverterScreen(
                toolName = "Time Converter",
                units = listOf("Seconds (s)", "Minutes (min)", "Hours (hr)", "Days (d)", "Weeks (wk)", "Months (~30d)", "Years (365d)"),
                convertFn = { v, f, t -> UnitConversionEngine.convertTime(v, f, t) }
            )
            "data" -> GenericUnitConverterScreen(
                toolName = "Data Storage Converter",
                units = listOf("Byte (B)", "Kilobyte (KB)", "Megabyte (MB)", "Gigabyte (GB)", "Terabyte (TB)", "Petabyte (PB)"),
                convertFn = { v, f, t -> UnitConversionEngine.convertData(v, f, t) }
            )
            "bmi" -> BmiCalculatorScreen()
            "discount" -> DiscountSolverScreen()
            "numeral" -> NumeralSystemScreen()
            "margin" -> MarginMarkupScreen()
            "loan_emi" -> LoanEmiScreen()
            "date_age" -> DateAgeScreen()
            "currency" -> GenericUnitConverterScreen(
                toolName = "Currency Converter",
                units = listOf(
                    "Indian Rupee (INR ₹)",
                    "US Dollar (USD $)",
                    "Euro (EUR €)",
                    "British Pound (GBP £)",
                    "UAE Dirham (AED)",
                    "Saudi Riyal (SAR)",
                    "Kuwaiti Dinar (KWD)",
                    "Qatari Riyal (QAR)",
                    "Omani Rial (OMR)",
                    "Canadian Dollar (CAD C$)",
                    "Australian Dollar (AUD A$)",
                    "Singapore Dollar (SGD S$)",
                    "Japanese Yen (JPY ¥)",
                    "Swiss Franc (CHF)",
                    "Chinese Yuan (CNY ¥)"
                ),
                convertFn = { v, f, t -> UnitConversionEngine.convertCurrency(v, f, t) }
            )
            else -> {
                Text(
                    text = "Tool Coming Soon!",
                    style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 16.sp, color = colors.textSecondary)
                )
            }
        }
    }
}

// --- 1. GENERIC UNIT CONVERTER SCREEN ---
@Composable
fun GenericUnitConverterScreen(
    toolName: String = "Unit Converter",
    units: List<String>,
    convertFn: (BigDecimal, String, String) -> BigDecimal
) {
    val colors = LocalNeumorphicColors.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val historyRepo = remember { LocalCalculationHistoryRepository(context) }
    var fromUnit by remember { mutableStateOf(units[0]) }
    var toUnit by remember { mutableStateOf(units.getOrElse(1) { units[0] }) }
    var inputValueText by remember { mutableStateOf("1") }
    var isSyncingForex by remember { mutableStateOf(false) }
    var syncVersion by remember { mutableStateOf(0) }

    val isCurrency = toolName == "Currency Converter"

    LaunchedEffect(isCurrency) {
        if (isCurrency && !UnitConversionEngine.isLiveForexFeed) {
            isSyncingForex = true
            val res = ForexRateService.fetchAndSyncLiveRates()
            isSyncingForex = false
            if (res.isSuccess) {
                syncVersion++
            }
        }
    }

    val inputVal = inputValueText.toBigDecimalOrNull() ?: BigDecimal.ZERO
    val convertedVal = remember(inputVal, fromUnit, toUnit, syncVersion) {
        try {
            convertFn(inputVal, fromUnit, toUnit)
        } catch (_: Exception) {
            BigDecimal.ZERO
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (isCurrency) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                if (UnitConversionEngine.isLiveForexFeed) RupeeEmeraldGreen else colors.textSecondary,
                                shape = CircleShape
                            )
                    )
                    Text(
                        text = if (isSyncingForex) "Syncing live rates..."
                        else if (UnitConversionEngine.isLiveForexFeed) "Live Forex Rates (Synced)"
                        else "Standard Baseline Rates",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (UnitConversionEngine.isLiveForexFeed) RupeeEmeraldGreen else colors.textSecondary
                        )
                    )
                }

                NeumorphicButton(
                    text = if (isSyncingForex) "⏳ Syncing" else "🔄 Refresh",
                    onClick = {
                        if (!isSyncingForex) {
                            scope.launch {
                                isSyncingForex = true
                                val res = ForexRateService.fetchAndSyncLiveRates()
                                isSyncingForex = false
                                if (res.isSuccess) {
                                    syncVersion++
                                    Toast.makeText(context, "Forex rates updated live!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Offline: Using saved rates", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier.width(110.dp).height(34.dp),
                    fontSize = 11,
                    textColor = colors.textPrimary
                )
            }
        }
        // Top Unit Selector & Input Well
        UnitCard(
            title = "FROM",
            unit = fromUnit,
            allUnits = units,
            onUnitSelected = { fromUnit = it },
            valueText = inputValueText,
            onValueChange = { inputValueText = it },
            isEditable = true
        )

        // Swap Units Button
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            NeumorphicButton(
                text = "⇅ SWAP",
                onClick = {
                    val temp = fromUnit
                    fromUnit = toUnit
                    toUnit = temp
                },
                modifier = Modifier.width(130.dp).height(44.dp),
                fontSize = 14
            )
        }

        // Bottom Converted Result Card
        UnitCard(
            title = "TO (RESULT)",
            unit = toUnit,
            allUnits = units,
            onUnitSelected = { toUnit = it },
            valueText = convertedVal.toPlainString(),
            onValueChange = {},
            isEditable = false
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Save to History Button
        NeumorphicButton(
            text = "💾 Save Conversion to History",
            onClick = {
                val expr = "$inputValueText $fromUnit → $toUnit"
                val res = "${convertedVal.stripTrailingZeros().toPlainString()} $toUnit"
                historyRepo.insert(
                    CalculationHistoryItem(
                        type = CalculationType.TOOLS_CONVERTER,
                        formulaExpression = expr,
                        primaryResult = res
                    )
                )
                Toast.makeText(context, "Conversion Saved to History", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth().height(46.dp),
            textColor = RupeeEmeraldGreen,
            fontSize = 13
        )
    }
}

@Composable
fun UnitCard(
    title: String,
    unit: String,
    allUnits: List<String>,
    onUnitSelected: (String) -> Unit,
    valueText: String,
    onValueChange: (String) -> Unit,
    isEditable: Boolean
) {
    val colors = LocalNeumorphicColors.current
    var expanded by remember { mutableStateOf(false) }

    NeumorphicPlate(
        modifier = Modifier.fillMaxWidth(),
        shape = NeumorphicShape.CONVEX,
        cornerRadius = 18.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = colors.textSecondary
                    )
                )

                // Dropdown Unit Selector
                Box {
                    Text(
                        text = "$unit ▾",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = colors.accentEmerald
                        ),
                        modifier = Modifier
                            .clickable { expanded = true }
                            .padding(4.dp)
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        allUnits.forEach { u ->
                            DropdownMenuItem(
                                text = { Text(u) },
                                onClick = {
                                    onUnitSelected(u)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Input Well with Cursor
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .neumorphic(
                        shape = NeumorphicShape.CONCAVE,
                        cornerRadius = 12.dp,
                        elevation = 3.dp,
                        lightShadowColor = colors.lightHighlight,
                        darkShadowColor = colors.darkShadow,
                        backgroundColor = colors.lcdWellBackground
                    )
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (isEditable) {
                    BasicTextField(
                        value = valueText,
                        onValueChange = onValueChange,
                        textStyle = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = colors.textPrimary,
                            textAlign = TextAlign.End
                        ),
                        cursorBrush = SolidColor(colors.accentEmerald),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(
                        text = valueText,
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = colors.accentEmerald,
                            textAlign = TextAlign.End
                        ).withNeonGlow(
                            glowColor = colors.accentEmerald,
                            blurRadius = if (colors.isDark) 16f else 10f,
                            glowAlpha = if (colors.isDark) 0.85f else 0.45f
                        ),
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

// --- 2. BMI CALCULATOR SCREEN ---
@Composable
fun BmiCalculatorScreen() {
    val colors = LocalNeumorphicColors.current
    val context = LocalContext.current
    val historyRepo = remember { LocalCalculationHistoryRepository(context) }
    var weightText by remember { mutableStateOf("70") }
    var heightText by remember { mutableStateOf("175") }

    val weight = weightText.toBigDecimalOrNull() ?: BigDecimal.ZERO
    val height = heightText.toBigDecimalOrNull() ?: BigDecimal.ZERO
    val bmiResult = UnitConversionEngine.calculateBmi(weight, height)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        NeumorphicInput(label = "Weight (kg)", value = weightText, onValueChange = { weightText = it })
        NeumorphicInput(label = "Height (cm)", value = heightText, onValueChange = { heightText = it })

        NeumorphicPlate(
            modifier = Modifier.fillMaxWidth(),
            shape = NeumorphicShape.CONVEX,
            cornerRadius = 20.dp
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "YOUR BMI SCORE",
                    style = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = colors.textSecondary)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = bmiResult.bmiScore.toPlainString(),
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 38.sp,
                        color = colors.accentEmerald
                    ).withNeonGlow(
                        glowColor = colors.accentEmerald,
                        blurRadius = if (colors.isDark) 20f else 12f,
                        glowAlpha = if (colors.isDark) 0.85f else 0.45f
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Category: ${bmiResult.category}",
                    style = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = colors.textPrimary)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Ideal Weight Range: ${bmiResult.healthyWeightRangeKg}",
                    style = TextStyle(fontFamily = FontFamily.Default, fontSize = 12.sp, color = colors.textSecondary)
                )
            }
        }

        NeumorphicButton(
            text = "💾 Save BMI Score to History",
            onClick = {
                historyRepo.insert(
                    CalculationHistoryItem(
                        type = CalculationType.BMI_CALCULATOR,
                        formulaExpression = "Weight: ${weightText}kg | Height: ${heightText}cm",
                        primaryResult = "BMI ${bmiResult.bmiScore} (${bmiResult.category})"
                    )
                )
                Toast.makeText(context, "BMI Score Saved to History", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth().height(46.dp),
            textColor = RupeeEmeraldGreen,
            fontSize = 13
        )
    }
}

// --- 3. DISCOUNT SOLVER SCREEN ---
@Composable
fun DiscountSolverScreen() {
    val colors = LocalNeumorphicColors.current
    val context = LocalContext.current
    val historyRepo = remember { LocalCalculationHistoryRepository(context) }
    var originalPriceText by remember { mutableStateOf("1999") }
    var discountPercentText by remember { mutableStateOf("25") }

    val original = originalPriceText.toBigDecimalOrNull() ?: BigDecimal.ZERO
    val discount = discountPercentText.toBigDecimalOrNull() ?: BigDecimal.ZERO
    val result = UnitConversionEngine.calculateDiscount(original, discount)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        NeumorphicInput(label = "Original Price (₹)", value = originalPriceText, onValueChange = { originalPriceText = it })
        NeumorphicInput(label = "Discount (%)", value = discountPercentText, onValueChange = { discountPercentText = it })

        NeumorphicPlate(
            modifier = Modifier.fillMaxWidth(),
            shape = NeumorphicShape.CONVEX,
            cornerRadius = 20.dp
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                ResultRow(label = "Final Discounted Price", value = IndianVedicFormatter.formatCurrency(result.finalPrice), isHighlight = true)
                ResultRow(label = "You Save (Total Discount)", value = IndianVedicFormatter.formatCurrency(result.totalSavings))
            }
        }

        NeumorphicButton(
            text = "💾 Save Discount to History",
            onClick = {
                historyRepo.insert(
                    CalculationHistoryItem(
                        type = CalculationType.DISCOUNT_STACK,
                        formulaExpression = "₹$originalPriceText − $discountPercentText% Discount",
                        primaryResult = IndianVedicFormatter.formatCurrency(result.finalPrice)
                    )
                )
                Toast.makeText(context, "Discount Saved to History", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth().height(46.dp),
            textColor = RupeeEmeraldGreen,
            fontSize = 13
        )
    }
}

// --- 4. NUMERAL SYSTEM BASE CONVERTER ---
@Composable
fun NumeralSystemScreen() {
    val colors = LocalNeumorphicColors.current
    val context = LocalContext.current
    val historyRepo = remember { LocalCalculationHistoryRepository(context) }
    var decimalInput by remember { mutableStateOf("255") }

    val bin = UnitConversionEngine.convertNumeral(decimalInput, 10, 2)
    val oct = UnitConversionEngine.convertNumeral(decimalInput, 10, 8)
    val hex = UnitConversionEngine.convertNumeral(decimalInput, 10, 16)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        NeumorphicInput(label = "Decimal Number (Base 10)", value = decimalInput, onValueChange = { decimalInput = it })

        NeumorphicPlate(
            modifier = Modifier.fillMaxWidth(),
            shape = NeumorphicShape.CONVEX,
            cornerRadius = 18.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                ResultRow(label = "Binary (BIN - Base 2)", value = bin)
                ResultRow(label = "Octal (OCT - Base 8)", value = oct)
                ResultRow(label = "Hexadecimal (HEX - Base 16)", value = hex, isHighlight = true)
            }
        }

        NeumorphicButton(
            text = "💾 Save Conversion to History",
            onClick = {
                historyRepo.insert(
                    CalculationHistoryItem(
                        type = CalculationType.TOOLS_CONVERTER,
                        formulaExpression = "Base 10 ($decimalInput) → BIN: $bin | OCT: $oct | HEX: $hex",
                        primaryResult = "HEX: $hex"
                    )
                )
                Toast.makeText(context, "Conversion Saved to History", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth().height(46.dp),
            textColor = RupeeEmeraldGreen,
            fontSize = 13
        )
    }
}

// --- 5. MARGIN & MARKUP SCREEN ---
@Composable
fun MarginMarkupScreen() {
    val colors = LocalNeumorphicColors.current
    val context = LocalContext.current
    val historyRepo = remember { LocalCalculationHistoryRepository(context) }
    var costPriceText by remember { mutableStateOf("1200") }
    var sellingPriceText by remember { mutableStateOf("1600") }

    val cp = costPriceText.toBigDecimalOrNull() ?: BigDecimal.ZERO
    val sp = sellingPriceText.toBigDecimalOrNull() ?: BigDecimal.ZERO
    val marginResult = CommercialCalculatorEngine.calculateMarginMarkup(cp, sp)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        NeumorphicInput(label = "Cost Price (CP)", value = costPriceText, onValueChange = { costPriceText = it }, prefix = "₹")
        NeumorphicInput(label = "Selling Price (SP)", value = sellingPriceText, onValueChange = { sellingPriceText = it }, prefix = "₹")

        NeumorphicPlate(
            modifier = Modifier.fillMaxWidth(),
            shape = NeumorphicShape.CONVEX,
            cornerRadius = 20.dp
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                ResultRow(label = "Gross Profit", value = IndianVedicFormatter.formatCurrency(marginResult.grossProfit), isHighlight = true)
                ResultRow(label = "Profit Margin (%)", value = "${marginResult.profitMarginPercent}%")
                ResultRow(label = "Markup on Cost (%)", value = "${marginResult.markupPercent}%")
            }
        }

        NeumorphicButton(
            text = "💾 Save Margin to History",
            onClick = {
                historyRepo.insert(
                    CalculationHistoryItem(
                        type = CalculationType.MARGIN_MARKUP,
                        formulaExpression = "CP: ₹$costPriceText | SP: ₹$sellingPriceText",
                        primaryResult = "Profit: ${IndianVedicFormatter.formatCurrency(marginResult.grossProfit)} (${marginResult.profitMarginPercent}%)"
                    )
                )
                Toast.makeText(context, "Margin Calculation Saved to History", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth().height(46.dp),
            textColor = RupeeEmeraldGreen,
            fontSize = 13
        )
    }
}

// --- 6. LOAN EMI SCREEN ---
@Composable
fun LoanEmiScreen() {
    val colors = LocalNeumorphicColors.current
    val context = LocalContext.current
    val historyRepo = remember { LocalCalculationHistoryRepository(context) }
    var principalText by remember { mutableStateOf("500000") }
    var interestRateText by remember { mutableStateOf("8.5") }
    var tenureMonthsText by remember { mutableStateOf("36") }

    val principal = principalText.toBigDecimalOrNull() ?: BigDecimal.ZERO
    val rate = interestRateText.toBigDecimalOrNull() ?: BigDecimal.ZERO
    val tenure = tenureMonthsText.toIntOrNull() ?: 12
    val emiResult = CommercialCalculatorEngine.calculateLoanEmi(principal, rate, tenure)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        NeumorphicInput(label = "Principal Loan Amount", value = principalText, onValueChange = { principalText = it }, prefix = "₹")
        NeumorphicInput(label = "Annual Interest Rate (%)", value = interestRateText, onValueChange = { interestRateText = it }, prefix = "%")
        NeumorphicInput(label = "Tenure (Months)", value = tenureMonthsText, onValueChange = { tenureMonthsText = it })

        NeumorphicPlate(
            modifier = Modifier.fillMaxWidth(),
            shape = NeumorphicShape.CONVEX,
            cornerRadius = 20.dp
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                ResultRow(label = "Monthly EMI", value = IndianVedicFormatter.formatCurrency(emiResult.monthlyEmi), isHighlight = true)
                ResultRow(label = "Total Interest", value = IndianVedicFormatter.formatCurrency(emiResult.totalInterest))
                ResultRow(label = "Total Amount Payable", value = IndianVedicFormatter.formatCurrency(emiResult.totalPayment))
            }
        }

        NeumorphicButton(
            text = "💾 Save Loan EMI to History",
            onClick = {
                historyRepo.insert(
                    CalculationHistoryItem(
                        type = CalculationType.LOAN_EMI,
                        formulaExpression = "Loan: ₹$principalText @ $interestRateText% for $tenureMonthsText mos",
                        primaryResult = "EMI: ${IndianVedicFormatter.formatCurrency(emiResult.monthlyEmi)}"
                    )
                )
                Toast.makeText(context, "Loan EMI Saved to History", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth().height(46.dp),
            textColor = RupeeEmeraldGreen,
            fontSize = 13
        )
    }
}

// --- 7. DATE & AGE SCREEN ---
@Composable
fun DateAgeScreen() {
    val colors = LocalNeumorphicColors.current
    val context = LocalContext.current
    val historyRepo = remember { LocalCalculationHistoryRepository(context) }
    var birthYearText by remember { mutableStateOf("1998") }
    var birthMonthText by remember { mutableStateOf("8") }
    var birthDayText by remember { mutableStateOf("15") }

    val currentYear = 2026
    val currentMonth = 8
    val currentDay = 20

    val bYear = birthYearText.toIntOrNull() ?: 2000
    val bMonth = birthMonthText.toIntOrNull() ?: 1
    val bDay = birthDayText.toIntOrNull() ?: 1

    var ageYears = currentYear - bYear
    var ageMonths = currentMonth - bMonth
    var ageDays = currentDay - bDay

    if (ageDays < 0) {
        ageMonths--
        ageDays += 30
    }
    if (ageMonths < 0) {
        ageYears--
        ageMonths += 12
    }

    val ageResultStr = "$ageYears Years, $ageMonths Months, $ageDays Days"

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Enter Date of Birth",
            style = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = colors.textSecondary)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                NeumorphicInput(label = "Day", value = birthDayText, onValueChange = { birthDayText = it })
            }
            Box(modifier = Modifier.weight(1f)) {
                NeumorphicInput(label = "Month", value = birthMonthText, onValueChange = { birthMonthText = it })
            }
            Box(modifier = Modifier.weight(1.2f)) {
                NeumorphicInput(label = "Year", value = birthYearText, onValueChange = { birthYearText = it })
            }
        }

        NeumorphicPlate(
            modifier = Modifier.fillMaxWidth(),
            shape = NeumorphicShape.CONVEX,
            cornerRadius = 20.dp
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "EXACT AGE", style = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = colors.textSecondary))
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = ageResultStr,
                    style = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = colors.accentEmerald, textAlign = TextAlign.Center)
                )
            }
        }

        NeumorphicButton(
            text = "💾 Save Age to History",
            onClick = {
                historyRepo.insert(
                    CalculationHistoryItem(
                        type = CalculationType.DATE_AGE,
                        formulaExpression = "DOB: $birthDayText/$birthMonthText/$birthYearText",
                        primaryResult = ageResultStr
                    )
                )
                Toast.makeText(context, "Age Saved to History", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth().height(46.dp),
            textColor = RupeeEmeraldGreen,
            fontSize = 13
        )
    }
}

// --- HELPER COMPONENTS ---
@Composable
fun NeumorphicInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    prefix: String? = null,
    modifier: Modifier = Modifier
) {
    val colors = LocalNeumorphicColors.current
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = colors.textSecondary
            )
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .neumorphic(
                    shape = NeumorphicShape.CONCAVE,
                    cornerRadius = 12.dp,
                    elevation = 3.dp,
                    lightShadowColor = colors.lightHighlight,
                    darkShadowColor = colors.darkShadow,
                    backgroundColor = colors.lcdWellBackground
                )
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (prefix != null) {
                    Text(
                        text = "$prefix ",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = colors.accentEmerald
                        )
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = colors.textPrimary
                    ),
                    cursorBrush = SolidColor(colors.accentEmerald),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun ResultRow(
    label: String,
    value: String,
    isHighlight: Boolean = false
) {
    val colors = LocalNeumorphicColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = FontFamily.Default,
                fontSize = 13.sp,
                color = colors.textSecondary
            )
        )
        val valueColor = if (isHighlight) colors.accentEmerald else colors.textPrimary
        val baseStyle = TextStyle(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = if (isHighlight) 16.sp else 14.sp,
            color = valueColor
        )
        val finalStyle = if (isHighlight) {
            baseStyle.withNeonGlow(
                glowColor = valueColor,
                blurRadius = if (colors.isDark) 16f else 10f,
                glowAlpha = if (colors.isDark) 0.85f else 0.45f
            )
        } else {
            baseStyle
        }
        Text(
            text = value,
            style = finalStyle
        )
    }
}

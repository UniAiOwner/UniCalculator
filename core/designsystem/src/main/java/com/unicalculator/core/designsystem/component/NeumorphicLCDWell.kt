package com.unicalculator.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.platform.TextToolbar
import androidx.compose.ui.platform.TextToolbarStatus
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicalculator.core.designsystem.modifier.NeumorphicShape
import com.unicalculator.core.designsystem.modifier.neumorphic
import com.unicalculator.core.designsystem.theme.LocalNeumorphicColors
import com.unicalculator.core.designsystem.theme.RupeeEmeraldGreen

data class LCDTapeItem(
    val expression: String,
    val result: String
)

@OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun NeumorphicLCDWell(
    expressionText: String,
    resultText: String,
    wordsText: String? = null,
    cursorPosition: Int = expressionText.length,
    selectionStart: Int = -1,
    selectionEnd: Int = -1,
    onSetCursorPosition: ((Int) -> Unit)? = null,
    tapeHistory: List<LCDTapeItem> = emptyList(),
    onTapeItemClick: ((LCDTapeItem) -> Unit)? = null,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    resultColor: Color? = null
) {
    val colors = LocalNeumorphicColors.current
    val scrollState = rememberScrollState()

    // Automatically scroll to bottom when new calculation or input arrives
    LaunchedEffect(tapeHistory.size, expressionText, resultText) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    val dynamicFontSize = when {
        resultText.length > 14 -> 24.sp
        resultText.length > 10 -> 30.sp
        resultText.length > 8 -> 36.sp
        else -> 42.sp
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 180.dp, max = 220.dp)
            .neumorphic(
                shape = NeumorphicShape.CONCAVE,
                cornerRadius = cornerRadius,
                elevation = 5.dp,
                lightShadowColor = colors.lightHighlight,
                darkShadowColor = colors.darkShadow,
                backgroundColor = colors.lcdWellBackground
            )
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {
            // 📜 Previous Calculation Tape Items (Scrollable History)
            if (tapeHistory.isNotEmpty()) {
                tapeHistory.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onTapeItemClick?.invoke(item)
                            }
                            .padding(vertical = 3.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.expression,
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Normal,
                                fontSize = 13.sp,
                                color = colors.textSecondary.copy(alpha = 0.8f)
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "= ${item.result}",
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = colors.textPrimary.copy(alpha = 0.75f)
                            ),
                            maxLines = 1
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 6.dp),
                    thickness = 0.8.dp,
                    color = colors.darkShadow.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 🎯 Active Interactive Expression Line with Native Per-Digit Cursor Placement
            if (expressionText.isNotEmpty() || onSetCursorPosition != null) {
                val activeCursorIndex = cursorPosition.coerceIn(0, expressionText.length)
                val textFieldValue = remember(expressionText, activeCursorIndex) {
                    TextFieldValue(
                        text = expressionText,
                        selection = TextRange(activeCursorIndex)
                    )
                }

                CompositionLocalProvider(
                    LocalTextToolbar provides object : TextToolbar {
                        override val status: TextToolbarStatus get() = TextToolbarStatus.Hidden
                        override fun hide() {}
                        override fun showMenu(
                            rect: androidx.compose.ui.geometry.Rect,
                            onCopyRequested: (() -> Unit)?,
                            onPasteRequested: (() -> Unit)?,
                            onCutRequested: (() -> Unit)?,
                            onSelectAllRequested: (() -> Unit)?
                        ) {}
                    }
                ) {
                    androidx.compose.ui.platform.InterceptPlatformTextInput(
                        interceptor = { _, _ ->
                            kotlinx.coroutines.awaitCancellation()
                        }
                    ) {
                        BasicTextField(
                            value = textFieldValue,
                            onValueChange = { updatedValue ->
                                onSetCursorPosition?.invoke(updatedValue.selection.start)
                            },
                            textStyle = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Medium,
                                fontSize = 20.sp,
                                color = colors.textSecondary,
                                textAlign = TextAlign.End
                            ),
                            cursorBrush = SolidColor(RupeeEmeraldGreen),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                showKeyboardOnFocus = false
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )
                    }
                }
            }

            // Primary Result Line
            Text(
                text = resultText,
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = dynamicFontSize,
                    color = resultColor ?: colors.accentEmerald
                ),
                maxLines = 1,
                textAlign = TextAlign.End,
                modifier = Modifier.padding(vertical = 2.dp)
            )

            // Words Sub-Badge
            if (!wordsText.isNullOrEmpty()) {
                Text(
                    text = wordsText,
                    style = TextStyle(
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = colors.textSecondary
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}



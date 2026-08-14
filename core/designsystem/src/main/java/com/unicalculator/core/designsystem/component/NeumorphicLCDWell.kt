package com.unicalculator.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicalculator.core.designsystem.modifier.NeumorphicShape
import com.unicalculator.core.designsystem.modifier.neumorphic
import com.unicalculator.core.designsystem.theme.LocalNeumorphicColors

@Composable
fun NeumorphicLCDWell(
    expressionText: String,
    resultText: String,
    wordsText: String? = null,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    resultColor: Color? = null
) {
    val colors = LocalNeumorphicColors.current

    val dynamicFontSize = when {
        resultText.length > 14 -> 24.sp
        resultText.length > 10 -> 30.sp
        resultText.length > 8 -> 36.sp
        else -> 42.sp
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .neumorphic(
                shape = NeumorphicShape.CONCAVE,
                cornerRadius = cornerRadius,
                elevation = 5.dp,
                lightShadowColor = colors.lightHighlight,
                darkShadowColor = colors.darkShadow,
                backgroundColor = colors.lcdWellBackground
            )
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.End
    ) {
        // Expression Line
        if (expressionText.isNotEmpty()) {
            Text(
                text = expressionText,
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = colors.textSecondary
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.End
            )
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
            modifier = Modifier.padding(vertical = 4.dp)
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

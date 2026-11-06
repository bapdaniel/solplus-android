package com.solplus.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SolColorScheme = lightColorScheme(
    primary = SolDarkGreen,
    onPrimary = Color.White,
    primaryContainer = SolBadgeConfirmedBg,
    onPrimaryContainer = SolDarkGreen,
    secondary = SolSunGold,
    onSecondary = SolTextDark,
    secondaryContainer = SolSunGoldLight,
    onSecondaryContainer = SolBadgePendingText,
    background = SolCreamBg,
    onBackground = SolTextDark,
    surface = SolCardBg,
    onSurface = SolTextDark,
    surfaceVariant = SolCreamBg,
    onSurfaceVariant = SolTextSecondary
)

@Composable
fun SolPlusTheme(
    fontScale: Float = 1.15f,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SolColorScheme,
        typography = createSolTypography(fontScale),
        content = content
    )
}

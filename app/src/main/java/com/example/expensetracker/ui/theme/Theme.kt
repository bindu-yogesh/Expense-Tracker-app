package com.example.expensetracker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,

    secondary = Teal,
    onSecondary = Color.White,

    tertiary = PrimaryBlueDark,
    onTertiary = Color.White,

    background = LightBackground,
    onBackground = LightTextPrimary,

    surface = LightSurface,
    onSurface = LightTextPrimary,

    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = LightTextSecondary,

    error = ExpenseRed,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF60A5FA),
    onPrimary = Color(0xFF0F172A),

    secondary = Color(0xFF2DD4BF),
    onSecondary = Color(0xFF042F2E),

    tertiary = Color(0xFF93C5FD),
    onTertiary = Color(0xFF172554),

    background = DarkBackground,
    onBackground = DarkTextPrimary,

    surface = DarkSurface,
    onSurface = DarkTextPrimary,

    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = DarkTextSecondary,

    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A)
)

@Composable
fun ExpenseTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
package com.athar.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = AtharGold,
    onPrimary = AtharBlack,
    primaryContainer = AtharMutedGold,
    onPrimaryContainer = AtharGold,
    secondary = AtharMutedGold,
    onSecondary = AtharWhite,
    secondaryContainer = AtharCardGray,
    onSecondaryContainer = AtharGold,
    tertiary = AtharGreen,
    background = AtharBlack,
    surface = AtharDarkGray,
    surfaceVariant = AtharCardGray,
    onBackground = AtharWhite,
    onSurface = AtharWhite,
    onSurfaceVariant = Color(0xFFB0B0B0),
    outline = Color(0xFF2A2A2A),
    outlineVariant = Color(0xFF1E1E1E)
)

private val LightColorScheme = lightColorScheme(
    primary = AtharGold,
    onPrimary = AtharBlack,
    secondary = AtharMutedGold,
    onSecondary = AtharWhite,
    tertiary = AtharGreen,
    background = AtharWhite,
    surface = Color(0xFFF5F5F5),
    onBackground = AtharBlack,
    onSurface = AtharBlack
)

@Composable
fun AtharTheme(
    darkTheme: Boolean = true, // Athar is a dark-first app
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    // Set status bar color to match the dark theme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = AtharBlack.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
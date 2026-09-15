package com.athar.app.ui.theme

import android.app.Activity
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
    primary = AtharPrimary,
    onPrimary = AtharTextOnPrimary,
    primaryContainer = AtharPrimaryDark,
    onPrimaryContainer = AtharPrimaryLight,
    secondary = AtharPrimaryDark,
    onSecondary = AtharTextPrimary,
    secondaryContainer = AtharCardSurface,
    onSecondaryContainer = AtharPrimary,
    tertiary = AtharEmerald,
    onTertiary = AtharTextOnPrimary,
    background = AtharBackground,
    onBackground = AtharTextPrimary,
    surface = AtharSurface,
    onSurface = AtharTextPrimary,
    surfaceVariant = AtharCardSurface,
    onSurfaceVariant = AtharTextSecondary,
    outline = AtharOutline,
    outlineVariant = AtharOutlineVariant
)

private val LightColorScheme = lightColorScheme(
    primary = AtharPrimaryDark,
    onPrimary = Color.White,
    primaryContainer = AtharPrimaryLight,
    onPrimaryContainer = AtharTextOnPrimary,
    secondary = AtharPrimaryDark,
    onSecondary = Color.White,
    tertiary = AtharEmerald,
    background = Color(0xFFF2F7F4),
    onBackground = Color(0xFF1A2723),
    surface = Color(0xFFEAF0EC),
    onSurface = Color(0xFF1A2723),
    surfaceVariant = Color(0xFFDDE6E0),
    onSurfaceVariant = Color(0xFF4A5B52)
)

@Composable
fun AtharTheme(
    darkTheme: Boolean = true, // Athar is a dark-first app
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    // Set system bars to match the theme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
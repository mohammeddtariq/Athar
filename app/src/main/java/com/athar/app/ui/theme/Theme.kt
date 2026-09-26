package com.athar.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val AtharDarkScheme = darkColorScheme(
    primary = AtharPrimary,
    onPrimary = AtharTextOnPrimary,
    primaryContainer = AtharPrimarySubtle,
    onPrimaryContainer = AtharPrimaryLight,
    secondary = AtharPrimaryMuted,
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
    outlineVariant = AtharOutlineVariant,
    surfaceContainerLowest = AtharBackground,
    surfaceContainerLow = AtharSurface,
    surfaceContainer = AtharCardSurface,
    surfaceContainerHigh = AtharCardGlow,
    surfaceContainerHighest = AtharCardBorder
)

@Composable
fun AtharTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            @Suppress("DEPRECATION")
            window.statusBarColor = Color.Transparent.toArgb()
            @Suppress("DEPRECATION")
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = AtharDarkScheme,
        typography = Typography,
        content = content
    )
}
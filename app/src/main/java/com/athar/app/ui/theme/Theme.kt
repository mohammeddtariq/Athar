package com.athar.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = AtharGold,
    onPrimary = AtharBlack,
    secondary = AtharMutedGold,
    onSecondary = AtharWhite,
    tertiary = AtharGreen,
    background = AtharBlack,
    surface = AtharCardGray,
    onBackground = AtharWhite,
    onSurface = AtharWhite
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
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
package com.athar.app.ui.theme

import android.app.Activity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/** Maps the active palette onto the Material colour roles the app consumes. */
private fun AtharPalette.toColorScheme() = darkColorScheme(
    primary = primary,
    onPrimary = textOnPrimary,
    primaryContainer = primarySubtle,
    onPrimaryContainer = primaryLight,
    secondary = primaryMuted,
    onSecondary = textPrimary,
    secondaryContainer = cardSurface,
    onSecondaryContainer = primary,
    tertiary = emerald,
    onTertiary = textOnPrimary,
    background = background,
    onBackground = textPrimary,
    surface = surface,
    onSurface = textPrimary,
    surfaceVariant = cardSurface,
    onSurfaceVariant = textSecondary,
    outline = outline,
    outlineVariant = outlineVariant,
    surfaceContainerLowest = background,
    surfaceContainerLow = surface,
    surfaceContainer = cardSurface,
    surfaceContainerHigh = cardGlow,
    surfaceContainerHighest = cardBorder
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

    val palette = AtharThemeState.current
    val colorScheme = remember(palette) { palette.toColorScheme() }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography
    ) {
        ThemeSwitchWash(palette = palette, content = content)
    }
}

/**
 * Plays a single radial bloom in the incoming accent whenever the theme
 * changes, so repainting the whole app reads as a deliberate choice instead of
 * a flicker. It draws over everything but never takes touch input.
 */
@Composable
private fun ThemeSwitchWash(
    palette: AtharPalette,
    content: @Composable () -> Unit
) {
    val wash = remember { Animatable(0f) }
    var applied by remember { mutableStateOf(palette) }

    LaunchedEffect(palette) {
        if (applied != palette) {
            applied = palette
            wash.snapTo(1f)
            wash.animateTo(0f, tween(durationMillis = 780, easing = FastOutSlowInEasing))
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        content()
        ThemeWashOverlay(palette = palette, wash = wash)
    }
}

@Composable
private fun ThemeWashOverlay(
    palette: AtharPalette,
    wash: Animatable<Float, AnimationVector1D>
) {
    // Read in its own composable so the animation never recomposes the app.
    val progress = wash.value
    if (progress <= 0.001f) return

    Canvas(modifier = Modifier.fillMaxSize()) {
        val radius = size.maxDimension * 0.85f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    palette.primary.copy(alpha = 0.32f * progress),
                    palette.primary.copy(alpha = 0.10f * progress),
                    Color.Transparent
                ),
                center = center,
                radius = radius
            ),
            radius = radius,
            center = center
        )
    }
}

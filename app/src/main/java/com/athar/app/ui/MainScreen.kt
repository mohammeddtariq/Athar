package com.athar.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.athar.app.R
import com.athar.app.ui.corner.DuasScreen
import com.athar.app.ui.corner.QiblaScreen
import com.athar.app.ui.corner.QuranScreen
import com.athar.app.ui.home.HomeScreen
import com.athar.app.ui.settings.SettingsScreen
import com.athar.app.ui.theme.AtharBackground
import com.athar.app.ui.theme.AtharNavGlow
import com.athar.app.ui.theme.AtharNavIconActive
import com.athar.app.ui.theme.AtharNavIconInactive
import com.athar.app.ui.theme.AtharNavPillBorder
import com.athar.app.ui.theme.AtharNavPillSelected
import com.athar.app.ui.theme.AtharNavbarBg
import com.athar.app.ui.theme.AtharNavbarBorder
import com.athar.app.ui.theme.ThmanyahSans

sealed class Screen(
    val route: String,
    val labelResId: Int,
    val selectedVector: ImageVector? = null,
    val unselectedVector: ImageVector? = null,
    val iconResId: Int? = null
) {
    object Home : Screen(
        route = "home",
        labelResId = R.string.nav_home,
        selectedVector = Icons.Rounded.Home,
        unselectedVector = Icons.Outlined.Home
    )

    object Quran : Screen(
        route = "quran",
        labelResId = R.string.nav_quran,
        iconResId = R.drawable.ic_quran_book
    )

    object Duas : Screen(
        route = "duas",
        labelResId = R.string.nav_duas,
        iconResId = R.drawable.ic_prayer_hands
    )

    object Qibla : Screen(
        route = "qibla",
        labelResId = R.string.nav_qibla,
        selectedVector = Icons.Rounded.Explore,
        unselectedVector = Icons.Outlined.Explore
    )

    object Settings : Screen(
        route = "settings",
        labelResId = R.string.nav_settings,
        selectedVector = Icons.Rounded.Settings,
        unselectedVector = Icons.Outlined.Settings
    )
}

val allNavScreens = listOf(
    Screen.Home,
    Screen.Quran,
    Screen.Duas,
    Screen.Qibla,
    Screen.Settings
)

private val TabEnterCurve = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)
private val TabExitCurve = CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f)

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    var isQuranReading by remember { mutableStateOf(false) }
    var targetSettingsSection by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AtharBackground)
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.fillMaxSize(),
            enterTransition = {
                fadeIn(animationSpec = tween(220, easing = TabEnterCurve)) +
                    scaleIn(initialScale = 0.96f, animationSpec = tween(220, easing = TabEnterCurve)) +
                    slideInVertically(
                        initialOffsetY = { (it * 0.015f).toInt() },
                        animationSpec = tween(220, easing = TabEnterCurve)
                    )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(140, easing = TabExitCurve)) +
                    scaleOut(targetScale = 0.98f, animationSpec = tween(140, easing = TabExitCurve))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(220, easing = TabEnterCurve)) +
                    scaleIn(initialScale = 0.96f, animationSpec = tween(220, easing = TabEnterCurve)) +
                    slideInVertically(
                        initialOffsetY = { (it * 0.015f).toInt() },
                        animationSpec = tween(220, easing = TabEnterCurve)
                    )
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(140, easing = TabExitCurve)) +
                    scaleOut(targetScale = 0.98f, animationSpec = tween(140, easing = TabExitCurve))
            }
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onOpenSettings = { navController.navigateToTab(Screen.Settings) },
                    onOpenNotifications = {
                        targetSettingsSection = "notifications"
                        navController.navigateToTab(Screen.Settings)
                    },
                    onNavigateToDuas = { navController.navigateToTab(Screen.Duas) }
                )
            }
            composable(Screen.Quran.route) {
                QuranScreen(
                    onBack = { navController.navigateToTab(Screen.Home) },
                    onReadingModeChanged = { isReading -> isQuranReading = isReading }
                )
            }
            composable(Screen.Duas.route) {
                DuasScreen(onBack = { navController.navigateToTab(Screen.Home) })
            }
            composable(Screen.Qibla.route) {
                QiblaScreen(
                    onBack = { navController.navigateToTab(Screen.Home) },
                    onOpenSettings = { navController.navigateToTab(Screen.Settings) }
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    targetSection = targetSettingsSection,
                    onTargetSectionConsumed = { targetSettingsSection = null }
                )
            }
        }

        // Floating Nav Dock
        AnimatedVisibility(
            visible = !isQuranReading,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = spring(dampingRatio = 0.85f, stiffness = 350f)
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = spring(dampingRatio = 0.85f, stiffness = 350f)
            ) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            AtharNavBar(navController)
        }
    }
}

private fun NavHostController.navigateToTab(screen: Screen) {
    navigate(screen.route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

/**
 * Bottom navigation: elevated floating capsule dock:
 * - Solid opaque dock container with ambient glow.
 * - Always ordered left-to-right starting with Home tab, regardless of active locale.
 * - Tactile spring press feedback and fluid expanding active pill.
 * - High-contrast active icon indicator and refined inactive icons.
 * - Elevated cleanly above system navigation bar.
 */
@Composable
fun AtharNavBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    fun isSelected(screen: Screen): Boolean {
        return currentDestination?.hierarchy?.any { it.route == screen.route } == true
    }

    // Force Left-to-Right layout order regardless of active language (Arabic or English)
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        BoxWithConstraints(
            modifier = modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            val isCompact = maxWidth < 380.dp
            val outerPadHorizontal = if (isCompact) 8.dp else 14.dp
            val outerPadVertical = if (isCompact) 10.dp else 14.dp
            val dockInnerPadH = if (isCompact) 6.dp else 8.dp
            val dockInnerPadV = if (isCompact) 5.dp else 7.dp
            val itemSpacing = if (isCompact) 2.dp else 4.dp
            val unselectedPadH = if (isCompact) 8.dp else 12.dp
            val selectedPadH = if (isCompact) 12.dp else 16.dp
            val iconSize = if (isCompact) 21.dp else 24.dp
            val fontSize = if (isCompact) 12.sp else 13.5.sp

            Box(
                modifier = Modifier
                    .padding(horizontal = outerPadHorizontal, vertical = outerPadVertical)
                    .shadow(
                        elevation = 18.dp,
                        shape = RoundedCornerShape(38.dp),
                        spotColor = AtharNavGlow.copy(alpha = 0.35f),
                        ambientColor = Color.Black.copy(alpha = 0.70f)
                    )
                    .clip(RoundedCornerShape(38.dp))
                    .background(AtharNavbarBg)
                    .border(
                        width = 1.2.dp,
                        color = AtharNavbarBorder,
                        shape = RoundedCornerShape(38.dp)
                    )
                    .padding(horizontal = dockInnerPadH, vertical = dockInnerPadV),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(itemSpacing, Alignment.CenterHorizontally)
                ) {
                    allNavScreens.forEach { screen ->
                        NavDockItem(
                            screen = screen,
                            selected = isSelected(screen),
                            unselectedPadH = unselectedPadH,
                            selectedPadH = selectedPadH,
                            iconSize = iconSize,
                            fontSize = fontSize,
                            onClick = { navController.navigateToTab(screen) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NavDockItem(
    screen: Screen,
    selected: Boolean,
    unselectedPadH: Dp = 12.dp,
    selectedPadH: Dp = 16.dp,
    iconSize: Dp = 24.dp,
    fontSize: TextUnit = 13.5.sp,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "navItemScale"
    )

    val pillBackground by animateColorAsState(
        targetValue = if (selected) AtharNavPillSelected else Color.Transparent,
        animationSpec = tween(220, easing = FastOutSlowInEasing),
        label = "pillBg"
    )

    val pillBorderColor by animateColorAsState(
        targetValue = if (selected) AtharNavPillBorder else Color.Transparent,
        animationSpec = tween(220, easing = FastOutSlowInEasing),
        label = "pillBorder"
    )

    val iconTint by animateColorAsState(
        targetValue = if (selected) AtharNavIconActive else AtharNavIconInactive,
        animationSpec = tween(200, easing = FastOutSlowInEasing),
        label = "iconTint"
    )

    val labelColor by animateColorAsState(
        targetValue = if (selected) AtharNavIconActive else AtharNavIconInactive,
        animationSpec = tween(200, easing = FastOutSlowInEasing),
        label = "labelColor"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(pillBackground)
            .border(
                width = if (selected) 1.dp else 0.dp,
                color = pillBorderColor,
                shape = RoundedCornerShape(24.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(
                horizontal = if (selected) selectedPadH else unselectedPadH,
                vertical = 0.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Icon container with ambient glowing halo on selected tab
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(iconSize)
            ) {
                if (selected) {
                    Box(
                        modifier = Modifier
                            .size(iconSize * 0.7f)
                            .shadow(
                                elevation = 10.dp,
                                shape = CircleShape,
                                spotColor = AtharNavGlow.copy(alpha = 0.85f),
                                ambientColor = AtharNavGlow.copy(alpha = 0.50f)
                            )
                    )
                }
                ScreenIcon(
                    screen = screen,
                    selected = selected,
                    tint = iconTint,
                    modifier = Modifier.size(iconSize)
                )
            }

            AnimatedVisibility(
                visible = selected,
                enter = fadeIn(animationSpec = tween(180, easing = FastOutSlowInEasing)) +
                        expandHorizontally(
                            animationSpec = spring(
                                dampingRatio = 0.82f,
                                stiffness = 380f
                            ),
                            expandFrom = Alignment.Start
                        ),
                exit = fadeOut(animationSpec = tween(120, easing = FastOutSlowInEasing)) +
                       shrinkHorizontally(
                           animationSpec = spring(
                               dampingRatio = 0.95f,
                               stiffness = 450f
                           ),
                           shrinkTowards = Alignment.Start
                       )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(screen.labelResId),
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = fontSize,
                        color = labelColor,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }
    }
}

@Composable
private fun ScreenIcon(
    screen: Screen,
    selected: Boolean,
    tint: Color,
    modifier: Modifier = Modifier
) {
    val description = stringResource(screen.labelResId)
    if (screen.iconResId != null) {
        Icon(
            painter = painterResource(screen.iconResId),
            contentDescription = description,
            tint = tint,
            modifier = modifier
        )
    } else {
        val vector = if (selected) screen.selectedVector else screen.unselectedVector
        if (vector != null) {
            Icon(
                imageVector = vector,
                contentDescription = description,
                tint = tint,
                modifier = modifier
            )
        }
    }
}

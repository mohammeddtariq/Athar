package com.athar.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import com.athar.app.ui.theme.AtharNavIconInactive
import com.athar.app.ui.theme.AtharNavPillSelected
import com.athar.app.ui.theme.AtharNavbarBg
import com.athar.app.ui.theme.AtharNavbarBorder
import com.athar.app.ui.theme.AtharPrimary
import com.athar.app.ui.theme.AtharPrimaryLight
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

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    var isQuranReading by remember { mutableStateOf(false) }

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
                fadeIn(animationSpec = tween(140, easing = FastOutSlowInEasing))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(110, easing = FastOutSlowInEasing))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(140, easing = FastOutSlowInEasing))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(110, easing = FastOutSlowInEasing))
            }
        ) {
            composable(Screen.Home.route) {
                HomeScreen(onOpenSettings = { navController.navigateToTab(Screen.Settings) })
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
                SettingsScreen()
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
 * Bottom navigation: elevated floating frosted-glass dock matching reference design:
 * - Substantial, tall pill dock matching reference photo height (~68dp).
 * - Active tab smoothly expands into an inner capsule containing [Icon] [Label].
 * - Inactive tabs display clean minimalist outline icons.
 * - Floats comfortably upward (bottom = 24dp) with fluid spring physics.
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

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(38.dp),
                    spotColor = AtharPrimary.copy(alpha = 0.28f),
                    ambientColor = Color.Black.copy(alpha = 0.90f)
                )
                .clip(RoundedCornerShape(38.dp))
                .background(AtharNavbarBg.copy(alpha = 0.94f))
                .border(
                    width = 1.2.dp,
                    color = AtharNavbarBorder.copy(alpha = 0.90f),
                    shape = RoundedCornerShape(38.dp)
                )
                .padding(horizontal = 8.dp, vertical = 7.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
            ) {
                allNavScreens.forEach { screen ->
                    NavDockItem(
                        screen = screen,
                        selected = isSelected(screen),
                        onClick = { navController.navigateToTab(screen) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NavDockItem(
    screen: Screen,
    selected: Boolean,
    onClick: () -> Unit
) {
    val iconTint = if (selected) AtharPrimaryLight else AtharNavIconInactive
    val labelColor = if (selected) AtharPrimaryLight else AtharNavIconInactive

    val pillBackground = if (selected) {
        AtharNavPillSelected.copy(alpha = 0.98f)
    } else {
        Color.Transparent
    }

    val pillBorderModifier = if (selected) {
        Modifier.border(
            width = 1.dp,
            color = AtharPrimary.copy(alpha = 0.45f),
            shape = RoundedCornerShape(26.dp)
        )
    } else {
        Modifier
    }

    Box(
        modifier = Modifier
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = 0.78f,
                    stiffness = 400f
                )
            )
            .clip(RoundedCornerShape(26.dp))
            .background(pillBackground)
            .then(pillBorderModifier)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(
                horizontal = if (selected) 16.dp else 12.dp,
                vertical = 12.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            ScreenIcon(
                screen = screen,
                selected = selected,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )

            if (selected) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(screen.labelResId),
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = labelColor,
                    maxLines = 1
                )
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

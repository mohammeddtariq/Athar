package com.athar.app.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
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
import com.athar.app.ui.corner.MuslimCornerScreen
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
import com.athar.app.ui.theme.AtharTextPrimary
import com.athar.app.ui.theme.ThmanyahSans

sealed class Screen(
    val route: String,
    val labelResId: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    // Standalone floating button tab (kept floating alone)
    object Settings : Screen(
        "settings", R.string.nav_settings,
        Icons.Rounded.Settings, Icons.Outlined.Settings
    )

    // Combined dock capsule tabs
    object Corner : Screen(
        "corner", R.string.nav_services,
        Icons.Rounded.AutoStories, Icons.Outlined.AutoStories
    )

    object Home : Screen(
        "home", R.string.nav_home,
        Icons.Rounded.Today, Icons.Outlined.Today
    )
}

// Detail screens (not in the dock — dock highlights their parent)
object DetailRoutes {
    const val QURAN = "quran"
    const val QIBLA = "qibla"
    const val DUAS = "duas"
}

// Combined dock items: Essence, Today
val dockItems = listOf(
    Screen.Corner,
    Screen.Home
)

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            AtharNavBar(navController)
        },
        containerColor = AtharBackground
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(onOpenSettings = { navController.navigateToTab(Screen.Settings) })
            }
            composable(Screen.Corner.route) {
                MuslimCornerScreen(
                    onOpenQuran = { navController.navigate(DetailRoutes.QURAN) },
                    onOpenQibla = { navController.navigate(DetailRoutes.QIBLA) },
                    onOpenDuas = { navController.navigate(DetailRoutes.DUAS) }
                )
            }
            composable(DetailRoutes.QURAN) { QuranScreen(onBack = { navController.popBackStack() }) }
            composable(DetailRoutes.QIBLA) {
                QiblaScreen(
                    onBack = { navController.popBackStack() },
                    onOpenSettings = { navController.navigateToTab(Screen.Settings) }
                )
            }
            composable(DetailRoutes.DUAS) { DuasScreen(onBack = { navController.popBackStack() }) }
            composable(Screen.Settings.route) { SettingsScreen() }
        }
    }
}

private fun NavHostController.navigateToTab(screen: Screen) {
    navigate(screen.route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

/**
 * Bottom navigation: slim frosted-glass dock + standalone Settings circle.
 * In RTL (Arabic) the combined dock starts from the right and the
 * standalone Settings sits on the left; mirrored in LTR.
 */
@Composable
fun AtharNavBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    fun isSelected(screen: Screen): Boolean {
        if (currentDestination?.hierarchy?.any { it.route == screen.route } == true) return true
        if (screen == Screen.Corner && currentRoute in listOf(
                DetailRoutes.QURAN, DetailRoutes.QIBLA, DetailRoutes.DUAS
            )
        ) return true
        return false
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isRtl) {
                DockCapsule(
                    modifier = Modifier.weight(1f),
                    isSelected = ::isSelected,
                    onSelect = { navController.navigateToTab(it) }
                )
                Spacer(modifier = Modifier.width(10.dp))
                StandaloneSettingsButton(
                    selected = isSelected(Screen.Settings),
                    onClick = { navController.navigateToTab(Screen.Settings) }
                )
            } else {
                StandaloneSettingsButton(
                    selected = isSelected(Screen.Settings),
                    onClick = { navController.navigateToTab(Screen.Settings) }
                )
                Spacer(modifier = Modifier.width(10.dp))
                DockCapsule(
                    modifier = Modifier.weight(1f),
                    isSelected = ::isSelected,
                    onSelect = { navController.navigateToTab(it) }
                )
            }
        }
    }
}

/** Combined floating capsule dock — frosted glass, weight-aligned items. */
@Composable
private fun DockCapsule(
    modifier: Modifier = Modifier,
    isSelected: (Screen) -> Boolean,
    onSelect: (Screen) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(34.dp),
                spotColor = Color.Black.copy(alpha = 0.6f)
            )
            .clip(RoundedCornerShape(34.dp))
            .background(AtharNavbarBg.copy(alpha = 0.78f))
            .border(1.dp, AtharNavbarBorder.copy(alpha = 0.9f), RoundedCornerShape(34.dp))
            .padding(horizontal = 6.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            dockItems.forEach { screen ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    DockNavItem(
                        screen = screen,
                        selected = isSelected(screen),
                        onClick = { onSelect(screen) }
                    )
                }
            }
        }
    }
}

/** Standalone circular floating button (Settings tab) — frosted glass. */
@Composable
private fun StandaloneSettingsButton(
    selected: Boolean,
    onClick: () -> Unit
) {
    val screen = Screen.Settings
    val bgAnim by animateColorAsState(
        targetValue = if (selected) AtharNavPillSelected.copy(alpha = 0.9f)
        else AtharNavbarBg.copy(alpha = 0.72f),
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "standaloneBg"
    )
    val borderAnim by animateColorAsState(
        targetValue = if (selected) AtharPrimary.copy(alpha = 0.45f) else AtharNavbarBorder,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "standaloneBorder"
    )
    val iconTint by animateColorAsState(
        targetValue = if (selected) AtharPrimaryLight else AtharNavIconInactive,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "standaloneIcon"
    )
    val scaleAnim by animateFloatAsState(
        targetValue = if (selected) 1.05f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "standaloneScale"
    )

    Box(
        modifier = Modifier
            .size(64.dp)
            .graphicsLayer {
                scaleX = scaleAnim
                scaleY = scaleAnim
            }
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(32.dp),
                spotColor = Color.Black.copy(alpha = 0.6f)
            )
            .clip(RoundedCornerShape(32.dp))
            .background(bgAnim)
            .border(1.dp, borderAnim, RoundedCornerShape(32.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (selected) screen.selectedIcon else screen.unselectedIcon,
                contentDescription = stringResource(screen.labelResId),
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = stringResource(screen.labelResId),
                fontFamily = ThmanyahSans,
                fontWeight = if (selected) FontWeight.Black else FontWeight.Bold,
                fontSize = 9.5.sp,
                color = iconTint
            )
        }
    }
}

/**
 * Dock item: weight-centered so icon and label stay perfectly aligned.
 * Selected: olive pill with icon + label. Unselected: compact icon + label.
 */
@Composable
private fun DockNavItem(
    screen: Screen,
    selected: Boolean,
    onClick: () -> Unit
) {
    val pillBg by animateColorAsState(
        targetValue = if (selected) AtharNavPillSelected.copy(alpha = 0.95f) else Color.Transparent,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow),
        label = "pillBg"
    )
    val iconTint by animateColorAsState(
        targetValue = if (selected) AtharPrimaryLight else AtharNavIconInactive,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "dockIconTint"
    )
    val labelColor by animateColorAsState(
        targetValue = if (selected) AtharTextPrimary else AtharNavIconInactive,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "dockLabelColor"
    )
    val paddingHorizontal by animateDpAsState(
        targetValue = if (selected) 16.dp else 4.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow),
        label = "dockPadding"
    )

    Box(
        modifier = Modifier
            .then(if (selected) Modifier.fillMaxHeight() else Modifier)
            .clip(RoundedCornerShape(24.dp))
            .background(pillBg)
            .then(
                if (selected) {
                    Modifier.border(1.dp, AtharPrimary.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                } else Modifier
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = paddingHorizontal, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = screen.selectedIcon,
                    contentDescription = stringResource(screen.labelResId),
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(screen.labelResId),
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.5.sp,
                    color = labelColor
                )
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = screen.unselectedIcon,
                    contentDescription = stringResource(screen.labelResId),
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = stringResource(screen.labelResId),
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.5.sp,
                    color = labelColor
                )
            }
        }
    }
}

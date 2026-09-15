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
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.athar.app.R
import com.athar.app.ui.home.HomeScreen
import com.athar.app.ui.services.ServicesScreen
import com.athar.app.ui.theme.AtharBackground
import com.athar.app.ui.theme.AtharNavIconActive
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
    // Standalone floating button tab
    object Settings : Screen(
        "settings", R.string.nav_settings,
        Icons.Rounded.Settings, Icons.Outlined.Settings
    )

    // Combined dock capsule tabs
    object Explore : Screen(
        "explore", R.string.nav_explore,
        Icons.Rounded.Layers, Icons.Outlined.Layers
    )

    object Services : Screen(
        "services", R.string.nav_services,
        Icons.Rounded.AutoStories, Icons.Outlined.AutoStories
    )

    object Home : Screen(
        "home", R.string.nav_home,
        Icons.Rounded.Home, Icons.Outlined.Home
    )
}

// Combined dock items: Explore, Services, Home
val dockItems = listOf(
    Screen.Explore,
    Screen.Services,
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
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.Services.route) { ServicesScreen() }
            composable(Screen.Explore.route) { ExplorePlaceholder() }
            composable(Screen.Settings.route) { SettingsPlaceholder() }
        }
    }
}

/**
 * Bottom navigation layout:
 * - Standalone floating Settings button on one side
 * - Combined floating capsule dock holding Explore, Services, and Home
 */
@Composable
fun AtharNavBar(navController: androidx.navigation.NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ─── Standalone Floating Button (Settings) ───
            val isSettingsSelected =
                currentDestination?.hierarchy?.any { it.route == Screen.Settings.route } == true

            StandaloneFloatingSettingsButton(
                screen = Screen.Settings,
                selected = isSettingsSelected,
                onClick = {
                    navController.navigate(Screen.Settings.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )

            Spacer(modifier = Modifier.width(10.dp))

            // ─── Combined Floating Capsule Dock (Explore, Services, Home) ───
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(62.dp)
                    .clip(RoundedCornerShape(31.dp))
                    .background(AtharNavbarBg.copy(alpha = 0.96f))
                    .border(1.dp, AtharNavbarBorder, RoundedCornerShape(31.dp)),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    dockItems.forEach { screen ->
                        val selected =
                            currentDestination?.hierarchy?.any { it.route == screen.route } == true

                        DockNavItem(
                            screen = screen,
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Standalone circular/squircle floating button (Settings tab).
 */
@Composable
private fun StandaloneFloatingSettingsButton(
    screen: Screen,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bgAnim by animateColorAsState(
        targetValue = if (selected) AtharNavPillSelected else AtharNavbarBg.copy(alpha = 0.96f),
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
        targetValue = if (selected) 1.04f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "standaloneScale"
    )

    Box(
        modifier = Modifier
            .size(62.dp)
            .graphicsLayer {
                scaleX = scaleAnim
                scaleY = scaleAnim
            }
            .clip(RoundedCornerShape(31.dp))
            .background(bgAnim)
            .border(1.dp, borderAnim, RoundedCornerShape(31.dp))
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
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = stringResource(screen.labelResId),
                fontFamily = ThmanyahSans,
                fontWeight = if (selected) FontWeight.Black else FontWeight.Bold,
                fontSize = 10.sp,
                color = iconTint
            )
        }
    }
}

/**
 * Item inside the main combined dock capsule.
 * When selected: expands into a dark sage green pill with icon + label.
 * When unselected: shows icon with small label below.
 */
@Composable
private fun DockNavItem(
    screen: Screen,
    selected: Boolean,
    onClick: () -> Unit
) {
    val pillBg by animateColorAsState(
        targetValue = if (selected) AtharNavPillSelected else Color.Transparent,
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
        targetValue = if (selected) 14.dp else 6.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow),
        label = "dockPadding"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(pillBg)
            .then(
                if (selected) {
                    Modifier.border(0.8.dp, AtharPrimary.copy(alpha = 0.35f), RoundedCornerShape(24.dp))
                } else Modifier
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = paddingHorizontal, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            // Selected layout: Horizontal pill [ Icon  Label ]
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = screen.selectedIcon,
                    contentDescription = stringResource(screen.labelResId),
                    tint = iconTint,
                    modifier = Modifier.size(19.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = stringResource(screen.labelResId),
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = labelColor
                )
            }
        } else {
            // Unselected layout: Stacked [ Icon / Label ]
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = screen.unselectedIcon,
                    contentDescription = stringResource(screen.labelResId),
                    tint = iconTint,
                    modifier = Modifier.size(19.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(screen.labelResId),
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = labelColor
                )
            }
        }
    }
}

@Composable
private fun SettingsPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AtharBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            stringResource(R.string.nav_settings),
            color = AtharTextPrimary,
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Black,
            fontSize = 24.sp
        )
    }
}

@Composable
private fun ExplorePlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AtharBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            stringResource(R.string.nav_explore),
            color = AtharTextPrimary,
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Black,
            fontSize = 24.sp
        )
    }
}

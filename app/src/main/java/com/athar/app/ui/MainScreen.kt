package com.athar.app.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.athar.app.ui.home.HomeScreen
import com.athar.app.ui.services.ServicesScreen
import com.athar.app.ui.theme.AtharBlack
import com.athar.app.ui.theme.AtharGold
import com.athar.app.ui.theme.AtharWhite

sealed class Screen(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Settings : Screen(
        "settings", "الإعدادات",
        Icons.Rounded.Settings, Icons.Outlined.Settings
    )

    object Home : Screen(
        "home", "اليوم",
        Icons.Rounded.Home, Icons.Outlined.Home
    )

    object Services : Screen(
        "services", "ركن المسلم",
        Icons.Rounded.Explore, Icons.Outlined.Explore
    )
}

val items = listOf(
    Screen.Settings,
    Screen.Home,
    Screen.Services
)

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            FrostedGlassBottomBar(navController)
        },
        containerColor = AtharBlack
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Settings.route) {
                Text("Settings Screen", color = AtharGold, modifier = Modifier.padding(16.dp))
            }
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.Services.route) { ServicesScreen() }
        }
    }
}

@Composable
fun FrostedGlassBottomBar(navController: androidx.navigation.NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Frosted glass container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .clip(RoundedCornerShape(34.dp))
                .background(
                    Color(0xFF1A1A1A).copy(alpha = 0.85f)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { screen ->
                    val selected =
                        currentDestination?.hierarchy?.any { it.route == screen.route } == true

                    NavBarItem(
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

@Composable
private fun NavBarItem(
    screen: Screen,
    selected: Boolean,
    onClick: () -> Unit
) {
    val iconTint by animateColorAsState(
        targetValue = if (selected) AtharWhite else Color(0xFF8E8E93),
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "iconTint"
    )

    val labelColor by animateColorAsState(
        targetValue = if (selected) AtharWhite else Color(0xFF8E8E93),
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "labelColor"
    )

    val bgColor by animateColorAsState(
        targetValue = if (selected) Color(0xFF2C2C2E) else Color.Transparent,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "bgColor"
    )

    val itemPadding by animateDpAsState(
        targetValue = if (selected) 14.dp else 10.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "itemPadding"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(bgColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = itemPadding, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (selected) screen.selectedIcon else screen.unselectedIcon,
                contentDescription = screen.label,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
            if (selected) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = screen.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = labelColor
                )
            }
        }
    }
}

package com.athar.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Mosque
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Mosque
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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

    // Combined dock capsule tabs: Home + Essence
    object Corner : Screen(
        "corner", R.string.nav_services,
        Icons.Rounded.Mosque, Icons.Outlined.Mosque
    )

    object Home : Screen(
        "home", R.string.nav_home,
        Icons.Rounded.Home, Icons.Outlined.Home
    )
}

// Detail screens (not in the dock — dock highlights their parent)
object DetailRoutes {
    const val QURAN = "quran"
    const val QIBLA = "qibla"
    const val DUAS = "duas"
}

// Combined dock items: Home, Essence
val dockItems = listOf(
    Screen.Home,
    Screen.Corner
)

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isDetailScreen = currentRoute in listOf(
        DetailRoutes.QURAN,
        DetailRoutes.QIBLA,
        DetailRoutes.DUAS
    )

    Scaffold(
        bottomBar = {
            if (!isDetailScreen) {
                AtharNavBar(navController)
            }
        },
        containerColor = AtharBackground
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(if (isDetailScreen) PaddingValues(0.dp) else innerPadding)
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
 * Bottom navigation: slim frosted-glass dock with the two tabs packed
 * close together in the center (static, no expanding animation) +
 * standalone Settings circle. In RTL (Arabic) the dock starts from the
 * right and Settings sits alone on the left; mirrored in LTR.
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
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isRtl) {
                // In RTL: first child in Row is placed on the RIGHT side.
                // Combined Dock on the RIGHT, Standalone Settings on the LEFT.
                DockCapsule(
                    isSelected = ::isSelected,
                    onSelect = { navController.navigateToTab(it) }
                )
                Spacer(modifier = Modifier.width(10.dp))
                StandaloneSettingsButton(
                    selected = isSelected(Screen.Settings),
                    onClick = { navController.navigateToTab(Screen.Settings) }
                )
            } else {
                // In LTR: first child in Row is placed on the LEFT side.
                // Standalone Settings on the LEFT, Combined Dock on the RIGHT.
                StandaloneSettingsButton(
                    selected = isSelected(Screen.Settings),
                    onClick = { navController.navigateToTab(Screen.Settings) }
                )
                Spacer(modifier = Modifier.width(10.dp))
                DockCapsule(
                    isSelected = ::isSelected,
                    onSelect = { navController.navigateToTab(it) }
                )
            }
        }
    }
}

/**
 * Combined floating capsule dock — frosted glass, compact squircle tabs
 * closely mimicking the Islamic Figures reference screenshot.
 */
@Composable
private fun DockCapsule(
    modifier: Modifier = Modifier,
    isSelected: (Screen) -> Boolean,
    onSelect: (Screen) -> Unit
) {
    Box(
        modifier = modifier
            .height(60.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(30.dp),
                spotColor = Color.Black.copy(alpha = 0.6f)
            )
            .clip(RoundedCornerShape(30.dp))
            .background(AtharNavbarBg.copy(alpha = 0.85f))
            .border(1.dp, AtharNavbarBorder.copy(alpha = 0.85f), RoundedCornerShape(30.dp))
            .padding(horizontal = 6.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally)
        ) {
            dockItems.forEach { screen ->
                DockNavItem(
                    screen = screen,
                    selected = isSelected(screen),
                    onClick = { onSelect(screen) }
                )
            }
        }
    }
}

/** Standalone squircle floating button (Settings tab) — frosted glass. */
@Composable
private fun StandaloneSettingsButton(
    selected: Boolean,
    onClick: () -> Unit
) {
    val screen = Screen.Settings
    val iconTint = if (selected) AtharPrimaryLight else AtharNavIconInactive
    val labelColor = if (selected) AtharTextPrimary else AtharNavIconInactive

    Box(
        modifier = Modifier
            .size(width = 66.dp, height = 60.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(26.dp),
                spotColor = Color.Black.copy(alpha = 0.6f)
            )
            .clip(RoundedCornerShape(26.dp))
            .background(
                if (selected) AtharNavPillSelected.copy(alpha = 0.95f)
                else AtharNavbarBg.copy(alpha = 0.85f)
            )
            .border(
                1.dp,
                if (selected) AtharPrimary.copy(alpha = 0.45f) else AtharNavbarBorder.copy(alpha = 0.85f),
                RoundedCornerShape(26.dp)
            )
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
                modifier = Modifier.size(21.dp)
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = stringResource(screen.labelResId),
                fontFamily = ThmanyahSans,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 10.sp,
                color = labelColor
            )
        }
    }
}

/**
 * Compact dock item — vertical squircle tab.
 * Selected: dark sage green squircle pill enclosing icon & label.
 * Unselected: subtle icon & label without background pill.
 */
@Composable
private fun DockNavItem(
    screen: Screen,
    selected: Boolean,
    onClick: () -> Unit
) {
    val iconTint = if (selected) AtharPrimaryLight else AtharNavIconInactive
    val labelColor = if (selected) AtharTextPrimary else AtharNavIconInactive

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(22.dp))
            .background(
                if (selected) AtharNavPillSelected.copy(alpha = 0.95f) else Color.Transparent
            )
            .then(
                if (selected) {
                    Modifier.border(1.dp, AtharPrimary.copy(alpha = 0.35f), RoundedCornerShape(22.dp))
                } else Modifier
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = if (selected) 18.dp else 14.dp, vertical = 6.dp),
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
                modifier = Modifier.size(21.dp)
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = stringResource(screen.labelResId),
                fontFamily = ThmanyahSans,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 10.5.sp,
                color = labelColor
            )
        }
    }
}

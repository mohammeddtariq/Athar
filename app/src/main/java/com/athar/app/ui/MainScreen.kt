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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import com.athar.app.R
import com.athar.app.data.AppPreferences
import com.athar.app.ui.components.edgeMotionBlur
import com.athar.app.ui.components.rememberMotionBlurState
import com.athar.app.ui.corner.AppAnnouncementDialog
import kotlinx.coroutines.launch
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
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.GraphicEq
import com.athar.app.ui.corner.AlAmanahAnnouncementDialog
import com.athar.app.ui.corner.AlAmanahScreen
import com.athar.app.ui.corner.DuasScreen
import com.athar.app.ui.corner.QiblaScreen
import com.athar.app.ui.corner.QuranScreen
import com.athar.app.ui.home.HomeScreen
import com.athar.app.ui.search.UnifiedSearchOverlay
import com.athar.app.ui.settings.SettingsScreen
import com.athar.app.ui.theme.AtharBackground
import com.athar.app.ui.theme.AtharNavGlow
import com.athar.app.ui.theme.AtharNavIconActive
import com.athar.app.ui.theme.AtharNavIconInactive
import com.athar.app.ui.theme.AtharNavPillBorder
import com.athar.app.ui.theme.AtharNavPillSelected
import com.athar.app.ui.theme.AtharNavbarBg
import com.athar.app.ui.theme.AtharNavbarBorder
import com.athar.app.ui.theme.AtharPrimary
import com.athar.app.ui.theme.AtharPrimaryLight
import com.athar.app.ui.theme.AtharTextOnPrimary
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

    object Amanah : Screen(
        route = "amanah",
        labelResId = R.string.nav_amanah,
        selectedVector = Icons.Rounded.GraphicEq,
        unselectedVector = Icons.Outlined.GraphicEq
    )
}

val allNavScreens = listOf(
    Screen.Home,
    Screen.Quran,
    Screen.Duas,
    Screen.Qibla
)

private val TabMotionEasing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)

@Composable
fun MainScreen(
    openWidgetSettings: Boolean = false,
    onWidgetSettingsHandled: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val motionBlur = rememberMotionBlurState()
    val appPrefs = remember { AppPreferences(context.applicationContext) }
    val hasSeenDirayahAnnouncement by appPrefs.hasSeenDirayahAnnouncement.collectAsState(initial = null)
    val hasSeenAmanahAnnouncement by appPrefs.hasSeenAmanahAnnouncement.collectAsState(initial = null)
    val motionBlurEnabled by appPrefs.motionBlurEnabled.collectAsState(initial = true)
    var showDirayahAnnouncement by remember { mutableStateOf(false) }
    var showAmanahAnnouncement by remember { mutableStateOf(false) }
    var openDirayahDirectly by remember { mutableStateOf(false) }
    var showUnifiedSearch by remember { mutableStateOf(false) }
    var quranSearchTarget by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var duasSearchTarget by remember { mutableStateOf<Pair<Int, Int?>?>(null) }

    LaunchedEffect(hasSeenAmanahAnnouncement, hasSeenDirayahAnnouncement) {
        val amanahSeen = hasSeenAmanahAnnouncement ?: return@LaunchedEffect
        val dirayahSeen = hasSeenDirayahAnnouncement ?: return@LaunchedEffect
        if (!amanahSeen) {
            showAmanahAnnouncement = true
        } else if (!dirayahSeen) {
            showDirayahAnnouncement = true
        }
    }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    var isQuranReading by remember { mutableStateOf(false) }
    var targetSettingsSection by remember { mutableStateOf<String?>(null) }
    var isNavBarVisible by remember { mutableStateOf(true) }
    // Measured height of the floating dock, so content can sit exactly on top of it
    // instead of guessing a constant that drifts at high display zoom.
    var navDockTopInset by remember { mutableStateOf(104.dp) }

    val isImmersive = isQuranReading || currentRoute == Screen.Amanah.route

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                motionBlur.onScroll(available.y)
                if (available.y < -12f) {
                    isNavBarVisible = false
                } else if (available.y > 12f) {
                    isNavBarVisible = true
                }
                return Offset.Zero
            }
        }
    }

    LaunchedEffect(currentRoute) {
        isNavBarVisible = true
    }

    LaunchedEffect(openWidgetSettings) {
        if (openWidgetSettings) {
            targetSettingsSection = "widgets"
            navController.navigateToTab(Screen.Settings)
            onWidgetSettingsHandled()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AtharBackground)
            .nestedScroll(nestedScrollConnection)
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .edgeMotionBlur(state = motionBlur, enabled = !isImmersive && motionBlurEnabled),
            enterTransition = {
                fadeIn(animationSpec = tween(260, easing = TabMotionEasing)) +
                    scaleIn(
                        initialScale = 0.975f,
                        animationSpec = spring(dampingRatio = 0.92f, stiffness = 420f)
                    ) +
                    slideInVertically(
                        initialOffsetY = { (it * 0.025f).toInt() },
                        animationSpec = spring(dampingRatio = 0.92f, stiffness = 420f)
                    )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(170, easing = TabMotionEasing)) +
                    scaleOut(targetScale = 0.988f, animationSpec = tween(170, easing = TabMotionEasing))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(260, easing = TabMotionEasing)) +
                    scaleIn(
                        initialScale = 0.975f,
                        animationSpec = spring(dampingRatio = 0.92f, stiffness = 420f)
                    ) +
                    slideInVertically(
                        initialOffsetY = { (it * 0.025f).toInt() },
                        animationSpec = spring(dampingRatio = 0.92f, stiffness = 420f)
                    )
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(170, easing = TabMotionEasing)) +
                    scaleOut(targetScale = 0.988f, animationSpec = tween(170, easing = TabMotionEasing))
            }
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onOpenSettings = { navController.navigateToTab(Screen.Settings) },
                    onOpenNotifications = {
                        targetSettingsSection = "notifications"
                        navController.navigateToTab(Screen.Settings)
                    },
                    onNavigateToDuas = { navController.navigateToTab(Screen.Duas) },
                    onOpenSearch = { showUnifiedSearch = true }
                )
            }
            composable(Screen.Quran.route) {
                QuranScreen(
                    onBack = { navController.navigateToTab(Screen.Home) },
                    onReadingModeChanged = { isReading -> isQuranReading = isReading },
                    openDirayahDirectly = openDirayahDirectly,
                    onDirayahDirectlyConsumed = { openDirayahDirectly = false },
                    initialSurahNumber = quranSearchTarget?.first,
                    initialAyahNumber = quranSearchTarget?.second,
                    onInitialTargetConsumed = { quranSearchTarget = null },
                    isNavBarVisible = isNavBarVisible,
                    navDockTopInset = navDockTopInset
                )
            }
            composable(Screen.Duas.route) {
                DuasScreen(
                    onBack = { navController.navigateToTab(Screen.Home) },
                    initialLibraryChapter = duasSearchTarget?.first,
                    initialDuaIndex = duasSearchTarget?.second,
                    onInitialTargetConsumed = { duasSearchTarget = null }
                )
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
            composable(Screen.Amanah.route) {
                AlAmanahScreen(
                    onBack = { navController.navigateToTab(Screen.Home) }
                )
            }
        }

        // Floating Nav Dock (hides when scrolling down)
        AnimatedVisibility(
            visible = !isImmersive && isNavBarVisible,
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
            AtharNavBar(
                navController = navController,
                onTopInsetChange = { navDockTopInset = it }
            )
        }

        if (showUnifiedSearch) {
            UnifiedSearchOverlay(
                onDismiss = { showUnifiedSearch = false },
                onOpenVerse = { surah, ayah ->
                    showUnifiedSearch = false
                    quranSearchTarget = surah to ayah
                    navController.navigateToTab(Screen.Quran)
                },
                onOpenDua = { chapter, duaIndex ->
                    showUnifiedSearch = false
                    duasSearchTarget = chapter to duaIndex
                    navController.navigateToTab(Screen.Duas)
                }
            )
        }

        // Feature Announcements (Al-Amanah or Al-Dirayah)
        if (showAmanahAnnouncement) {
            AlAmanahAnnouncementDialog(
                onExplore = {
                    showAmanahAnnouncement = false
                    scope.launch { appPrefs.setHasSeenAmanahAnnouncement(true) }
                    navController.navigateToTab(Screen.Amanah)
                },
                onDismiss = {
                    showAmanahAnnouncement = false
                    scope.launch { appPrefs.setHasSeenAmanahAnnouncement(true) }
                    if (hasSeenDirayahAnnouncement == false) {
                        showDirayahAnnouncement = true
                    }
                }
            )
        } else if (showDirayahAnnouncement) {
            AppAnnouncementDialog(
                onExplore = {
                    showDirayahAnnouncement = false
                    scope.launch { appPrefs.setHasSeenAppAnnouncement(true) }
                    openDirayahDirectly = true
                    navController.navigateToTab(Screen.Quran)
                },
                onDismiss = {
                    showDirayahAnnouncement = false
                    scope.launch { appPrefs.setHasSeenAppAnnouncement(true) }
                }
            )
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
    modifier: Modifier = Modifier,
    onTopInsetChange: (Dp) -> Unit = {}
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    fun isSelected(screen: Screen): Boolean {
        return currentDestination?.hierarchy?.any { it.route == screen.route } == true
    }

    // Force Left-to-Right layout order regardless of active language (Arabic or English)
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        val navBarBottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        val effectiveBottomPadding = maxOf(navBarBottomInset, 20.dp) + 6.dp
        val density = LocalDensity.current

        BoxWithConstraints(
            modifier = modifier
                .fillMaxWidth()
                // Measured before the padding is applied, so the reported height is
                // the full space the dock occupies from the bottom of the screen.
                .onGloballyPositioned { coordinates ->
                    onTopInsetChange(with(density) { coordinates.size.height.toDp() })
                }
                .padding(bottom = effectiveBottomPadding),
            contentAlignment = Alignment.Center
        ) {
            val isCompact = maxWidth < 380.dp
            val outerPadHorizontal = if (isCompact) 8.dp else 12.dp
            val outerPadVertical = 8.dp
            val dockInnerPadH = if (isCompact) 8.dp else 10.dp
            val dockInnerPadV = if (isCompact) 6.dp else 7.dp
            val itemSpacing = if (isCompact) 3.dp else 5.dp
            val unselectedPadH = if (isCompact) 10.dp else 13.dp
            val selectedPadH = if (isCompact) 14.dp else 18.dp
            val iconSize = if (isCompact) 22.dp else 24.dp
            val fontSize = if (isCompact) 12.5.sp else 13.5.sp

            Row(
                modifier = Modifier.padding(horizontal = outerPadHorizontal, vertical = outerPadVertical),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                // Main Navigation Dock Capsule
                Box(
                    modifier = Modifier
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

                // Standalone Floating Al-Amanah Action Button (Beside the Dock - Squircle Style)
                val isAmanahSelected = isSelected(Screen.Amanah)
                val amanahButtonSize = if (isCompact) 52.dp else 58.dp
                val amanahCornerRadius = if (isCompact) 18.dp else 22.dp
                val amanahInteraction = remember { MutableInteractionSource() }
                val isAmanahPressed by amanahInteraction.collectIsPressedAsState()
                val amanahScale by animateFloatAsState(
                    targetValue = if (isAmanahPressed) 0.90f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "amanahBtnScale"
                )
                val amanahContainer by animateColorAsState(
                    targetValue = if (isAmanahSelected) AtharPrimaryLight else AtharPrimary,
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                    label = "amanahBtnContainer"
                )
                val amanahBorder by animateColorAsState(
                    targetValue = if (isAmanahSelected) AtharPrimaryLight else AtharPrimaryLight.copy(alpha = 0.55f),
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                    label = "amanahBtnBorder"
                )
                val amanahInk by animateColorAsState(
                    targetValue = AtharTextOnPrimary,
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                    label = "amanahBtnInk"
                )

                Box(
                    modifier = Modifier
                        .scale(amanahScale)
                        .size(amanahButtonSize)
                        .shadow(
                            elevation = 14.dp,
                            shape = RoundedCornerShape(amanahCornerRadius),
                            spotColor = AtharPrimary.copy(alpha = 0.45f),
                            ambientColor = Color.Black.copy(alpha = 0.60f)
                        )
                        .clip(RoundedCornerShape(amanahCornerRadius))
                        .background(amanahContainer)
                        .border(
                            width = 1.dp,
                            color = amanahBorder,
                            shape = RoundedCornerShape(amanahCornerRadius)
                        )
                        .clickable(
                            interactionSource = amanahInteraction,
                            indication = null,
                            onClick = { navController.navigateToTab(Screen.Amanah) }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.GraphicEq,
                        contentDescription = "Al-Amanah",
                        tint = amanahInk,
                        modifier = Modifier.size(if (isCompact) 23.dp else 26.dp)
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
        animationSpec = spring(dampingRatio = 0.90f, stiffness = 650f),
        label = "pillBg"
    )

    val pillBorderColor by animateColorAsState(
        targetValue = if (selected) AtharNavPillBorder else Color.Transparent,
        animationSpec = spring(dampingRatio = 0.90f, stiffness = 650f),
        label = "pillBorder"
    )

    val iconTint by animateColorAsState(
        targetValue = if (selected) AtharNavIconActive else AtharNavIconInactive,
        animationSpec = spring(dampingRatio = 0.90f, stiffness = 700f),
        label = "iconTint"
    )

    val labelColor by animateColorAsState(
        targetValue = if (selected) AtharNavIconActive else AtharNavIconInactive,
        animationSpec = spring(dampingRatio = 0.90f, stiffness = 700f),
        label = "labelColor"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .height(if (fontSize < 13.sp) 48.dp else 52.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(pillBackground)
            .border(
                width = if (selected) 1.dp else 0.dp,
                color = pillBorderColor,
                shape = RoundedCornerShape(26.dp)
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

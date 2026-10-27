package com.athar.app.ui.corner

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import com.athar.app.R
import com.athar.app.data.AppPreferences
import com.athar.app.data.DuaLibrary
import com.athar.app.data.DuaLibraryRepository
import com.athar.app.data.LibraryChapter
import com.athar.app.data.LibraryDua
import com.athar.app.data.LibrarySearchHit
import com.athar.app.data.NumberStylePreference
import com.athar.app.data.formatDigits
import com.athar.app.data.search
import com.athar.app.ui.components.LocalizedTextDirection
import com.athar.app.ui.components.DuaCompletionCelebration
import com.athar.app.ui.components.PatternScaffold
import com.athar.app.ui.share.ShareCardRenderer
import com.athar.app.ui.theme.AtharBackground
import com.athar.app.ui.theme.AtharCardBorder
import com.athar.app.ui.theme.AtharCardSurface
import com.athar.app.ui.theme.AtharPrimary
import com.athar.app.ui.theme.AtharPrimaryLight
import com.athar.app.ui.theme.AtharPrimarySubtle
import com.athar.app.ui.theme.AtharTextOnPrimary
import com.athar.app.ui.theme.AtharTextPrimary
import com.athar.app.ui.theme.AtharTextSecondary
import com.athar.app.ui.theme.ThmanyahSans
import com.athar.app.ui.theme.ThmanyahSerifDisplay
import com.athar.app.ui.theme.ThmanyahSerifText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Duas & Adhkar browser with:
 * - Scroll-aware unified top bar (back button, titles, category chips, and sub-selectors hide on scroll down, reappear on scroll up)
 * - In-card hadith virtue & guidelines notes (eliminates bottom floating popup and nav bar collisions)
 * - Sub-collection selector for After Prayer (Other Prayers, Fajr, Maghrib)
 * - 100% collision-free LazyColumn keys preventing any scroll crashes
 * - Interactive dhikr counter with haptics, completion checkmark, and reset
 * - Western / Arabic-Indic number preferences formatting
 */
@Composable
fun DuasScreen(
    onBack: () -> Unit = {},
    initialLibraryChapter: Int? = null,
    initialDuaIndex: Int? = null,
    onInitialTargetConsumed: () -> Unit = {}
) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val prefs = remember { AppPreferences(context.applicationContext) }

    val numberStyle by prefs.numberStyle.collectAsState(initial = NumberStylePreference.WESTERN)
    val langCode by prefs.selectedLanguage.collectAsState(initial = "ar")
    val isArabic = langCode == "ar"
    val shareDuaSubject = stringResource(R.string.share_dua_subject)
    val shareChooserTitle = stringResource(R.string.duas_share)

    // Currently selected primary category
    var selectedCategoryId by rememberSaveable { mutableStateOf(duaCategories.first().id) }
    val currentCategory = remember(selectedCategoryId) {
        duaCategories.firstOrNull { it.id == selectedCategoryId } ?: duaCategories.first()
    }

    // Sub-collection for After Prayer ("other", "fajr", "maghrib")
    var selectedPrayerSubCategory by rememberSaveable { mutableStateOf("other") }

    // Full Hisn al-Muslim library (loaded from assets on first use)
    var library by remember { mutableStateOf<DuaLibrary?>(null) }
    var selectedLibraryChapter by rememberSaveable { mutableStateOf<Int?>(null) }
    var libraryQuery by rememberSaveable { mutableStateOf("") }
    var pendingLibraryScroll by rememberSaveable { mutableStateOf<Int?>(null) }
    LaunchedEffect(selectedCategoryId) {
        if (selectedCategoryId == "library" && library == null) {
            library = withContext(Dispatchers.IO) { DuaLibraryRepository.load(context) }
        }
    }
    LaunchedEffect(initialLibraryChapter, initialDuaIndex) {
        val chapter = initialLibraryChapter ?: return@LaunchedEffect
        if (library == null) {
            library = withContext(Dispatchers.IO) { DuaLibraryRepository.load(context) }
        }
        libraryQuery = ""
        selectedCategoryId = "library"
        selectedLibraryChapter = chapter
        pendingLibraryScroll = initialDuaIndex
        onInitialTargetConsumed()
    }
    val libraryChapter = remember(library, selectedLibraryChapter) {
        selectedLibraryChapter?.let { number -> library?.chapters?.firstOrNull { it.number == number } }
    }
    val libraryHits = remember(library, libraryQuery) {
        if (libraryQuery.isBlank()) emptyList() else library?.search(libraryQuery).orEmpty()
    }

    // Active list of duas depending on category and sub-category
    val activeDuas = remember(selectedCategoryId, selectedPrayerSubCategory, libraryChapter) {
        when {
            selectedCategoryId == "library" -> libraryChapter?.duas?.map { it.toDua() } ?: emptyList()
            selectedCategoryId == "after_prayer" -> when (selectedPrayerSubCategory) {
                "fajr" -> afterPrayerFajrDuas
                "maghrib" -> afterPrayerMaghribDuas
                else -> afterPrayerOtherDuas
            }
            else -> currentCategory.duas
        }
    }

    val isLibraryBrowsing = selectedCategoryId == "library" && selectedLibraryChapter == null
    val isLibrarySearching = isLibraryBrowsing && libraryQuery.isNotBlank()

    // Dynamic titles
    val displayTitleAr = when {
        selectedCategoryId == "library" -> libraryChapter?.titleAr ?: stringResource(R.string.duas_library_title)
        selectedCategoryId == "after_prayer" -> when (selectedPrayerSubCategory) {
            "fajr" -> stringResource(R.string.duas_after_fajr_title)
            "maghrib" -> stringResource(R.string.duas_after_maghrib_title)
            else -> stringResource(R.string.duas_after_prayers_title)
        }
        else -> currentCategory.titleAr
    }
    val displayTitleEn = when {
        selectedCategoryId == "library" -> libraryChapter?.titleEn ?: stringResource(R.string.duas_library_title)
        selectedCategoryId == "after_prayer" -> when (selectedPrayerSubCategory) {
            "fajr" -> stringResource(R.string.duas_after_fajr_title)
            "maghrib" -> stringResource(R.string.duas_after_maghrib_title)
            else -> stringResource(R.string.duas_after_prayers_title)
        }
        else -> currentCategory.titleEn
    }

    // Per-dua session dhikr counts, kept across config changes and tab switches
    var counts by rememberSaveable(
        stateSaver = listSaver<Map<String, Int>, Any>(
            save = { map -> map.entries.flatMap { listOf(it.key, it.value) } },
            restore = { flat ->
                flat.chunked(2)
                    .filter { it.size == 2 }
                    .associate { (it[0] as String) to (it[1] as Int) }
            }
        )
    ) { mutableStateOf(emptyMap()) }

    // Congratulate when the tap completes the last remaining dua of the visible collection
    var showCelebration by remember(activeDuas) { mutableStateOf(false) }

    // Scroll state & scroll-aware floating top bar
    val listState = rememberLazyListState()
    var isTopBarVisible by remember { mutableStateOf(true) }

    // Jump to the matching dua once its chapter is on screen.
    LaunchedEffect(selectedLibraryChapter, activeDuas.size, pendingLibraryScroll) {
        val target = pendingLibraryScroll
        if (target != null && selectedLibraryChapter != null && activeDuas.isNotEmpty()) {
            pendingLibraryScroll = null
            listState.scrollToItem((target + 1).coerceAtMost(activeDuas.size))
        }
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val dy = available.y
                if (dy < -6f) {
                    isTopBarVisible = false
                } else if (dy > 6f) {
                    isTopBarVisible = true
                }
                return Offset.Zero
            }
        }
    }

    // Always keep visible when near the very top
    val isNearTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset <= 30
        }
    }
    val shouldShowTopBar = isTopBarVisible || isNearTop

    // The header is as tall as its content, and that changes with display zoom and
    // font scale, so the list is padded from the measured height instead of the
    // constant that used to let the first banner's rule ride up into the chips.
    val density = LocalDensity.current
    var headerHeightPx by remember { mutableStateOf(0) }
    val measuredHeaderHeight = with(density) { headerHeightPx.toDp() }
    val listTopPadding = if (measuredHeaderHeight > 0.dp) {
        measuredHeaderHeight + 10.dp
    } else {
        if (selectedCategoryId == "after_prayer" ||
            (selectedCategoryId == "library" && selectedLibraryChapter != null)
        ) 244.dp else 199.dp
    }

    val backToList: () -> Unit = {
        if (selectedCategoryId == "library" && selectedLibraryChapter != null) {
            selectedLibraryChapter = null
            scope.launch { listState.scrollToItem(0) }
        } else {
            onBack()
        }
    }
    BackHandler(enabled = selectedCategoryId == "library" && selectedLibraryChapter != null) {
        selectedLibraryChapter = null
    }

    PatternScaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(nestedScrollConnection)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = listTopPadding,
                    bottom = 150.dp
                )
            ) {
                // Category Banner Header
                item(key = "banner_${selectedCategoryId}_${if (selectedCategoryId == "after_prayer") selectedPrayerSubCategory else ""}_${selectedLibraryChapter ?: ""}") {
                    CategoryHeaderBanner(
                        titleAr = displayTitleAr,
                        titleEn = displayTitleEn,
                        countText = when {
                            isLibrarySearching -> countLabel(
                                R.plurals.duas_result_count,
                                libraryHits.size,
                                numberStyle
                            )
                            isLibraryBrowsing -> countLabel(
                                R.plurals.duas_chapter_count,
                                library?.chapters?.size ?: 0,
                                numberStyle
                            )
                            else -> countLabel(
                                R.plurals.duas_dua_count,
                                activeDuas.size,
                                numberStyle
                            )
                        },
                        isArabic = isArabic
                    )
                }

                if (isLibraryBrowsing) {
                    val chapters = library?.chapters
                    item(key = "library_search") {
                        LibrarySearchField(
                            query = libraryQuery,
                            onQueryChange = { libraryQuery = it },
                            isArabic = isArabic
                        )
                    }
                    if (chapters == null) {
                        item(key = "library_loading") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = AtharPrimaryLight,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                    } else if (isLibrarySearching) {
                        if (libraryHits.isEmpty()) {
                            item(key = "library_search_empty") {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp, vertical = 36.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stringResource(R.string.duas_search_empty),
                                        fontFamily = ThmanyahSans,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 13.sp,
                                        color = AtharTextSecondary
                                    )
                                }
                            }
                        } else {
                            items(
                                items = libraryHits,
                                key = { "lib_hit_${it.chapter.number}_${it.duaIndex ?: -1}" }
                            ) { hit ->
                                LibrarySearchResultRow(
                                    hit = hit,
                                    isArabic = isArabic,
                                    numberStyle = numberStyle,
                                    onOpen = {
                                        libraryQuery = ""
                                        selectedLibraryChapter = hit.chapter.number
                                        pendingLibraryScroll = hit.duaIndex
                                        if (hit.duaIndex == null) {
                                            scope.launch { listState.scrollToItem(0) }
                                        }
                                    }
                                )
                            }
                        }
                    } else {
                        items(chapters, key = { "lib_chapter_${it.number}" }) { chapter ->
                            LibraryChapterRow(
                                chapter = chapter,
                                isArabic = isArabic,
                                numberStyle = numberStyle,
                                onOpen = {
                                    selectedLibraryChapter = chapter.number
                                    scope.launch { listState.scrollToItem(0) }
                                }
                            )
                        }
                        item(key = "library_credit") {
                            LibraryAttribution(
                                text = library?.attribution.orEmpty(),
                                isArabic = isArabic
                            )
                        }
                    }
                } else {
                    // Duas List — unique key = dua.id (100% collision free)
                    itemsIndexed(
                        items = activeDuas,
                        key = { _, dua -> dua.id }
                    ) { index, dua ->
                        val target = dua.repeat
                        val count = counts[dua.id] ?: 0
                        val isDone = count >= target

                        DuaItemCard(
                            index = index,
                            totalCount = activeDuas.size,
                            dua = dua,
                            count = count,
                            target = target,
                            isDone = isDone,
                            isArabic = isArabic,
                            numberStyle = numberStyle,
                            onCount = {
                                if (count < target) {
                                    val next = count + 1
                                    val remaining = activeDuas.count { other ->
                                        (counts[other.id] ?: 0) < other.repeat
                                    }
                                    counts = counts + (dua.id to next)
                                    if (next >= target && remaining <= 1) {
                                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                        showCelebration = true
                                    } else {
                                        haptics.performHapticFeedback(
                                            if (next >= target) HapticFeedbackType.LongPress
                                            else HapticFeedbackType.TextHandleMove
                                        )
                                    }
                                }
                            },
                            onReset = {
                                if (count > 0) {
                                    counts = counts + (dua.id to 0)
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                            },
                            onShare = {
                                scope.launch {
                                    val rendered = withContext(Dispatchers.IO) {
                                        runCatching {
                                            ShareCardRenderer.renderDua(
                                                context = context,
                                                arabic = dua.arabic,
                                                translation = dua.translationFor(isArabic),
                                                note = if (isArabic) dua.noteAr else dua.noteEn,
                                                source = dua.source,
                                                isArabic = isArabic
                                            )
                                        }
                                    }
                                    val shared = rendered.getOrNull()?.let { bitmap ->
                                        ShareCardRenderer.shareBitmap(
                                            context = context,
                                            bitmap = bitmap,
                                            subject = shareDuaSubject,
                                            chooserTitle = shareChooserTitle
                                        )
                                    } ?: false
                                    if (!shared) {
                                        Toast.makeText(context, R.string.share_failed, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        )
                    }
                }
            }

            // Scroll-Aware Floating Top Bar (the entire header: back button, title, chips, sub-chips)
            AnimatedVisibility(
                visible = shouldShowTopBar,
                enter = fadeIn(tween(220)) + slideInVertically(tween(220)) { -it },
                exit = fadeOut(tween(180)) + slideOutVertically(tween(180)) { -it },
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        // Measured before the background and insets, so the reported
                        // height covers the whole header including the status bar.
                        .onGloballyPositioned { coordinates ->
                            headerHeightPx = coordinates.size.height
                        }
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    AtharBackground,
                                    AtharBackground.copy(alpha = 0.98f),
                                    AtharBackground.copy(alpha = 0.92f),
                                    Color.Transparent
                                )
                            )
                        )
                        .statusBarsPadding()
                        .padding(bottom = 8.dp)
                ) {
                    // Main Navigation Bar (Back button + screen title)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = backToList
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = null,
                                tint = AtharTextPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(Modifier.weight(1f))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                stringResource(R.string.duas_title),
                                fontFamily = ThmanyahSans,
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp,
                                color = AtharTextPrimary
                            )
                            Text(
                                if (isArabic) displayTitleAr else displayTitleEn,
                                fontFamily = ThmanyahSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp,
                                color = AtharPrimaryLight
                            )
                        }
                        Spacer(Modifier.weight(1f))
                        Spacer(Modifier.size(44.dp))
                    }

                    HisnAlMuslimHero(
                        isSelected = selectedCategoryId == "library",
                        onClick = {
                            if (selectedCategoryId != "library" || selectedLibraryChapter != null) {
                                selectedCategoryId = "library"
                                selectedLibraryChapter = null
                                libraryQuery = ""
                                scope.launch { listState.scrollToItem(0) }
                            }
                        }
                    )

                    // Main categories row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 14.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        duaCategories.filter { it.id != "library" }.forEach { category ->
                            val isSelected = category.id == selectedCategoryId
                            CategoryChip(
                                title = if (isArabic) category.titleAr else category.titleEn,
                                isSelected = isSelected,
                                onClick = {
                                    if (!isSelected) {
                                        selectedCategoryId = category.id
                                        selectedLibraryChapter = null
                                        libraryQuery = ""
                                        scope.launch { listState.scrollToItem(0) }
                                    }
                                }
                            )
                        }
                    }

                    // Sub-selector for the Hisn al-Muslim library (back to the chapter list)
                    if (selectedCategoryId == "library" && selectedLibraryChapter != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 14.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            PrayerSubChip(
                                title = stringResource(R.string.duas_all_chapters),
                                isSelected = false,
                                onClick = {
                                    selectedLibraryChapter = null
                                    scope.launch { listState.scrollToItem(0) }
                                }
                            )
                        }
                    }

                    // Sub-selector for After Prayer (Other Prayers, Fajr, Maghrib)
                    if (selectedCategoryId == "after_prayer") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 14.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            PrayerSubChip(
                                title = stringResource(R.string.duas_other_prayers),
                                isSelected = selectedPrayerSubCategory == "other",
                                onClick = {
                                    if (selectedPrayerSubCategory != "other") {
                                        selectedPrayerSubCategory = "other"
                                        scope.launch { listState.scrollToItem(0) }
                                    }
                                }
                            )
                            PrayerSubChip(
                                title = stringResource(R.string.duas_fajr),
                                isSelected = selectedPrayerSubCategory == "fajr",
                                onClick = {
                                    if (selectedPrayerSubCategory != "fajr") {
                                        selectedPrayerSubCategory = "fajr"
                                        scope.launch { listState.scrollToItem(0) }
                                    }
                                }
                            )
                            PrayerSubChip(
                                title = stringResource(R.string.duas_maghrib),
                                isSelected = selectedPrayerSubCategory == "maghrib",
                                onClick = {
                                    if (selectedPrayerSubCategory != "maghrib") {
                                        selectedPrayerSubCategory = "maghrib"
                                        scope.launch { listState.scrollToItem(0) }
                                    }
                                }
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = showCelebration,
                enter = fadeIn(tween(350)),
                exit = fadeOut(tween(250)),
                modifier = Modifier.matchParentSize()
            ) {
                DuaCompletionCelebration(
                    title = stringResource(R.string.home_allah_accept),
                    body = stringResource(R.string.duas_celebration_done),
                    onDismiss = { showCelebration = false }
                )
            }
        }
    }
}

@Composable
private fun countLabel(resId: Int, count: Int, numberStyle: NumberStylePreference): String =
    pluralStringResource(resId, count, formatDigits("$count", numberStyle))

@Composable
private fun HisnAlMuslimHero(
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(20.dp)
    val accent = if (isSelected) 0.85f else 0.40f

    Row(
        modifier = Modifier
            .padding(horizontal = 14.dp, vertical = 5.dp)
            .fillMaxWidth()
            .shadow(
                elevation = if (isSelected) 16.dp else 8.dp,
                shape = shape,
                spotColor = AtharPrimaryLight.copy(alpha = if (isSelected) 0.35f else 0.14f),
                ambientColor = Color.Black.copy(alpha = 0.50f)
            )
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    listOf(AtharPrimarySubtle, AtharBackground)
                )
            )
            .border(
                width = if (isSelected) 1.4.dp else 1.dp,
                color = AtharPrimary.copy(alpha = accent),
                shape = shape
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(AtharPrimary.copy(alpha = 0.20f))
                .border(1.dp, AtharPrimary.copy(alpha = 0.45f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.AutoMirrored.Rounded.MenuBook,
                contentDescription = null,
                tint = AtharPrimaryLight,
                modifier = Modifier.size(19.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.duas_library_title),
                fontFamily = ThmanyahSerifDisplay,
                fontWeight = FontWeight.Black,
                fontSize = 17.sp,
                color = AtharTextPrimary
            )
            Spacer(Modifier.height(1.dp))
            Text(
                text = stringResource(R.string.duas_hisn_hero_sub),
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Medium,
                fontSize = 10.5.sp,
                color = AtharTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Icon(
            Icons.AutoMirrored.Rounded.ArrowForward,
            contentDescription = null,
            tint = AtharPrimaryLight.copy(alpha = 0.85f),
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun CategoryHeaderBanner(
    titleAr: String,
    titleEn: String,
    countText: String,
    isArabic: Boolean
) {
    val mainTitle = if (isArabic) titleAr else titleEn

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(18.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(Modifier.weight(1f).height(1.dp).background(AtharCardBorder))
            Box(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .size(6.dp)
                    .background(AtharPrimaryLight, CircleShape)
            )
            Box(Modifier.weight(1f).height(1.dp).background(AtharCardBorder))
        }
        Spacer(Modifier.height(14.dp))
        Text(
            text = mainTitle,
            fontFamily = ThmanyahSerifDisplay,
            fontWeight = FontWeight.Black,
            fontSize = 20.sp,
            color = AtharTextPrimary,
            textAlign = TextAlign.Center
        )
        Text(
            text = countText,
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            letterSpacing = 1.sp,
            color = AtharPrimaryLight,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(10.dp))
    }
}

@Composable
private fun CategoryChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .then(
                if (isSelected) {
                    Modifier.shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(18.dp),
                        spotColor = AtharPrimaryLight.copy(alpha = 0.45f)
                    )
                } else Modifier
            )
            .clip(RoundedCornerShape(18.dp))
            .background(
                if (isSelected) AtharPrimary.copy(alpha = 0.26f)
                else AtharCardSurface.copy(alpha = 0.90f)
            )
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) AtharPrimaryLight else AtharCardBorder,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontFamily = ThmanyahSans,
            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
            fontSize = 12.sp,
            color = if (isSelected) AtharPrimaryLight else AtharTextSecondary
        )
    }
}

@Composable
private fun PrayerSubChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .then(
                if (isSelected) {
                    Modifier.shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(14.dp),
                        spotColor = AtharPrimaryLight.copy(alpha = 0.35f)
                    )
                } else Modifier
            )
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isSelected) AtharPrimary.copy(alpha = 0.32f)
                else AtharCardSurface.copy(alpha = 0.85f)
            )
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) AtharPrimaryLight else AtharCardBorder.copy(alpha = 0.6f),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontFamily = ThmanyahSans,
            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
            fontSize = 12.sp,
            color = if (isSelected) AtharPrimaryLight else AtharTextSecondary
        )
    }
}

@Composable
private fun LibraryChapterRow(
    chapter: LibraryChapter,
    isArabic: Boolean,
    numberStyle: NumberStylePreference,
    onOpen: () -> Unit
) {
    val primary = if (isArabic) chapter.titleAr else chapter.titleEn
    val secondary = if (isArabic) chapter.titleEn else chapter.titleAr

    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AtharCardSurface.copy(alpha = 0.88f))
            .border(1.dp, AtharCardBorder, RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onOpen
            )
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(AtharPrimary.copy(alpha = 0.16f))
                    .border(1.dp, AtharPrimary.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Text(
                        text = formatDigits(chapter.number.toString(), numberStyle),
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.5.sp,
                        color = AtharPrimaryLight
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = primary,
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    lineHeight = 19.sp,
                    color = AtharTextPrimary,
                    textAlign = TextAlign.Start,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = secondary,
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.5.sp,
                    lineHeight = 14.sp,
                    color = AtharTextSecondary.copy(alpha = 0.75f),
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.width(10.dp))
            Text(
                text = countLabel(
                    R.plurals.duas_dua_count,
                    chapter.totalDuas,
                    numberStyle
                ),
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = AtharPrimaryLight
            )
        }
    }
}

@Composable
private fun LibrarySearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    isArabic: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .height(44.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(AtharCardSurface.copy(alpha = 0.9f))
            .border(1.dp, AtharCardBorder, RoundedCornerShape(22.dp))
            .padding(start = 14.dp, end = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Rounded.Search,
            contentDescription = null,
            tint = AtharTextSecondary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(10.dp))
        Box(modifier = Modifier.weight(1f)) {
            if (query.isEmpty()) {
                Text(
                    text = stringResource(R.string.duas_search_hint),
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.5.sp,
                    color = AtharTextSecondary.copy(alpha = 0.7f)
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = TextStyle(
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = AtharTextPrimary,
                    textAlign = TextAlign.Start
                ),
                cursorBrush = SolidColor(AtharPrimaryLight),
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (query.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onQueryChange("") }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Rounded.Close,
                    contentDescription = stringResource(R.string.duas_clear_search),
                    tint = AtharTextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun LibrarySearchResultRow(
    hit: LibrarySearchHit,
    isArabic: Boolean,
    numberStyle: NumberStylePreference,
    onOpen: () -> Unit
) {
    val chapter = hit.chapter
    val dua = hit.dua
    val chapterTitle = if (isArabic) chapter.titleAr else chapter.titleEn
    val snippet = when {
        dua == null -> if (isArabic) chapter.titleEn else chapter.titleAr
        isArabic -> dua.arabic
        else -> dua.translation
    }

    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AtharCardSurface.copy(alpha = 0.88f))
            .border(1.dp, AtharCardBorder, RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onOpen
            )
            .padding(horizontal = 14.dp, vertical = 11.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(AtharPrimary.copy(alpha = 0.16f))
                    .border(1.dp, AtharPrimary.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Text(
                        text = formatDigits(chapter.number.toString(), numberStyle),
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp,
                        color = AtharPrimaryLight
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = chapterTitle,
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp,
                    lineHeight = 15.sp,
                    color = AtharPrimaryLight,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = snippet,
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = AtharTextPrimary,
                    textAlign = TextAlign.Start,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun LibraryAttribution(text: String, isArabic: Boolean) {
    if (text.isBlank()) return
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
            .padding(top = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.duas_sources_license),
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Bold,
            fontSize = 10.5.sp,
            color = AtharPrimaryLight,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(5.dp))
        LocalizedTextDirection(text) {
            Text(
                text = text,
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Medium,
                fontSize = 9.5.sp,
                lineHeight = 14.sp,
                color = AtharTextSecondary.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private fun LibraryDua.toDua(): Dua = Dua(
    id = id,
    arabic = arabic,
    translation = translation,
    source = "حصن المسلم / Hisn al-Muslim",
    repeat = repeat,
    noteAr = reference?.let { "المرجع: $it" },
    noteEn = reference?.let { "Reference: $it" }
)

@Composable
private fun DuaItemCard(
    index: Int,
    totalCount: Int,
    dua: Dua,
    count: Int,
    target: Int,
    isDone: Boolean,
    isArabic: Boolean,
    numberStyle: NumberStylePreference,
    onCount: () -> Unit,
    onReset: () -> Unit,
    onShare: () -> Unit
) {
    val shape = RoundedCornerShape(26.dp)
    val noteText = if (isArabic) dua.noteAr ?: dua.noteEn else dua.noteEn ?: dua.noteAr
    val translationText = dua.translationFor(isArabic)

    Box(
        modifier = Modifier
            .padding(horizontal = 18.dp, vertical = 7.dp)
            .fillMaxWidth()
            .shadow(
                elevation = if (isDone) 16.dp else 6.dp,
                shape = shape,
                spotColor = AtharPrimaryLight.copy(alpha = if (isDone) 0.30f else 0.10f),
                ambientColor = Color.Black.copy(alpha = 0.55f)
            )
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        if (isDone) AtharPrimarySubtle else AtharCardSurface,
                        AtharBackground
                    )
                )
            )
            .border(
                width = if (isDone) 1.2.dp else 1.dp,
                color = if (isDone) AtharPrimary.copy(alpha = 0.55f) else AtharCardBorder,
                shape = shape
            )
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onCount,
                onLongClick = onReset
            )
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = formatDigits(
                            (index + 1).toString().padStart(2, '0'),
                            numberStyle
                        ),
                        fontFamily = ThmanyahSerifDisplay,
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        color = AtharPrimaryLight.copy(alpha = 0.90f)
                    )

                    Box(
                        Modifier
                            .width(1.dp)
                            .height(20.dp)
                            .background(AtharCardBorder)
                    )

                    Text(
                        text = dua.source,
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 0.8.sp,
                        color = AtharTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onShare
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Rounded.Share,
                        contentDescription = stringResource(R.string.duas_share),
                        tint = AtharPrimaryLight.copy(alpha = 0.80f),
                        modifier = Modifier.size(17.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = dua.arabic,
                fontFamily = ThmanyahSerifText,
                fontWeight = FontWeight.SemiBold,
                fontSize = 21.sp,
                lineHeight = 40.sp,
                color = AtharTextPrimary,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(14.dp))

            Box(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(AtharCardBorder.copy(alpha = 0.55f))
            )

            Spacer(Modifier.height(12.dp))

            LocalizedTextDirection(translationText) {
                Text(
                    text = translationText,
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.5.sp,
                    lineHeight = 19.sp,
                    color = AtharTextSecondary,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (!noteText.isNullOrBlank()) {
                Spacer(Modifier.height(10.dp))
                LocalizedTextDirection(noteText) {
                    Text(
                        text = noteText,
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.SemiBold,
                        fontStyle = FontStyle.Italic,
                        fontSize = 11.5.sp,
                        lineHeight = 17.sp,
                        color = AtharPrimaryLight.copy(alpha = 0.85f),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            if (target > 1 && count > 0) {
                Spacer(Modifier.height(13.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(AtharCardBorder.copy(alpha = 0.7f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth((count.toFloat() / target).coerceAtMost(1f))
                            .height(3.dp)
                            .background(if (isDone) AtharPrimaryLight else AtharPrimary)
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when {
                        count == 0 -> stringResource(R.string.duas_tap_to_count)
                        isDone -> stringResource(R.string.duas_completed)
                        else -> stringResource(R.string.duas_keep_counting)
                    },
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.5.sp,
                    color = if (isDone) AtharPrimaryLight else AtharTextSecondary.copy(alpha = 0.7f)
                )

                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Text(
                        text = if (count == 0) formatDigits("×$target", numberStyle)
                               else if (isDone) "${formatDigits(target.toString(), numberStyle)} ✓"
                               else "${formatDigits(count.toString(), numberStyle)} / ${formatDigits(target.toString(), numberStyle)}",
                        fontFamily = ThmanyahSerifDisplay,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = if (isDone) AtharPrimaryLight else AtharTextPrimary.copy(alpha = 0.92f)
                    )
                }
            }
        }
    }
}

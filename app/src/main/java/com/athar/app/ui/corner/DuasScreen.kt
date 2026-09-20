package com.athar.app.ui.corner

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athar.app.R
import com.athar.app.data.AppPreferences
import com.athar.app.data.NumberStylePreference
import com.athar.app.data.formatDigits
import com.athar.app.ui.components.PatternScaffold
import com.athar.app.ui.theme.AtharBackground
import com.athar.app.ui.theme.AtharCardBorder
import com.athar.app.ui.theme.AtharCardSurface
import com.athar.app.ui.theme.AtharPrimary
import com.athar.app.ui.theme.AtharPrimaryLight
import com.athar.app.ui.theme.AtharTextOnPrimary
import com.athar.app.ui.theme.AtharTextPrimary
import com.athar.app.ui.theme.AtharTextSecondary
import com.athar.app.ui.theme.ThmanyahSans
import com.athar.app.ui.theme.ThmanyahSerifDisplay
import com.athar.app.ui.theme.ThmanyahSerifText
import kotlinx.coroutines.launch

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
fun DuasScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val prefs = remember { AppPreferences(context.applicationContext) }

    val numberStyle by prefs.numberStyle.collectAsState(initial = NumberStylePreference.WESTERN)
    val langCode by prefs.selectedLanguage.collectAsState(initial = "ar")
    val isArabic = langCode == "ar"

    // Currently selected primary category
    var selectedCategoryId by remember { mutableStateOf(duaCategories.first().id) }
    val currentCategory = remember(selectedCategoryId) {
        duaCategories.firstOrNull { it.id == selectedCategoryId } ?: duaCategories.first()
    }

    // Sub-collection for After Prayer ("other", "fajr", "maghrib")
    var selectedPrayerSubCategory by remember { mutableStateOf("other") }

    // Active list of duas depending on category and sub-category
    val activeDuas = remember(selectedCategoryId, selectedPrayerSubCategory) {
        if (selectedCategoryId == "after_prayer") {
            when (selectedPrayerSubCategory) {
                "fajr" -> afterPrayerFajrDuas
                "maghrib" -> afterPrayerMaghribDuas
                else -> afterPrayerOtherDuas
            }
        } else {
            currentCategory.duas
        }
    }

    // Dynamic titles
    val displayTitleAr = remember(selectedCategoryId, selectedPrayerSubCategory) {
        if (selectedCategoryId == "after_prayer") {
            when (selectedPrayerSubCategory) {
                "fajr" -> "أذكار بعد صلاة الفجر"
                "maghrib" -> "أذكار بعد صلاة المغرب"
                else -> "أذكار بعد الصلوات"
            }
        } else {
            currentCategory.titleAr
        }
    }
    val displayTitleEn = remember(selectedCategoryId, selectedPrayerSubCategory) {
        if (selectedCategoryId == "after_prayer") {
            when (selectedPrayerSubCategory) {
                "fajr" -> "After Fajr Prayer"
                "maghrib" -> "After Maghrib Prayer"
                else -> "After Other Prayers"
            }
        } else {
            currentCategory.titleEn
        }
    }

    // Per-dua session dhikr counts
    var counts by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }

    // Scroll state & scroll-aware floating top bar
    val listState = rememberLazyListState()
    var isTopBarVisible by remember { mutableStateOf(true) }

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
                    top = if (selectedCategoryId == "after_prayer") 170.dp else 125.dp,
                    bottom = 130.dp
                )
            ) {
                // Category Banner Header
                item(key = "banner_${selectedCategoryId}_${if (selectedCategoryId == "after_prayer") selectedPrayerSubCategory else ""}") {
                    CategoryHeaderBanner(
                        titleAr = displayTitleAr,
                        titleEn = displayTitleEn,
                        itemCount = activeDuas.size,
                        isArabic = isArabic,
                        numberStyle = numberStyle
                    )
                }

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
                                counts = counts + (dua.id to next)
                                haptics.performHapticFeedback(
                                    if (next >= target) HapticFeedbackType.LongPress
                                    else HapticFeedbackType.TextHandleMove
                                )
                            }
                        },
                        onReset = {
                            if (count > 0) {
                                counts = counts + (dua.id to 0)
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                        }
                    )
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
                                    onClick = onBack
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
                                fontFamily = ThmanyahSerifDisplay,
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp,
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

                    // Main categories row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 14.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        duaCategories.forEach { category ->
                            val isSelected = category.id == selectedCategoryId
                            CategoryChip(
                                title = if (isArabic) category.titleAr else category.titleEn,
                                isSelected = isSelected,
                                onClick = {
                                    if (!isSelected) {
                                        selectedCategoryId = category.id
                                        scope.launch { listState.scrollToItem(0) }
                                    }
                                }
                            )
                        }
                    }

                    // Sub-selector for After Prayer (Other Prayers, Fajr, Maghrib)
                    if (selectedCategoryId == "after_prayer") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            PrayerSubChip(
                                title = if (isArabic) "باقي الصلوات" else "Other Prayers",
                                badgeCount = formatDigits("12", numberStyle),
                                isSelected = selectedPrayerSubCategory == "other",
                                onClick = {
                                    if (selectedPrayerSubCategory != "other") {
                                        selectedPrayerSubCategory = "other"
                                        scope.launch { listState.scrollToItem(0) }
                                    }
                                }
                            )
                            Spacer(Modifier.width(8.dp))
                            PrayerSubChip(
                                title = if (isArabic) "صلاة الفجر" else "Fajr",
                                badgeCount = formatDigits("15", numberStyle),
                                isSelected = selectedPrayerSubCategory == "fajr",
                                onClick = {
                                    if (selectedPrayerSubCategory != "fajr") {
                                        selectedPrayerSubCategory = "fajr"
                                        scope.launch { listState.scrollToItem(0) }
                                    }
                                }
                            )
                            Spacer(Modifier.width(8.dp))
                            PrayerSubChip(
                                title = if (isArabic) "صلاة المغرب" else "Maghrib",
                                badgeCount = formatDigits("14", numberStyle),
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
        }
    }
}

@Composable
private fun CategoryHeaderBanner(
    titleAr: String,
    titleEn: String,
    itemCount: Int,
    isArabic: Boolean,
    numberStyle: NumberStylePreference
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
        Spacer(Modifier.height(8.dp))
        Text(
            text = titleAr,
            fontFamily = ThmanyahSerifDisplay,
            fontWeight = FontWeight.Black,
            fontSize = 20.sp,
            color = AtharTextPrimary,
            textAlign = TextAlign.Center
        )
        Text(
            text = titleEn + " • " + formatDigits("$itemCount", numberStyle) + " " + (if (isArabic) "أذكار" else "Duas"),
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Bold,
            fontSize = 11.5.sp,
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
            .then(
                if (isSelected) {
                    Modifier.shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(18.dp),
                        spotColor = AtharPrimaryLight.copy(alpha = 0.45f)
                    )
                } else Modifier
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
    badgeCount: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
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
            .then(
                if (isSelected) {
                    Modifier.shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(14.dp),
                        spotColor = AtharPrimaryLight.copy(alpha = 0.35f)
                    )
                } else Modifier
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 11.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = title,
                fontFamily = ThmanyahSans,
                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                fontSize = 11.5.sp,
                color = if (isSelected) AtharPrimaryLight else AtharTextSecondary
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        if (isSelected) AtharPrimaryLight.copy(alpha = 0.25f)
                        else AtharCardBorder.copy(alpha = 0.4f)
                    )
                    .padding(horizontal = 5.dp, vertical = 1.dp)
            ) {
                Text(
                    text = badgeCount,
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = if (isSelected) AtharPrimaryLight else AtharTextSecondary.copy(alpha = 0.8f)
                )
            }
        }
    }
}

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
    onReset: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 5.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(AtharCardSurface.copy(alpha = 0.88f))
            .border(
                width = 1.dp,
                color = if (isDone) AtharPrimary.copy(alpha = 0.65f) else AtharCardBorder,
                shape = RoundedCornerShape(18.dp)
            )
            .then(
                if (isDone) {
                    Modifier.shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(18.dp),
                        spotColor = AtharPrimaryLight.copy(alpha = 0.25f)
                    )
                } else Modifier
            )
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onCount,
                onLongClick = onReset
            )
            .padding(16.dp)
    ) {
        Column {
            // Top Row: Number badge (e.g. 1/31 matching the user's reference)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Index badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(AtharPrimary.copy(alpha = 0.16f))
                        .border(1.dp, AtharPrimary.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 9.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${formatDigits((index + 1).toString(), numberStyle)} / ${formatDigits(totalCount.toString(), numberStyle)}",
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        color = AtharPrimaryLight
                    )
                }

                // Source badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AtharPrimary.copy(alpha = 0.12f))
                        .padding(horizontal = 9.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = dua.source,
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = AtharPrimaryLight
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Arabic text
            Text(
                text = dua.arabic,
                fontFamily = ThmanyahSerifText,
                fontWeight = FontWeight.Normal,
                fontSize = 19.sp,
                lineHeight = 36.sp,
                color = AtharTextPrimary,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            // English translation
            Text(
                text = dua.translation,
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Medium,
                fontSize = 12.5.sp,
                lineHeight = 18.sp,
                color = AtharTextSecondary,
                modifier = Modifier.fillMaxWidth()
            )

            // In-card Hadith Guidelines / Virtue note (for After Prayer & specific duas)
            val noteText = if (isArabic) dua.noteAr ?: dua.noteEn else dua.noteEn ?: dua.noteAr
            if (!noteText.isNullOrBlank()) {
                Spacer(Modifier.height(11.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AtharPrimary.copy(alpha = 0.10f))
                        .border(
                            width = 1.dp,
                            color = AtharPrimaryLight.copy(alpha = 0.35f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 11.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(AtharPrimary.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Rounded.Info,
                                contentDescription = null,
                                tint = AtharPrimaryLight,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                        Text(
                            text = noteText,
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.5.sp,
                            lineHeight = 16.5.sp,
                            color = AtharTextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Progress bar
            if (target > 1 && count > 0) {
                Spacer(Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(AtharCardBorder)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth((count.toFloat() / target).coerceAtMost(1f))
                            .height(4.dp)
                            .background(if (isDone) AtharPrimaryLight else AtharPrimary)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Bottom action row: Tap hint & Counter pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (count == 0) "اضغط للعدّ • Tap to count"
                           else if (isDone) "اكتمل الذكر • Completed"
                           else "استمر في العد • Keep counting",
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.5.sp,
                    color = if (isDone) AtharPrimaryLight else AtharTextSecondary.copy(alpha = 0.7f)
                )

                // Counter pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isDone) AtharPrimary
                            else AtharPrimary.copy(alpha = 0.22f)
                        )
                        .border(
                            1.dp,
                            if (isDone) AtharPrimaryLight else AtharPrimary.copy(alpha = 0.45f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (count == 0) formatDigits("×$target", numberStyle)
                               else if (isDone) "${formatDigits(target.toString(), numberStyle)} ✓"
                               else "${formatDigits(count.toString(), numberStyle)} / ${formatDigits(target.toString(), numberStyle)}",
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        color = if (isDone) AtharTextOnPrimary else AtharPrimaryLight
                    )
                }
            }
        }
    }
}

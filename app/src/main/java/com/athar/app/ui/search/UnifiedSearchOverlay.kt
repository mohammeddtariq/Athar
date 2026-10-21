package com.athar.app.ui.search

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athar.app.R
import com.athar.app.data.AppPreferences
import com.athar.app.data.NumberStylePreference
import com.athar.app.data.formatDigits
import com.athar.app.ui.components.PatternScaffold
import com.athar.app.ui.theme.AtharCardBorder
import com.athar.app.ui.theme.AtharCardSurface
import com.athar.app.ui.theme.AtharPrimary
import com.athar.app.ui.theme.AtharPrimaryLight
import com.athar.app.ui.theme.AtharTextPrimary
import com.athar.app.ui.theme.AtharTextSecondary
import com.athar.app.ui.theme.ThmanyahSans
import com.athar.app.ui.theme.ThmanyahSerifText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * Full-screen offline search over surah names, Quran verses, and the dua library.
 * Verse and dua results navigate straight to their reader position.
 */
@Composable
fun UnifiedSearchOverlay(
    onDismiss: () -> Unit,
    onOpenVerse: (surah: Int, ayah: Int) -> Unit,
    onOpenDua: (chapter: Int, duaIndex: Int?) -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { AppPreferences(context.applicationContext) }
    val language by prefs.selectedLanguage.collectAsState(initial = "ar")
    val numberStyle by prefs.numberStyle.collectAsState(initial = NumberStylePreference.WESTERN)
    val isArabic = language == "ar"

    var query by rememberSaveable { mutableStateOf("") }
    var results by remember { mutableStateOf(UnifiedSearchResults()) }
    var isSearching by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    BackHandler(onBack = onDismiss)
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    LaunchedEffect(query) {
        val trimmed = query.trim()
        if (trimmed.length < 2) {
            results = UnifiedSearchResults()
            isSearching = false
            return@LaunchedEffect
        }
        delay(280)
        isSearching = true
        results = withContext(Dispatchers.IO) { unifiedSearch(context.applicationContext, trimmed) }
        isSearching = false
    }

    CompositionLocalProvider(
        LocalLayoutDirection provides if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr
    ) {
        PatternScaffold {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .imePadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onDismiss
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.search_all_back),
                            tint = AtharTextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.width(4.dp))
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .background(AtharCardSurface, RoundedCornerShape(24.dp))
                            .border(1.dp, AtharCardBorder, RoundedCornerShape(24.dp))
                            .padding(start = 14.dp, end = 4.dp),
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
                                    text = stringResource(R.string.search_all_hint),
                                    fontFamily = ThmanyahSans,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 13.sp,
                                    color = AtharTextSecondary.copy(alpha = 0.7f)
                                )
                            }
                            BasicTextField(
                                value = query,
                                onValueChange = { query = it },
                                singleLine = true,
                                textStyle = TextStyle(
                                    fontFamily = ThmanyahSans,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 13.sp,
                                    color = AtharTextPrimary,
                                    textAlign = TextAlign.Start
                                ),
                                cursorBrush = SolidColor(AtharPrimaryLight),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .focusRequester(focusRequester)
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
                                        onClick = {
                                            query = ""
                                            keyboard?.show()
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Rounded.Close,
                                    contentDescription = stringResource(R.string.duas_clear_search),
                                    tint = AtharTextSecondary,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    when {
                        isSearching -> CircularProgressIndicator(
                            color = AtharPrimaryLight,
                            strokeWidth = 2.dp,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 32.dp)
                                .size(26.dp)
                        )

                        query.trim().length >= 2 && results.total == 0 -> Text(
                            text = stringResource(R.string.search_all_empty),
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            color = AtharTextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(horizontal = 32.dp, vertical = 48.dp)
                        )

                        else -> LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 120.dp)
                        ) {
                            if (results.surahs.isNotEmpty()) {
                                item(key = "h_surahs") {
                                    SearchSectionHeader(
                                        title = stringResource(R.string.search_all_surahs),
                                        count = results.surahs.size,
                                        numberStyle = numberStyle
                                    )
                                }
                                items(results.surahs, key = { "s_${it.surah.number}" }) { hit ->
                                    SurahResultRow(
                                        hit = hit,
                                        isArabic = isArabic,
                                        numberStyle = numberStyle,
                                        onClick = { onOpenVerse(hit.surah.number, 1) }
                                    )
                                }
                            }
                            if (results.verses.isNotEmpty()) {
                                item(key = "h_verses") {
                                    SearchSectionHeader(
                                        title = stringResource(R.string.search_all_verses),
                                        count = results.verses.size,
                                        numberStyle = numberStyle
                                    )
                                }
                                items(
                                    results.verses,
                                    key = { "v_${it.surah.number}_${it.ayah}" }
                                ) { hit ->
                                    VerseResultRow(
                                        hit = hit,
                                        isArabic = isArabic,
                                        numberStyle = numberStyle,
                                        onClick = { onOpenVerse(hit.surah.number, hit.ayah) }
                                    )
                                }
                            }
                            if (results.duas.isNotEmpty()) {
                                item(key = "h_duas") {
                                    SearchSectionHeader(
                                        title = stringResource(R.string.search_all_duas),
                                        count = results.duas.size,
                                        numberStyle = numberStyle
                                    )
                                }
                                items(
                                    results.duas,
                                    key = { "d_${it.chapter.number}_${it.duaIndex ?: -1}" }
                                ) { hit ->
                                    DuaResultRow(
                                        hit = hit,
                                        isArabic = isArabic,
                                        onClick = { onOpenDua(hit.chapter.number, hit.duaIndex) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchSectionHeader(
    title: String,
    count: Int,
    numberStyle: NumberStylePreference
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Black,
            fontSize = 13.sp,
            color = AtharPrimaryLight
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = formatDigits(count.toString(), numberStyle),
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = AtharTextSecondary
        )
    }
}

@Composable
private fun SurahResultRow(
    hit: SurahSearchHit,
    isArabic: Boolean,
    numberStyle: NumberStylePreference,
    onClick: () -> Unit
) {
    ResultRow(onClick = onClick) {
        NumberBadge(hit.surah.number, numberStyle)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (isArabic) hit.surah.arabicName else hit.surah.englishName,
                fontFamily = if (isArabic) ThmanyahSerifText else ThmanyahSans,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = AtharTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = if (isArabic) hit.surah.englishName else hit.surah.englishTranslation,
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Medium,
                fontSize = 10.5.sp,
                color = AtharTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun VerseResultRow(
    hit: VerseSearchHit,
    isArabic: Boolean,
    numberStyle: NumberStylePreference,
    onClick: () -> Unit
) {
    ResultRow(onClick = onClick) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${if (isArabic) hit.surah.arabicName else hit.surah.englishName} · ${formatDigits(hit.ayah.toString(), numberStyle)}",
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = AtharPrimaryLight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = hit.text,
                fontFamily = ThmanyahSerifText,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 24.sp,
                color = AtharTextPrimary,
                textAlign = TextAlign.Start,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun DuaResultRow(
    hit: DuaSearchHit,
    isArabic: Boolean,
    onClick: () -> Unit
) {
    ResultRow(onClick = onClick) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (isArabic) hit.chapter.titleAr else hit.chapter.titleEn,
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Bold,
                fontSize = 11.5.sp,
                color = AtharPrimaryLight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = hit.dua?.let { if (isArabic) it.arabic else it.translation }
                    ?: hit.chapter.titleEn,
                fontFamily = if (isArabic) ThmanyahSerifText else ThmanyahSans,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                lineHeight = 21.sp,
                color = AtharTextPrimary,
                textAlign = TextAlign.Start,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ResultRow(
    onClick: () -> Unit,
    content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .fillMaxWidth()
            .background(AtharCardSurface.copy(alpha = 0.88f), RoundedCornerShape(14.dp))
            .border(1.dp, AtharCardBorder, RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        content = content
    )
}

@Composable
private fun NumberBadge(number: Int, numberStyle: NumberStylePreference) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .background(AtharPrimary.copy(alpha = 0.16f), CircleShape)
            .border(1.dp, AtharPrimary.copy(alpha = 0.35f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = formatDigits(number.toString(), numberStyle),
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Black,
            fontSize = 10.5.sp,
            color = AtharPrimaryLight
        )
    }
}

package com.athar.app.ui.corner

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athar.app.R
import com.athar.app.ui.components.PatternScaffold
import com.athar.app.ui.theme.AtharCardBorder
import com.athar.app.ui.theme.AtharCardSurface
import com.athar.app.ui.theme.AtharPrimary
import com.athar.app.ui.theme.AtharTextPrimary
import com.athar.app.ui.theme.AtharTextSecondary
import com.athar.app.ui.theme.ThmanyahSans
import com.athar.app.ui.theme.ThmanyahSerifDisplay
import com.athar.app.ui.theme.ThmanyahSerifText

/** Duas browser — Hisnul Muslim selection, olive cards, no emojis. */
@Composable
fun DuasScreen(onBack: () -> Unit) {
    PatternScaffold(patternAlpha = 0.05f) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentPadding = PaddingValues(bottom = 110.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
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
                            fontSize = 22.sp,
                            color = AtharTextPrimary
                        )
                        Text(
                            stringResource(R.string.duas_sub),
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            color = AtharTextSecondary
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Spacer(Modifier.size(44.dp))
                }
            }

            duaCategories.forEach { category ->
                item(key = "header_${category.titleEn}") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                category.titleAr,
                                fontFamily = ThmanyahSans,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = AtharTextPrimary,
                                textAlign = TextAlign.End
                            )
                            Text(
                                category.titleEn,
                                fontFamily = ThmanyahSans,
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.sp,
                                color = AtharTextSecondary,
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
                items(category.duas, key = { it.arabic.take(24) + it.source }) { dua ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 20.dp, vertical = 4.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(AtharCardSurface.copy(alpha = 0.85f))
                            .border(1.dp, AtharCardBorder, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Text(
                                dua.arabic,
                                fontFamily = ThmanyahSerifText,
                                fontWeight = FontWeight.Normal,
                                fontSize = 18.sp,
                                lineHeight = 34.sp,
                                color = AtharTextPrimary,
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                dua.translation,
                                fontFamily = ThmanyahSans,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                color = AtharTextSecondary,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(AtharPrimary.copy(alpha = 0.14f))
                                        .padding(horizontal = 9.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        dua.source,
                                        fontFamily = ThmanyahSans,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.5.sp,
                                        color = AtharPrimary
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

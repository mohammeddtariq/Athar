package com.athar.app.ui.services

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.LibraryBooks
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.athar.app.R
import com.athar.app.ui.theme.*
import kotlinx.coroutines.delay

data class ServiceItem(val nameResId: Int, val icon: ImageVector, val color: Color, val count: Int? = null)

val mainServices = listOf(
    ServiceItem(R.string.services_quran, Icons.Outlined.AutoStories, AtharEmerald),
    ServiceItem(R.string.services_books, Icons.Outlined.LibraryBooks, AtharAmber)
)

val tools = listOf(
    ServiceItem(R.string.services_agenda, Icons.Outlined.Checklist, AtharTerracotta),
    ServiceItem(R.string.services_calendar, Icons.Outlined.CalendarMonth, AtharTeal),
    ServiceItem(R.string.services_tasbih, Icons.Outlined.TouchApp, AtharLavender)
)

@Composable
fun ServicesScreen() {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(60)
        isVisible = true
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AtharBackground)
            .statusBarsPadding(),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // ─── Title ───
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(400)) + slideInVertically(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) { -25 }
            ) {
                Text(
                    stringResource(R.string.services_title),
                    color = AtharTextPrimary,
                    fontFamily = ThmanyahSerifDisplay,
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp, bottom = 20.dp)
                )
            }
        }

        // ─── Featured Section Header ───
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(400, 80))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.services_featured),
                        color = AtharTextPrimary,
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("✨", fontSize = 15.sp)
                }
            }
        }

        // ─── Main Service Cards (Side by side) ───
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(450, 150)) + slideInVertically(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) { 40 }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    mainServices.forEach { service ->
                        FeaturedServiceCard(
                            service = service,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // ─── Explore Categories Header ───
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(400, 220))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.services_explore_categories),
                        color = AtharTextPrimary,
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("🔲", fontSize = 15.sp)
                }
            }
        }

        // ─── Tool Cards (staggered) ───
        itemsIndexed(tools) { index, tool ->
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(350, 260 + index * 50)) +
                        slideInVertically(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) { 30 }
            ) {
                ToolListItem(tool)
            }
        }
    }
}

@Composable
fun FeaturedServiceCard(service: ServiceItem, modifier: Modifier = Modifier) {
    val scale = remember { Animatable(0.95f) }
    LaunchedEffect(Unit) {
        scale.animateTo(
            1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    Box(
        modifier = modifier
            .height(138.dp)
            .scale(scale.value)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(AtharGradientStart, AtharGradientEnd)
                )
            )
            .border(1.dp, AtharCardBorder, RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { /* Handle service tap */ }
            )
            .padding(16.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.End
        ) {
            // Icon Container
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(service.color.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    service.icon,
                    contentDescription = null,
                    tint = service.color,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Name — refined size
            Text(
                stringResource(service.nameResId),
                color = AtharTextPrimary,
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp,
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
fun ToolListItem(service: ServiceItem) {
    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 3.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AtharCardSurface)
            .border(1.dp, AtharCardBorder, RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { /* Handle tool tap */ }
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon on left
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(service.color.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    service.icon,
                    contentDescription = null,
                    tint = service.color,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Name on right
            Text(
                stringResource(service.nameResId),
                color = AtharTextPrimary,
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ServicesScreenPreview() {
    AtharTheme {
        ServicesScreen()
    }
}

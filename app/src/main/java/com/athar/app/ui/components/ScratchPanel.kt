package com.athar.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athar.app.ui.theme.AtharBackground
import com.athar.app.ui.theme.AtharPrimary
import com.athar.app.ui.theme.AtharPrimaryLight
import com.athar.app.ui.theme.AtharTextPrimary
import com.athar.app.ui.theme.AtharTextSecondary
import com.athar.app.ui.theme.ThmanyahSans
import com.athar.app.ui.theme.ThmanyahSerifDisplay
import kotlin.math.roundToInt

private const val DEFAULT_SCRATCH_COLS = 24
private const val DEFAULT_SCRATCH_ROWS = 3

/**
 * A scratch-off strip. The ayah sits underneath a barcode-like foil that is
 * erased in real time wherever the finger passes. The strip only counts as done
 * once nearly every part of it has been scratched, then the remainder wipes
 * away in a soft expanding circle.
 *
 * The gesture belongs to the foil, so the card it is printed on never sees the
 * scratch as a tap or a turn. A touch that never moves is reported through
 * [onTap] instead.
 */
@Composable
fun ScratchPanel(
    arabic: String,
    reference: String,
    subtitle: String?,
    revealed: Boolean,
    onRevealed: () -> Unit,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
    arabicFontFamily: FontFamily = ThmanyahSerifDisplay,
    arabicFontWeight: FontWeight = FontWeight.Bold,
    arabicFontSize: TextUnit = 10.5.sp,
    arabicLineHeight: TextUnit = 14.sp,
    arabicMaxLines: Int = 3,
    referenceFontFamily: FontFamily = ThmanyahSerifDisplay,
    foilLabel: String = "SCRATCH · ",
    columns: Int = DEFAULT_SCRATCH_COLS,
    rows: Int = DEFAULT_SCRATCH_ROWS
) {
    val haptics = LocalHapticFeedback.current
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    val shape = RoundedCornerShape(9.dp)

    val strokes = remember { mutableStateListOf<ScratchStroke>() }
    val cells = remember { BooleanArray(columns * rows) }
    var scratchedCells by remember { mutableIntStateOf(0) }
    val reveal = remember { Animatable(if (revealed) 1f else 0f) }
    val onRevealedRef = rememberUpdatedState(onRevealed)
    val onTapRef = rememberUpdatedState(onTap)

    LaunchedEffect(revealed) {
        if (revealed) {
            if (reveal.value < 1f) {
                reveal.animateTo(1f, tween(durationMillis = 560, easing = FastOutSlowInEasing))
            }
        } else {
            reveal.snapTo(0f)
            strokes.clear()
            cells.fill(false)
            scratchedCells = 0
        }
    }

    val clearWidth = with(density) { 15.dp.toPx() }
    val coverageRadius = with(density) { 6.5.dp.toPx() }
    val totalCells = columns * rows
    // The card only relaxes once the strip has been scratched through.
    val revealAt = (totalCells * 0.97f).roundToInt()
    val foilStyle = TextStyle(
        color = Color(0xFF7E8089),
        fontFamily = ThmanyahSans,
        fontWeight = FontWeight.Bold,
        fontSize = 8.5.sp,
        letterSpacing = 2.4.sp
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(AtharBackground.copy(alpha = 0.55f))
            .border(1.dp, AtharPrimary.copy(alpha = 0.22f), shape)
    ) {
        // The ayah, waiting underneath the foil.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp, vertical = 1.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = arabic,
                fontFamily = arabicFontFamily,
                fontWeight = arabicFontWeight,
                fontSize = arabicFontSize,
                lineHeight = arabicLineHeight,
                color = AtharTextPrimary,
                textAlign = TextAlign.Center,
                maxLines = arabicMaxLines,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = reference,
                fontFamily = referenceFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 8.5.sp,
                lineHeight = 11.5.sp,
                color = AtharPrimaryLight.copy(alpha = 0.92f),
                textAlign = TextAlign.Center,
                maxLines = 1,
                softWrap = false
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 7.5.sp,
                    lineHeight = 9.5.sp,
                    color = AtharTextSecondary,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Canvas(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                .pointerInput(revealed) {
                    if (revealed) return@pointerInput
                    val cellW = size.width.toFloat() / columns
                    val cellH = size.height.toFloat() / rows

                    fun markCoverage(from: Offset, to: Offset) {
                        var added = 0
                        for (rowIndex in 0 until rows) {
                            val cellY = (rowIndex + 0.5f) * cellH
                            for (colIndex in 0 until columns) {
                                val index = rowIndex * columns + colIndex
                                if (cells[index]) continue
                                val cellX = (colIndex + 0.5f) * cellW
                                if (distanceToSegment(Offset(cellX, cellY), from, to) <= coverageRadius) {
                                    cells[index] = true
                                    added++
                                }
                            }
                        }
                        if (added > 0) {
                            scratchedCells += added
                            if (scratchedCells >= revealAt) onRevealedRef.value()
                        }
                    }

                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        // The whole gesture belongs to the foil, so neither the
                        // card's turn nor the deck's fan ever sees it.
                        down.consume()
                        var last = down.position
                        var travelled = 0f
                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull { it.id == down.id } ?: break
                            change.consume()
                            if (!change.pressed) break
                            val to = change.position
                            if (to != last) {
                                travelled += (to - last).getDistance()
                                strokes += ScratchStroke(last, to)
                                markCoverage(last, to)
                                if (strokes.size % 4 == 0) {
                                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                }
                                last = to
                            }
                        }
                        // A tap that never moved is a hand on the card, not a
                        // scratch: it puts the card away.
                        if (travelled < 4.dp.toPx()) onTapRef.value()
                    }
                }
        ) {
            if (reveal.value >= 1f) return@Canvas

            // A cooler, darker silver plate: greyer metal brushed with a fine
            // diagonal grain, so it reads as a fresh panel waiting to be
            // scratched without glowing out of the card.
            drawRect(
                brush = Brush.verticalGradient(
                    0f to Color(0xFFE2E3E6),
                    0.10f to Color(0xFFC7CAD0),
                    0.24f to Color(0xFFD9DBE0),
                    0.38f to Color(0xFFAFB3BC),
                    0.52f to Color(0xFFCBCED5),
                    0.66f to Color(0xFFA6AAB4),
                    0.80f to Color(0xFFC2C5CC),
                    0.93f to Color(0xFFACB0B9),
                    1f to Color(0xFF979CA6)
                )
            )
            // Fine diagonal brushing across the metal.
            val grain = 1.4.dp.toPx()
            var grainX = -size.height
            var grainAlt = false
            while (grainX < size.width) {
                drawLine(
                    color = if (grainAlt) {
                        Color.White.copy(alpha = 0.13f)
                    } else {
                        Color.Black.copy(alpha = 0.045f)
                    },
                    start = Offset(grainX, 0f),
                    end = Offset(grainX + size.height, size.height),
                    strokeWidth = grain * 0.45f
                )
                grainX += grain
                grainAlt = !grainAlt
            }
            // A repeating "SCRATCH" wordmark, embossed into the plate.
            val tileText = foilLabel
            val tile = textMeasurer.measure(AnnotatedString(tileText), style = foilStyle)
            val tileWidth = tile.size.width
            val tileHeight = tile.size.height
            if (tileWidth > 0f) {
                val foilRows = 3
                val rowGap = size.height / foilRows
                for (rowIndex in 0 until foilRows) {
                    val y = rowGap * rowIndex + (rowGap - tileHeight) / 2f
                    var x = if (rowIndex % 2 == 0) -tileWidth * 0.35f else -tileWidth * 0.85f
                    while (x < size.width) {
                        drawText(
                            textLayoutResult = tile,
                            color = Color.White.copy(alpha = 0.52f),
                            topLeft = Offset(x + 0.7f, y + 0.8f)
                        )
                        drawText(textLayoutResult = tile, topLeft = Offset(x, y))
                        x += tileWidth
                    }
                }
            }
            // A slow sheen travelling across the metal.
            drawRect(
                brush = Brush.linearGradient(
                    0f to Color.White.copy(alpha = 0.13f),
                    0.24f to Color.White.copy(alpha = 0.02f),
                    0.44f to Color(0xFFE8D9B8).copy(alpha = 0.05f),
                    0.62f to Color.White.copy(alpha = 0.11f),
                    0.82f to Color.White.copy(alpha = 0.00f),
                    1f to Color.Black.copy(alpha = 0.16f),
                    start = Offset(0f, size.height),
                    end = Offset(size.width, 0f)
                )
            )
            // The strip is set into the card: a lit top lip, a shadow under it,
            // side hairlines, and a dark line where the metal leaves the card.
            drawRect(
                color = Color.White.copy(alpha = 0.50f),
                topLeft = Offset(0f, 0f),
                size = Size(size.width, 1.dp.toPx())
            )
            drawRect(
                color = Color.Black.copy(alpha = 0.18f),
                topLeft = Offset(0f, 1.dp.toPx()),
                size = Size(size.width, 0.5.dp.toPx())
            )
            drawRect(
                color = Color.Black.copy(alpha = 0.32f),
                topLeft = Offset(0f, size.height - 1.dp.toPx()),
                size = Size(size.width, 1.dp.toPx())
            )
            drawRect(
                color = Color.White.copy(alpha = 0.30f),
                topLeft = Offset(0f, 0f),
                size = Size(0.8.dp.toPx(), size.height)
            )
            drawRect(
                color = Color.Black.copy(alpha = 0.10f),
                topLeft = Offset(size.width - 0.8.dp.toPx(), 0f),
                size = Size(0.8.dp.toPx(), size.height)
            )
            // Erase where the finger has been.
            for (stroke in strokes) {
                drawLine(
                    color = Color.Transparent,
                    start = stroke.from,
                    end = stroke.to,
                    strokeWidth = clearWidth,
                    cap = StrokeCap.Round,
                    blendMode = BlendMode.Clear
                )
            }
            // The final wipe: a circle of light spreading from the middle.
            if (reveal.value > 0f) {
                drawCircle(
                    color = Color.Transparent,
                    radius = reveal.value * size.maxDimension,
                    center = Offset(size.width / 2f, size.height / 2f),
                    blendMode = BlendMode.Clear
                )
            }
        }
    }
}

private fun distanceToSegment(point: Offset, a: Offset, b: Offset): Float {
    val abX = b.x - a.x
    val abY = b.y - a.y
    val lengthSq = abX * abX + abY * abY
    if (lengthSq <= 0.0001f) return (point - a).getDistance()
    val t = (((point.x - a.x) * abX + (point.y - a.y) * abY) / lengthSq).coerceIn(0f, 1f)
    val projX = a.x + abX * t
    val projY = a.y + abY * t
    return kotlin.math.hypot(point.x - projX, point.y - projY)
}

private data class ScratchStroke(val from: Offset, val to: Offset)

package com.athar.app.ui.share

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextDirectionHeuristics
import android.text.TextPaint
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import com.athar.app.R
import com.athar.app.ui.theme.AtharBackground
import com.athar.app.ui.theme.AtharCardSurface
import com.athar.app.ui.theme.AtharPrimary
import com.athar.app.ui.theme.AtharTextPrimary
import com.athar.app.ui.theme.AtharTextSecondary
import java.io.File
import java.io.FileOutputStream

/**
 * Renders a dua or tafsir passage into a shareable portrait card.
 * Drawn with plain Android text layouts so Arabic shaping and bidi come from the platform.
 */
object ShareCardRenderer {

    private const val WIDTH = 1080
    private const val PADDING = 96
    private const val FRAME_INSET = 38f
    private const val MAX_TAFSIR_CHARS = 1400

    fun renderDua(
        context: Context,
        arabic: String,
        translation: String,
        note: String?,
        source: String,
        isArabic: Boolean
    ): Bitmap = render(
        context = context,
        arabic = arabic,
        body = translation,
        note = note,
        source = source,
        isArabic = isArabic,
        footerLabel = if (isArabic) "دعاء" else "Dua"
    )

    fun renderTafsir(
        context: Context,
        ayahArabic: String,
        reference: String,
        tafsir: String,
        source: String,
        isArabic: Boolean
    ): Bitmap = render(
        context = context,
        arabic = ayahArabic,
        body = tafsir.take(MAX_TAFSIR_CHARS).let { if (tafsir.length > MAX_TAFSIR_CHARS) "$it…" else it },
        note = reference,
        source = source,
        isArabic = isArabic,
        footerLabel = context.getString(R.string.al_marifah_name)
    )

    private fun render(
        context: Context,
        arabic: String,
        body: String,
        note: String?,
        source: String,
        isArabic: Boolean,
        footerLabel: String
    ): Bitmap {
        val serif = font(context, R.font.thmanyah_serif_text_regular)
        val serifBlack = font(context, R.font.thmanyah_serif_display_black)
        val sans = font(context, R.font.thmanyah_sans_regular)
        val sansBold = font(context, R.font.thmanyah_sans_bold)

        val contentWidth = WIDTH - PADDING * 2

        val arabicPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = serif
            textSize = 50f
            color = AtharTextPrimary.toArgb()
            isSubpixelText = true
        }
        val bodyPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = sans
            textSize = 30f
            color = Color.argb(235, 235, 239, 227)
        }
        val notePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = sans
            textSize = 26f
            color = AtharTextSecondary.toArgb()
        }

        val arabicLayout = buildLayout(arabic.trim(), arabicPaint, contentWidth, center = true, rtl = true)
        val bodyLayout = buildLayout(body.trim(), bodyPaint, contentWidth, center = body.length < 260, rtl = isArabic)
        val noteLayout = note?.trim()?.takeIf { it.isNotEmpty() }?.let {
            buildLayout(it, notePaint, contentWidth, center = true, rtl = isArabic)
        }

        val headerHeight = 250
        val dividerHeight = 90
        val footerHeight = 150
        val gap = 42
        var height = headerHeight + arabicLayout.height + gap +
            dividerHeight + bodyLayout.height + footerHeight
        if (noteLayout != null) height += gap + noteLayout.height
        height += PADDING
        height = height.coerceIn(900, 4096)

        val bitmap = Bitmap.createBitmap(WIDTH, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        canvas.drawPaint(Paint().apply {
            shader = LinearGradient(
                0f, 0f, 0f, height.toFloat(),
                AtharCardSurface.toArgb(), AtharBackground.toArgb(),
                Shader.TileMode.CLAMP
            )
        })

        drawIslamicLattice(
            canvas = canvas,
            width = WIDTH,
            height = height,
            cellPx = 200f,
            tint = AtharPrimary.toArgb(),
            baseAlpha = 0.16f
        )

        val frame = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 2.5f
            color = Color.argb(70, 201, 216, 180)
        }
        canvas.drawRoundRect(
            RectF(FRAME_INSET, FRAME_INSET, WIDTH - FRAME_INSET, height - FRAME_INSET),
            58f, 58f, frame
        )

        val brandPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = serifBlack
            textSize = 74f
            color = AtharPrimary.toArgb()
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("أثر", WIDTH / 2f, 148f, brandPaint)

        val taglinePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = sansBold
            textSize = 24f
            color = Color.argb(150, 147, 160, 134)
            textAlign = Paint.Align.CENTER
            letterSpacing = 0.45f
        }
        canvas.drawText("A T H A R", WIDTH / 2f, 200f, taglinePaint)

        var y = headerHeight.toFloat()
        canvas.save()
        canvas.translate(PADDING.toFloat(), y)
        arabicLayout.draw(canvas)
        canvas.restore()
        y += arabicLayout.height + gap

        val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(90, 201, 216, 180)
            strokeWidth = 2f
        }
        val centerX = WIDTH / 2f
        canvas.drawLine(centerX - 70f, y + 24f, centerX + 70f, y + 24f, dividerPaint)
        canvas.drawCircle(centerX, y + 24f, 5.5f, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = AtharPrimary.toArgb()
        })
        y += dividerHeight

        canvas.save()
        canvas.translate(PADDING.toFloat(), y)
        bodyLayout.draw(canvas)
        canvas.restore()
        y += bodyLayout.height

        if (noteLayout != null) {
            y += gap
            canvas.save()
            canvas.translate(PADDING.toFloat(), y)
            noteLayout.draw(canvas)
            canvas.restore()
        }

        val footerPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = sansBold
            textSize = 24f
            color = Color.argb(170, 201, 216, 180)
            textAlign = Paint.Align.CENTER
        }
        val footerY = height - PADDING.toFloat() + 10f
        canvas.drawText(source.ifBlank { if (isArabic) "أثر" else "Athar" }, centerX, footerY, footerPaint)
        val footerSubPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = sans
            textSize = 21f
            color = Color.argb(110, 147, 160, 134)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(
            if (isArabic) "أَثَر · $footerLabel" else "Athar · $footerLabel",
            centerX,
            footerY + 34f,
            footerSubPaint
        )

        return bitmap
    }

    /**
     * Port of the in-app [com.athar.app.ui.components.IslamicPatternBackground] so shared
     * cards carry the same eight-pointed star lattice instead of loose gradient blobs.
     */
    private fun drawIslamicLattice(
        canvas: Canvas,
        width: Int,
        height: Int,
        cellPx: Float,
        tint: Int,
        baseAlpha: Float
    ) {
        val w = width.toFloat()
        val h = height.toFloat()
        val cols = (w / cellPx).toInt() + 3
        val rows = (h / cellPx).toInt() + 3
        val starRadius = cellPx * 0.38f
        val starHalfSide = starRadius * 0.707f
        val starStroke = (starRadius * 0.045f).coerceAtLeast(1.2f)
        val ribbonStroke = (cellPx * 0.015f).coerceAtLeast(1f)
        val diamondSize = cellPx * 0.12f
        val diamondStroke = (diamondSize * 0.30f).coerceAtLeast(1f)
        val red = Color.red(tint)
        val green = Color.green(tint)
        val blue = Color.blue(tint)

        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            isSubpixelText = true
        }
        val rect = RectF()

        for (r in -1..rows) {
            for (c in -1..cols) {
                val cx = c * cellPx + (if (r % 2 == 0) 0f else cellPx / 2f)
                val cy = r * cellPx

                val diagonal = ((cx / w) * 0.7f + (1f - cy / h) * 0.3f).coerceIn(0f, 1f)
                val localAlpha = (baseAlpha * (diagonal * diagonal * 1.2f)).coerceIn(0.01f, 1f)
                if (localAlpha <= 0.005f) continue

                val strokeColor = Color.argb((localAlpha * 255f).toInt(), red, green, blue)
                val highlightColor = Color.argb((localAlpha * 0.45f * 255f).toInt(), red, green, blue)

                linePaint.color = strokeColor
                linePaint.strokeWidth = starStroke
                for (deg in floatArrayOf(0f, 45f)) {
                    canvas.save()
                    canvas.rotate(deg, cx, cy)
                    canvas.drawLine(cx - starHalfSide, cy - starHalfSide, cx + starHalfSide, cy - starHalfSide, linePaint)
                    canvas.drawLine(cx + starHalfSide, cy - starHalfSide, cx + starHalfSide, cy + starHalfSide, linePaint)
                    canvas.drawLine(cx + starHalfSide, cy + starHalfSide, cx - starHalfSide, cy + starHalfSide, linePaint)
                    canvas.drawLine(cx - starHalfSide, cy + starHalfSide, cx - starHalfSide, cy - starHalfSide, linePaint)
                    canvas.restore()
                }

                val innerRadius = starRadius * 0.32f
                linePaint.color = highlightColor
                linePaint.strokeWidth = starStroke * 0.8f
                var prevX = 0f
                var prevY = 0f
                var startX = 0f
                var startY = 0f
                for (i in 0..8) {
                    val angle = Math.toRadians(i * 45.0 + 22.5)
                    val x = cx + (innerRadius * Math.cos(angle)).toFloat()
                    val y = cy + (innerRadius * Math.sin(angle)).toFloat()
                    if (i == 0) {
                        startX = x
                        startY = y
                    } else {
                        canvas.drawLine(prevX, prevY, x, y, linePaint)
                    }
                    prevX = x
                    prevY = y
                }
                canvas.drawLine(prevX, prevY, startX, startY, linePaint)

                linePaint.color = strokeColor
                linePaint.strokeWidth = ribbonStroke
                for (i in 0 until 8) {
                    val angle = Math.toRadians(i * 45.0)
                    val cos = Math.cos(angle).toFloat()
                    val sin = Math.sin(angle).toFloat()
                    canvas.drawLine(
                        cx + starRadius * 0.82f * cos,
                        cy + starRadius * 0.82f * sin,
                        cx + cellPx * 0.50f * cos,
                        cy + cellPx * 0.50f * sin,
                        linePaint
                    )
                }

                linePaint.strokeWidth = diamondStroke
                drawCornerDiamond(canvas, linePaint, rect, cx + cellPx / 2f, cy, diamondSize)
                drawCornerDiamond(canvas, linePaint, rect, cx, cy + cellPx / 2f, diamondSize)
            }
        }
    }

    private fun drawCornerDiamond(
        canvas: Canvas,
        paint: Paint,
        rect: RectF,
        cx: Float,
        cy: Float,
        size: Float
    ) {
        canvas.save()
        canvas.rotate(45f, cx, cy)
        rect.set(cx - size, cy - size, cx + size, cy + size)
        canvas.drawRect(rect, paint)
        canvas.restore()
    }

    private fun buildLayout(
        text: String,
        paint: TextPaint,
        width: Int,
        center: Boolean,
        rtl: Boolean
    ): StaticLayout {
        if (text.isBlank()) {
            return StaticLayout.Builder.obtain(" ", 0, 1, paint, width).build()
        }
        return StaticLayout.Builder.obtain(text, 0, text.length, paint, width)
            .setAlignment(if (center) Layout.Alignment.ALIGN_CENTER else Layout.Alignment.ALIGN_NORMAL)
            .setTextDirection(if (rtl) TextDirectionHeuristics.RTL else TextDirectionHeuristics.LTR)
            .setLineSpacing(0f, 1.35f)
            .setIncludePad(false)
            .build()
    }

    private fun font(context: Context, resId: Int): Typeface =
        ResourcesCompat.getFont(context, resId) ?: Typeface.DEFAULT

    fun shareBitmap(
        context: Context,
        bitmap: Bitmap,
        subject: String,
        chooserTitle: String
    ): Boolean = try {
        val dir = File(context.cacheDir, "shares").apply { mkdirs() }
        dir.listFiles()?.forEach { if (it.isFile) it.delete() }
        val file = File(dir, "athar-share-${System.currentTimeMillis()}.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, subject)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(intent, chooserTitle).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
        bitmap.recycle()
        true
    } catch (_: Throwable) {
        false
    }
}

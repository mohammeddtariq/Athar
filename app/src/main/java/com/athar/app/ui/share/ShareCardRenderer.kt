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
        isArabic = isArabic
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
        isArabic = isArabic
    )

    private fun render(
        context: Context,
        arabic: String,
        body: String,
        note: String?,
        source: String,
        isArabic: Boolean
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

        val halo = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(14, 201, 216, 180)
        }
        canvas.drawCircle(WIDTH - 60f, 110f, 220f, halo)
        canvas.drawCircle(40f, height - 80f, 190f, halo)

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
            if (isArabic) "أَثَر · تدبّر القرآن" else "Athar · Contemplating the Qur'an",
            centerX,
            footerY + 34f,
            footerSubPaint
        )

        return bitmap
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

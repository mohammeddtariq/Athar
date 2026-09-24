package com.athar.app.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.SystemClock
import android.widget.RemoteViews
import com.athar.app.MainActivity
import com.athar.app.R
import com.athar.app.data.AppPreferences
import com.athar.app.data.AppPrefsSnapshot
import com.athar.app.data.CalcMethod
import com.athar.app.data.DayPrayers
import com.athar.app.data.MadhabOption
import com.athar.app.data.WidgetBgStyle
import com.athar.app.data.computeDayPrayers
import com.athar.app.data.fallbackDayPrayers
import com.athar.app.data.findNextPrayer
import com.athar.app.data.formatDigits
import com.athar.app.data.prayerDateToday
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object AtharWidgetUpdater {

    private val timeFmt = DateTimeFormatter.ofPattern("H:mm")

    fun updateAllWidgets(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context) ?: return
        val nextPrayerComponent = ComponentName(context, NextPrayerWidgetProvider::class.java)
        val wideComponent = ComponentName(context, PrayersWideWidgetProvider::class.java)

        val nextPrayerIds = appWidgetManager.getAppWidgetIds(nextPrayerComponent)
        val wideIds = appWidgetManager.getAppWidgetIds(wideComponent)

        if (nextPrayerIds.isEmpty() && wideIds.isEmpty()) return

        CoroutineScope(Dispatchers.IO).launch {
            val prefs = AppPreferences(context.applicationContext)
            val snapshot = prefs.getPreferencesSnapshot()

            val day = if (snapshot.lat != null && snapshot.lng != null) {
                runCatching {
                    computeDayPrayers(
                        snapshot.lat,
                        snapshot.lng,
                        date = LocalDate.now(),
                        method = CalcMethod.fromId(snapshot.methodId),
                        madhab = MadhabOption.fromId(snapshot.madhabId)
                    )
                }.getOrNull() ?: fallbackDayPrayers()
            } else {
                fallbackDayPrayers()
            }

            val next = findNextPrayer(day)

            nextPrayerIds.forEach { widgetId ->
                renderNextPrayerWidget(context, appWidgetManager, widgetId, snapshot, day, next)
            }

            wideIds.forEach { widgetId ->
                renderPrayersWideWidget(context, appWidgetManager, widgetId, snapshot, day, next)
            }

            scheduleNextAlarm(context, next.time, next.isTomorrow)
        }
    }

    fun renderImmediate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        widgetIds: IntArray,
        isWide: Boolean
    ) {
        val isAr = runCatching {
            context.resources.configuration.locales[0]?.language == "ar"
        }.getOrDefault(true)

        for (widgetId in widgetIds) {
            val layoutRes = if (isWide) {
                if (isAr) R.layout.widget_prayers_wide_rtl else R.layout.widget_prayers_wide
            } else {
                R.layout.widget_next_prayer
            }
            val views = RemoteViews(context.packageName, layoutRes)
            setupClickIntent(context, views)
            runCatching {
                appWidgetManager.updateAppWidget(widgetId, views)
            }
        }
    }

    private fun renderNextPrayerWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        widgetId: Int,
        snapshot: AppPrefsSnapshot,
        day: DayPrayers,
        next: com.athar.app.data.NextPrayer
    ) {
        val isAr = snapshot.language == "ar"
        val views = RemoteViews(context.packageName, R.layout.widget_next_prayer)

        // Dynamic background
        val options = appWidgetManager.getAppWidgetOptions(widgetId)
        val widthDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 160).coerceAtLeast(120)
        val heightDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 140).coerceAtLeast(120)
        val bgBitmap = generateWidgetBackground(
            context,
            widthDp,
            heightDp,
            snapshot.widgetBgStyle,
            snapshot.widgetBlurIntensity
        )
        if (bgBitmap != null) {
            views.setImageViewBitmap(R.id.widget_bg, bgBitmap)
        } else {
            views.setImageViewResource(R.id.widget_bg, R.drawable.bg_widget_theme_surface)
        }

        // Labels
        val nextLabel = if (isAr) "الصلاة التالية" else "Next Prayer"
        val remainingLabel = if (isAr) "الوقت المتبقي" else "Time Remaining"
        views.setTextViewText(R.id.widget_label_next, nextLabel)
        views.setTextViewText(R.id.widget_remaining_label, remainingLabel)

        // Location
        val locationText = snapshot.city ?: if (isAr) "موقعي" else "My Location"
        views.setTextViewText(R.id.widget_location_text, locationText)

        // Prayer name & Iqamah
        val prayerName = getPrayerName(next.key, isAr)
        val iqamahText = if (isAr) "إقامة $prayerName" else "Iqamah for $prayerName"
        views.setTextViewText(R.id.widget_prayer_name, prayerName)
        views.setTextViewText(R.id.widget_prayer_sub, iqamahText)

        // Prayer time
        val formattedTime = formatDigits(next.time.format(timeFmt), snapshot.widgetNumberStyle)
        views.setTextViewText(R.id.widget_prayer_time, formattedTime)

        // Live Countdown via Chronometer
        setupChronometer(views, R.id.widget_countdown_chrono, next.time, next.isTomorrow)

        // Tap to open app
        setupClickIntent(context, views)

        appWidgetManager.updateAppWidget(widgetId, views)
    }

    private fun renderPrayersWideWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        widgetId: Int,
        snapshot: AppPrefsSnapshot,
        day: DayPrayers,
        next: com.athar.app.data.NextPrayer
    ) {
        val isAr = snapshot.language == "ar"
        val layoutRes = if (isAr) R.layout.widget_prayers_wide_rtl else R.layout.widget_prayers_wide
        val views = RemoteViews(context.packageName, layoutRes)

        // Dynamic background
        val options = appWidgetManager.getAppWidgetOptions(widgetId)
        val widthDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 300).coerceAtLeast(260)
        val heightDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 80).coerceAtLeast(60)
        val bgBitmap = generateWidgetBackground(
            context,
            widthDp,
            heightDp,
            snapshot.widgetBgStyle,
            snapshot.widgetBlurIntensity
        )
        if (bgBitmap != null) {
            views.setImageViewBitmap(R.id.widget_bg, bgBitmap)
        } else {
            views.setImageViewResource(R.id.widget_bg, R.drawable.bg_widget_theme_surface)
        }

        // Next prayer summary
        val nextName = getPrayerName(next.key, isAr)
        val nextFormattedTime = formatDigits(next.time.format(timeFmt), snapshot.widgetNumberStyle)
        views.setTextViewText(R.id.widget_next_name, nextName)
        views.setTextViewText(R.id.widget_next_time, nextFormattedTime)

        val locationText = snapshot.city ?: if (isAr) "موقعي" else "My Location"
        views.setTextViewText(R.id.widget_next_location, locationText)

        // Live Countdown
        setupChronometer(views, R.id.widget_countdown_chrono, next.time, next.isTomorrow)

        // All prayers row configuration
        val prayerRows = listOf(
            Triple("fajr", R.id.widget_item_fajr, Pair(R.id.widget_name_fajr, R.id.widget_time_fajr)),
            Triple("sunrise", R.id.widget_item_sunrise, Pair(R.id.widget_name_sunrise, R.id.widget_time_sunrise)),
            Triple("dhuhr", R.id.widget_item_dhuhr, Pair(R.id.widget_name_dhuhr, R.id.widget_time_dhuhr)),
            Triple("asr", R.id.widget_item_asr, Pair(R.id.widget_name_asr, R.id.widget_time_asr)),
            Triple("maghrib", R.id.widget_item_maghrib, Pair(R.id.widget_name_maghrib, R.id.widget_time_maghrib)),
            Triple("isha", R.id.widget_item_isha, Pair(R.id.widget_name_isha, R.id.widget_time_isha))
        )

        for ((key, containerId, textIds) in prayerRows) {
            val isNext = (next.key == key)
            val pName = getPrayerName(key, isAr)
            val pTime = when (key) {
                "fajr" -> day.fajr
                "sunrise" -> day.sunrise
                "dhuhr" -> day.dhuhr
                "asr" -> day.asr
                "maghrib" -> day.maghrib
                else -> day.isha
            }
            val formatted = formatDigits(pTime.format(timeFmt), snapshot.widgetNumberStyle)

            views.setTextViewText(textIds.first, pName)
            views.setTextViewText(textIds.second, formatted)

            if (isNext) {
                views.setInt(containerId, "setBackgroundResource", R.drawable.bg_widget_highlight_pill)
                views.setTextColor(textIds.first, android.graphics.Color.parseColor("#A5C89E"))
                views.setTextColor(textIds.second, android.graphics.Color.parseColor("#A5C89E"))
            } else {
                views.setInt(containerId, "setBackgroundResource", R.drawable.bg_widget_normal_pill)
                views.setTextColor(textIds.first, android.graphics.Color.parseColor("#FFFFFF"))
                views.setTextColor(textIds.second, android.graphics.Color.parseColor("#C8D6C6"))
            }
        }

        // Tap to open app
        setupClickIntent(context, views)

        appWidgetManager.updateAppWidget(widgetId, views)
    }

    private fun setupChronometer(
        views: RemoteViews,
        chronoId: Int,
        targetTime: LocalTime,
        isTomorrow: Boolean
    ) {
        val targetDate = prayerDateToday(targetTime, isTomorrow)
        val diffMillis = targetDate.time - System.currentTimeMillis()
        if (diffMillis > 0) {
            val base = SystemClock.elapsedRealtime() + diffMillis
            views.setChronometerCountDown(chronoId, true)
            views.setChronometer(chronoId, base, null, true)
        } else {
            views.setChronometerCountDown(chronoId, false)
            views.setChronometer(chronoId, SystemClock.elapsedRealtime(), "00:00", false)
        }
    }

    private fun setupClickIntent(context: Context, views: RemoteViews) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

        val settingsIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("open_widget_settings", true)
        }
        val settingsPending = PendingIntent.getActivity(
            context,
            1,
            settingsIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_btn_settings, settingsPending)
    }

    private fun scheduleNextAlarm(context: Context, nextTime: LocalTime, isTomorrow: Boolean) {
        val targetDate = prayerDateToday(nextTime, isTomorrow)
        val triggerAtMillis = targetDate.time + 1000 // 1 second after prayer time

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, WidgetAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            9191,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        } catch (_: SecurityException) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
    }

    private fun getPrayerName(key: String, isAr: Boolean): String {
        val isFriday = LocalDate.now().dayOfWeek == DayOfWeek.FRIDAY
        return when (key) {
            "fajr" -> if (isAr) "الفجر" else "Fajr"
            "sunrise" -> if (isAr) "الشروق" else "Sunrise"
            "dhuhr" -> if (isFriday) {
                if (isAr) "الجمعة" else "Jumu'ah"
            } else {
                if (isAr) "الظهر" else "Dhuhr"
            }
            "asr" -> if (isAr) "العصر" else "Asr"
            "maghrib" -> if (isAr) "المغرب" else "Maghrib"
            "isha" -> if (isAr) "العشاء" else "Isha"
            else -> key
        }
    }

    fun generateWidgetBackground(
        context: Context,
        widthDp: Int,
        heightDp: Int,
        bgStyle: WidgetBgStyle,
        blurIntensity: Int
    ): Bitmap? {
        return runCatching {
            val density = context.resources.displayMetrics.density
            val w = (widthDp * density).toInt().coerceIn(100, 1920)
            val h = (heightDp * density).toInt().coerceIn(60, 1080)
            val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            val cornerRadius = 22f * density

            val rect = RectF(1.5f * density, 1.5f * density, w - 1.5f * density, h - 1.5f * density)

            val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.FILL
                when (bgStyle) {
                    WidgetBgStyle.THEME -> {
                        color = android.graphics.Color.parseColor("#141913")
                    }
                    WidgetBgStyle.TRANSLUCENT -> {
                        val alpha = ((blurIntensity / 100f) * 235).toInt().coerceIn(30, 240)
                        color = android.graphics.Color.argb(alpha, 12, 17, 12)
                    }
                }
            }
            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, bgPaint)

            val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 1.2f * density
                when (bgStyle) {
                    WidgetBgStyle.THEME -> {
                        color = android.graphics.Color.parseColor("#263224")
                    }
                    WidgetBgStyle.TRANSLUCENT -> {
                        val strokeAlpha = (((blurIntensity / 100f) * 55) + 20).toInt().coerceIn(25, 95)
                        color = android.graphics.Color.argb(strokeAlpha, 255, 255, 255)
                    }
                }
            }
            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, strokePaint)

            bitmap
        }.getOrNull()
    }
}

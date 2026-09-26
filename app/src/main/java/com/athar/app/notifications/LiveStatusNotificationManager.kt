package com.athar.app.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.athar.app.MainActivity
import com.athar.app.R
import com.athar.app.data.AppPreferences
import com.athar.app.data.CalcMethod
import com.athar.app.data.DayPrayers
import com.athar.app.data.HijriDateHelper
import com.athar.app.data.LiveStatusStyle
import com.athar.app.data.MadhabOption
import com.athar.app.data.computeDayPrayers
import com.athar.app.data.fallbackDayPrayers
import com.athar.app.data.findNextPrayer
import com.athar.app.data.formatDigits
import com.athar.app.data.prayerDateToday
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Manages the Persistent Live Status Bar notification and Android / Samsung One UI "Now Bar" / Live Activity.
 * Provides live ticking countdown, rich AMOLED olive presentation, and roll-over to upcoming prayers.
 */
object LiveStatusNotificationManager {

    const val NOTIFICATION_ID = 9550
    const val CHANNEL_ID = "athar_live_status_v1"
    private const val ALARM_REQUEST_CODE = 9551
    private val timeFmt = DateTimeFormatter.ofPattern("H:mm")

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
        if (manager.getNotificationChannel(CHANNEL_ID) != null) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.live_status_channel_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = context.getString(R.string.live_status_channel_desc)
            setShowBadge(false)
            setSound(null, null)
            enableVibration(false)
        }
        manager.createNotificationChannel(channel)
    }

    suspend fun update(context: Context) = withContext(Dispatchers.IO) {
        val appContext = context.applicationContext
        val prefs = AppPreferences(appContext)
        val enabled = prefs.liveStatusEnabled.first()
        if (!enabled) {
            cancel(appContext)
            return@withContext
        }

        ensureChannel(appContext)

        val lat = prefs.latitude.first()
        val lng = prefs.longitude.first()
        val methodId = prefs.calcMethodId.first()
        val madhabId = prefs.madhabId.first()
        val appLanguage = prefs.selectedLanguage.first()
        val liveStatusLanguage = prefs.liveStatusLanguage.first()
        val liveStatusNumberStyle = prefs.liveStatusNumberStyle.first()
        val liveStatusStyle = prefs.liveStatusStyle.first()
        val savedCity = prefs.cityLabel.first()

        val effectiveLang = if (liveStatusLanguage == "match_app") appLanguage else liveStatusLanguage
        val isAr = (effectiveLang == "ar")

        val todayDate = LocalDate.now()
        val day = if (lat != null && lng != null) {
            runCatching {
                computeDayPrayers(
                    lat, lng,
                    date = todayDate,
                    method = CalcMethod.fromId(methodId),
                    madhab = MadhabOption.fromId(madhabId)
                )
            }.getOrNull() ?: fallbackDayPrayers()
        } else {
            fallbackDayPrayers()
        }

        val next = findNextPrayer(day)
        val targetDate = LocalDate.now().plusDays(if (next.isTomorrow) 1L else 0L)
        val hijriDateText = HijriDateHelper.formatHijriDate(
            date = targetDate,
            isArabic = isAr,
            numberStyle = liveStatusNumberStyle
        )

        val prayerName = getPrayerName(next.key, isAr)
        val formattedTime = formatDigits(next.time.format(timeFmt), liveStatusNumberStyle)
        val adhanText = if (isAr) "أذان $prayerName" else "Adhan for $prayerName"
        val locationText = savedCity ?: if (isAr) "موقعي" else "My Location"
        val nextLabel = if (isAr) "الصلاة التالية" else "Next Prayer"
        val remainingLabel = if (isAr) "الوقت المتبقي" else "Time Remaining"

        // Layout selection based on chosen style and direction
        val (collapsedRes, expandedRes) = when (liveStatusStyle) {
            LiveStatusStyle.HERO -> {
                if (isAr) {
                    R.layout.notification_live_status_hero_collapsed_rtl to R.layout.notification_live_status_hero_expanded_rtl
                } else {
                    R.layout.notification_live_status_hero_collapsed to R.layout.notification_live_status_hero_expanded
                }
            }
            LiveStatusStyle.TIMELINE -> {
                if (isAr) {
                    R.layout.notification_live_status_timeline_collapsed_rtl to R.layout.notification_live_status_timeline_expanded_rtl
                } else {
                    R.layout.notification_live_status_timeline_collapsed to R.layout.notification_live_status_timeline_expanded
                }
            }
        }

        val collapsedViews = RemoteViews(appContext.packageName, collapsedRes)
        val expandedViews = RemoteViews(appContext.packageName, expandedRes)

        // Bind collapsed view
        collapsedViews.setTextViewText(R.id.live_prayer_name, prayerName)
        collapsedViews.setTextViewText(R.id.live_prayer_time, formattedTime)
        collapsedViews.setTextViewText(R.id.live_sub_info, "$locationText • $hijriDateText")
        setupChronometer(collapsedViews, R.id.live_countdown_chrono, next.time, next.isTomorrow)

        // Bind expanded view
        if (liveStatusStyle == LiveStatusStyle.HERO) {
            expandedViews.setTextViewText(R.id.live_label_next, nextLabel)
            expandedViews.setTextViewText(R.id.live_hijri_date, hijriDateText)
            expandedViews.setTextViewText(R.id.live_location_text, locationText)
            expandedViews.setTextViewText(R.id.live_prayer_name, prayerName)
            expandedViews.setTextViewText(R.id.live_prayer_sub, adhanText)
            expandedViews.setTextViewText(R.id.live_prayer_time, formattedTime)
            expandedViews.setTextViewText(R.id.live_remaining_label, remainingLabel)
            setupChronometer(expandedViews, R.id.live_countdown_chrono, next.time, next.isTomorrow)
        } else {
            expandedViews.setTextViewText(R.id.live_prayer_name, "$nextLabel: $prayerName")
            expandedViews.setTextViewText(R.id.live_prayer_time, formattedTime)
            expandedViews.setTextViewText(R.id.live_sub_info, "$locationText • $hijriDateText")
            setupChronometer(expandedViews, R.id.live_countdown_chrono, next.time, next.isTomorrow)

            // Bind 6 prayer slots
            bindTimelineChips(expandedViews, day, next.key, isAr, liveStatusNumberStyle)
        }

        // Tap intent to launch app
        val contentIntent = Intent(appContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            appContext,
            9020,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        collapsedViews.setOnClickPendingIntent(R.id.live_root, pendingIntent)
        expandedViews.setOnClickPendingIntent(R.id.live_root, pendingIntent)

        val targetPrayerDate = prayerDateToday(next.time, next.isTomorrow)
        val notifBuilder = NotificationCompat.Builder(appContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_prayer_hands)
            .setContentTitle("$prayerName • $formattedTime")
            .setContentText("$locationText • $hijriDateText")
            .setSubText(hijriDateText)
            .setWhen(targetPrayerDate.time)
            .setUsesChronometer(true)
            .setChronometerCountDown(true)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setCustomContentView(collapsedViews)
            .setCustomBigContentView(expandedViews)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setContentIntent(pendingIntent)

        // Request promotion for Android 16 Live Updates & Samsung One UI Now Bar
        val extras = Bundle().apply {
            putBoolean("android.requestPromotedOngoing", true)
        }
        notifBuilder.addExtras(extras)

        val manager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        manager?.notify(NOTIFICATION_ID, notifBuilder.build())

        // Schedule next alarm to refresh when this prayer arrives
        scheduleNextAlarm(appContext, next.time, next.isTomorrow)
    }

    fun updateAsync(context: Context) {
        val appContext = context.applicationContext
        CoroutineScope(Dispatchers.IO).launch {
            update(appContext)
        }
    }

    fun cancel(context: Context) {
        val appContext = context.applicationContext
        val manager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        manager?.cancel(NOTIFICATION_ID)

        val alarmManager = appContext.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val intent = Intent(appContext, LiveStatusAlarmReceiver::class.java)
        val pending = PendingIntent.getBroadcast(
            appContext,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager?.cancel(pending)
    }

    private fun bindTimelineChips(
        views: RemoteViews,
        day: DayPrayers,
        nextKey: String,
        isAr: Boolean,
        numberStyle: com.athar.app.data.NumberStylePreference
    ) {
        val prayers = listOf(
            Triple("fajr", R.id.live_item_fajr, Pair(R.id.live_name_fajr, R.id.live_time_fajr)),
            Triple("sunrise", R.id.live_item_sunrise, Pair(R.id.live_name_sunrise, R.id.live_time_sunrise)),
            Triple("dhuhr", R.id.live_item_dhuhr, Pair(R.id.live_name_dhuhr, R.id.live_time_dhuhr)),
            Triple("asr", R.id.live_item_asr, Pair(R.id.live_name_asr, R.id.live_time_asr)),
            Triple("maghrib", R.id.live_item_maghrib, Pair(R.id.live_name_maghrib, R.id.live_time_maghrib)),
            Triple("isha", R.id.live_item_isha, Pair(R.id.live_name_isha, R.id.live_time_isha))
        )

        for ((key, containerId, textIds) in prayers) {
            val isNext = (nextKey == key)
            val pName = getPrayerName(key, isAr)
            val pTime = when (key) {
                "fajr" -> day.fajr
                "sunrise" -> day.sunrise
                "dhuhr" -> day.dhuhr
                "asr" -> day.asr
                "maghrib" -> day.maghrib
                else -> day.isha
            }
            val formatted = formatDigits(pTime.format(timeFmt), numberStyle)

            views.setTextViewText(textIds.first, pName)
            views.setTextViewText(textIds.second, formatted)

            if (isNext) {
                views.setInt(containerId, "setBackgroundResource", R.drawable.bg_widget_chip_active)
                views.setTextColor(textIds.first, android.graphics.Color.parseColor("#C9D8B4"))
                views.setTextColor(textIds.second, android.graphics.Color.parseColor("#F4F8F3"))
            } else {
                views.setInt(containerId, "setBackgroundResource", R.drawable.bg_widget_chip_idle)
                views.setTextColor(textIds.first, android.graphics.Color.parseColor("#A4B8A2"))
                views.setTextColor(textIds.second, android.graphics.Color.parseColor("#E0E6DF"))
            }
        }
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

    private fun scheduleNextAlarm(context: Context, nextTime: LocalTime, isTomorrow: Boolean) {
        val targetDate = prayerDateToday(nextTime, isTomorrow)
        val triggerAtMillis = targetDate.time + 1000L

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, LiveStatusAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
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
}

/**
 * Receiver to roll over Live Status notification when prayer time passes.
 */
class LiveStatusAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        LiveStatusNotificationManager.updateAsync(context)
    }
}

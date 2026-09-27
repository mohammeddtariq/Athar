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
import com.athar.app.data.NumberStylePreference
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
    const val PERSISTENT_NOTIFICATION_ID = 9550
    const val NOW_BAR_NOTIFICATION_ID = 9560
    const val CHANNEL_ID = "athar_live_status_v2"
    const val NOW_BAR_CHANNEL_ID = "athar_now_bar_live_v1"
    private const val ALARM_REQUEST_CODE = 9551
    private const val DISMISS_REQUEST_CODE_PERSISTENT = 9552
    private const val DISMISS_REQUEST_CODE_NOW_BAR = 9553
    private val timeFmt = DateTimeFormatter.ofPattern("H:mm")

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

        // Clean up legacy v1 channel
        try {
            manager.deleteNotificationChannel("athar_live_status_v1")
        } catch (_: Exception) {}

        if (manager.getNotificationChannel(CHANNEL_ID) == null) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.live_status_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.live_status_channel_desc)
                setShowBadge(false)
                setSound(null, null)
                enableVibration(false)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
            }
            manager.createNotificationChannel(channel)
        }

        if (manager.getNotificationChannel(NOW_BAR_CHANNEL_ID) == null) {
            val nowBarChannel = NotificationChannel(
                NOW_BAR_CHANNEL_ID,
                context.getString(R.string.now_bar_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.now_bar_channel_desc)
                setShowBadge(false)
                setSound(null, null)
                enableVibration(false)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
            }
            manager.createNotificationChannel(nowBarChannel)
        }
    }

    suspend fun update(context: Context) = withContext(Dispatchers.IO) {
        val appContext = context.applicationContext
        val prefs = AppPreferences(appContext)
        val persistentEnabled = prefs.liveStatusEnabled.first()
        val nowBarEnabled = prefs.nowBarLiveActivityEnabled.first()
        if (!persistentEnabled && !nowBarEnabled) {
            cancel(appContext)
            return@withContext
        }

        ensureChannel(appContext)
        val manager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

        val lat = prefs.latitude.first()
        val lng = prefs.longitude.first()
        val methodId = prefs.calcMethodId.first()
        val madhabId = prefs.madhabId.first()
        val appLanguage = prefs.selectedLanguage.first()
        val savedCity = prefs.cityLabel.first()

        // 1. Persistent Notification Preferences (Independent)
        val persistentStyle = prefs.liveStatusStyle.first()
        val persistentLangPref = prefs.liveStatusLanguage.first()
        val persistentNumberStyle = prefs.liveStatusNumberStyle.first()
        val persistentEffectiveLang = if (persistentLangPref == "match_app") appLanguage else persistentLangPref
        val persistentIsAr = (persistentEffectiveLang == "ar")

        // 2. Now Bar / Live Activity Preferences (Independent)
        val nowBarStyle = prefs.nowBarStyle.first()
        val nowBarLangPref = prefs.nowBarLanguage.first()
        val nowBarNumberStyle = prefs.nowBarNumberStyle.first()
        val nowBarEffectiveLang = if (nowBarLangPref == "match_app") appLanguage else nowBarLangPref
        val nowBarIsAr = (nowBarEffectiveLang == "ar")

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
        val targetPrayerDate = prayerDateToday(next.time, next.isTomorrow)

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

        // Delete intents to auto-restore notification if dismissed from status bar
        val dismissIntentPersistent = Intent(appContext, LiveStatusDismissReceiver::class.java).apply {
            action = "com.athar.app.ACTION_RESTORE_PERSISTENT"
        }
        val dismissPendingPersistent = PendingIntent.getBroadcast(
            appContext,
            DISMISS_REQUEST_CODE_PERSISTENT,
            dismissIntentPersistent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val dismissIntentNowBar = Intent(appContext, LiveStatusDismissReceiver::class.java).apply {
            action = "com.athar.app.ACTION_RESTORE_NOW_BAR"
        }
        val dismissPendingNowBar = PendingIntent.getBroadcast(
            appContext,
            DISMISS_REQUEST_CODE_NOW_BAR,
            dismissIntentNowBar,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 1. Post or Cancel Persistent Notification (Custom RemoteViews)
        if (persistentEnabled) {
            val (collapsedRes, expandedRes) = when (persistentStyle) {
                LiveStatusStyle.HERO -> {
                    if (persistentIsAr) {
                        R.layout.notification_live_status_hero_collapsed_rtl to R.layout.notification_live_status_hero_expanded_rtl
                    } else {
                        R.layout.notification_live_status_hero_collapsed to R.layout.notification_live_status_hero_expanded
                    }
                }
                LiveStatusStyle.TIMELINE -> {
                    if (persistentIsAr) {
                        R.layout.notification_live_status_timeline_collapsed_rtl to R.layout.notification_live_status_timeline_expanded_rtl
                    } else {
                        R.layout.notification_live_status_timeline_collapsed to R.layout.notification_live_status_timeline_expanded
                    }
                }
            }

            val persistentHijriDate = HijriDateHelper.formatHijriDate(
                date = targetDate,
                isArabic = persistentIsAr,
                numberStyle = persistentNumberStyle
            )
            val persistentPrayerName = getPrayerName(next.key, persistentIsAr)
            val persistentFormattedTime = formatDigits(next.time.format(timeFmt), persistentNumberStyle)
            val persistentAdhanText = if (persistentIsAr) "أذان $persistentPrayerName" else "Adhan for $persistentPrayerName"
            val persistentLocationText = savedCity ?: if (persistentIsAr) "موقعي" else "My Location"
            val persistentNextLabel = if (persistentIsAr) "الصلاة التالية" else "Next Prayer"
            val persistentRemainingLabel = if (persistentIsAr) "الوقت المتبقي" else "Time Remaining"

            val collapsedViews = RemoteViews(appContext.packageName, collapsedRes)
            val expandedViews = RemoteViews(appContext.packageName, expandedRes)

            collapsedViews.setTextViewText(R.id.live_prayer_name, persistentPrayerName)
            collapsedViews.setTextViewText(R.id.live_prayer_time, persistentFormattedTime)
            collapsedViews.setTextViewText(R.id.live_sub_info, "$persistentLocationText • $persistentHijriDate")
            bindCountdown(collapsedViews, next.time, next.isTomorrow, persistentNumberStyle)

            if (persistentStyle == LiveStatusStyle.HERO) {
                expandedViews.setTextViewText(R.id.live_label_next, persistentNextLabel)
                expandedViews.setTextViewText(R.id.live_hijri_date, persistentHijriDate)
                expandedViews.setTextViewText(R.id.live_location_text, persistentLocationText)
                expandedViews.setTextViewText(R.id.live_prayer_name, persistentPrayerName)
                expandedViews.setTextViewText(R.id.live_prayer_sub, persistentAdhanText)
                expandedViews.setTextViewText(R.id.live_prayer_time, persistentFormattedTime)
                expandedViews.setTextViewText(R.id.live_remaining_label, persistentRemainingLabel)
                bindCountdown(expandedViews, next.time, next.isTomorrow, persistentNumberStyle)
            } else {
                bindTimelineChips(collapsedViews, day, next.key, persistentIsAr, persistentNumberStyle)
                expandedViews.setTextViewText(R.id.live_prayer_name, "$persistentNextLabel: $persistentPrayerName")
                expandedViews.setTextViewText(R.id.live_prayer_time, persistentFormattedTime)
                expandedViews.setTextViewText(R.id.live_sub_info, "$persistentLocationText • $persistentHijriDate")
                bindCountdown(expandedViews, next.time, next.isTomorrow, persistentNumberStyle)
                bindTimelineChips(expandedViews, day, next.key, persistentIsAr, persistentNumberStyle)
            }

            collapsedViews.setOnClickPendingIntent(R.id.live_root, pendingIntent)
            expandedViews.setOnClickPendingIntent(R.id.live_root, pendingIntent)

            val persistentBuilder = NotificationCompat.Builder(appContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_prayer_hands)
                .setContentTitle("$persistentPrayerName • $persistentFormattedTime")
                .setContentText("$persistentLocationText • $persistentHijriDate")
                .setSubText(persistentHijriDate)
                .setWhen(targetPrayerDate.time)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setSilent(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_STATUS)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCustomContentView(collapsedViews)
                .setCustomBigContentView(expandedViews)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setContentIntent(pendingIntent)
                .setDeleteIntent(dismissPendingPersistent)

            if (persistentNumberStyle == NumberStylePreference.WESTERN) {
                persistentBuilder.setUsesChronometer(true)
                persistentBuilder.setChronometerCountDown(true)
            } else {
                persistentBuilder.setUsesChronometer(false)
            }

            manager?.notify(PERSISTENT_NOTIFICATION_ID, persistentBuilder.build())
        } else {
            manager?.cancel(PERSISTENT_NOTIFICATION_ID)
        }

        // 2. Post or Cancel Now Bar / Live Activity Notification (Completely Independent Settings)
        if (nowBarEnabled) {
            val nowBarHijriDate = HijriDateHelper.formatHijriDate(
                date = targetDate,
                isArabic = nowBarIsAr,
                numberStyle = nowBarNumberStyle
            )
            val nowBarPrayerName = getPrayerName(next.key, nowBarIsAr)
            val nowBarFormattedTime = formatDigits(next.time.format(timeFmt), nowBarNumberStyle)
            val nowBarAdhanText = if (nowBarIsAr) "أذان $nowBarPrayerName" else "Adhan for $nowBarPrayerName"
            val nowBarLocationText = savedCity ?: if (nowBarIsAr) "موقعي" else "My Location"
            val nowBarNextLabel = if (nowBarIsAr) "الصلاة التالية" else "Next Prayer"

            val totalSecs = ((targetPrayerDate.time - System.currentTimeMillis()) / 1000L).coerceAtLeast(0L)
            val hours = totalSecs / 3600L
            val mins = (totalSecs % 3600L) / 60L
            val remainingStr = formatDigits("%02d:%02d".format(hours, mins), nowBarNumberStyle)

            val nowBarBuilder = NotificationCompat.Builder(appContext, NOW_BAR_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_prayer_hands)
                .setContentTitle("$nowBarPrayerName • $nowBarFormattedTime")
                .setSubText(nowBarPrayerName)
                .setWhen(targetPrayerDate.time)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setSilent(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_STATUS)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent)
                .setDeleteIntent(dismissPendingNowBar)

            if (nowBarNumberStyle == NumberStylePreference.WESTERN) {
                nowBarBuilder.setUsesChronometer(true)
                nowBarBuilder.setChronometerCountDown(true)
            } else {
                nowBarBuilder.setUsesChronometer(false)
            }

            if (nowBarStyle == LiveStatusStyle.HERO) {
                nowBarBuilder.setContentText("$nowBarLocationText • $nowBarHijriDate")
                nowBarBuilder.setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText("$nowBarLocationText • $nowBarHijriDate\n$nowBarAdhanText • ⏱ $remainingStr")
                )
            } else {
                val timelineText = formatTimelineSummary(day, next.key, nowBarIsAr, nowBarNumberStyle)
                nowBarBuilder.setContentText(timelineText)
                nowBarBuilder.setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText("$nowBarLocationText • $nowBarHijriDate\n$timelineText")
                )
            }

            val extras = Bundle().apply {
                putBoolean("android.requestPromotedOngoing", true)
                putString("android.substName", appContext.getString(R.string.app_name))
            }
            nowBarBuilder.addExtras(extras)

            manager?.notify(NOW_BAR_NOTIFICATION_ID, nowBarBuilder.build())
        } else {
            manager?.cancel(NOW_BAR_NOTIFICATION_ID)
        }

        // Schedule next alarm to refresh countdown / when this prayer arrives
        val requiresMinuteTick = (persistentEnabled && persistentNumberStyle == NumberStylePreference.ARABIC_INDIC) ||
                (nowBarEnabled && nowBarNumberStyle == NumberStylePreference.ARABIC_INDIC)
        scheduleNextAlarm(appContext, next.time, next.isTomorrow, requiresMinuteTick)
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
        manager?.cancel(PERSISTENT_NOTIFICATION_ID)
        manager?.cancel(NOW_BAR_NOTIFICATION_ID)

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

    private data class TimelinePrayerSlot(
        val key: String,
        val bgActiveId: Int,
        val bgIdleId: Int,
        val nameId: Int,
        val timeId: Int
    )

    private fun bindTimelineChips(
        views: RemoteViews,
        day: DayPrayers,
        nextKey: String,
        isAr: Boolean,
        numberStyle: com.athar.app.data.NumberStylePreference
    ) {
        val prayers = listOf(
            TimelinePrayerSlot("fajr", R.id.live_bg_active_fajr, R.id.live_bg_idle_fajr, R.id.live_name_fajr, R.id.live_time_fajr),
            TimelinePrayerSlot("sunrise", R.id.live_bg_active_sunrise, R.id.live_bg_idle_sunrise, R.id.live_name_sunrise, R.id.live_time_sunrise),
            TimelinePrayerSlot("dhuhr", R.id.live_bg_active_dhuhr, R.id.live_bg_idle_dhuhr, R.id.live_name_dhuhr, R.id.live_time_dhuhr),
            TimelinePrayerSlot("asr", R.id.live_bg_active_asr, R.id.live_bg_idle_asr, R.id.live_name_asr, R.id.live_time_asr),
            TimelinePrayerSlot("maghrib", R.id.live_bg_active_maghrib, R.id.live_bg_idle_maghrib, R.id.live_name_maghrib, R.id.live_time_maghrib),
            TimelinePrayerSlot("isha", R.id.live_bg_active_isha, R.id.live_bg_idle_isha, R.id.live_name_isha, R.id.live_time_isha)
        )

        for (slot in prayers) {
            val isNext = (nextKey == slot.key)
            val pName = getPrayerName(slot.key, isAr)
            val pTime = when (slot.key) {
                "fajr" -> day.fajr
                "sunrise" -> day.sunrise
                "dhuhr" -> day.dhuhr
                "asr" -> day.asr
                "maghrib" -> day.maghrib
                else -> day.isha
            }
            val formatted = formatDigits(pTime.format(timeFmt), numberStyle)

            views.setTextViewText(slot.nameId, pName)
            views.setTextViewText(slot.timeId, formatted)

            views.setViewVisibility(slot.bgActiveId, if (isNext) android.view.View.VISIBLE else android.view.View.GONE)
            views.setViewVisibility(slot.bgIdleId, if (isNext) android.view.View.GONE else android.view.View.VISIBLE)

            if (isNext) {
                views.setTextColor(slot.nameId, android.graphics.Color.parseColor("#C9D8B4"))
                views.setTextColor(slot.timeId, android.graphics.Color.parseColor("#F4F8F3"))
            } else {
                views.setTextColor(slot.nameId, android.graphics.Color.parseColor("#A4B8A2"))
                views.setTextColor(slot.timeId, android.graphics.Color.parseColor("#E0E6DF"))
            }
        }
    }

    private fun bindCountdown(
        views: RemoteViews,
        targetTime: LocalTime,
        isTomorrow: Boolean,
        numberStyle: NumberStylePreference
    ) {
        val targetDate = prayerDateToday(targetTime, isTomorrow)
        val diffMillis = targetDate.time - System.currentTimeMillis()

        if (numberStyle == NumberStylePreference.ARABIC_INDIC) {
            views.setViewVisibility(R.id.live_countdown_chrono, android.view.View.GONE)
            views.setViewVisibility(R.id.live_countdown_text, android.view.View.VISIBLE)

            val totalSecs = (diffMillis / 1000L).coerceAtLeast(0L)
            val hours = totalSecs / 3600L
            val mins = (totalSecs % 3600L) / 60L
            val secs = totalSecs % 60L
            val raw = if (hours > 0) {
                "%02d:%02d:%02d".format(hours, mins, secs)
            } else {
                "%02d:%02d".format(mins, secs)
            }
            views.setTextViewText(R.id.live_countdown_text, formatDigits(raw, NumberStylePreference.ARABIC_INDIC))
        } else {
            views.setViewVisibility(R.id.live_countdown_text, android.view.View.GONE)
            views.setViewVisibility(R.id.live_countdown_chrono, android.view.View.VISIBLE)

            if (diffMillis > 0) {
                val base = SystemClock.elapsedRealtime() + diffMillis
                views.setChronometerCountDown(R.id.live_countdown_chrono, true)
                views.setChronometer(R.id.live_countdown_chrono, base, null, true)
            } else {
                views.setChronometerCountDown(R.id.live_countdown_chrono, false)
                views.setChronometer(R.id.live_countdown_chrono, SystemClock.elapsedRealtime(), "00:00", false)
            }
        }
    }

    private fun formatTimelineSummary(
        day: DayPrayers,
        nextKey: String,
        isAr: Boolean,
        numberStyle: NumberStylePreference
    ): String {
        val slots = listOf("fajr", "sunrise", "dhuhr", "asr", "maghrib", "isha")
        return slots.joinToString(" • ") { key ->
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
            if (key == nextKey) "[$pName $formatted]" else "$pName $formatted"
        }
    }

    private fun scheduleNextAlarm(
        context: Context,
        nextTime: LocalTime,
        isTomorrow: Boolean,
        requiresMinuteTick: Boolean
    ) {
        val targetDate = prayerDateToday(nextTime, isTomorrow)
        val prayerTriggerMillis = targetDate.time + 1000L

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, LiveStatusAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerAtMillis = if (requiresMinuteTick) {
            val now = System.currentTimeMillis()
            val nextMinute = now + (60_000L - (now % 60_000L))
            minOf(nextMinute, prayerTriggerMillis)
        } else {
            prayerTriggerMillis
        }

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

/**
 * Receiver invoked if user dismisses ongoing status notifications from status bar.
 * Auto-restores them immediately if still enabled in settings.
 */
class LiveStatusDismissReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        LiveStatusNotificationManager.updateAsync(context)
    }
}

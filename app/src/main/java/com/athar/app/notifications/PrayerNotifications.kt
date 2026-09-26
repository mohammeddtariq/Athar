package com.athar.app.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.athar.app.MainActivity
import com.athar.app.R
import com.athar.app.data.AppPreferences
import com.athar.app.data.CalcMethod
import com.athar.app.data.MadhabOption
import com.athar.app.data.computeDayPrayers
import com.athar.app.data.prayerDateToday
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale

object PrayerNotifications {
    const val CHANNEL_ID = "athar_prayer_times_v2"
    private const val LEGACY_CHANNEL_ID = "athar_prayer_times"
    const val REQUEST_BASE = 4000

    fun getNotificationSoundUri(context: Context): Uri {
        return Uri.parse("android.resource://${context.packageName}/${R.raw.athar_notification}")
    }

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Clean up legacy channel so that the new unique custom sound is applied unconditionally
        try {
            manager.deleteNotificationChannel(LEGACY_CHANNEL_ID)
        } catch (_: Exception) {}

        if (manager.getNotificationChannel(CHANNEL_ID) != null) return

        val soundUri = getNotificationSoundUri(context)
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()

        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.notif_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = context.getString(R.string.notif_channel_desc)
            setSound(soundUri, audioAttributes)
            enableVibration(true)
        }
        manager.createNotificationChannel(channel)
    }

    fun prayerRequestCode(key: String): Int = when (key) {
        "fajr" -> 4001
        "sunrise" -> 4002
        "dhuhr" -> 4003
        "asr" -> 4004
        "maghrib" -> 4005
        "isha" -> 4006
        else -> REQUEST_BASE
    }

    /**
     * Schedules exact alarms for all enabled prayers across the next 24-48 hours.
     * Computes prayer times for both today and tomorrow to ensure every enabled prayer
     * is registered in AlarmManager with its distinct PendingIntent.
     */
    suspend fun scheduleNext(context: Context) = withContext(Dispatchers.IO) {
        val prefs = AppPreferences(context.applicationContext)
        val master = prefs.notificationsMaster.first()
        if (!master) {
            cancelAll(context)
            return@withContext
        }
        val lat = prefs.latitude.first() ?: return@withContext
        val lng = prefs.longitude.first() ?: return@withContext
        val method = CalcMethod.fromId(prefs.calcMethodId.first())
        val madhab = MadhabOption.fromId(prefs.madhabId.first())

        val todayDate = LocalDate.now()
        val tomorrowDate = todayDate.plusDays(1)

        val today = runCatching {
            computeDayPrayers(lat, lng, date = todayDate, method = method, madhab = madhab)
        }.getOrNull() ?: return@withContext

        val tomorrow = runCatching {
            computeDayPrayers(lat, lng, date = tomorrowDate, method = method, madhab = madhab)
        }.getOrNull() ?: return@withContext

        val nowMs = System.currentTimeMillis()
        val alarmManager = context.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val prayerKeys = listOf("fajr", "sunrise", "dhuhr", "asr", "maghrib", "isha")

        for (key in prayerKeys) {
            val enabled = prefs.prayerNotificationEnabled(key).first()
            val intent = Intent(context.applicationContext, PrayerAlarmReceiver::class.java).apply {
                putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_KEY, key)
            }
            val pending = PendingIntent.getBroadcast(
                context.applicationContext,
                prayerRequestCode(key),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            if (!enabled) {
                alarmManager.cancel(pending)
                continue
            }

            val todayTime = when (key) {
                "fajr" -> today.fajr
                "sunrise" -> today.sunrise
                "dhuhr" -> today.dhuhr
                "asr" -> today.asr
                "maghrib" -> today.maghrib
                "isha" -> today.isha
                else -> today.fajr
            }

            val tomorrowTime = when (key) {
                "fajr" -> tomorrow.fajr
                "sunrise" -> tomorrow.sunrise
                "dhuhr" -> tomorrow.dhuhr
                "asr" -> tomorrow.asr
                "maghrib" -> tomorrow.maghrib
                "isha" -> tomorrow.isha
                else -> tomorrow.fajr
            }

            val todayTriggerMs = prayerDateToday(todayTime, tomorrow = false).time
            val triggerAt = if (todayTriggerMs > nowMs + 1000L) {
                todayTriggerMs
            } else {
                prayerDateToday(tomorrowTime, tomorrow = true).time
            }

            try {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pending)
            } catch (_: SecurityException) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pending)
            }
        }

        // Keep Live Status (Now Bar & status bar) synchronized with prayer calculations
        LiveStatusNotificationManager.update(context)
    }


    /** Helper for non-suspending callers (e.g. Activity callbacks) to trigger scheduling asynchronously. */
    fun scheduleNextAsync(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            scheduleNext(context)
        }
    }

    fun cancelAll(context: Context) {
        val alarm = context.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        for (key in listOf("fajr", "sunrise", "dhuhr", "asr", "maghrib", "isha")) {
            val intent = Intent(context.applicationContext, PrayerAlarmReceiver::class.java)
            val pending = PendingIntent.getBroadcast(
                context.applicationContext,
                prayerRequestCode(key),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarm.cancel(pending)
        }
    }

    private fun getLocalizedContext(context: Context, languageCode: String): Context {
        val locale = Locale.forLanguageTag(languageCode)
        val config = Configuration(context.resources.configuration).apply {
            setLocale(locale)
        }
        return context.createConfigurationContext(config)
    }

    fun showPrayerNotification(context: Context, prayerKey: String, languageCode: String = "ar") {
        ensureChannel(context)
        val localizedContext = getLocalizedContext(context, languageCode)

        val openIntent = Intent(context.applicationContext, MainActivity::class.java)
        val openPending = PendingIntent.getActivity(
            context.applicationContext, 100,
            openIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val (titleRes, bodyRes) = when (prayerKey) {
            "fajr" -> R.string.notif_fajr_title to R.string.notif_fajr_body
            "sunrise" -> R.string.notif_sunrise_title to R.string.notif_sunrise_body
            "dhuhr" -> R.string.notif_dhuhr_title to R.string.notif_dhuhr_body
            "asr" -> R.string.notif_asr_title to R.string.notif_asr_body
            "maghrib" -> R.string.notif_maghrib_title to R.string.notif_maghrib_body
            "isha" -> R.string.notif_isha_title to R.string.notif_isha_body
            else -> R.string.notif_fajr_title to R.string.notif_fajr_body
        }

        val title = localizedContext.getString(titleRes)
        val body = localizedContext.getString(bodyRes)
        val soundUri = getNotificationSoundUri(context)

        val notification = NotificationCompat.Builder(context.applicationContext, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setSound(soundUri)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(openPending)
            .build()

        val manager = context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(prayerRequestCode(prayerKey), notification)
    }
}

class PrayerAlarmReceiver : BroadcastReceiver() {
    companion object {
        const val EXTRA_PRAYER_KEY = "prayer_key"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val key = intent.getStringExtra(EXTRA_PRAYER_KEY) ?: return
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prefs = AppPreferences(context.applicationContext)
                val langCode = prefs.selectedLanguage.first()
                PrayerNotifications.showPrayerNotification(context, key, langCode)
                PrayerNotifications.scheduleNext(context)
                com.athar.app.widget.AtharWidgetUpdater.updateAllWidgets(context)
                LiveStatusNotificationManager.update(context)
            } finally {
                pendingResult.finish()
            }
        }
    }
}

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == Intent.ACTION_TIME_CHANGED ||
            action == Intent.ACTION_TIMEZONE_CHANGED ||
            action == "android.intent.action.TIME_SET"
        ) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    if (action == Intent.ACTION_MY_PACKAGE_REPLACED) {
                        com.athar.app.updater.AppUpdateManager.cleanupDownloadedApks(context)
                    }
                    PrayerNotifications.scheduleNext(context)
                    com.athar.app.widget.AtharWidgetUpdater.updateAllWidgets(context)
                    LiveStatusNotificationManager.update(context)
                    com.athar.app.updater.UpdateCheckWorker.schedule(context)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}

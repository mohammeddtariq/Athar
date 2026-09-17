package com.athar.app.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.athar.app.MainActivity
import com.athar.app.R
import com.athar.app.data.AppPreferences
import com.athar.app.data.CalcMethod
import com.athar.app.data.MadhabOption
import com.athar.app.data.computeDayPrayers
import com.athar.app.data.findNextPrayer
import com.athar.app.data.prayerDateToday
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalTime

object PrayerNotifications {
    const val CHANNEL_ID = "athar_prayer_times"
    const val REQUEST_BASE = 4000

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (manager.getNotificationChannel(CHANNEL_ID) != null) return
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.notif_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.notif_channel_desc)
            }
        )
    }

    fun prayerRequestCode(key: String): Int = REQUEST_BASE + key.hashCode().rem(900)

    /** Schedule an exact alarm for the next enabled prayer. Call after prefs change & on boot. */
    fun scheduleNext(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val prefs = AppPreferences(context.applicationContext)
            val master = prefs.notificationsMaster.first()
            if (!master) {
                cancelAll(context)
                return@launch
            }
            val lat = prefs.latitude.first() ?: return@launch
            val lng = prefs.longitude.first() ?: return@launch
            val method = CalcMethod.fromId(prefs.calcMethodId.first())
            val madhab = MadhabOption.fromId(prefs.madhabId.first())
            val today = runCatching { computeDayPrayers(lat, lng, method = method, madhab = madhab) }
                .getOrNull() ?: return@launch

            val now = LocalTime.now()
            val ordered = listOf(
                "fajr" to today.fajr, "dhuhr" to today.dhuhr, "asr" to today.asr,
                "maghrib" to today.maghrib, "isha" to today.isha
            )
            var target: Pair<String, java.time.LocalTime>? = null
            var tomorrow = false
            for (entry in ordered) {
                val enabled = prefs.prayerNotificationEnabled(entry.first).first()
                if (!enabled) continue
                if (!entry.second.isBefore(now)) {
                    target = entry
                    break
                }
            }
            if (target == null) {
                for (entry in ordered) {
                    val enabled = prefs.prayerNotificationEnabled(entry.first).first()
                    if (!enabled) continue
                    target = entry
                    tomorrow = true
                    break
                }
            }
            val chosen = target ?: return@launch
            val triggerAt = prayerDateToday(chosen.second, tomorrow).time
            if (triggerAt <= System.currentTimeMillis()) return@launch

            val intent = Intent(context.applicationContext, PrayerAlarmReceiver::class.java).apply {
                putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_KEY, chosen.first)
            }
            val pending = PendingIntent.getBroadcast(
                context.applicationContext,
                prayerRequestCode(chosen.first),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val alarm = context.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            try {
                alarm.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pending)
            } catch (_: SecurityException) {
                alarm.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pending)
            }
        }
    }

    fun cancelAll(context: Context) {
        val alarm = context.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        for (key in listOf("fajr", "dhuhr", "asr", "maghrib", "isha")) {
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

    fun showPrayerNotification(context: Context, prayerKey: String) {
        ensureChannel(context)
        val openIntent = Intent(context.applicationContext, MainActivity::class.java)
        val openPending = PendingIntent.getActivity(
            context.applicationContext, 100,
            openIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val title = prayerTitle(context, prayerKey)
        val notification = NotificationCompat.Builder(context.applicationContext, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(context.getString(R.string.notif_prayer_body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(openPending)
            .build()
        val manager = context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(prayerRequestCode(prayerKey), notification)
    }

    private fun prayerTitle(context: Context, key: String): String {
        val res = when (key) {
            "fajr" -> R.string.home_prayer_fajr
            "dhuhr" -> R.string.home_prayer_dhuhr
            "asr" -> R.string.home_prayer_asr
            "maghrib" -> R.string.home_prayer_maghrib
            "isha" -> R.string.home_prayer_isha
            else -> R.string.home_prayer_fajr
        }
        return context.getString(R.string.notif_prayer_title, context.getString(res))
    }
}

class PrayerAlarmReceiver : BroadcastReceiver() {
    companion object {
        const val EXTRA_PRAYER_KEY = "prayer_key"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val key = intent.getStringExtra(EXTRA_PRAYER_KEY) ?: return
        PrayerNotifications.showPrayerNotification(context, key)
        // Chain the following prayer.
        PrayerNotifications.scheduleNext(context)
    }
}

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            intent.action == Intent.ACTION_TIME_CHANGED ||
            intent.action == Intent.ACTION_TIMEZONE_CHANGED
        ) {
            PrayerNotifications.scheduleNext(context)
        }
    }
}

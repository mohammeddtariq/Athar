package com.athar.app.updater

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Polls GitHub for new releases with a wake-up alarm instead of relying only on
 * WorkManager. WorkManager periodic work is deferred by Doze and OEM battery
 * managers, which is why update notifications used to appear only after the app
 * was opened. `setAndAllowWhileIdle` alarms keep firing in Doze (throttled to
 * roughly once per 15 minutes), so a release is noticed within ~30 minutes
 * without any push infrastructure.
 */
object UpdateAlarmScheduler {

    const val ACTION_CHECK_UPDATES = "com.athar.app.action.CHECK_UPDATES"
    const val DEFAULT_INTERVAL_MINUTES = 30L

    private const val REQUEST_CODE = 9101
    private const val PREFS_NAME = "athar_update_check_meta"
    private const val KEY_NEXT_ALARM_AT = "next_update_alarm_at"

    fun schedule(context: Context, delayMinutes: Long = DEFAULT_INTERVAL_MINUTES) {
        val appContext = context.applicationContext
        val alarmManager = appContext.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        // Small jitter so a fleet of devices doesn't hit the API in lockstep.
        val jitterMinutes = Random.nextLong(0, 6)
        val triggerAt = System.currentTimeMillis() +
            (delayMinutes + jitterMinutes) * 60_000L

        val pending = pendingIntent(appContext, alarmManager)
        runCatching {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pending)
        }.onFailure {
            runCatching { alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAt, pending) }
        }
        prefs(appContext).edit().putLong(KEY_NEXT_ALARM_AT, triggerAt).apply()
    }

    /** Makes sure a poll is armed; leaves an already pending alarm untouched. */
    fun ensureScheduled(context: Context) {
        val nextAt = prefs(context.applicationContext).getLong(KEY_NEXT_ALARM_AT, 0L)
        if (nextAt > System.currentTimeMillis() + 60_000L) return
        schedule(context)
    }

    fun cancel(context: Context) {
        val appContext = context.applicationContext
        val alarmManager = appContext.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        alarmManager.cancel(pendingIntent(appContext, alarmManager))
        prefs(appContext).edit().remove(KEY_NEXT_ALARM_AT).apply()
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private fun pendingIntent(context: Context, alarmManager: AlarmManager): PendingIntent {
        val intent = Intent(context, UpdateAlarmReceiver::class.java)
            .setAction(ACTION_CHECK_UPDATES)
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}

/**
 * Receives the update poll alarm, runs the GitHub check and re-arms the next poll.
 */
class UpdateAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != UpdateAlarmScheduler.ACTION_CHECK_UPDATES) return
        val appContext = context.applicationContext
        val pendingResult = goAsync()
        val startedAt = SystemClock.elapsedRealtime()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                UpdateCheckWorker.runCheck(appContext)
            } finally {
                // Keep the poll cadence stable even if the check failed.
                val elapsedMinutes = (SystemClock.elapsedRealtime() - startedAt) / 60_000L
                val delay = (UpdateAlarmScheduler.DEFAULT_INTERVAL_MINUTES - elapsedMinutes)
                    .coerceAtLeast(1L)
                UpdateAlarmScheduler.schedule(appContext, delay)
                pendingResult.finish()
            }
        }
    }
}

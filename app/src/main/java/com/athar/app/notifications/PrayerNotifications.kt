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
import com.athar.app.data.PrayerNotifMode
import com.athar.app.data.computeDayPrayers
import com.athar.app.data.prayerDateToday
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale

object PrayerNotifications {
    const val CHANNEL_ID = "athar_prayer_times_v4"
    const val CHANNEL_ID_SILENT = "athar_prayer_silent"
    private val LEGACY_CHANNEL_IDS = listOf(
        "athar_prayer_times",
        "athar_prayer_times_v2",
        "athar_prayer_times_v3"
    )
    const val REQUEST_BASE = 4000
    private const val PRE_REQUEST_BASE = 4100
    private const val POST_REQUEST_BASE = 4200
    private const val POST_REMINDER_DELAY_MS = 20 * 60_000L
    private val REMINDER_PRAYERS = listOf("fajr", "dhuhr", "asr", "maghrib", "isha")

    fun getNotificationSoundUri(context: Context): Uri {
        // Addressed by name, not by the numeric R.raw id. A channel keeps the URI
        // it was created with forever, while the numeric id is rebuilt with every
        // release (it has already moved 0x7f0a0000 -> 0x7f0b0000 -> 0x7f0c0000),
        // so channels made by older versions ended up pointing at an unrelated
        // resource and the system fell back to the default notification sound.
        //
        // The sound ships as OGG Vorbis rather than WAV: a channel whose sound
        // the system cannot decode is silently dropped on some OEMs, and OGG is
        // the format every Android build decodes for notifications.
        return Uri.parse("android.resource://${context.packageName}/raw/athar_notification")
    }

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // A channel's sound cannot be changed after it is created, so channels
        // that captured a stale sound URI are removed before the new one is made.
        LEGACY_CHANNEL_IDS.forEach { legacyId ->
            runCatching { manager.deleteNotificationChannel(legacyId) }
        }

        if (manager.getNotificationChannel(CHANNEL_ID) == null) {
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
                vibrationPattern = longArrayOf(0, 220, 180, 220)
                enableLights(true)
                setShowBadge(true)
            }
            manager.createNotificationChannel(channel)
        }

        if (manager.getNotificationChannel(CHANNEL_ID_SILENT) == null) {
            val silentChannel = NotificationChannel(
                CHANNEL_ID_SILENT,
                context.getString(R.string.notif_channel_silent_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.notif_channel_silent_desc)
                setSound(null, null)
                enableVibration(false)
            }
            manager.createNotificationChannel(silentChannel)
        }
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

    private fun beforeRequestCode(key: String): Int =
        PRE_REQUEST_BASE + REMINDER_PRAYERS.indexOf(key).coerceAtLeast(0) + 1

    private fun afterRequestCode(key: String): Int =
        POST_REQUEST_BASE + REMINDER_PRAYERS.indexOf(key).coerceAtLeast(0) + 1

    private fun scheduleExact(manager: AlarmManager, triggerAt: Long, pending: PendingIntent) {
        try {
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pending)
        } catch (_: SecurityException) {
            manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pending)
        }
    }

    /**
     * Schedules exact alarms for all enabled prayers across the next 24-48 hours.
     * Computes prayer times for both today and tomorrow to ensure every enabled prayer
     * is registered in AlarmManager with its distinct PendingIntent.
     */
    suspend fun scheduleNext(context: Context) = withContext(Dispatchers.IO) {
        val prefs = AppPreferences(context.applicationContext)
        val masterMode = prefs.notifMasterMode.first()
        if (masterMode == PrayerNotifMode.OFF) {
            cancelAll(context)
            return@withContext
        }
        val lat = prefs.latitude.first() ?: return@withContext
        val lng = prefs.longitude.first() ?: return@withContext
        val method = CalcMethod.fromId(prefs.calcMethodId.first())
        val madhab = MadhabOption.fromId(prefs.madhabId.first())
        val offsetMs = prefs.prayerNotificationOffsetMinutes.first() * 60_000L

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
            val mode = prefs.prayerNotificationMode(key).first()
            val intent = Intent(context.applicationContext, PrayerAlarmReceiver::class.java).apply {
                putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_KEY, key)
            }
            val pending = PendingIntent.getBroadcast(
                context.applicationContext,
                prayerRequestCode(key),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            if (mode == PrayerNotifMode.OFF) {
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

            val todayTriggerMs = prayerDateToday(todayTime, tomorrow = false).time + offsetMs
            val triggerAt = if (todayTriggerMs > nowMs + 1000L) {
                todayTriggerMs
            } else {
                prayerDateToday(tomorrowTime, tomorrow = true).time + offsetMs
            }

            try {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pending)
            } catch (_: SecurityException) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pending)
            }
        }

        // The two optional reminders ride alongside the prayer alarms: one a
        // quiet question after the prayer, one a calm nudge before the next.
        val preEnabled = prefs.prayerPreReminderEnabled.first()
        val preMinutes = prefs.prayerPreReminderMinutes.first()
        val postEnabled = prefs.prayerPostReminderEnabled.first()

        fun timeFor(key: String, tomorrowDay: Boolean): LocalTime {
            val day = if (tomorrowDay) tomorrow else today
            return when (key) {
                "fajr" -> day.fajr
                "sunrise" -> day.sunrise
                "dhuhr" -> day.dhuhr
                "asr" -> day.asr
                "maghrib" -> day.maghrib
                "isha" -> day.isha
                else -> day.fajr
            }
        }

        for (key in REMINDER_PRAYERS) {
            val mode = prefs.prayerNotificationMode(key).first()
            val beforePending = PendingIntent.getBroadcast(
                context.applicationContext,
                beforeRequestCode(key),
                Intent(context.applicationContext, PrayerAlarmReceiver::class.java).apply {
                    putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_KEY, key)
                    putExtra(PrayerAlarmReceiver.EXTRA_KIND, PrayerAlarmReceiver.KIND_BEFORE)
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val afterPending = PendingIntent.getBroadcast(
                context.applicationContext,
                afterRequestCode(key),
                Intent(context.applicationContext, PrayerAlarmReceiver::class.java).apply {
                    putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_KEY, key)
                    putExtra(PrayerAlarmReceiver.EXTRA_KIND, PrayerAlarmReceiver.KIND_AFTER)
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // A prayer whose own notification is off gets no reminders either.
            if (mode == PrayerNotifMode.OFF) {
                alarmManager.cancel(beforePending)
                alarmManager.cancel(afterPending)
                continue
            }

            if (preEnabled) {
                val todayAt = prayerDateToday(timeFor(key, tomorrowDay = false), tomorrow = false).time -
                    preMinutes * 60_000L
                val triggerAt = if (todayAt > nowMs + 1000L) {
                    todayAt
                } else {
                    prayerDateToday(timeFor(key, tomorrowDay = true), tomorrow = true).time -
                        preMinutes * 60_000L
                }
                scheduleExact(alarmManager, triggerAt, beforePending)
            } else {
                alarmManager.cancel(beforePending)
            }

            if (postEnabled) {
                val todayAt = prayerDateToday(timeFor(key, tomorrowDay = false), tomorrow = false).time +
                    POST_REMINDER_DELAY_MS
                val triggerAt = if (todayAt > nowMs + 1000L) {
                    todayAt
                } else {
                    prayerDateToday(timeFor(key, tomorrowDay = true), tomorrow = true).time +
                        POST_REMINDER_DELAY_MS
                }
                scheduleExact(alarmManager, triggerAt, afterPending)
            } else {
                alarmManager.cancel(afterPending)
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
        for (key in REMINDER_PRAYERS) {
            for ((code, kind) in listOf(
                beforeRequestCode(key) to PrayerAlarmReceiver.KIND_BEFORE,
                afterRequestCode(key) to PrayerAlarmReceiver.KIND_AFTER
            )) {
                val pending = PendingIntent.getBroadcast(
                    context.applicationContext,
                    code,
                    Intent(context.applicationContext, PrayerAlarmReceiver::class.java).apply {
                        putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_KEY, key)
                        putExtra(PrayerAlarmReceiver.EXTRA_KIND, kind)
                    },
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                alarm.cancel(pending)
            }
        }
    }

    private fun getLocalizedContext(context: Context, languageCode: String): Context {
        val locale = Locale.forLanguageTag(languageCode)
        val config = Configuration(context.resources.configuration).apply {
            setLocale(locale)
        }
        return context.createConfigurationContext(config)
    }

    fun showPrayerNotification(
        context: Context,
        prayerKey: String,
        languageCode: String = "ar",
        mode: PrayerNotifMode = PrayerNotifMode.SOUND
    ) {
        if (mode == PrayerNotifMode.OFF) return
        ensureChannel(context)
        val localizedContext = getLocalizedContext(context, languageCode)

        val openIntent = Intent(context.applicationContext, MainActivity::class.java)
        val openPending = PendingIntent.getActivity(
            context.applicationContext, 100,
            openIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val isFriday = LocalDate.now().dayOfWeek == DayOfWeek.FRIDAY
        val (titleRes, bodyRes) = when (prayerKey) {
            "fajr" -> R.string.notif_fajr_title to R.string.notif_fajr_body
            "sunrise" -> R.string.notif_sunrise_title to R.string.notif_sunrise_body
            "dhuhr" -> if (isFriday) {
                R.string.notif_jumaah_title to R.string.notif_jumaah_body
            } else {
                R.string.notif_dhuhr_title to R.string.notif_dhuhr_body
            }
            "asr" -> R.string.notif_asr_title to R.string.notif_asr_body
            "maghrib" -> R.string.notif_maghrib_title to R.string.notif_maghrib_body
            "isha" -> R.string.notif_isha_title to R.string.notif_isha_body
            else -> R.string.notif_fajr_title to R.string.notif_fajr_body
        }

        val title = localizedContext.getString(titleRes)
        val body = localizedContext.getString(bodyRes)
        val soundUri = getNotificationSoundUri(context)

        val targetChannel = if (mode == PrayerNotifMode.SILENT) CHANNEL_ID_SILENT else CHANNEL_ID

        val notificationBuilder = NotificationCompat.Builder(context.applicationContext, targetChannel)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(openPending)

        if (mode == PrayerNotifMode.SILENT) {
            notificationBuilder.setSound(null)
            notificationBuilder.setSilent(true)
        } else {
            notificationBuilder.setSound(soundUri)
        }

        val manager = context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(prayerRequestCode(prayerKey), notificationBuilder.build())
    }

    /**
     * The two optional reminders. They speak in the same quiet, questioning
     * tone as the prayer cards, and they follow the same sound mode as the
     * prayer they belong to.
     */
    fun showReminderNotification(
        context: Context,
        prayerKey: String,
        before: Boolean,
        languageCode: String = "ar",
        mode: PrayerNotifMode = PrayerNotifMode.SOUND
    ) {
        if (mode == PrayerNotifMode.OFF) return
        val lines = REMINDER_LINES[prayerKey] ?: return
        ensureChannel(context)
        val isArabic = languageCode.startsWith("ar")
        val localizedContext = getLocalizedContext(context, languageCode)
        val prayerName = localizedContext.getString(prayerNameRes(prayerKey))
        val title = localizedContext.getString(
            if (before) R.string.reminder_title_before else R.string.reminder_title_after,
            prayerName
        )
        val body = if (before) {
            if (isArabic) lines.beforeAr else lines.beforeEn
        } else {
            if (isArabic) lines.afterAr else lines.afterEn
        }

        val openPending = PendingIntent.getActivity(
            context.applicationContext,
            if (before) 101 else 102,
            Intent(context.applicationContext, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val targetChannel = if (mode == PrayerNotifMode.SILENT) CHANNEL_ID_SILENT else CHANNEL_ID
        val builder = NotificationCompat.Builder(context.applicationContext, targetChannel)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(openPending)

        if (mode == PrayerNotifMode.SILENT) {
            builder.setSound(null)
            builder.setSilent(true)
        } else {
            builder.setSound(getNotificationSoundUri(context))
        }

        val manager = context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(
            if (before) beforeRequestCode(prayerKey) else afterRequestCode(prayerKey),
            builder.build()
        )
    }

    private fun prayerNameRes(key: String): Int = when (key) {
        "dhuhr" -> R.string.home_prayer_dhuhr
        "asr" -> R.string.home_prayer_asr
        "maghrib" -> R.string.home_prayer_maghrib
        "isha" -> R.string.home_prayer_isha
        else -> R.string.home_prayer_fajr
    }

    private data class ReminderLines(
        val beforeAr: String,
        val beforeEn: String,
        val afterAr: String,
        val afterEn: String
    )

    private val REMINDER_LINES = mapOf(
        "fajr" to ReminderLines(
            beforeAr = "يَقْتَرِبُ الْفَجْرُ… مَاذَا تُهْدِي قَلْبَكَ قَبْلَ أَنْ يُشْرِقَ؟",
            beforeEn = "Dawn is near… what will you offer your heart before the light?",
            afterAr = "أَشْرَقَ الْفَجْرُ… هَلْ وَجَدَ قَلْبُكَ سَكِينَتَهُ بَيْنَ يَدَيْ رَبِّهِ؟",
            afterEn = "Dawn has broken… did your heart find its stillness before its Lord?"
        ),
        "dhuhr" to ReminderLines(
            beforeAr = "يَقْتَرِبُ الظُّهْرُ… أَيْنَ تُرِيحُ قَلْبَكَ فِي ذُرْوَةِ النَّهَارِ؟",
            beforeEn = "Dhuhr is near… where will you rest your heart at the height of the day?",
            afterAr = "مَضَى الظُّهْرُ… هَلْ أَخَذَ قَلْبُكَ نَصِيبَهُ مِنْ رَاحَةِ الصَّلَاةِ؟",
            afterEn = "Dhuhr has passed… did your heart take its share of the prayer's rest?"
        ),
        "asr" to ReminderLines(
            beforeAr = "حِينَ يَذُوبُ الضَّوْءُ ذَهَبًا، مَاذَا تُرْسِلُ قَبْلَ الْعَصْرِ؟",
            beforeEn = "When the light melts into gold, what will you send before Asr?",
            afterAr = "مَالَ النَّهَارُ… هَلْ سَكَنْتَ فِي الْعَصْرِ إِلَى مَنْ لَا يَغِيبُ؟",
            afterEn = "The day has leaned… did you settle into Asr with the One who never leaves?"
        ),
        "maghrib" to ReminderLines(
            beforeAr = "تَهُمُّ الشَّمْسُ بِالرَّحِيلِ… مَاذَا تَحْمِلُ مَعَكَ إِلَى الْمَغْرِبِ؟",
            beforeEn = "The sun is about to leave… what will you carry into Maghrib?",
            afterAr = "غَابَ الضَّوْءُ… هَلْ أَوَيْتَ إِلَى سَجْدَةٍ تُطْفِئُ تَعَبَ النَّهَارِ؟",
            afterEn = "The light has gone… did you take refuge in a prostration that quiets the day's fatigue?"
        ),
        "isha" to ReminderLines(
            beforeAr = "يَقْتَرِبُ اللَّيْلُ… بِمَاذَا تَخْتِمُ يَوْمَكَ قَبْلَ الْعِشَاءِ؟",
            beforeEn = "Night is near… how will you close your day before Isha?",
            afterAr = "سَكَنَ اللَّيْلُ… هَلْ خَتَمْتَ يَوْمَكَ بِسَجْدَةٍ تُنِيرُ ظَلَامَهُ؟",
            afterEn = "The night has settled… did you close your day with a prostration that lights its dark?"
        )
    )
}

class PrayerAlarmReceiver : BroadcastReceiver() {
    companion object {
        const val EXTRA_PRAYER_KEY = "prayer_key"
        const val EXTRA_KIND = "reminder_kind"
        const val KIND_PRAYER = "prayer"
        const val KIND_BEFORE = "before"
        const val KIND_AFTER = "after"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val key = intent.getStringExtra(EXTRA_PRAYER_KEY) ?: return
        val kind = intent.getStringExtra(EXTRA_KIND) ?: KIND_PRAYER
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prefs = AppPreferences(context.applicationContext)
                val langCode = prefs.selectedLanguage.first()
                val mode = prefs.prayerNotificationMode(key).first()
                if (mode != PrayerNotifMode.OFF) {
                    if (kind == KIND_PRAYER) {
                        PrayerNotifications.showPrayerNotification(context, key, langCode, mode)
                    } else {
                        PrayerNotifications.showReminderNotification(
                            context = context,
                            prayerKey = key,
                            before = kind == KIND_BEFORE,
                            languageCode = langCode,
                            mode = mode
                        )
                    }
                }
                // Only the prayer itself pauses Al-Amanah and refreshes the
                // widgets; a reminder is just a quiet word.
                if (kind == KIND_PRAYER) {
                    val shouldPauseAmanah = prefs.amanahPauseOnPrayer.first()
                    if (shouldPauseAmanah && com.athar.app.data.AlAmanahAudioController.isPlaying) {
                        withContext(Dispatchers.Main) {
                            com.athar.app.data.AlAmanahAudioController.togglePlayPause(context)
                        }
                    }
                    com.athar.app.widget.AtharWidgetUpdater.updateAllWidgets(context)
                    LiveStatusNotificationManager.update(context)
                    com.athar.app.updater.UpdateCheckWorker.checkIfDue(context.applicationContext, minIntervalMinutes = 120)
                }
                PrayerNotifications.scheduleNext(context)
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
                    MasbahaNotificationManager.update(context)
                    com.athar.app.updater.UpdateCheckWorker.schedule(context)
                    // Poll GitHub soon after boot/update, then every ~30 minutes via alarms
                    com.athar.app.updater.UpdateAlarmScheduler.schedule(context, delayMinutes = 3)
                    com.athar.app.updater.UpdateCheckWorker.runCheck(context)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}

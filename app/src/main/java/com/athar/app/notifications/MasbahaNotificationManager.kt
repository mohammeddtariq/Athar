package com.athar.app.notifications

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
import com.athar.app.data.MasbahaNotificationSnapshot
import com.athar.app.data.formatDigits
import com.athar.app.ui.corner.Dhikr
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

private const val ACTION_INCREMENT = "com.athar.app.masbaha.INCREMENT"
private const val ACTION_NEXT = "com.athar.app.masbaha.NEXT"
private const val ACTION_RESET = "com.athar.app.masbaha.RESET"

private const val REQUEST_INCREMENT = 9571
private const val REQUEST_NEXT = 9572
private const val REQUEST_RESET = 9573
private const val REQUEST_RESTORE = 9574
private const val REQUEST_CONTENT = 9575

/**
 * Mirrors Al-Yaqut's tally in an ongoing notification, the way the live prayer
 * card mirrors the next prayer. The notification carries its own +1, next-dhikr
 * and reset actions so the count can keep going after the app is left.
 *
 * The card is drawn from a snapshot so an action can repaint it *before* the
 * write lands: pressing +1 shows the new number immediately and the store is
 * updated behind it.
 */
object MasbahaNotificationManager {

    const val CHANNEL_ID = "athar_masbaha_status_v1"
    const val NOTIFICATION_ID = 9570

    private const val CYCLE = 33

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
        if (manager.getNotificationChannel(CHANNEL_ID) != null) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.masbaha_notification_channel_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = context.getString(R.string.masbaha_notification_channel_desc)
            setShowBadge(false)
            setSound(null, null)
            enableVibration(false)
            lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
        }
        manager.createNotificationChannel(channel)
    }

    fun update(context: Context) {
        val app = context.applicationContext
        CoroutineScope(Dispatchers.IO).launch { updateNow(app) }
    }

    suspend fun updateNow(context: Context) = withContext(Dispatchers.IO) {
        val prefs = AppPreferences(context)
        if (!prefs.masbahaNotificationEnabled.first()) {
            cancel(context)
            return@withContext
        }
        post(context, prefs.masbahaSnapshot())
    }

    /** Draws the card for [snapshot]; no store access, so it can run first. */
    fun post(context: Context, snapshot: MasbahaNotificationSnapshot) {
        if (!snapshot.enabled) {
            cancel(context)
            return
        }
        ensureChannel(context)
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            ?: return

        val selected = snapshot.selected.coerceIn(0, Dhikr.entries.lastIndex)
        val dhikr = Dhikr.entries[selected]
        val count = snapshot.counts.getOrElse(dhikr.storeIndex) { 0 }
        val cycle = if (count == 0) 0 else ((count - 1) % CYCLE) + 1

        val title = if (snapshot.isArabic) dhikr.arabic else dhikr.english
        val text = context.getString(
            R.string.masbaha_notification_count,
            formatDigits(count.toString(), snapshot.numberStyle),
            formatDigits(cycle.toString(), snapshot.numberStyle),
            formatDigits(CYCLE.toString(), snapshot.numberStyle)
        )

        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentPending = PendingIntent.getActivity(
            context,
            REQUEST_CONTENT,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val restorePending = PendingIntent.getBroadcast(
            context,
            REQUEST_RESTORE,
            Intent(context, MasbahaNotificationRestoreReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_masbaha)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setContentIntent(contentPending)
            .setDeleteIntent(restorePending)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setShowWhen(false)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(
                0,
                context.getString(R.string.masbaha_notification_action_increment),
                actionPending(context, ACTION_INCREMENT, REQUEST_INCREMENT)
            )
            .addAction(
                0,
                context.getString(R.string.masbaha_notification_action_next),
                actionPending(context, ACTION_NEXT, REQUEST_NEXT)
            )
            .addAction(
                0,
                context.getString(R.string.masbaha_notification_action_reset),
                actionPending(context, ACTION_RESET, REQUEST_RESET)
            )
            .build()

        manager.notify(NOTIFICATION_ID, notification)
    }

    fun cancel(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            ?: return
        manager.cancel(NOTIFICATION_ID)
    }

    private fun actionPending(context: Context, action: String, requestCode: Int): PendingIntent {
        val intent = Intent(context, MasbahaNotificationActionReceiver::class.java).apply {
            this.action = action
        }
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}

/** Applies the notification's own counting actions to the shared tally. */
class MasbahaNotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val app = context.applicationContext
        val action = intent.action ?: return
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                actionMutex.withLock { applyAction(app, action) }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private suspend fun applyAction(app: Context, action: String) {
        val prefs = AppPreferences(app)
        val snapshot = prefs.masbahaSnapshot()
        if (!snapshot.enabled) return
        val selected = snapshot.selected.coerceIn(0, Dhikr.entries.lastIndex)

        // Work out the next state and paint it straight away, then let the
        // write catch up: the count in the shade should never wait on the disk.
        val counts = snapshot.counts.toMutableList()
        var nextSelected = selected
        when (action) {
            ACTION_INCREMENT -> {
                val slot = Dhikr.entries[selected].storeIndex
                counts[slot] = counts.getOrElse(slot) { 0 } + 1
            }
            ACTION_NEXT -> nextSelected = (selected + 1) % Dhikr.entries.size
            ACTION_RESET -> {
                val slot = Dhikr.entries[selected].storeIndex
                counts[slot] = 0
            }
        }

        MasbahaNotificationManager.post(
            app,
            snapshot.copy(counts = counts, selected = nextSelected)
        )

        when (action) {
            ACTION_INCREMENT -> {
                val slot = Dhikr.entries[selected].storeIndex
                prefs.setMasbahaCount(slot, counts[slot])
            }
            ACTION_NEXT -> prefs.setMasbahaSelected(nextSelected)
            ACTION_RESET -> {
                val slot = Dhikr.entries[selected].storeIndex
                prefs.setMasbahaCount(slot, 0)
            }
        }
    }

    companion object {
        /**
         * Serialises the actions. Two quick +1 taps arrive as two broadcasts;
         * without this they would both read the same old tally and one would
         * be lost.
         */
        private val actionMutex = Mutex()
    }
}

/** Restores the ongoing card if the system or the user swipes it away. */
class MasbahaNotificationRestoreReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        MasbahaNotificationManager.update(context.applicationContext)
    }
}

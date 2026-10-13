package com.athar.app.data

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.drawable.Icon
import android.media.MediaMetadata
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.os.Build
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.athar.app.MainActivity
import com.athar.app.R

/**
 * Foreground Audio Service and MediaSession Provider for Al-Amanah.
 *
 * Exposes playback controls to Android System UI, quick settings,
 * lockscreen, Bluetooth media accessories, and the system Audio Output Switcher.
 */
class AlAmanahAudioService : Service() {

    private var mediaSession: MediaSession? = null

    companion object {
        const val CHANNEL_ID = "athar_amanah_playback"
        const val NOTIFICATION_ID = 9580

        const val ACTION_PLAY_PAUSE = "com.athar.app.amanah.PLAY_PAUSE"
        const val ACTION_NEXT = "com.athar.app.amanah.NEXT"
        const val ACTION_PREV = "com.athar.app.amanah.PREV"
        const val ACTION_STOP = "com.athar.app.amanah.STOP"
        const val ACTION_UPDATE = "com.athar.app.amanah.UPDATE"

        fun startOrUpdate(context: Context) {
            val intent = Intent(context, AlAmanahAudioService::class.java).apply {
                action = ACTION_UPDATE
            }
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ContextCompat.startForegroundService(context, intent)
                } else {
                    context.startService(intent)
                }
            } catch (_: Exception) {}
        }

        fun stop(context: Context) {
            try {
                val intent = Intent(context, AlAmanahAudioService::class.java).apply {
                    action = ACTION_STOP
                }
                context.startService(intent)
            } catch (_: Exception) {}
        }
    }

    override fun onCreate() {
        super.onCreate()
        ensureNotificationChannel()
        setupMediaSession()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY_PAUSE -> {
                AlAmanahAudioController.togglePlayPause(this)
            }
            ACTION_NEXT -> {
                AlAmanahAudioController.skipNext(this)
            }
            ACTION_PREV -> {
                AlAmanahAudioController.skipPrevious(this)
            }
            ACTION_STOP -> {
                AlAmanahAudioController.releasePlayer()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return START_NOT_STICKY
            }
        }

        updateNotificationAndSession()
        return START_STICKY
    }

    private fun setupMediaSession() {
        mediaSession = MediaSession(this, "AlAmanahAudioSession").apply {
            setCallback(object : MediaSession.Callback() {
                override fun onPlay() {
                    AlAmanahAudioController.togglePlayPause(this@AlAmanahAudioService)
                    updateNotificationAndSession()
                }

                override fun onPause() {
                    AlAmanahAudioController.togglePlayPause(this@AlAmanahAudioService)
                    updateNotificationAndSession()
                }

                override fun onSkipToNext() {
                    AlAmanahAudioController.skipNext(this@AlAmanahAudioService)
                    updateNotificationAndSession()
                }

                override fun onSkipToPrevious() {
                    AlAmanahAudioController.skipPrevious(this@AlAmanahAudioService)
                    updateNotificationAndSession()
                }

                override fun onSeekTo(pos: Long) {
                    AlAmanahAudioController.seekTo(pos)
                    updateNotificationAndSession()
                }

                override fun onStop() {
                    AlAmanahAudioController.releasePlayer()
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }
            })
            isActive = true
        }
    }

    fun updateNotificationAndSession() {
        val session = mediaSession ?: return
        val isPlaying = AlAmanahAudioController.isPlaying
        val surah = AlAmanahAudioController.currentSurah
        val reciter = AlAmanahAudioController.currentReciter
        val positionMs = AlAmanahAudioController.currentPositionMs
        val durationMs = AlAmanahAudioController.totalDurationMs

        // 1. Update PlaybackState in MediaSession
        val stateActions = PlaybackState.ACTION_PLAY or
                PlaybackState.ACTION_PAUSE or
                PlaybackState.ACTION_PLAY_PAUSE or
                PlaybackState.ACTION_SKIP_TO_NEXT or
                PlaybackState.ACTION_SKIP_TO_PREVIOUS or
                PlaybackState.ACTION_SEEK_TO or
                PlaybackState.ACTION_STOP

        val playbackState = PlaybackState.Builder()
            .setActions(stateActions)
            .setState(
                if (isPlaying) PlaybackState.STATE_PLAYING else PlaybackState.STATE_PAUSED,
                positionMs,
                AlAmanahAudioController.playbackSpeed
            )
            .build()
        session.setPlaybackState(playbackState)

        // 2. Update MediaMetadata in MediaSession
        val metadata = MediaMetadata.Builder()
            .putString(MediaMetadata.METADATA_KEY_TITLE, "سورة ${surah.arabicName} • ${surah.englishName}")
            .putString(MediaMetadata.METADATA_KEY_ARTIST, reciter.arabicName)
            .putString(MediaMetadata.METADATA_KEY_ALBUM, "الأَمَنَة")
            .putLong(MediaMetadata.METADATA_KEY_DURATION, durationMs)
            .build()
        session.setMetadata(metadata)

        // 3. Build & update Foreground Notification with MediaStyle
        val mainIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val mainPending = PendingIntent.getActivity(
            this, 100, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val prevIntent = Intent(this, AlAmanahAudioService::class.java).apply { action = ACTION_PREV }
        val prevPending = PendingIntent.getService(
            this, 101, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val prevAction = Notification.Action.Builder(
            Icon.createWithResource(this, android.R.drawable.ic_media_previous),
            "Previous",
            prevPending
        ).build()

        val playPauseIntent = Intent(this, AlAmanahAudioService::class.java).apply { action = ACTION_PLAY_PAUSE }
        val playPausePending = PendingIntent.getService(
            this, 102, playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val playPauseIcon = if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
        val playPauseAction = Notification.Action.Builder(
            Icon.createWithResource(this, playPauseIcon),
            if (isPlaying) "Pause" else "Play",
            playPausePending
        ).build()

        val nextIntent = Intent(this, AlAmanahAudioService::class.java).apply { action = ACTION_NEXT }
        val nextPending = PendingIntent.getService(
            this, 103, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val nextAction = Notification.Action.Builder(
            Icon.createWithResource(this, android.R.drawable.ic_media_next),
            "Next",
            nextPending
        ).build()

        val stopIntent = Intent(this, AlAmanahAudioService::class.java).apply { action = ACTION_STOP }
        val stopPending = PendingIntent.getService(
            this, 104, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val mediaStyle = Notification.MediaStyle()
            .setMediaSession(session.sessionToken)
            .setShowActionsInCompactView(0, 1, 2)

        val notification = Notification.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_quran_book)
            .setContentTitle("سورة ${surah.arabicName}")
            .setContentText("${reciter.arabicName} • الجزء ${surah.juz}")
            .setSubText("الأَمَنَة")
            .setContentIntent(mainPending)
            .setDeleteIntent(stopPending)
            .setStyle(mediaStyle)
            .addAction(prevAction)
            .addAction(playPauseAction)
            .addAction(nextAction)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setOngoing(isPlaying)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun ensureNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
            val existing = manager.getNotificationChannel(CHANNEL_ID)
            if (existing == null) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "تشغيل الأَمَنَة",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "إشعارات التحكم في تلاوة وسماع القرآن الكريم للأَمَنَة"
                    setShowBadge(false)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                }
                manager.createNotificationChannel(channel)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSession?.isActive = false
        mediaSession?.release()
        mediaSession = null
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

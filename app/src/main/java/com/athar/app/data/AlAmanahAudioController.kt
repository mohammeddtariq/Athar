package com.athar.app.data

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.athar.app.ui.corner.SurahMeta
import com.athar.app.ui.corner.allSurahs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URI

/**
 * Core Audio Controller & Offline Engine for "Al-Amanah" (الأَمَنَة).
 *
 * Powers the synchronized recitation player with:
 * - Real-time acoustic synchronization (verse timestamps)
 * - Auto-scrolling lyrics coordination
 * - Seamless offline downloads & local file playback
 * - Daily listening time tracking & streaks
 * - Mini-player state sharing
 */
object AlAmanahAudioController {

    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var mediaPlayer: MediaPlayer? = null
    private var syncJob: Job? = null
    private var listeningTrackerJob: Job? = null

    // Reactive State for UI
    var currentSurah by mutableStateOf<SurahMeta>(allSurahs.first())
        private set

    var currentReciter by mutableStateOf<QuranReciter>(QuranReciter.MINSHAWI)
        private set

    var isPlaying by mutableStateOf(false)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var currentPositionMs by mutableLongStateOf(0L)
        private set

    var totalDurationMs by mutableLongStateOf(0L)
        private set

    var chapterTiming by mutableStateOf<ChapterRecitationTiming?>(null)
        private set

    var activeVerseNumber by mutableStateOf<Int?>(null)
        private set

    var isOfflinePlayback by mutableStateOf(false)
        private set

    // Download progress map: surahNumber -> 0.0f..1.0f (or null if not downloading)
    val downloadProgressMap = mutableStateMapOf<Int, Float>()

    // Volume level 0f..1f
    var playerVolume by mutableStateOf(1f)
        private set

    var playbackSpeed by androidx.compose.runtime.mutableFloatStateOf(1.0f)
        private set

    var sleepTimerMinutes by mutableStateOf<Int?>(null)
        private set
    private var sleepTimerJob: Job? = null

    enum class PlaybackMode {
        SEQUENTIAL,
        REPEAT_ONE,
        REPEAT_ALL,
        SHUFFLE
    }

    var playbackMode by mutableStateOf(PlaybackMode.SEQUENTIAL)
        private set

    fun cyclePlaybackMode(): PlaybackMode {
        val modes = PlaybackMode.entries
        val next = modes[(playbackMode.ordinal + 1) % modes.size]
        playbackMode = next
        return next
    }

    fun updatePlaybackMode(mode: PlaybackMode) {
        playbackMode = mode
    }

    fun getAudioStorageDir(context: Context): File =
        File(context.filesDir, "amanah_audio").apply { if (!exists()) mkdirs() }

    fun getDownloadedFile(context: Context, surahNumber: Int, reciter: QuranReciter): File =
        File(getAudioStorageDir(context), "${reciter.id}_${surahNumber}.mp3")

    fun isSurahDownloaded(context: Context, surahNumber: Int, reciter: QuranReciter): Boolean =
        getDownloadedFile(context, surahNumber, reciter).run { exists() && length() > 1024L }

    fun playSurah(
        context: Context,
        surah: SurahMeta,
        reciter: QuranReciter? = null,
        startFromVerse: Int? = null
    ) {
        val selectedReciter = reciter ?: currentReciter
        currentSurah = surah
        currentReciter = selectedReciter
        isLoading = true
        isPlaying = false

        // Save last playback in preferences
        scope.launch(Dispatchers.IO) {
            val prefs = AppPreferences(context.applicationContext)
            prefs.setAmanahLastPlayback(surah.number, selectedReciter.id)
        }

        // Release existing player
        releasePlayer()

        scope.launch {
            // 1. Fetch or load timing
            val timing = QuranRecitationSyncRepository.getChapterTiming(
                context = context,
                reciter = selectedReciter,
                chapter = surah.number
            )
            chapterTiming = timing

            // 2. Setup MediaPlayer
            val localFile = getDownloadedFile(context, surah.number, selectedReciter)
            val isLocal = localFile.exists() && localFile.length() > 1024L
            isOfflinePlayback = isLocal

            val audioUri = if (isLocal) {
                localFile.absolutePath
            } else {
                timing?.audioUrl?.ifBlank { null } ?: selectedReciter.getPrimaryAudioUrl(surah.number)
            }

            try {
                val player = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    setVolume(playerVolume, playerVolume)
                    setDataSource(audioUri)
                }

                player.setOnPreparedListener { mp ->
                    isLoading = false
                    totalDurationMs = mp.duration.toLong().coerceAtLeast(0L)
                    
                    if (startFromVerse != null && timing != null) {
                        val verseTiming = timing.verses.firstOrNull { it.verseNumber == startFromVerse }
                        if (verseTiming != null) {
                            mp.seekTo(verseTiming.startMs.toInt())
                        }
                    }

                    mp.start()
                    isPlaying = true
                    startSyncLoop(context)
                }

                player.setOnCompletionListener {
                    isPlaying = false
                    currentPositionMs = totalDurationMs
                    handleTrackCompletion(context)
                }

                player.setOnErrorListener { _, _, _ ->
                    isLoading = false
                    isPlaying = false
                    // Try fallback stream if primary failed and not local
                    if (!isLocal) {
                        playFallbackStream(context, surah, selectedReciter)
                    }
                    true
                }

                player.prepareAsync()
                mediaPlayer = player

            } catch (_: Exception) {
                isLoading = false
                isPlaying = false
            }
        }
    }

    private fun playFallbackStream(context: Context, surah: SurahMeta, reciter: QuranReciter) {
        val fallbackUrl = reciter.getFallbackAudioUrl(surah.number)
        try {
            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setVolume(playerVolume, playerVolume)
                setDataSource(fallbackUrl)
            }
            player.setOnPreparedListener { mp ->
                isLoading = false
                totalDurationMs = mp.duration.toLong().coerceAtLeast(0L)
                mp.start()
                isPlaying = true
                startSyncLoop(context)
            }
            player.setOnCompletionListener {
                isPlaying = false
                handleTrackCompletion(context)
            }
            player.prepareAsync()
            mediaPlayer = player
        } catch (_: Exception) {
            isLoading = false
            isPlaying = false
        }
    }

    fun togglePlayPause(context: Context? = null) {
        val player = mediaPlayer ?: return
        try {
            if (player.isPlaying) {
                player.pause()
                isPlaying = false
                stopSyncLoop()
            } else {
                player.start()
                isPlaying = true
                if (context != null) startSyncLoop(context)
            }
        } catch (_: Exception) {}
    }

    fun seekTo(positionMs: Long) {
        val player = mediaPlayer ?: return
        try {
            player.seekTo(positionMs.toInt())
            currentPositionMs = positionMs
            updateActiveVerse(positionMs)
        } catch (_: Exception) {}
    }

    fun seekToVerse(verseNumber: Int) {
        val timing = chapterTiming ?: return
        val verse = timing.verses.firstOrNull { it.verseNumber == verseNumber } ?: return
        seekTo(verse.startMs)
    }

    private fun handleTrackCompletion(context: Context) {
        when (playbackMode) {
            PlaybackMode.REPEAT_ONE -> {
                seekTo(0)
                mediaPlayer?.start()
                isPlaying = true
                startSyncLoop(context)
            }
            PlaybackMode.SHUFFLE -> {
                playRandomSurah(context)
            }
            PlaybackMode.REPEAT_ALL, PlaybackMode.SEQUENTIAL -> {
                playNextSurah(context)
            }
        }
    }

    fun playRandomSurah(context: Context) {
        val remaining = allSurahs.filter { it.number != currentSurah.number }
        val nextSurah = if (remaining.isNotEmpty()) remaining.random() else allSurahs.random()
        playSurah(context, nextSurah, currentReciter)
    }

    fun playNextSurah(context: Context) {
        val nextNum = if (currentSurah.number < 114) currentSurah.number + 1 else 1
        val nextSurah = allSurahs.firstOrNull { it.number == nextNum } ?: allSurahs.first()
        playSurah(context, nextSurah, currentReciter)
    }

    fun playPreviousSurah(context: Context) {
        val prevNum = if (currentSurah.number > 1) currentSurah.number - 1 else 114
        val prevSurah = allSurahs.firstOrNull { it.number == prevNum } ?: allSurahs.last()
        playSurah(context, prevSurah, currentReciter)
    }

    fun skipNext(context: Context) {
        if (playbackMode == PlaybackMode.SHUFFLE) {
            playRandomSurah(context)
            return
        }
        val timing = chapterTiming
        val curVerse = activeVerseNumber
        if (timing != null && curVerse != null) {
            val nextVerseTiming = timing.verses.firstOrNull { it.verseNumber == curVerse + 1 }
            if (nextVerseTiming != null) {
                seekTo(nextVerseTiming.startMs)
                return
            }
        }
        playNextSurah(context)
    }

    fun skipPrevious(context: Context) {
        if (playbackMode == PlaybackMode.SHUFFLE) {
            playRandomSurah(context)
            return
        }
        val timing = chapterTiming
        val curVerse = activeVerseNumber
        val curPos = currentPositionMs
        if (timing != null && curVerse != null) {
            val curVerseTiming = timing.verses.firstOrNull { it.verseNumber == curVerse }
            if (curVerseTiming != null && curPos - curVerseTiming.startMs > 2500L) {
                seekTo(curVerseTiming.startMs)
                return
            }
            val prevVerseTiming = timing.verses.firstOrNull { it.verseNumber == curVerse - 1 }
            if (prevVerseTiming != null) {
                seekTo(prevVerseTiming.startMs)
                return
            }
        }
        playPreviousSurah(context)
    }

    fun cycleSpeed() {
        val speeds = listOf(1.0f, 1.25f, 1.5f, 0.75f)
        val curIdx = speeds.indexOf(playbackSpeed)
        val nextSpeed = if (curIdx != -1) speeds[(curIdx + 1) % speeds.size] else 1.0f
        setSpeed(nextSpeed)
    }

    fun setSpeed(speed: Float) {
        playbackSpeed = speed
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                val player = mediaPlayer ?: return
                val params = player.playbackParams
                params.speed = speed
                player.playbackParams = params
            }
        } catch (_: Exception) {}
    }

    fun setSleepTimer(minutes: Int?) {
        sleepTimerMinutes = minutes
        sleepTimerJob?.cancel()
        if (minutes == null || minutes <= 0) {
            sleepTimerJob = null
            return
        }
        sleepTimerJob = scope.launch {
            delay(minutes * 60 * 1000L)
            togglePlayPause()
            sleepTimerMinutes = null
        }
    }

    fun changeReciter(context: Context, newReciter: QuranReciter) {
        if (newReciter == currentReciter) return
        currentReciter = newReciter
        playSurah(context, currentSurah, newReciter)
    }

    fun setVolume(volume: Float) {
        val clamped = volume.coerceIn(0f, 1f)
        playerVolume = clamped
        try {
            mediaPlayer?.setVolume(clamped, clamped)
        } catch (_: Exception) {}
    }

    private fun startSyncLoop(context: Context) {
        syncJob?.cancel()
        listeningTrackerJob?.cancel()

        // 1. Fast position & verse syncing (30ms for 120Hz responsiveness)
        syncJob = scope.launch {
            while (isActive && isPlaying) {
                val player = mediaPlayer
                if (player != null && player.isPlaying) {
                    try {
                        val pos = player.currentPosition.toLong()
                        currentPositionMs = pos
                        updateActiveVerse(pos)
                    } catch (_: Exception) {}
                }
                delay(30L)
            }
        }

        // 2. Listening Time Tracker (increments seconds to today's goal)
        listeningTrackerJob = scope.launch(Dispatchers.IO) {
            val prefs = AppPreferences(context.applicationContext)
            while (isActive && isPlaying) {
                delay(1000L)
                if (isPlaying) {
                    prefs.recordAmanahListeningSeconds(1L)
                }
            }
        }
    }

    private fun stopSyncLoop() {
        syncJob?.cancel()
        syncJob = null
        listeningTrackerJob?.cancel()
        listeningTrackerJob = null
    }

    private fun updateActiveVerse(posMs: Long) {
        val timing = chapterTiming ?: return
        val active = timing.findActiveVerse(posMs)
        activeVerseNumber = active?.verseNumber
    }

    fun releasePlayer() {
        stopSyncLoop()
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {}
        mediaPlayer = null
        isPlaying = false
    }

    /**
     * Downloads complete Surah audio file + chapter timing JSON for 100% offline playback.
     */
    fun downloadSurah(
        context: Context,
        surah: SurahMeta,
        reciter: QuranReciter,
        onComplete: (Boolean) -> Unit = {}
    ) {
        if (isSurahDownloaded(context, surah.number, reciter)) {
            onComplete(true)
            return
        }

        val surahNum = surah.number
        downloadProgressMap[surahNum] = 0.05f

        scope.launch(Dispatchers.IO) {
            try {
                // Step A: Ensure timing JSON is downloaded and cached
                val timing = QuranRecitationSyncRepository.getChapterTiming(context, reciter, surahNum)
                downloadProgressMap[surahNum] = 0.15f

                // Step B: Resolve audio download URL
                val audioUrl = timing?.audioUrl?.ifBlank { null }
                    ?: reciter.getPrimaryAudioUrl(surahNum)

                val targetFile = getDownloadedFile(context, surahNum, reciter)
                val tempFile = File(targetFile.parentFile, "${targetFile.name}.tmp")

                val conn = (URI.create(audioUrl).toURL().openConnection() as HttpURLConnection).apply {
                    connectTimeout = 10000
                    readTimeout = 15000
                    requestMethod = "GET"
                    setRequestProperty("User-Agent", "AtharApp/1.0.6")
                }

                if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                    val contentLength = conn.contentLength.toLong()
                    conn.inputStream.use { input ->
                        FileOutputStream(tempFile).use { output ->
                            val buffer = ByteArray(8192)
                            var bytesRead: Int
                            var totalRead = 0L

                            while (input.read(buffer).also { bytesRead = it } != -1) {
                                output.write(buffer, 0, bytesRead)
                                totalRead += bytesRead
                                if (contentLength > 0) {
                                    val progress = 0.15f + ((totalRead.toFloat() / contentLength) * 0.85f)
                                    withContext(Dispatchers.Main) {
                                        downloadProgressMap[surahNum] = progress.coerceIn(0f, 1f)
                                    }
                                }
                            }
                        }
                    }

                    if (tempFile.exists() && tempFile.length() > 1024L) {
                        if (targetFile.exists()) targetFile.delete()
                        tempFile.renameTo(targetFile)
                    }

                    withContext(Dispatchers.Main) {
                        downloadProgressMap.remove(surahNum)
                        if (currentSurah.number == surahNum) {
                            isOfflinePlayback = true
                        }
                        onComplete(true)
                    }
                } else {
                    tempFile.delete()
                    withContext(Dispatchers.Main) {
                        downloadProgressMap.remove(surahNum)
                        onComplete(false)
                    }
                }
            } catch (_: Exception) {
                withContext(Dispatchers.Main) {
                    downloadProgressMap.remove(surahNum)
                    onComplete(false)
                }
            }
        }
    }

    fun deleteDownloadedSurah(context: Context, surahNumber: Int, reciter: QuranReciter) {
        val file = getDownloadedFile(context, surahNumber, reciter)
        if (file.exists()) {
            file.delete()
        }
        if (currentSurah.number == surahNumber && currentReciter == reciter) {
            isOfflinePlayback = false
        }
    }

    enum class AudioOutputType {
        PHONE,
        BLUETOOTH_OR_HEADSET
    }

    private var explicitTarget by mutableStateOf<AudioOutputType?>(null)

    fun setPreferredAudioTarget(target: AudioOutputType) {
        explicitTarget = target
    }

    fun getActiveAudioOutputType(context: Context): AudioOutputType {
        val connectedDevice = getConnectedAudioDeviceName(context)
        if (connectedDevice == null) {
            return AudioOutputType.PHONE
        }
        return explicitTarget ?: AudioOutputType.BLUETOOTH_OR_HEADSET
    }

    fun getDeviceModelName(): String {
        val model = android.os.Build.MODEL ?: "Phone"
        return model
    }

    fun getConnectedAudioDeviceName(context: Context): String? {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? android.media.AudioManager ?: return null
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val devices = audioManager.getDevices(android.media.AudioManager.GET_DEVICES_OUTPUTS)
            for (device in devices) {
                val isMediaAudioDevice = device.type == android.media.AudioDeviceInfo.TYPE_BLUETOOTH_A2DP ||
                        device.type == android.media.AudioDeviceInfo.TYPE_WIRED_HEADSET ||
                        device.type == android.media.AudioDeviceInfo.TYPE_WIRED_HEADPHONES ||
                        device.type == android.media.AudioDeviceInfo.TYPE_USB_HEADSET ||
                        (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S &&
                            (device.type == android.media.AudioDeviceInfo.TYPE_BLE_HEADSET ||
                             device.type == android.media.AudioDeviceInfo.TYPE_BLE_SPEAKER))
                if (isMediaAudioDevice) {
                    val name = device.productName?.toString() ?: ""
                    val lower = name.lowercase(java.util.Locale.ROOT)
                    // Filter out smartwatches / fitness bands that are not actual audio listening headphones
                    if (lower.contains("watch") || lower.contains("band") || lower.contains("fit")) {
                        continue
                    }
                    if (name.isNotBlank()) return name
                }
            }
        }
        @Suppress("DEPRECATION")
        if (audioManager.isBluetoothA2dpOn) {
            return "Bluetooth Audio"
        }
        return null
    }

    fun openCastSettings(context: Context) {
        try {
            val intent = android.content.Intent(android.provider.Settings.ACTION_CAST_SETTINGS).apply {
                flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            try {
                val intent = android.content.Intent(android.provider.Settings.ACTION_SOUND_SETTINGS).apply {
                    flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            } catch (_: Exception) {}
        }
    }

    fun openSystemAudioOutputPicker(context: Context) {
        try {
            val intent = android.content.Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS).apply {
                flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            try {
                val intent = android.content.Intent(android.provider.Settings.ACTION_SOUND_SETTINGS).apply {
                    flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            } catch (_: Exception) {}
        }
    }
}

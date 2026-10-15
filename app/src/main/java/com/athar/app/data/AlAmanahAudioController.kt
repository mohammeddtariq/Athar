package com.athar.app.data

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioDeviceCallback
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import java.util.Locale
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
    private var playJob: Job? = null
    private var syncJob: Job? = null
    private var listeningTrackerJob: Job? = null
    private var appContext: Context? = null

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
        startFromVerse: Int? = null,
        startPositionMs: Long? = null,
        autoPlay: Boolean = true,
        recordHistory: Boolean = true
    ) {
        appContext = context.applicationContext
        initAudioRoutingListener(context)
        val selectedReciter = reciter ?: currentReciter
        currentSurah = surah
        currentReciter = selectedReciter
        isLoading = true
        isPlaying = false

        // Record playback and add surah to user's real listening history
        if (recordHistory) {
            scope.launch(Dispatchers.IO) {
                val prefs = AppPreferences(context.applicationContext)
                prefs.setAmanahLastPlayback(surah.number, selectedReciter.id)
                prefs.recordAmanahPlayedSurah(surah.number)
            }
        }

        // Release existing player without killing the service
        releasePlayer()

        playJob?.cancel()
        playJob = scope.launch {
            val localFile = getDownloadedFile(context, surah.number, selectedReciter)
            val isLocal = localFile.exists() && localFile.length() > 1024L
            isOfflinePlayback = isLocal

            val audioUri = if (isLocal) {
                localFile.absolutePath
            } else {
                selectedReciter.getPrimaryAudioUrl(surah.number)
            }

            // Asynchronously resolve verse timings without delaying audio buffering
            launch(Dispatchers.IO) {
                val timing = QuranRecitationSyncRepository.getChapterTiming(
                    context = context,
                    reciter = selectedReciter,
                    chapter = surah.number
                )
                withContext(Dispatchers.Main) {
                    chapterTiming = timing
                    if (startFromVerse != null && timing != null) {
                        val verseTiming = timing.verses.firstOrNull { it.verseNumber == startFromVerse }
                        if (verseTiming != null) {
                            try {
                                mediaPlayer?.seekTo(verseTiming.startMs.toInt())
                            } catch (_: Exception) {}
                        }
                    }
                }
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
                    if (isLocal) {
                        setDataSource(audioUri)
                    } else {
                        val headers = mapOf(
                            "User-Agent" to "AtharApp/1.0.6",
                            "Accept" to "*/*"
                        )
                        setDataSource(context, Uri.parse(audioUri), headers)
                    }
                }

                player.setOnPreparedListener { mp ->
                    isLoading = false
                    totalDurationMs = mp.duration.toLong().coerceAtLeast(0L)
                    applyAudioRouting(context)

                    val seekTargetMs: Int? = when {
                        startPositionMs != null -> startPositionMs.toInt()
                        startFromVerse != null -> chapterTiming?.verses
                            ?.firstOrNull { it.verseNumber == startFromVerse }
                            ?.startMs?.toInt()
                        else -> null
                    }
                    if (seekTargetMs != null) {
                        try {
                            mp.seekTo(seekTargetMs)
                        } catch (_: Exception) {}
                    }

                    if (autoPlay) {
                        mp.start()
                        isPlaying = true
                        startSyncLoop(context)
                        AlAmanahAudioService.startOrUpdate(context)
                    }
                }

                player.setOnCompletionListener {
                    isPlaying = false
                    currentPositionMs = totalDurationMs
                    handleTrackCompletion(context)
                }

                player.setOnErrorListener { mp, _, _ ->
                    runCatching { mp.reset(); mp.release() }
                    if (!isLocal) {
                        playFallbackStream(context, surah, selectedReciter)
                    } else {
                        isLoading = false
                        isPlaying = false
                    }
                    true
                }

                mediaPlayer = player
                applyAudioRouting(context)
                player.prepareAsync()

            } catch (_: Exception) {
                if (!isLocal) {
                    playFallbackStream(context, surah, selectedReciter)
                } else {
                    isLoading = false
                    isPlaying = false
                }
            }

            // Safety watchdog timeout (18s): ensure spinner never spins indefinitely
            launch {
                delay(18000L)
                if (isLoading && !isPlaying) {
                    isLoading = false
                }
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
                val headers = mapOf(
                    "User-Agent" to "AtharApp/1.0.6",
                    "Accept" to "*/*"
                )
                setDataSource(context, Uri.parse(fallbackUrl), headers)
            }
            player.setOnPreparedListener { mp ->
                isLoading = false
                totalDurationMs = mp.duration.toLong().coerceAtLeast(0L)
                applyAudioRouting(context)
                mp.start()
                isPlaying = true
                startSyncLoop(context)
                AlAmanahAudioService.startOrUpdate(context)
            }
            player.setOnCompletionListener {
                isPlaying = false
                handleTrackCompletion(context)
            }
            player.setOnErrorListener { mp, _, _ ->
                runCatching { mp.reset(); mp.release() }
                isLoading = false
                isPlaying = false
                true
            }
            mediaPlayer = player
            applyAudioRouting(context)
            player.prepareAsync()
        } catch (_: Exception) {
            isLoading = false
            isPlaying = false
        }
    }

    fun togglePlayPause(context: Context? = null) {
        if (context != null) appContext = context.applicationContext
        val player = mediaPlayer ?: return
        try {
            if (player.isPlaying) {
                player.pause()
                isPlaying = false
                stopSyncLoop()
            } else {
                player.start()
                isPlaying = true
                appContext?.let { startSyncLoop(it) }
            }
            appContext?.let { AlAmanahAudioService.startOrUpdate(it) }
        } catch (_: Exception) {}
    }

    fun seekTo(positionMs: Long) {
        val player = mediaPlayer ?: return
        try {
            player.seekTo(positionMs.toInt())
            currentPositionMs = positionMs
            updateActiveVerse(positionMs)
            appContext?.let { AlAmanahAudioService.startOrUpdate(it) }
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
        playJob?.cancel()
        playJob = null
        stopSyncLoop()
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {}
        mediaPlayer = null
        isPlaying = false
    }

    fun stopPlayback(context: Context? = null) {
        releasePlayer()
        isLoading = false
        isPlaying = false
        val ctx = context ?: appContext
        ctx?.let { AlAmanahAudioService.stop(it) }
    }

    fun getAudioCacheSizeBytes(context: Context): Long {
        val dir = getAudioStorageDir(context)
        var size = 0L
        dir.listFiles()?.forEach { file ->
            if (file.isFile) size += file.length()
        }
        return size
    }

    fun clearAudioCache(context: Context): Long {
        val dir = getAudioStorageDir(context)
        var freed = 0L
        dir.listFiles()?.forEach { file ->
            if (file.isFile) {
                freed += file.length()
                file.delete()
            }
        }
        isOfflinePlayback = false
        return freed
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
    var currentAudioOutputType by mutableStateOf(AudioOutputType.PHONE)
        private set
    var connectedAudioDeviceName by mutableStateOf<String?>(null)
        private set
    private var isAudioRoutingListenerRegistered = false

    fun initAudioRoutingListener(context: Context) {
        val appCtx = context.applicationContext
        appContext = appCtx
        updateConnectedAudioDevices(appCtx)
        if (!isAudioRoutingListenerRegistered) {
            val audioManager = appCtx.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return
            try {
                audioManager.registerAudioDeviceCallback(object : AudioDeviceCallback() {
                    override fun onAudioDevicesAdded(addedDevices: Array<out AudioDeviceInfo>?) {
                        updateConnectedAudioDevices(appCtx)
                        applyAudioRouting(appCtx)
                    }

                    override fun onAudioDevicesRemoved(removedDevices: Array<out AudioDeviceInfo>?) {
                        updateConnectedAudioDevices(appCtx)
                        applyAudioRouting(appCtx)
                    }
                }, Handler(Looper.getMainLooper()))
                isAudioRoutingListenerRegistered = true
            } catch (_: Exception) {}
        }
    }

    private fun isHeadsetOrBluetoothDevice(device: AudioDeviceInfo): Boolean {
        if (!device.isSink) return false
        val type = device.type
        if (type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP ||
            type == AudioDeviceInfo.TYPE_WIRED_HEADSET ||
            type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES ||
            type == AudioDeviceInfo.TYPE_USB_HEADSET ||
            type == AudioDeviceInfo.TYPE_USB_DEVICE
        ) return true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (type == AudioDeviceInfo.TYPE_HEARING_AID) return true
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (type == AudioDeviceInfo.TYPE_BLE_HEADSET || type == AudioDeviceInfo.TYPE_BLE_SPEAKER) return true
        }
        if (Build.VERSION.SDK_INT >= 33) {
            if (type == AudioDeviceInfo.TYPE_BLE_BROADCAST) return true
        }
        if (type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO) return true
        return false
    }

    fun getConnectedAudioDevice(context: Context): AudioDeviceInfo? {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return null
        val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        for (device in devices) {
            if (isHeadsetOrBluetoothDevice(device)) {
                val name = device.productName?.toString()?.trim()
                val lower = name?.lowercase(Locale.ROOT) ?: ""
                if (lower.contains("watch") || lower.contains("band") || lower.contains("fit")) {
                    continue
                }
                return device
            }
        }
        return null
    }

    fun getBuiltinSpeakerDevice(context: Context): AudioDeviceInfo? {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return null
        val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        return devices.firstOrNull { it.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER }
            ?: devices.firstOrNull { it.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER_SAFE }
            ?: devices.firstOrNull { it.type == AudioDeviceInfo.TYPE_BUILTIN_EARPIECE }
    }

    fun updateConnectedAudioDevices(context: Context) {
        val connectedDevice = getConnectedAudioDevice(context)
        if (connectedDevice != null) {
            val rawName = connectedDevice.productName?.toString()?.trim()
            connectedAudioDeviceName = if (!rawName.isNullOrBlank() && !rawName.equals("Bluetooth", ignoreCase = true)) {
                rawName
            } else {
                when (connectedDevice.type) {
                    AudioDeviceInfo.TYPE_WIRED_HEADSET, AudioDeviceInfo.TYPE_WIRED_HEADPHONES -> "Wired Headphones"
                    AudioDeviceInfo.TYPE_USB_HEADSET, AudioDeviceInfo.TYPE_USB_DEVICE -> "USB Audio"
                    else -> "Bluetooth Earbuds"
                }
            }
            currentAudioOutputType = explicitTarget ?: AudioOutputType.BLUETOOTH_OR_HEADSET
        } else {
            connectedAudioDeviceName = null
            if (explicitTarget == AudioOutputType.BLUETOOTH_OR_HEADSET) {
                explicitTarget = null
            }
            currentAudioOutputType = AudioOutputType.PHONE
        }
    }

    fun setPreferredAudioTarget(context: Context, target: AudioOutputType) {
        initAudioRoutingListener(context)
        val player = mediaPlayer
        val expectedDevice = if (target == AudioOutputType.PHONE) getBuiltinSpeakerDevice(context) else null
        val alreadyActive = currentAudioOutputType == target && isPreferredDeviceSet(player, expectedDevice)
        explicitTarget = target
        currentAudioOutputType = target
        if (alreadyActive) return

        // A live MediaPlayer does not reliably re-route when only setPreferredDevice
        // is called, so rebuild the output at the current position: the preferred
        // device is then applied before prepare, which is the sequence the platform
        // honors on every Android version we support.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P &&
            player != null && (isPlaying || currentPositionMs > 0L)
        ) {
            val wasPlaying = isPlaying
            val position = runCatching { player.currentPosition.toLong() }.getOrDefault(currentPositionMs)
            playSurah(
                context = context,
                surah = currentSurah,
                reciter = currentReciter,
                startPositionMs = position,
                autoPlay = wasPlaying,
                recordHistory = false
            )
        } else {
            applyAudioRouting(context, target)
        }
    }

    private fun isPreferredDeviceSet(player: MediaPlayer?, device: AudioDeviceInfo?): Boolean {
        if (player == null) return false
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            player.preferredDevice?.id == device?.id
        } else {
            false
        }
    }

    fun setPreferredAudioTarget(target: AudioOutputType) {
        val ctx = appContext
        if (ctx != null) {
            setPreferredAudioTarget(ctx, target)
        } else {
            explicitTarget = target
            currentAudioOutputType = target
        }
    }

    fun applyAudioRouting(context: Context, target: AudioOutputType? = explicitTarget) {
        val mp = mediaPlayer ?: return
        applyPreferredDevice(mp, context, target)
    }

    private fun applyPreferredDevice(mp: MediaPlayer, context: Context, target: AudioOutputType?) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return
        try {
            // Only the built-in speaker needs to be forced, because the platform
            // would otherwise keep routing media to the connected Bluetooth sink.
            // For Bluetooth/headsets we clear the preference and let the platform
            // route to the currently active output: pinning a specific Bluetooth
            // sink is unreliable and can leave the audio track in a broken state.
            val device = if (target == AudioOutputType.PHONE) {
                getBuiltinSpeakerDevice(context)
            } else {
                null
            }
            if (mp.preferredDevice?.id == device?.id) return
            mp.setPreferredDevice(device)
        } catch (_: Exception) {}
    }

    fun getActiveAudioOutputType(context: Context): AudioOutputType {
        initAudioRoutingListener(context)
        return currentAudioOutputType
    }

    fun getDeviceModelName(): String {
        return Build.MODEL ?: "Phone"
    }

    fun getConnectedAudioDeviceName(context: Context): String? {
        initAudioRoutingListener(context)
        return connectedAudioDeviceName
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                val intent = android.content.Intent("android.settings.panel.action.MEDIA_OUTPUT").apply {
                    putExtra("android.provider.extra.PACKAGE_NAME", context.packageName)
                    flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
                return
            } catch (_: Exception) {}
        }
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

package com.athar.app.data

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * A small, self-contained loop player for the nature sounds.
 *
 * It is deliberately separate from [AlAmanahAudioController]: these are
 * textures, not recitations, so they have no reciter, no queue, no download and
 * no verse timing. Keeping them apart also lets a rain loop run underneath a
 * surah if the listener wants that, and it keeps the recitation state machine
 * free of a fourth audio kind.
 */
object AmanahAmbienceController {

    private var player: MediaPlayer? = null

    var currentSound by mutableStateOf<AmanahNatureSound?>(null)
        private set

    var isPlaying by mutableStateOf(false)
        private set

    var volume by mutableFloatStateOf(0.75f)
        private set

    fun isActive(sound: AmanahNatureSound): Boolean = currentSound?.id == sound.id

    /** Starts [sound], or resumes it if it is already the loaded loop. */
    fun play(context: Context, sound: AmanahNatureSound) {
        val existing = player
        if (existing != null && currentSound?.id == sound.id) {
            runCatching { existing.start() }
            isPlaying = true
            return
        }
        release()
        val created = runCatching {
            MediaPlayer.create(context.applicationContext, sound.resId)?.also { mp ->
                mp.setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                mp.isLooping = true
                mp.setVolume(volume, volume)
                mp.setOnErrorListener { _, _, _ ->
                    isPlaying = false
                    true
                }
            }
        }.getOrNull()
        if (created == null) {
            currentSound = null
            isPlaying = false
            return
        }
        currentSound = sound
        player = created
        runCatching { created.start() }
        isPlaying = true
    }

    fun pause() {
        runCatching { player?.pause() }
        isPlaying = false
    }

    fun toggle(context: Context) {
        val sound = currentSound
        if (sound == null) {
            AmanahNatureSounds.firstOrNull()?.let { play(context, it) }
            return
        }
        if (isPlaying) pause() else play(context, sound)
    }

    fun stop() {
        release()
        currentSound = null
    }

    fun updateVolume(value: Float) {
        volume = value.coerceIn(0f, 1f)
        runCatching { player?.setVolume(volume, volume) }
    }

    private fun release() {
        runCatching {
            player?.let {
                if (it.isPlaying) it.stop()
                it.release()
            }
        }
        player = null
        isPlaying = false
    }
}

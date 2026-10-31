package com.athar.app.data

/**
 * Lets a screen claim the volume keys while it is on screen.
 *
 * Volume presses never reach the view tree, so the activity asks this router
 * first. A screen binds a handler in a [androidx.compose.runtime.DisposableEffect]
 * and unbinds it on the way out, which is what turns the keys back into volume
 * controls everywhere else.
 */
object VolumeKeyRouter {

    @Volatile
    private var handler: (() -> Unit)? = null

    val isActive: Boolean get() = handler != null

    fun bind(onPress: () -> Unit) {
        handler = onPress
    }

    fun unbind() {
        handler = null
    }

    fun press() {
        handler?.invoke()
    }
}

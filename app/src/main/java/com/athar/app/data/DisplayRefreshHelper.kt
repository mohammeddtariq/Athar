package com.athar.app.data

import android.app.Activity
import android.os.Build

/**
 * Display refresh rate helper unlocking high refresh rates (90Hz, 120Hz, 144Hz)
 * on supported Android devices for silky smooth scrolling animations in Jetpack Compose.
 */
object DisplayRefreshHelper {

    fun setHighRefreshRate(activity: Activity?, enabled: Boolean = true) {
        if (activity == null) return
        val window = activity.window ?: return

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val display = activity.display ?: window.decorView.display
                val modes = display?.supportedModes ?: emptyArray()
                if (enabled) {
                    val maxMode = modes.maxByOrNull { it.refreshRate }
                    if (maxMode != null && maxMode.refreshRate >= 80f) {
                        val params = window.attributes
                        params.preferredDisplayModeId = maxMode.modeId
                        window.attributes = params
                    }
                } else {
                    val params = window.attributes
                    params.preferredDisplayModeId = 0
                    window.attributes = params
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                @Suppress("DEPRECATION")
                val display = window.windowManager.defaultDisplay
                @Suppress("DEPRECATION")
                val modes = display?.supportedModes ?: emptyArray()
                if (enabled) {
                    val maxMode = modes.maxByOrNull { it.refreshRate }
                    if (maxMode != null && maxMode.refreshRate >= 80f) {
                        val params = window.attributes
                        params.preferredDisplayModeId = maxMode.modeId
                        window.attributes = params
                    }
                } else {
                    val params = window.attributes
                    params.preferredDisplayModeId = 0
                    window.attributes = params
                }
            }
        } catch (_: Exception) {
            // Silently fall back to system default refresh rate
        }
    }
}

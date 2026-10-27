package com.athar.app.data

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import com.athar.app.ui.theme.AtharIconStyle

/**
 * Switches which launcher alias is visible. The enabled alias is the one the
 * home screen shows, so each theme colour gets its own icon entry.
 */
object LauncherIconController {

    // AGP qualifies manifest components with the module namespace, not the
    // runtime application id. Debug builds append a suffix to that id, so the
    // alias class must be built from the namespace or the lookup finds nothing.
    private const val NAMESPACE = "com.athar.app"

    fun apply(context: Context, style: AtharIconStyle) {
        val manager = context.packageManager
        val packageName = context.packageName

        // Turn the wanted icon on first, so a failed switch never leaves the
        // app with no launcher entry at all.
        setState(manager, packageName, style, enabled = true)

        AtharIconStyle.entries
            .filter { it != style }
            .forEach { other -> setState(manager, packageName, other, enabled = false) }
    }

    /** The alias currently enabled, or null when the manifest default is live. */
    fun current(context: Context): AtharIconStyle? {
        val manager = context.packageManager
        val packageName = context.packageName
        return AtharIconStyle.entries.firstOrNull { style ->
            runCatching {
                manager.getComponentEnabledSetting(
                    ComponentName(packageName, "$NAMESPACE.${style.alias}")
                ) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            }.getOrDefault(false)
        }
    }

    private fun setState(
        manager: PackageManager,
        packageName: String,
        style: AtharIconStyle,
        enabled: Boolean
    ) {
        runCatching {
            val component = ComponentName(packageName, "$NAMESPACE.${style.alias}")
            val wanted = if (enabled) {
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            } else {
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            }
            if (manager.getComponentEnabledSetting(component) != wanted) {
                manager.setComponentEnabledSetting(
                    component,
                    wanted,
                    PackageManager.DONT_KILL_APP
                )
            }
        }
    }
}

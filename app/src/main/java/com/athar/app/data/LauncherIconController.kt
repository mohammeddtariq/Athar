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

    private fun setState(
        manager: PackageManager,
        packageName: String,
        style: AtharIconStyle,
        enabled: Boolean
    ) {
        runCatching {
            val component = ComponentName(packageName, "$packageName.${style.alias}")
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

package com.athar.app.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.athar.app.MainActivity

class WidgetConfigureActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            setResult(RESULT_CANCELED)
            finish()
            return
        }

        val prefs = getSharedPreferences("athar_widgets_meta", Context.MODE_PRIVATE)
        val configuredIds = prefs.getStringSet("configured_ids", emptySet()) ?: emptySet()

        val isAlreadyConfigured = configuredIds.contains(appWidgetId.toString())

        if (isAlreadyConfigured) {
            // User actively held on the widget or tapped settings shortcut from launcher
            val resultValue = Intent().apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            setResult(RESULT_OK, resultValue)

            val mainIntent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("open_widget_settings", true)
            }
            startActivity(mainIntent)
            finish()
        } else {
            // First-time addition by launcher or floating widget host (e.g. Samsung One Hand Operation+)
            // Register ID so subsequent reconfigures open settings
            val newSet = configuredIds.toMutableSet().apply { add(appWidgetId.toString()) }
            prefs.edit().putStringSet("configured_ids", newSet).apply()

            val appWidgetManager = AppWidgetManager.getInstance(this)
            val info = appWidgetManager.getAppWidgetInfo(appWidgetId)
            val isWide = info?.provider?.className?.contains("Wide") == true

            AtharWidgetUpdater.renderImmediate(this, appWidgetManager, intArrayOf(appWidgetId), isWide)
            AtharWidgetUpdater.updateAllWidgets(this)

            val resultValue = Intent().apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            setResult(RESULT_OK, resultValue)
            finish()
        }
    }
}

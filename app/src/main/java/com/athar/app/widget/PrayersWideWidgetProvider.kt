package com.athar.app.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle

class PrayersWideWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val prefs = context.getSharedPreferences("athar_widgets_meta", Context.MODE_PRIVATE)
        val configuredIds = prefs.getStringSet("configured_ids", emptySet()) ?: emptySet()
        val newSet = configuredIds.toMutableSet()
        appWidgetIds.forEach { newSet.add(it.toString()) }
        prefs.edit().putStringSet("configured_ids", newSet).apply()

        AtharWidgetUpdater.renderImmediate(context, appWidgetManager, appWidgetIds, isWide = true)
        AtharWidgetUpdater.updateAllWidgets(context)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        val prefs = context.getSharedPreferences("athar_widgets_meta", Context.MODE_PRIVATE)
        val configuredIds = prefs.getStringSet("configured_ids", emptySet()) ?: emptySet()
        val newSet = configuredIds.toMutableSet()
        appWidgetIds.forEach { newSet.remove(it.toString()) }
        prefs.edit().putStringSet("configured_ids", newSet).apply()
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        AtharWidgetUpdater.updateAllWidgets(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == Intent.ACTION_LOCALE_CHANGED ||
            intent.action == Intent.ACTION_TIME_CHANGED ||
            intent.action == Intent.ACTION_TIMEZONE_CHANGED
        ) {
            AtharWidgetUpdater.updateAllWidgets(context)
        }
    }
}

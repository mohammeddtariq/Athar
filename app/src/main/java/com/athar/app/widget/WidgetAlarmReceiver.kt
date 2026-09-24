package com.athar.app.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class WidgetAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        AtharWidgetUpdater.updateAllWidgets(context)
    }
}

package com.example.widgetapp

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.RemoteViews
import java.text.SimpleDateFormat
import java.util.*

class ClockWidgetProvider : AppWidgetProvider() {

    // Create a handler with the main Looper
    private val handler = Handler(Looper.getMainLooper())

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.widget_clock_layout)

        // Update time with seconds
        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        views.setTextViewText(R.id.widget_time, currentTime)

        // Update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)

        // Schedule the next update in 1 second
        handler.postDelayed({
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }, 1000)
    }

    override fun onDisabled(context: Context) {
        // Remove callbacks to prevent memory leaks
        handler.removeCallbacksAndMessages(null)
    }
}

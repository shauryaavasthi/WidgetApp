package com.example.widgetapp

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.widget.RemoteViews
import java.text.SimpleDateFormat
import java.util.*

class ClockWidgetProvider : AppWidgetProvider() {

    private val handler = Handler(Looper.getMainLooper())
    private val updateRunnable = mutableMapOf<Int, Runnable>()

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_clock_layout)
        val prefs = context.getSharedPreferences("ClockWidgetPrefs", Context.MODE_PRIVATE)

        // Update time
        val timeFormat =
            if (prefs.getBoolean("use24HourFormat_$appWidgetId", true)) "HH:mm:ss" else "hh:mm:ss a"
        val currentTime = SimpleDateFormat(timeFormat, Locale.getDefault()).format(Date())
        views.setTextViewText(R.id.widget_time, currentTime)

        // Update date
        val dateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        views.setTextViewText(R.id.widget_date, currentDate)

        // Apply theme
        applyTheme(views, prefs, appWidgetId)

        // Update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)

        // Schedule the next update
        scheduleNextUpdate(context, appWidgetManager, appWidgetId)
    }

    private fun scheduleNextUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val runnable = Runnable { updateAppWidget(context, appWidgetManager, appWidgetId) }
        handler.postDelayed(runnable, 1000)
        updateRunnable[appWidgetId] = runnable
    }

    private fun applyTheme(views: RemoteViews, prefs: SharedPreferences, appWidgetId: Int) {
        val textColor = prefs.getInt("textColor_$appWidgetId", 0xFFFFFFFF.toInt())
        val backgroundColor = prefs.getInt("backgroundColor_$appWidgetId", 0x80333333.toInt())

        views.setTextColor(R.id.widget_time, textColor)
        views.setTextColor(R.id.widget_date, textColor)
        views.setInt(R.id.widget_root, "setBackgroundColor", backgroundColor)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateRunnable.remove(appWidgetId)?.let { handler.removeCallbacks(it) }
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        // This method is called when the first widget is created
        // You can initialize any one-time setup here
    }

    override fun onDisabled(context: Context) {
        handler.removeCallbacksAndMessages(null)
        updateRunnable.clear()
    }

    companion object {
        fun updatePreferences(
            context: Context,
            appWidgetId: Int,
            use24HourFormat: Boolean,
            textColor: Int,
            backgroundColor: Int
        ) {
            val prefs = context.getSharedPreferences("ClockWidgetPrefs", Context.MODE_PRIVATE)
            prefs.edit().apply {
                putBoolean("use24HourFormat_$appWidgetId", use24HourFormat)
                putInt("textColor_$appWidgetId", textColor)
                putInt("backgroundColor_$appWidgetId", backgroundColor)
                apply()
            }

            val appWidgetManager = AppWidgetManager.getInstance(context)
            val provider = ClockWidgetProvider()
            provider.updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }
}
package com.example.widgetapp

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity

class ClockWidgetConfigureActivity : AppCompatActivity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clock_widget_configure)

        // Set the result to CANCELED. This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED)

        // Find the widget id from the intent.
        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        val applyButton: Button = findViewById(R.id.applyButton)
        val formatSwitch: Switch = findViewById(R.id.formatSwitch)
        val textColorPicker: EditText = findViewById(R.id.textColorPicker)
        val backgroundColorPicker: EditText = findViewById(R.id.backgroundColorPicker)

        applyButton.setOnClickListener {
            val use24HourFormat = formatSwitch.isChecked
            val textColor = textColorPicker.text.toString().toIntOrNull() ?: 0xFFFFFFFF.toInt()
            val backgroundColor = backgroundColorPicker.text.toString().toIntOrNull() ?: 0x80333333.toInt()

            ClockWidgetProvider.updatePreferences(
                this,
                appWidgetId,
                use24HourFormat,
                textColor,
                backgroundColor
            )

            // Make sure we pass back the original appWidgetId
            val resultValue = Intent()
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            setResult(RESULT_OK, resultValue)
            finish()
        }
    }
}
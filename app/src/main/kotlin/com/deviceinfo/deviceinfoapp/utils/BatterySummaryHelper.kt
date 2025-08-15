package com.deviceinfo.deviceinfoapp.utils

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager

class BatterySummaryHelper(private val context: Context) {

    /**
     * Gets the current battery level as a percentage for the main dashboard.
     */
    fun getBatteryPercentage(): String {
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED)) ?: return "N/A"
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

        if (level == -1 || scale == -1) return "N/A"

        val percentage = (level * 100.0f / scale).toInt()
        return "$percentage%"
    }
}

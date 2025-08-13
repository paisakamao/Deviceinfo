package com.deviceinfo.deviceinfoapp.utils

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager

class BatteryInfoHelper(private val context: Context) {

    /**
     * This private function gets the "sticky intent" for battery status,
     * which contains all the current battery information.
     */
    private fun getBatteryStatusIntent(): Intent? {
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        return context.registerReceiver(null, filter)
    }

    /**
     * Calculates the current battery level as a percentage.
     */
    fun getBatteryPercentage(): String {
        val intent = getBatteryStatusIntent() ?: return "N/A"
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

        if (level == -1 || scale == -1) return "N/A"

        val percentage = (level * 100.0f / scale).toInt()
        return "$percentage%"
    }

    /**
     * Gets the battery temperature in Celsius.
     */
    fun getBatteryTemperature(): String {
        val intent = getBatteryStatusIntent() ?: return "N/A"
        // The value from the system is in tenths of a degree Celsius
        val temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)
        
        if (temperature == -1) return "N/A"
        
        val tempCelsius = temperature / 10.0f
        return "$tempCelsius °C"
    }

    /**
     * Gets the battery voltage in millivolts.
     */
    fun getBatteryVoltage(): String {
        val intent = getBatteryStatusIntent() ?: return "N/A"
        // The value from the system is in millivolts
        val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
        
        if (voltage == -1) return "N/A"
        
        return "$voltage mV"
    }
}

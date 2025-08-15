package com.deviceinfo.deviceinfoapp.utils

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import com.deviceinfo.deviceinfoapp.model.DeviceInfo
import java.util.concurrent.TimeUnit

class BatteryInfoHelper(private val context: Context) {

    private val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

    /**
     *  Gets the "slow changing" data from the system's last broadcast.
     *  This is efficient for things that don't change every second.
     */
    fun getSlowUpdateDetails(): List<DeviceInfo> {
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED)) ?: return emptyList()
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        
        return listOf(
            DeviceInfo("Health", getBatteryHealth(intent)),
            DeviceInfo("Level", getBatteryPercentage(intent)),
            DeviceInfo("Status", getBatteryStatus(status)),
            DeviceInfo("Power Source", getChargingSource(intent)),
            DeviceInfo("Technology", getBatteryTechnology(intent)),
            DeviceInfo("Temperature", getBatteryTemperature(intent)),
            DeviceInfo("Time to Charge/Discharge", getChargeTimeRemaining(status)),
            DeviceInfo("Capacity (Design)", getDesignCapacity())
        )
    }

    /**
     * Gets the "fast changing" data by directly querying the BatteryManager.
     * This is the key to real-time updates.
     */
    fun getFastUpdateDetails(): Map<String, String> {
        val voltage = (batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_VOLTAGE) / 1000.0)
        val current = (batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW) / 1000.0)
        val powerWatts = (voltage / 1000.0) * (current / 1000.0)

        val currentString = if (current.toInt() == 0) "N/A" else "${current.toInt()} mA"
        val powerString = if (current.toInt() == 0) "N/A" else "%.2f W".format(powerWatts)
        val voltageString = if (voltage.toInt() == 0) "N/A" else "${voltage.toInt()} mV"

        return mapOf(
            "Current (Real-time)" to currentString,
            "Power (Real-time)" to powerString,
            "Voltage" to voltageString
        )
    }

    // --- Helper functions for slow data ---
    private fun getBatteryPercentage(intent: Intent?): String {
        intent ?: return "N/A"
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        if (level == -1 || scale == -1) return "N/A"
        return "${(level * 100.0f / scale).toInt()}%"
    }
    
    // Public version for dashboard
    fun getBatteryPercentageForDashboard(): String = getBatteryPercentage(context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED)))

    private fun getBatteryStatus(status: Int): String { /* ... unchanged ... */ }
    private fun getBatteryHealth(intent: Intent): String { /* ... unchanged ... */ }
    private fun getChargingSource(intent: Intent): String { /* ... unchanged ... */ }
    private fun getBatteryTechnology(intent: Intent): String { /* ... unchanged ... */ }
    private fun getBatteryTemperature(intent: Intent): String { /* ... unchanged ... */ }
    private fun getChargeTimeRemaining(status: Int): String { /* ... unchanged ... */ }
    private fun formatDuration(millis: Long): String { /* ... unchanged ... */ }

    private fun getDesignCapacity(): String {
        try {
            val powerProfile = Class.forName("com.android.internal.os.PowerProfile")
                .getConstructor(Context::class.java).newInstance(context)
            val capacity = powerProfile.javaClass.getMethod("getBatteryCapacity").invoke(powerProfile) as Double
            if (capacity > 0) return "${capacity.toInt()} mAh"
        } catch (e: Exception) { /* Fall through */ }
        return "N/A"
    }
}

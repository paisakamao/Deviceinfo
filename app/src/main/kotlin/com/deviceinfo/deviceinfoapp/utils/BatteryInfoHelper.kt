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
     * Gets the "slow changing" data from the system's last broadcast.
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
     */
    fun getFastUpdateDetails(): Map<String, String> {
        // Voltage must be read from the sticky intent. It doesn't have a direct property.
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val voltage = intent?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) ?: -1
        
        val current = (batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW) / 1000.0)
        val powerWatts = (voltage / 1000.0) * (current / 1000.0)

        val currentString = if (current.toInt() == 0) "N/A" else "${current.toInt()} mA"
        val powerString = if (current.toInt() == 0 || voltage <= 0) "N/A" else "%.2f W".format(powerWatts)
        val voltageString = if (voltage <= 0) "N/A" else "$voltage mV"

        return mapOf(
            "Current (Real-time)" to currentString,
            "Power (Real-time)" to powerString,
            "Voltage" to voltageString
        )
    }
    
    fun getBatteryPercentageForDashboard(): String = getBatteryPercentage(context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED)))

    private fun getBatteryPercentage(intent: Intent?): String {
        intent ?: return "N/A"
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        if (level == -1 || scale == -1) return "N/A"
        return "${(level * 100.0f / scale).toInt()}%"
    }

    private fun getBatteryStatus(status: Int): String {
        return when (status) {
            BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
            BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
            BatteryManager.BATTERY_STATUS_FULL -> "Full"
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Not Charging"
            else -> "Unknown"
        }
    }

    private fun getBatteryHealth(intent: Intent): String {
        return when (intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheating"
            else -> "Unknown"
        }
    }

    private fun getChargingSource(intent: Intent): String {
        return when (intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)) {
            BatteryManager.BATTERY_PLUGGED_AC -> "AC Charger"
            BatteryManager.BATTERY_PLUGGED_USB -> "USB Port"
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless Charger"
            0 -> "On Battery"
            else -> "Unknown"
        }
    }

    private fun getBatteryTechnology(intent: Intent): String {
        return intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "N/A"
    }

    private fun getBatteryTemperature(intent: Intent): String {
        val temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)
        return if (temp != -1) "${temp / 10.0f}°C" else "N/A"
    }

    private fun getChargeTimeRemaining(status: Int): String {
        if (status == BatteryManager.BATTERY_STATUS_DISCHARGING) return "Discharging"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val time = batteryManager.computeChargeTimeRemaining()
            if (time > 0) return formatDuration(time)
        }
        return "Calculating..."
    }

    private fun formatDuration(millis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        return String.format("%d hr %d min", hours, minutes)
    }

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

package com.deviceinfo.deviceinfoapp.utils

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import com.deviceinfo.deviceinfoapp.model.DeviceInfo
import java.io.File
import java.util.concurrent.TimeUnit

class BatteryInfoHelper(private val context: Context) {

    private val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

    fun getBatteryStatusIntent(): Intent? {
        return context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    // --- MASTER FUNCTION FOR DETAIL SCREEN ---
    fun getBatteryDetailsList(): List<DeviceInfo> {
        val details = mutableListOf<DeviceInfo>()

        val intent = getBatteryStatusIntent()
        val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1

        // Slow-changing data (from broadcast intent)
        details.add(DeviceInfo("Health", intent?.let { getBatteryHealth(it) } ?: "N/A"))
        details.add(DeviceInfo("Level", getBatteryPercentage(intent)))
        details.add(DeviceInfo("Status", getBatteryStatus(status)))
        details.add(DeviceInfo("Power Source", intent?.let { getChargingSource(it) } ?: "N/A"))
        details.add(DeviceInfo("Technology", intent?.let { getBatteryTechnology(it) } ?: "N/A"))

        // Fast-changing data (read directly from sysfs or BatteryManager each time)
        details.add(DeviceInfo("Temperature", getBatteryTemperatureFresh()))
        details.add(DeviceInfo("Voltage", getBatteryVoltageFresh()))
        details.add(DeviceInfo("Current (Real-time)", getCurrentNow()))
        details.add(DeviceInfo("Power (Real-time)", getPowerNowFresh()))

        // Mixed
        details.add(DeviceInfo("Time to Charge/Discharge", getChargeTimeRemaining(status)))
        details.add(DeviceInfo("Capacity (Design)", getDesignCapacity()))

        return details
    }

    fun getBatteryPercentageForDashboard(): String {
        return getBatteryPercentage(getBatteryStatusIntent())
    }

    // --- HELPER FUNCTIONS ---

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
        val health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)
        return when (health) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
            BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Failure"
            else -> "Unknown"
        }
    }

    private fun getChargingSource(intent: Intent): String {
        val plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
        return when (plugged) {
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

    // --- Fresh reads from sysfs for real-time updates ---
    private fun getBatteryTemperatureFresh(): String {
        try {
            val file = File("/sys/class/power_supply/battery/temp")
            if (file.exists()) {
                val tempDeci = file.readText().trim().toInt()
                return "${tempDeci / 10} °C"
            }
        } catch (_: Exception) { }
        return "N/A"
    }

    private fun getBatteryVoltageFresh(): String {
        try {
            val file = File("/sys/class/power_supply/battery/voltage_now")
            if (file.exists()) {
                val voltageMicro = file.readText().trim().toLong()
                return "${voltageMicro / 1000} mV"
            }
        } catch (_: Exception) { }
        return "N/A"
    }

    private fun getCurrentNow(): String {
        try {
            val file = File("/sys/class/power_supply/battery/current_now")
            if (file.exists()) {
                val currentMicroamps = file.readText().trim().toLong()
                return "${currentMicroamps / 1000} mA"
            }
        } catch (_: Exception) { }

        var currentMicroamps = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
        if (currentMicroamps == 0L) {
            currentMicroamps = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE)
        }
        return if (currentMicroamps != 0L && currentMicroamps != Long.MIN_VALUE) {
            "${currentMicroamps / 1000} mA"
        } else "N/A"
    }

    private fun getPowerNowFresh(): String {
        val voltageMilliVolts = getBatteryVoltageFresh().split(" ")[0].toDoubleOrNull()
        val currentMilliAmps = getCurrentNow().split(" ")[0].toDoubleOrNull()
        if (voltageMilliVolts == null || currentMilliAmps == null) return "N/A"
        val powerWatts = (voltageMilliVolts / 1000.0) * (currentMilliAmps / 1000.0)
        return "%.2f W".format(powerWatts)
    }

    private fun getChargeTimeRemaining(status: Int): String {
        if (status == BatteryManager.BATTERY_STATUS_DISCHARGING) return "Discharging"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val timeInMillis = batteryManager.computeChargeTimeRemaining()
            if (timeInMillis > 0) return formatDuration(timeInMillis)
        }
        return "Calculating..."
    }

    private fun getDesignCapacity(): String {
        try {
            val powerProfile = Class.forName("com.android.internal.os.PowerProfile")
                .getConstructor(Context::class.java).newInstance(context)
            val capacity = powerProfile.javaClass
                .getMethod("getBatteryCapacity").invoke(powerProfile) as Double
            if (capacity > 0) return "${capacity.toInt()} mAh"
        } catch (_: Exception) { }
        return "N/A"
    }

    private fun formatDuration(millis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        return String.format("%d hr %d min remaining", hours, minutes)
    }
}

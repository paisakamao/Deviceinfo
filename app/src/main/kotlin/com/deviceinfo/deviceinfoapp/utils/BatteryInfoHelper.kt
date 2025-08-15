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

    private fun getBatteryStatusIntent(): Intent? {
        return context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    // --- MASTER FUNCTION ---
    fun getBatteryDetailsList(): List<DeviceInfo> {
        val details = mutableListOf<DeviceInfo>()
        val intent = getBatteryStatusIntent() ?: return emptyList()
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)

        details.add(DeviceInfo("Health", getBatteryHealth(intent)))
        details.add(DeviceInfo("Level", getBatteryPercentage(intent)))
        details.add(DeviceInfo("Status", getBatteryStatus(status)))
        details.add(DeviceInfo("Power Source", getChargingSource(intent)))
        details.add(DeviceInfo("Technology", getBatteryTechnology(intent)))
        details.add(DeviceInfo("Temperature", getBatteryTemperature(intent)))
        details.add(DeviceInfo("Voltage", getBatteryVoltage(intent)))
        details.add(DeviceInfo("Current (Real-time)", getCurrentNow()))
        details.add(DeviceInfo("Power (Real-time)", getPowerNow()))
        details.add(DeviceInfo("Time to Charge/Discharge", getChargeTimeRemaining(status)))
        details.add(DeviceInfo("Capacity (Design)", getDesignCapacity()))

        return details
    }

    // --- Individual Helper Functions (Final, Most Accurate Versions) ---

    fun getBatteryPercentage(intent: Intent? = getBatteryStatusIntent()): String {
        intent ?: return "N/A"
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        if (level == -1 || scale == -1) return "N/A"
        return "${(level * 100.0f / scale).toInt()}%"
    }

    private fun getBatteryStatus(status: Int): String {
        // ... (This function is correct)
        return when (status) {
            BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
            BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
            BatteryManager.BATTERY_STATUS_FULL -> "Full"
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Not Charging"
            else -> "Unknown"
        }
    }

    private fun getBatteryHealth(intent: Intent): String {
        // ... (This function is correct)
        val health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)
        return when (health) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheating"
            // ... other health statuses
            else -> "Unknown"
        }
    }

    private fun getChargingSource(intent: Intent): String {
        // ... (This function is correct)
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

    private fun getBatteryTemperature(intent: Intent): String {
        val temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)
        if (temperature == -1) return "N/A"
        return "${(temperature / 10.0f).toInt()} °C"
    }

    private fun getBatteryVoltage(intent: Intent): String {
        val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
        return if (voltage == -1) "N/A" else "$voltage mV"
    }

    private fun getCurrentNow(): String {
        // First, try the most reliable method: reading the system file.
        try {
            val file = File("/sys/class/power_supply/battery/current_now")
            if (file.exists()) {
                val currentMicroamps = file.readText().trim().toLong()
                if (currentMicroamps != 0L) {
                    return "${currentMicroamps / 1000} mA"
                }
            }
        } catch (e: Exception) {
            // File not found or couldn't be read, continue to next method.
        }

        // Second, try the official API properties as a fallback.
        var currentMicroamps = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
        if (currentMicroamps == 0L) {
            currentMicroamps = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE)
        }
        
        // If all methods fail, return N/A.
        return if (currentMicroamps != 0L && currentMicroamps != Long.MIN_VALUE) {
            "${currentMicroamps / 1000} mA"
        } else {
            "N/A"
        }
    }
    
    private fun getPowerNow(): String {
        val voltageMilliVolts = getBatteryVoltage(getBatteryStatusIntent()!!).split(" ")[0].toDoubleOrNull()
        val currentMilliAmps = getCurrentNow().split(" ")[0].toDoubleOrNull()

        if (voltageMilliVolts == null || currentMilliAmps == null) return "N/A"

        // Power (Watts) = Voltage (Volts) * Current (Amps)
        val powerWatts = (voltageMilliVolts / 1000.0) * (currentMilliAmps / 1000.0)
        
        if (powerWatts > -0.01 && powerWatts < 0.01) return "0.00 W"

        return "%.2f W".format(powerWatts)
    }

    private fun getChargeTimeRemaining(status: Int): String {
        // ... (This function is correct)
        if (status == BatteryManager.BATTERY_STATUS_DISCHARGING) return "Discharging"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val timeInMillis = batteryManager.computeChargeTimeRemaining()
            if (timeInMillis > 0) return formatDuration(timeInMillis)
        }
        return "Calculating..."
    }

    private fun getDesignCapacity(): String {
        // First, try the most reliable method: Reflection on PowerProfile.
        try {
            val powerProfile = Class.forName("com.android.internal.os.PowerProfile")
                .getConstructor(Context::class.java)
                .newInstance(context)
            val capacity = powerProfile.javaClass
                .getMethod("getBatteryCapacity")
                .invoke(powerProfile) as Double
            if (capacity > 0) {
                return "${capacity.toInt()} mAh"
            }
        } catch (e: Exception) {
            // Reflection failed, continue to next method.
        }

        // Fallback method for some devices.
        val capacityMicroAh = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
        val capacityPercent = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        if (capacityMicroAh != Long.MIN_VALUE && capacityPercent > 0) {
            val designCapacity = (capacityMicroAh / (capacityPercent / 100.0))
            return "${(designCapacity / 1000).toLong()} mAh (est.)"
        }
        
        return "N/A"
    }
    
    private fun formatDuration(millis: Long): String {
        // ... (This function is correct)
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        return String.format("%d hr %d min remaining", hours, minutes)
    }
}

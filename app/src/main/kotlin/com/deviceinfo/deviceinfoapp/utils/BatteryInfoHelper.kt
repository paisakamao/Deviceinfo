package com.deviceinfo.deviceinfoapp.utils

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import com.deviceinfo.deviceinfoapp.model.DeviceInfo
import java.util.concurrent.TimeUnit
import kotlin.math.abs

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
        details.add(DeviceInfo("Capacity (Design)", getTotalCapacity()))

        return details
    }

    // --- Individual Helper Functions (Corrected and Improved) ---

    fun getBatteryPercentage(intent: Intent? = getBatteryStatusIntent()): String {
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
        // ... (This function is already correct, keeping it for completeness)
        val health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)
        return when (health) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheating"
            BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Failure"
            BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
            else -> "Unknown"
        }
    }

    private fun getChargingSource(intent: Intent): String {
        // ... (This function is already correct)
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
        // 1. Try the real-time current first.
        var currentMicroamps = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)

        // 2. If real-time is 0, try the average current as a fallback.
        if (currentMicroamps == 0L) {
            currentMicroamps = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE)
        }

        // 3. If both are invalid, report N/A.
        if (currentMicroamps == 0L || currentMicroamps == Long.MIN_VALUE) {
            return "N/A"
        }

        val currentMilliamps = currentMicroamps / 1000
        return "$currentMilliamps mA"
    }
    
    private fun getPowerNow(): String {
        val intent = getBatteryStatusIntent() ?: return "N/A"
        val voltageMilliVolts = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1).toDouble()
        val currentString = getCurrentNow()

        if (currentString == "N/A" || voltageMilliVolts == -1.0) {
            return "N/A"
        }

        // Extract the number from the current string (e.g., "-716 mA" -> -716.0)
        val currentMilliAmps = currentString.split(" ")[0].toDoubleOrNull() ?: return "N/A"

        // Power (Watts) = Voltage (Volts) * Current (Amps)
        val powerWatts = (voltageMilliVolts / 1000.0) * (currentMilliAmps / 1000.0)
        
        // Don't show -0.00 W
        if (powerWatts > -0.01 && powerWatts < 0.01) return "0.00 W"

        return "%.2f W".format(powerWatts)
    }

    private fun getChargeTimeRemaining(status: Int): String {
        if (status == BatteryManager.BATTERY_STATUS_DISCHARGING) {
            return "Discharging"
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val timeInMillis = batteryManager.computeChargeTimeRemaining()
            if (timeInMillis > 0) {
                return formatDuration(timeInMillis)
            }
        }
        return "Calculating..."
    }

    private fun getTotalCapacity(): String {
        val capacityMicroAh = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
        val capacityPercent = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

        if (capacityMicroAh != Long.MIN_VALUE && capacityPercent > 0) {
            val designCapacity = (capacityMicroAh / (capacityPercent / 100.0))
            return "${designCapacity.toLong() / 1000} mAh"
        }
        return "N/A"
    }
    
    private fun formatDuration(millis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        return String.format("%d hr %d min remaining", hours, minutes)
    }
}

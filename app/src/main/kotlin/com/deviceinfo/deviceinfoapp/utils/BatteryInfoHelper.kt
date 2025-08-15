package com.deviceinfo.deviceinfoapp.utils

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import com.deviceinfo.deviceinfoapp.model.DeviceInfo
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class BatteryInfoHelper(private val context: Context) {

    private val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

    fun getBatteryDetailsList(): List<DeviceInfo> {
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED)) ?: return emptyList()
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        
        return listOf(
            DeviceInfo("Health", getBatteryHealth(intent)),
            DeviceInfo("Level", getBatteryPercentage(intent)),
            DeviceInfo("Status", getBatteryStatus(status)),
            DeviceInfo("Power Source", getChargingSource(intent)),
            DeviceInfo("Technology", getBatteryTechnology(intent)),
            DeviceInfo("Temperature", getBatteryTemperature(intent)),
            DeviceInfo("Voltage", getBatteryVoltageFresh()), // Use fresh voltage for power calculation
            DeviceInfo("Current (Real-time)", getCurrentNowFresh(status)), // Pass status for correct sign
            DeviceInfo("Power (Real-time)", getPowerNowFresh(status)),
            DeviceInfo("Time to Charge/Discharge", getChargeTimeRemaining(status)),
            DeviceInfo("Capacity (Design)", getDesignCapacity())
        )
    }
    
    fun getBatteryPercentageForDashboard(): String {
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        return getBatteryPercentage(intent)
    }

    // --- Core Real-time Function ---
    private fun getCurrentNowFresh(status: Int): String {
        // List of all known paths where the real-time current is stored.
        val pathsToTry = arrayOf(
            "/sys/class/power_supply/battery/current_now",
            "/sys/class/power_supply/bms/current_now", // Common on newer devices (Battery Management System)
            "/sys/class/power_supply/main/current_now",
            "/sys/class/power_supply/battery/batt_current"
        )

        for (path in pathsToTry) {
            try {
                val file = File(path)
                if (file.exists()) {
                    val currentMicroamps = file.readText().trim().toLong()
                    if (currentMicroamps != 0L) {
                        var milliamps = currentMicroamps / 1000
                        // IMPORTANT: The system file often reports a positive value even when discharging.
                        // We use the battery status to make sure it's negative.
                        if (status == BatteryManager.BATTERY_STATUS_DISCHARGING && milliamps > 0) {
                            milliamps *= -1
                        }
                        return "$milliamps mA"
                    }
                }
            } catch (e: Exception) {
                // This path failed, try the next one.
            }
        }

        // Fallback to the (often unreliable) Android API if no file works.
        val apiCurrent = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
        if (apiCurrent != 0L && apiCurrent != Long.MIN_VALUE) {
            return "${apiCurrent / 1000} mA"
        }
        
        return "N/A"
    }

    // --- Other Helpers ---
    
    private fun getBatteryVoltageFresh(): String {
        try {
            val file = File("/sys/class/power_supply/battery/voltage_now")
            if (file.exists()) {
                val voltageMicrovolts = file.readText().trim().toLong()
                return "${voltageMicrovolts / 1000} mV"
            }
        } catch (e: Exception) { /* Fall through */ }
        
        // Fallback for voltage
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val voltage = intent?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) ?: -1
        return if (voltage != -1) "$voltage mV" else "N/A"
    }
    
    private fun getPowerNowFresh(status: Int): String {
        val voltageString = getBatteryVoltageFresh()
        val currentString = getCurrentNowFresh(status)

        if (voltageString == "N/A" || currentString == "N/A") return "N/A"

        val voltageMilliVolts = voltageString.split(" ")[0].toDoubleOrNull()
        val currentMilliAmps = currentString.split(" ")[0].toDoubleOrNull()

        if (voltageMilliVolts == null || currentMilliAmps == null) return "N/A"

        val powerWatts = (voltageMilliVolts / 1000.0) * (currentMilliAmps / 1000.0)
        return "%.2f W".format(powerWatts)
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

    // --- Basic Intent-based functions ---
    private fun getBatteryPercentage(intent: Intent?): String { /* ... unchanged ... */ }
    private fun getBatteryStatus(status: Int): String { /* ... unchanged ... */ }
    private fun getBatteryHealth(intent: Intent): String { /* ... unchanged ... */ }
    private fun getChargingSource(intent: Intent): String { /* ... unchanged ... */ }
    private fun getBatteryTechnology(intent: Intent): String { /* ... unchanged ... */ }
    private fun getBatteryTemperature(intent: Intent): String { /* ... unchanged ... */ }
    private fun getChargeTimeRemaining(status: Int): String { /* ... unchanged ... */ }
    private fun formatDuration(millis: Long): String { /* ... unchanged ... */ }
}

package com.deviceinfo.deviceinfoapp.utils

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.deviceinfo.deviceinfoapp.model.DeviceInfo
import java.io.File

class BatteryInfoHelper(private val context: Context) {

    private val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

    /**
     *  This is the new master function. It performs a full diagnostic test.
     */
    fun getDiagnosticBatteryDetails(): List<DeviceInfo> {
        val details = mutableListOf<DeviceInfo>()
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        // --- Standard Intent Data (for context) ---
        details.add(DeviceInfo("--- INTENT DATA ---", "Broadcast-based info"))
        val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        details.add(DeviceInfo("Status", getBatteryStatus(status)))
        details.add(DeviceInfo("Level", getBatteryPercentage(intent)))

        // --- DIAGNOSTIC TEST 1: BatteryManager APIs ---
        details.add(DeviceInfo("--- API TEST ---", "Official Android APIs"))
        try {
            val currentNow = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
            details.add(DeviceInfo("API: CURRENT_NOW", "$currentNow µA"))
        } catch (e: Exception) {
            details.add(DeviceInfo("API: CURRENT_NOW", "Error: ${e.message}"))
        }
        try {
            val currentAvg = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE)
            details.add(DeviceInfo("API: CURRENT_AVERAGE", "$currentAvg µA"))
        } catch (e: Exception) {
            details.add(DeviceInfo("API: CURRENT_AVERAGE", "Error: ${e.message}"))
        }

        // --- DIAGNOSTIC TEST 2: Direct System File Reads ---
        details.add(DeviceInfo("--- SYSFS FILE TEST ---", "Direct hardware files"))
        val pathsToTry = arrayOf(
            "/sys/class/power_supply/battery/current_now",
            "/sys/class/power_supply/bms/current_now",
            "/sys/class/power_supply/main/current_now",
            "/sys/class/power_supply/battery/batt_current",
            "/sys/class/power_supply/battery/current_avg",
            "/sys/class/power_supply/battery/BatteryAverageCurrent"
        )
        for (path in pathsToTry) {
            details.add(readSysfs(path))
        }

        return details
    }
    
    // Simple helper for the dashboard
    fun getBatteryPercentageForDashboard(): String {
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        return getBatteryPercentage(intent)
    }

    private fun readSysfs(path: String): DeviceInfo {
        return try {
            val file = File(path)
            if (file.exists() && file.canRead()) {
                val content = file.readText().trim()
                DeviceInfo("File: ...${path.takeLast(25)}", "Value: $content")
            } else {
                DeviceInfo("File: ...${path.takeLast(25)}", "Not found or unreadable")
            }
        } catch (e: Exception) {
            DeviceInfo("File: ...${path.takeLast(25)}", "Error reading: ${e.message}")
        }
    }

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
}

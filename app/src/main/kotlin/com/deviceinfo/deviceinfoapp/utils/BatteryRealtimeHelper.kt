package com.deviceinfo.deviceinfoapp.utils

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import java.io.File

class BatteryRealtimeHelper(private val context: Context) {

    // A private helper to read from a list of possible system files
    private fun readSysfs(vararg paths: String): Long? {
        for (path in paths) {
            try {
                val file = File(path)
                if (file.exists() && file.canRead()) {
                    return file.readText().trim().toLong()
                }
            } catch (_: Exception) {
                // Ignore and try the next path
            }
        }
        return null
    }

    /**
     * Gets the instantaneous battery current in milliamps (mA).
     * It prioritizes direct sysfs reading for maximum accuracy.
     */
    fun getBatteryCurrentNow(): String {
        val microAmps = readSysfs(
            "/sys/class/power_supply/battery/current_now",
            "/sys/class/power_supply/Battery/current_now",
            "/sys/class/power_supply/bms/current_now", // For newer devices
            "/sys/class/power_supply/main/current_now"
        )

        if (microAmps != null) {
            val milliAmps = microAmps / 1000.0
            return String.format("%.0f mA", milliAmps)
        }

        return "N/A"
    }

    /**
     * Calculates the instantaneous power in Watts (W).
     */
    fun getBatteryPowerNow(): String {
        try {
            val voltageMicroVolts = readSysfs("/sys/class/power_supply/battery/voltage_now")
            val currentMicroAmps = readSysfs("/sys/class/power_supply/battery/current_now")

            if (voltageMicroVolts != null && currentMicroAmps != null) {
                val voltageVolts = voltageMicroVolts / 1_000_000.0
                val currentAmps = currentMicroAmps / 1_000_000.0
                val powerWatts = voltageVolts * currentAmps
                return String.format("%.2f W", powerWatts)
            }
        } catch (_: Exception) { }

        // Fallback calculation using the Intent for voltage
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val voltageMilliVolts = intent?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) ?: -1
        val currentString = getBatteryCurrentNow()

        if (voltageMilliVolts > 0 && currentString != "N/A") {
            val currentMilliAmps = currentString.removeSuffix(" mA").toDouble()
            val powerWatts = (voltageMilliVolts / 1000.0) * (currentMilliAmps / 1000.0)
            return String.format("%.2f W", powerWatts)
        }

        return "N/A"
    }
}
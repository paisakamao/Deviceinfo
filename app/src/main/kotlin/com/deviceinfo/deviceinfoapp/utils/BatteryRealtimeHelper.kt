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
}```

---

### **Action 2: The New, Simplified `BatteryStaticHelper.kt`**

This helper will be used for the slow-changing data.

1.  Navigate to `app/src/main/kotlin/com/deviceinfo/deviceinfoapp/utils/`.
2.  **Delete any old battery helpers** (`BatteryInfoHelper`, `BatteryInfoProvider`, etc.).
3.  Create a new file named `BatteryStaticHelper.kt`.
4.  **Paste this complete and correct code:**

```kotlin
package com.deviceinfo.deviceinfoapp.utils

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import com.deviceinfo.deviceinfoapp.model.DeviceInfo
import java.util.concurrent.TimeUnit

class BatteryStaticHelper(private val context: Context) {

    private val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

    fun getStaticBatteryDetails(): List<DeviceInfo> {
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED)) ?: return emptyList()
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        
        return listOf(
            DeviceInfo("Health", getBatteryHealth(intent)),
            DeviceInfo("Level", getBatteryPercentage(intent)),
            DeviceInfo("Status", getBatteryStatus(status)),
            DeviceInfo("Power Source", getChargingSource(intent)),
            DeviceInfo("Technology", getBatteryTechnology(intent)),
            DeviceInfo("Temperature", getBatteryTemperature(intent)),
            DeviceInfo("Voltage", getBatteryVoltage(intent)),
            DeviceInfo("Time to Charge/Discharge", getChargeTimeRemaining(status)),
            DeviceInfo("Capacity (Design)", getDesignCapacity())
        )
    }

    fun getBatteryPercentageForDashboard(): String {
        return getBatteryPercentage(context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED)))
    }

    private fun getBatteryPercentage(intent: Intent?): String {
        intent ?: return "N/A"
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        if (level == -1 || scale == -1) return "N/A"
        return "${(level * 100.0f / scale).toInt()}%"
    }
    
    private fun getBatteryVoltage(intent: Intent): String {
        val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
        return if (voltage != -1) "$voltage mV" else "N/A"
    }

    private fun getBatteryStatus(status: Int): String { /* ... (full code) ... */ }
    private fun getBatteryHealth(intent: Intent): String { /* ... (full code) ... */ }
    private fun getChargingSource(intent: Intent): String { /* ... (full code) ... */ }
    private fun getBatteryTechnology(intent: Intent): String { return intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "N/A" }
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
            val powerProfile = Class.forName("com.android.internal.os.PowerProfile").getConstructor(Context::class.java).newInstance(context)
            val capacity = powerProfile.javaClass.getMethod("getBatteryCapacity").invoke(powerProfile) as Double
            if (capacity > 0) return "${capacity.toInt()} mAh"
        } catch (e: Exception) { }
        return "N/A"
    }
}
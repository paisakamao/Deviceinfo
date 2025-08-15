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

    fun getBatteryStatusIntent(): Intent? {
        return context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    // --- MASTER FUNCTION FOR FULL LIST ---
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
        details.add(DeviceInfo("Current (Real-time)", getCurrentNowString()))
        details.add(DeviceInfo("Power (Real-time)", getPowerNowString(intent)))
        details.add(DeviceInfo("Time to Charge/Discharge", getChargeTimeRemaining(status)))
        details.add(DeviceInfo("Capacity (Design)", getDesignCapacity()))

        return details
    }

    // --- SIMPLE FUNCTION FOR MAIN SCREEN ---
    fun getBatteryPercentageForDashboard(): String {
        return getBatteryPercentage(getBatteryStatusIntent())
    }

    // --- INDIVIDUAL HELPERS ---

    private fun getBatteryPercentage(intent: Intent?): String {
        intent ?: return "N/A"
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        if (level == -1 || scale == -1) return "N/A"
        return "${(level * 100.0f / scale).toInt()}%"
    }

    private fun getBatteryStatus(status: Int): String { /* ... (code from your provided snippet) ... */ }
    private fun getBatteryHealth(intent: Intent): String { /* ... (code from your provided snippet) ... */ }
    private fun getChargingSource(intent: Intent): String { /* ... (code from your provided snippet) ... */ }
    private fun getBatteryTechnology(intent: Intent): String {
        return intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "N/A"
    }
    private fun getBatteryTemperature(intent: Intent): String { /* ... (code from your provided snippet) ... */ }

    private fun getBatteryVoltage(intent: Intent): String {
        val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
        return if (voltage == -1) "N/A" else "$voltage mV"
    }

    fun getCurrentNowMilliAmps(): Int? {
        var microA = try {
            batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW).toLong()
        } catch (_: Throwable) { 0L }

        if (microA == 0L) {
            microA = try {
                batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE)
            } catch (_: Throwable) { 0L }
        }

        if (microA == 0L || microA == Long.MIN_VALUE) return null
        return (microA / 1000.0).toInt()
    }

    private fun getCurrentNowString(): String {
        val mA = getCurrentNowMilliAmps() ?: return "N/A"
        return "$mA mA"
    }

    private fun getPowerNowString(intent: Intent): String {
        val mA = getCurrentNowMilliAmps() ?: return "N/A"
        val mV = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
        if (mV <= 0) return "N/A"
        val watts = (mA / 1000.0) * (mV / 1000.0)
        if (abs(watts) < 0.005) return "0.00 W"
        return String.format("%.2f W", watts)
    }

    private fun getChargeTimeRemaining(status: Int): String { /* ... (code from your provided snippet) ... */ }

    private fun getDesignCapacity(): String {
        try {
            val powerProfile = Class.forName("com.android.internal.os.PowerProfile")
                .getConstructor(Context::class.java).newInstance(context)
            val capacity = powerProfile.javaClass
                .getMethod("getBatteryCapacity").invoke(powerProfile) as Double
            if (capacity > 0) return "${capacity.toInt()} mAh"
        } catch (_: Throwable) { }
        return "N/A"
    }

    private fun formatDuration(millis: Long): String { /* ... (code from your provided snippet) ... */ }
}

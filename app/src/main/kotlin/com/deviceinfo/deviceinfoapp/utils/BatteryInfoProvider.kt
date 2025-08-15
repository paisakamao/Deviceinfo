package com.deviceinfo.deviceinfoapp.utils

import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.os.Build
import com.deviceinfo.deviceinfoapp.model.DeviceInfo
import java.util.concurrent.TimeUnit

class BatteryInfoProvider(private val context: Context) {

    private val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

    /**
     * This is the single, powerful function that processes a battery Intent
     * and returns a full, detailed list of information.
     */
    fun getDetailsFromIntent(intent: Intent): List<DeviceInfo> {
        val details = mutableListOf<DeviceInfo>()
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        
        details.add(DeviceInfo("Health", getBatteryHealth(intent)))
        details.add(DeviceInfo("Level", getBatteryPercentage(intent)))
        details.add(DeviceInfo("Status", getBatteryStatus(status)))
        details.add(DeviceInfo("Power Source", getChargingSource(intent)))
        details.add(DeviceInfo("Technology", getBatteryTechnology(intent)))
        details.add(DeviceInfo("Temperature", getBatteryTemperature(intent)))
        details.add(DeviceInfo("Voltage", getBatteryVoltage(intent)))
        details.add(DeviceInfo("Current (Real-time)", getCurrentNow(intent)))
        details.add(DeviceInfo("Power (Real-time)", getPowerNow(intent)))
        details.add(DeviceInfo("Time to Charge/Discharge", getChargeTimeRemaining(status)))
        details.add(DeviceInfo("Capacity (Design)", getDesignCapacity()))

        return details
    }

    private fun getCurrentNow(intent: Intent): String {
        val currentMicroAmps = try {
            batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
        } catch (e: Exception) { 0 }
        
        if (currentMicroAmps == 0) return "N/A"
        
        return "${currentMicroAmps / 1000} mA"
    }
    
    private fun getPowerNow(intent: Intent): String {
        val currentMicroAmps = try {
            batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
        } catch (e: Exception) { 0 }
        
        val voltageMilliVolts = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)

        if (currentMicroAmps == 0 || voltageMilliVolts <= 0) return "N/A"

        val powerWatts = (voltageMilliVolts / 1000.0) * (currentMicroAmps / 1000.0)
        return "%.2f W".format(powerWatts)
    }

    private fun getBatteryPercentage(intent: Intent): String {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        if (level == -1 || scale == -1) return "N/A"
        return "${(level * 100.0f / scale).toInt()}%"
    }

    // Other helpers (status, health, source, etc.) are the same as before
    private fun getBatteryStatus(status: Int): String { /* ... code ... */ }
    private fun getBatteryHealth(intent: Intent): String { /* ... code ... */ }
    private fun getChargingSource(intent: Intent): String { /* ... code ... */ }
    private fun getBatteryTechnology(intent: Intent): String { return intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "N/A" }
    private fun getBatteryTemperature(intent: Intent): String {
        val temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)
        return if (temp != -1) "${temp / 10.0f}°C" else "N/A"
    }
    private fun getBatteryVoltage(intent: Intent): String {
        val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
        return if (voltage != -1) "$voltage mV" else "N/A"
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

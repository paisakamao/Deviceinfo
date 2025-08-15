package com.deviceinfo.deviceinfoapp.utils

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import com.deviceinfo.deviceinfoapp.model.DeviceInfo
import java.util.concurrent.TimeUnit

class BatteryInfoHelper(private val context: Context) {

    // A single place to get the modern BatteryManager service
    private val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

    // A single place to get the classic battery Intent
    private fun getBatteryStatusIntent(): Intent? {
        return context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    // --- NEW MASTER FUNCTION ---
    // This builds the complete list for the detail screen.
    fun getBatteryDetailsList(): List<DeviceInfo> {
        val details = mutableListOf<DeviceInfo>()
        val intent = getBatteryStatusIntent() ?: return emptyList()

        details.add(DeviceInfo("Level", getBatteryPercentage(intent)))
        details.add(DeviceInfo("Status", getBatteryStatus(intent)))
        details.add(DeviceInfo("Health", getBatteryHealth(intent)))
        details.add(DeviceInfo("Charging Source", getChargingSource(intent)))
        details.add(DeviceInfo("Technology", getBatteryTechnology(intent)))
        details.add(DeviceInfo("Temperature", getBatteryTemperature(intent)))
        details.add(DeviceInfo("Voltage", getBatteryVoltage(intent)))
        details.add(DeviceInfo("Current (Real-time)", getCurrentNow()))
        details.add(DeviceInfo("Power (Real-time)", getPowerNow(intent)))
        details.add(DeviceInfo("Time Until Full (est.)", getChargeTimeRemaining()))
        details.add(DeviceInfo("Current Capacity (est.)", getCurrentCapacity()))
        details.add(DeviceInfo("Total Capacity (est.)", getTotalCapacity()))

        return details
    }

    // --- Individual Helper Functions ---

    // This is the public function for the main screen
    fun getBatteryPercentage(intent: Intent? = getBatteryStatusIntent()): String {
        intent ?: return "N/A"
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        if (level == -1 || scale == -1) return "N/A"
        val percentage = (level * 100.0f / scale).toInt()
        return "$percentage%"
    }

    private fun getBatteryStatus(intent: Intent): String {
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
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
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheating"
            BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Failure"
            BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
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

    private fun getBatteryTemperature(intent: Intent): String {
        val temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)
        if (temperature == -1) return "N/A"
        val tempCelsius = temperature / 10.0f
        return "$tempCelsius °C"
    }

    private fun getBatteryVoltage(intent: Intent): String {
        val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
        if (voltage == -1) return "N/A"
        return "$voltage mV"
    }

    private fun getCurrentNow(): String {
        val currentMicroamps = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
        // A positive value means charging, negative means discharging.
        val currentMilliamps = currentMicroamps / 1000
        return if (currentMilliamps != 0L) "$currentMilliamps mA" else "N/A"
    }
    
    private fun getPowerNow(intent: Intent): String {
        val current = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
        val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)

        if (current == 0L || voltage == -1) return "N/A"

        // Power (in milliwatts) = Voltage (in volts) * Current (in milliamps)
        val powerMilliwatts = (voltage / 1000.0) * (current / 1000.0)
        return "%.2f mW".format(powerMilliwatts)
    }

    private fun getChargeTimeRemaining(): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val timeInMillis = batteryManager.computeChargeTimeRemaining()
            if (timeInMillis > 0) {
                return formatDuration(timeInMillis)
            }
        }
        return "N/A"
    }

    private fun getCurrentCapacity(): String {
        val chargeCounter = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
        return if (chargeCounter != Long.MIN_VALUE) "${chargeCounter / 1000} mAh" else "N/A"
    }

    private fun getTotalCapacity(): String {
        // This is an estimate, as there is no universal API for total design capacity.
        val currentCapacity = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
        val capacityPercent = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

        if (currentCapacity != Long.MIN_VALUE && capacityPercent > 0) {
            val totalCapacity = (currentCapacity / (capacityPercent / 100.0))
            return "${totalCapacity.toLong() / 1000} mAh (est.)"
        }
        return "N/A"
    }
    
    private fun formatDuration(millis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        return String.format("%d hr %d min", hours, minutes)
    }
}

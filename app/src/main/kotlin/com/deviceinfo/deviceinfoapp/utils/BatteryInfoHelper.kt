package com.deviceinfo.deviceinfoapp.utils

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import com.deviceinfo.deviceinfoapp.model.DeviceInfo

class BatteryInfoHelper(private val context: Context) {

    private fun getBatteryStatusIntent(): Intent? {
        return context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    // --- NEW MASTER FUNCTION ---
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
        details.add(DeviceInfo("Current Charge (est.)", getCurrentCapacity(intent)))

        return details
    }

    // --- Individual Helper Functions ---

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
            BatteryManager.BATTERY_STATUS_UNKNOWN -> "Unknown"
            else -> "N/A"
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
            BatteryManager.BATTERY_HEALTH_UNKNOWN -> "Unknown"
            else -> "N/A"
        }
    }

    private fun getChargingSource(intent: Intent): String {
        val plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
        return when (plugged) {
            BatteryManager.BATTERY_PLUGGED_AC -> "AC Charger"
            BatteryManager.BATTERY_PLUGGED_USB -> "USB Port"
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless Charger"
            0 -> "On Battery"
            else -> "N/A"
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

    private fun getCurrentCapacity(intent: Intent): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            val chargeCounter = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
            if (chargeCounter != Long.MIN_VALUE) {
                // The value is in microampere-hours, convert to mAh
                return "${chargeCounter / 1000} mAh"
            }
        }
        return "N/A"
    }
}

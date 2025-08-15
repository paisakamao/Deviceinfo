package com.deviceinfo.deviceinfoapp.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.deviceinfo.deviceinfoapp.model.DeviceInfo

class BatteryDetailProvider(private val context: Context) {

    private var receiver: BroadcastReceiver? = null

    // The listener that the Activity will use to get updates
    private var listener: ((List<DeviceInfo>) -> Unit)? = null

    fun setListener(listener: (List<DeviceInfo>) -> Unit) {
        this.listener = listener
    }

    // This function starts listening for battery updates from the system
    fun startListening() {
        if (receiver == null) {
            receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    intent?.let {
                        // When we receive an update, process it and send it to the listener
                        listener?.invoke(processIntent(it))
                    }
                }
            }
            context.registerReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        }
    }

    // This function stops listening to save resources
    fun stopListening() {
        receiver?.let {
            context.unregisterReceiver(it)
            receiver = null
        }
    }

    // This is the direct Kotlin translation of the reference app's logic
    private fun processIntent(intent: Intent): List<DeviceInfo> {
        val details = mutableListOf<DeviceInfo>()

        // Health
        val health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)
        val healthString = when (health) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
            BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
            BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Failure"
            else -> "Unknown"
        }
        details.add(DeviceInfo("Health", healthString))

        // Level
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val levelString = if (level != -1 && scale != -1) {
            "${(level * 100f / scale).toInt()}%"
        } else {
            "N/A"
        }
        details.add(DeviceInfo("Level", levelString))

        // Status
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val statusString = when (status) {
            BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
            BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
            BatteryManager.BATTERY_STATUS_FULL -> "Full"
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Not Charging"
            else -> "Unknown"
        }
        details.add(DeviceInfo("Status", statusString))

        // Power Source
        val plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
        val sourceString = when (plugged) {
            BatteryManager.BATTERY_PLUGGED_AC -> "AC Charger"
            BatteryManager.BATTERY_PLUGGED_USB -> "USB Port"
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless"
            0 -> "On Battery"
            else -> "Unknown"
        }
        details.add(DeviceInfo("Power Source", sourceString))

        // Technology
        details.add(DeviceInfo("Technology", intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "N/A"))

        // Temperature
        val temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)
        val tempString = if (temperature != -1) "${temperature / 10.0f}°C" else "N/A"
        details.add(DeviceInfo("Temperature", tempString))

        // Voltage
        val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
        val voltageString = if (voltage != -1) "$voltage mV" else "N/A"
        details.add(DeviceInfo("Voltage", voltageString))

        return details
    }
}

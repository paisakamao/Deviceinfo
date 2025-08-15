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
        details.add(DeviceInfo("Voltage", getBatteryVoltageString(intent)))
        details.add(DeviceInfo("Current (Real-time)", getCurrentNowString()))
        details.add(DeviceInfo("Power (Real-time)", getPowerNowString()))
        details.add(DeviceInfo("Time to Charge/Discharge", getChargeTimeRemaining(status)))
        details.add(DeviceInfo("Capacity (Design)", getDesignCapacity()))

        return details
    }

    fun getBatteryPercentageForDashboard(): String {
        return getBatteryPercentage(getBatteryStatusIntent())
    }

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
    private fun getBatteryTemperature(intent: Intent): String {
        val t = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)
        if (t == -1) return "N/A"
        return "${(t / 10.0f)} °C"
    }

    private fun getBatteryVoltageString(intent: Intent): String {
        val mv = getInstantVoltageMilliVolts() ?: intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
        return if (mv <= 0) "N/A" else "$mv mV"
    }

    fun getCurrentNowMilliAmps(): Int? {
        val sysfsCurrentUA = readFirstExistingSysfsLong(
            "/sys/class/power_supply/battery/current_now",
            "/sys/class/power_supply/Battery/current_now",
            "/sys/class/power_supply/max170xx_battery/current_now",
            "/sys/class/power_supply/bms/current_now"
        )
        if (sysfsCurrentUA != null && sysfsCurrentUA != 0L) {
            return (sysfsCurrentUA / 1000.0).toInt()
        }

        var microA = try {
            batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW).toLong()
        } catch (_: Throwable) { 0L }

        if (microA == 0L || microA == Long.MIN_VALUE) {
            microA = try {
                batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE)
            } catch (_: Throwable) { 0L }
        }

        if (microA == 0L || microA == Long.MIN_VALUE) return null
        return (microA / 1000.0).toInt()
    }

    private fun getInstantVoltageMilliVolts(): Int? {
        val microV = readFirstExistingSysfsLong(
            "/sys/class/power_supply/battery/voltage_now",
            "/sys/class/power_supply/Battery/voltage_now",
            "/sys/class/power_supply/max170xx_battery/voltage_now",
            "/sys/class/power_supply/bms/voltage_now"
        )
        if (microV != null && microV > 0L) {
            return (microV / 1000L).toInt()
        }
        return null
    }

    private fun getCurrentNowString(): String {
        val mA = getCurrentNowMilliAmps() ?: return "N/A"
        return "$mA mA"
    }

    private fun getPowerNowString(): String {
        val mA = getCurrentNowMilliAmps() ?: return "N/A"
        val mV = getInstantVoltageMilliVolts()
            ?: getBatteryStatusIntent()?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
            ?: -1
        if (mV <= 0) return "N/A"
        val watts = (mA / 1000.0) * (mV / 1000.0)
        if (abs(watts) < 0.005) return "0.00 W"
        return String.format("%.2f W", watts)
    }

    private fun getChargeTimeRemaining(status: Int): String { /* ... (code from your provided snippet) ... */ }
    private fun getDesignCapacity(): String { /* ... (code from your provided snippet) ... */ }
    private fun formatDuration(millis: Long): String { /* ... (code from your provided snippet) ... */ }

    private fun readFirstExistingSysfsLong(vararg paths: String): Long? {
        for (p in paths) {
            val v = readSysfsLong(p)
            if (v != null) return v
        }
        return null
    }

    private fun readSysfsLong(path: String): Long? {
        return try {
            val f = File(path)
            if (!f.exists() || !f.canRead()) return null
            f.bufferedReader().use { br ->
                val raw = br.readLine()?.trim() ?: return null
                raw.toLongOrNull()
            }
        } catch (_: Throwable) { null }
    }
}

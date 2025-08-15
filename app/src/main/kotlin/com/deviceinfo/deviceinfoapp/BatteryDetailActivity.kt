package com.deviceinfo.deviceinfoapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deviceinfo.deviceinfoapp.adapter.DeviceInfoAdapter
import com.deviceinfo.deviceinfoapp.model.DeviceInfo
import java.util.concurrent.TimeUnit

class BatteryDetailActivity : AppCompatActivity() {

    private val batteryDetailsList = mutableListOf<DeviceInfo>()
    private lateinit var adapter: DeviceInfoAdapter
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var updateRunnable: Runnable
    private lateinit var batteryManager: BatteryManager

    private val batteryEventReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateBatteryInfo()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_battery_detail)
        supportActionBar?.title = "Battery Details"

        batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        
        val recyclerView: RecyclerView = findViewById(R.id.batteryDetailRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        adapter = DeviceInfoAdapter(batteryDetailsList)
        recyclerView.adapter = adapter

        updateRunnable = Runnable {
            updateBatteryInfo()
            handler.postDelayed(updateRunnable, 1000) // 1 second delay
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(batteryEventReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        handler.post(updateRunnable)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(batteryEventReceiver)
        handler.removeCallbacks(updateRunnable)
    }

    private fun updateBatteryInfo() {
        val intent = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED)) ?: return
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val voltageMilliVolts = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)

        val currentMicroAmps = try {
            batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
        } catch (e: Exception) { 0 }

        val currentMilliAmps = currentMicroAmps / 1000.0
        val powerWatts = (voltageMilliVolts / 1000.0) * (currentMilliAmps / 1000.0)
        
        val currentString = if (currentMicroAmps == 0) "N/A" else "${currentMilliAmps.toInt()} mA"
        val powerString = if (currentMicroAmps == 0 || voltageMilliVolts <= 0) "N/A" else "%.2f W".format(powerWatts)
        
        val newDetails = listOf(
            DeviceInfo("Health", getBatteryHealth(intent)),
            DeviceInfo("Level", getBatteryPercentage(intent)),
            DeviceInfo("Status", getBatteryStatus(status)),
            DeviceInfo("Power Source", getChargingSource(intent)),
            DeviceInfo("Technology", getBatteryTechnology(intent)),
            DeviceInfo("Temperature", getBatteryTemperature(intent)),
            DeviceInfo("Voltage", if (voltageMilliVolts > 0) "$voltageMilliVolts mV" else "N/A"),
            DeviceInfo("Current (Real-time)", currentString),
            DeviceInfo("Power (Real-time)", powerString),
            DeviceInfo("Time to Charge/Discharge", getChargeTimeRemaining(status)),
            DeviceInfo("Capacity (Design)", getDesignCapacity())
        )

        batteryDetailsList.clear()
        batteryDetailsList.addAll(newDetails)
        adapter.notifyDataSetChanged()
    }
    
    private fun getBatteryPercentage(intent: Intent): String {
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

    private fun getBatteryHealth(intent: Intent): String {
        return when (intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheating"
            BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
            BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Failure"
            else -> "Unknown"
        }
    }

    private fun getChargingSource(intent: Intent): String {
        return when (intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)) {
            BatteryManager.BATTERY_PLUGGED_AC -> "AC Charger"
            BatteryManager.BATTERY_PLUGGED_USB -> "USB Port"
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless"
            0 -> "On Battery"
            else -> "Unknown"
        }
    }
    
    private fun getBatteryTechnology(intent: Intent): String {
        return intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "N/A"
    }
    
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
            val powerProfile = Class.forName("com.android.internal.os.PowerProfile").getConstructor(Context::class.java).newInstance(this)
            val capacity = powerProfile.javaClass.getMethod("getBatteryCapacity").invoke(powerProfile) as Double
            if (capacity > 0) return "${capacity.toInt()} mAh"
        } catch (e: Exception) { /* Fall through */ }
        return "N/A"
    }
}

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

    // Receiver for slow updates (plugged in, health change, etc.)
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

        // This runnable will execute every second to update the UI
        updateRunnable = Runnable {
            updateBatteryInfo()
            handler.postDelayed(updateRunnable, 1000) // 1 second delay
        }
    }

    override fun onResume() {
        super.onResume()
        // Register the receiver for slow updates
        registerReceiver(batteryEventReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        // Start the handler for fast, real-time updates
        handler.post(updateRunnable)
    }

    override fun onPause() {
        super.onPause()
        // Stop both listeners to save battery
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

        // --- THE CRITICAL FIX IS HERE ---
        val currentMilliAmps = currentMicroAmps / 1000.0
        val powerWatts = (voltageMilliVolts / 1000.0) * (currentMilliAmps / 1000.0)

        // Check the raw microamp value, NOT the rounded integer.
        val currentString = if (currentMicroAmps == 0) "N/A" else "${currentMilliAmps.toInt()} mA"
        val powerString = if (currentMicroAmps == 0 || voltageMilliVolts <= 0) "N/A" else "%.2f W".format(powerWatts)
        // --- END OF FIX ---

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
    
    // --- All helper functions are now self-contained in this file ---

    private fun getBatteryPercentage(intent: Intent): String { /* ... same as before ... */ }
    private fun getBatteryStatus(status: Int): String { /* ... same as before ... */ }
    private fun getBatteryHealth(intent: Intent): String { /* ... same as before ... */ }
    private fun getChargingSource(intent: Intent): String { /* ... same as before ... */ }
    private fun getBatteryTechnology(intent: Intent): String = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "N/A"
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

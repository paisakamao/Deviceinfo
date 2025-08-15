package com.deviceinfo.deviceinfoapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deviceinfo.deviceinfoapp.adapter.DeviceInfoAdapter
import com.deviceinfo.deviceinfoapp.model.DeviceInfo
import com.deviceinfo.deviceinfoapp.utils.BatteryInfoProvider

class BatteryDetailActivity : AppCompatActivity() {

    private lateinit var batteryProvider: BatteryInfoProvider
    private val batteryDetailsList = mutableListOf<DeviceInfo>()
    private lateinit var adapter: DeviceInfoAdapter

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var realTimeRunnable: Runnable

    // Receiver for slow updates (plugged in, health change, etc.)
    private val batteryEventReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                updateSlowData(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_battery_detail)
        supportActionBar?.title = "Battery Details"

        val recyclerView: RecyclerView = findViewById(R.id.batteryDetailRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        batteryProvider = BatteryInfoProvider(this)
        adapter = DeviceInfoAdapter(batteryDetailsList)
        recyclerView.adapter = adapter

        // Initialize the runnable that will perform fast updates
        realTimeRunnable = Runnable {
            updateFastData()
            handler.postDelayed(realTimeRunnable, 1000) // Re-schedules itself every 1 second
        }
    }

    override fun onResume() {
        super.onResume()
        // Register the receiver for slow updates
        registerReceiver(batteryEventReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        // Start the handler for fast updates
        handler.post(realTimeRunnable)
    }

    override fun onPause() {
        super.onPause()
        // Stop both listeners to save battery
        unregisterReceiver(batteryEventReceiver)
        handler.removeCallbacks(realTimeRunnable)
    }

    private fun updateSlowData(intent: Intent) {
        val slowDetails = batteryProvider.getSlowUpdateDetails(intent)
        
        if (batteryDetailsList.isEmpty()) {
            batteryDetailsList.addAll(slowDetails)
            // Add placeholders for fast data on the very first load
            batteryDetailsList.add(DeviceInfo("Voltage", "Loading..."))
            batteryDetailsList.add(DeviceInfo("Current (Real-time)", "Loading..."))
            batteryDetailsList.add(DeviceInfo("Power (Real-time)", "Loading..."))
            adapter.notifyDataSetChanged()
        } else {
            slowDetails.forEach { newItem ->
                val index = batteryDetailsList.indexOfFirst { it.label == newItem.label }
                if (index != -1) {
                    batteryDetailsList[index] = newItem
                    adapter.notifyItemChanged(index)
                }
            }
        }
    }

    private fun updateFastData() {
        if (batteryDetailsList.isEmpty()) return // Don't run if the initial list isn't ready

        val fastDetails = batteryProvider.getFastUpdateDetails()
        fastDetails.forEach { (label, value) ->
            val index = batteryDetailsList.indexOfFirst { it.label == label }
            if (index != -1) {
                batteryDetailsList[index] = DeviceInfo(label, value)
                adapter.notifyItemChanged(index)
            }
        }
    }
}

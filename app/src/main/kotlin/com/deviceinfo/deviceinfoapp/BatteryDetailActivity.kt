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
import com.deviceinfo.deviceinfoapp.utils.BatteryInfoHelper

class BatteryDetailActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var batteryInfoHelper: BatteryInfoHelper
    private val batteryDetailsList = mutableListOf<DeviceInfo>()
    private lateinit var adapter: DeviceInfoAdapter

    private val handler = Handler(Looper.getMainLooper())
    private val updateIntervalMs = 1000L // 1 second timer

    // Receiver for slow updates (plugged in, health change, etc.)
    private val batteryEventReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateSlowData()
        }
    }

    // Runnable for fast updates (current, voltage, power)
    private val realTimeRunnable: Runnable = Runnable {
        updateFastData()
        handler.postDelayed(realTimeRunnable, updateIntervalMs)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_battery_detail)
        supportActionBar?.title = "Battery Details"

        recyclerView = findViewById(R.id.batteryDetailRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        batteryInfoHelper = BatteryInfoHelper(this)
        adapter = DeviceInfoAdapter(batteryDetailsList)
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        // Register the receiver for slow updates
        registerReceiver(batteryEventReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        // Start the handler for fast updates
        handler.post(realTimeRunnable)
        // Load initial data
        updateSlowData()
    }

    override fun onPause() {
        super.onPause()
        // Stop both listeners to save battery
        unregisterReceiver(batteryEventReceiver)
        handler.removeCallbacks(realTimeRunnable)
    }

    private fun updateSlowData() {
        val slowDetails = batteryInfoHelper.getSlowUpdateDetails()
        
        // If the list is empty, populate it. Otherwise, update existing items.
        if (batteryDetailsList.isEmpty()) {
            batteryDetailsList.addAll(slowDetails)
            // Add placeholders for fast data so the list has the right size
            batteryDetailsList.add(DeviceInfo("Voltage", "..."))
            batteryDetailsList.add(DeviceInfo("Current (Real-time)", "..."))
            batteryDetailsList.add(DeviceInfo("Power (Real-time)", "..."))
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
        val fastDetails = batteryInfoHelper.getFastUpdateDetails()

        fastDetails.forEach { (label, value) ->
            val index = batteryDetailsList.indexOfFirst { it.label == label }
            if (index != -1) {
                batteryDetailsList[index] = DeviceInfo(label, value)
                adapter.notifyItemChanged(index)
            }
        }
    }
}

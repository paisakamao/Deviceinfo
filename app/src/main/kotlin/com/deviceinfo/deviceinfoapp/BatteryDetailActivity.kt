package com.deviceinfo.deviceinfoapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
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

    // The BroadcastReceiver that will listen for battery updates
    private val batteryInfoReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // When an update is received, refresh our list
            updateBatteryInfo()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_battery_detail)
        supportActionBar?.title = "Battery Details"

        recyclerView = findViewById(R.id.batteryDetailRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        batteryInfoHelper = BatteryInfoHelper(this)
        
        // Set up the adapter once with an empty list
        adapter = DeviceInfoAdapter(batteryDetailsList)
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        // Register the receiver when the activity is visible
        registerReceiver(batteryInfoReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        // Load the initial data
        updateBatteryInfo()
    }

    override fun onPause() {
        super.onPause()
        // Unregister the receiver when the activity is no longer visible to save resources
        unregisterReceiver(batteryInfoReceiver)
    }

    private fun updateBatteryInfo() {
        // Get the fresh, complete list of battery details
        val newDetails = batteryInfoHelper.getBatteryDetailsList()
        
        // Update the list data and notify the adapter
        batteryDetailsList.clear()
        batteryDetailsList.addAll(newDetails)
        adapter.notifyDataSetChanged()
    }
}

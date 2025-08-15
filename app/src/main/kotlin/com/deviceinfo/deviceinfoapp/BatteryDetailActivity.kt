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
import com.deviceinfo.deviceinfoapp.utils.BatteryInfoProvider

class BatteryDetailActivity : AppCompatActivity() {

    private val batteryDetailsList = mutableListOf<DeviceInfo>()
    private lateinit var adapter: DeviceInfoAdapter
    private lateinit var batteryProvider: BatteryInfoProvider

    // This is the core of the solution, just like the reference app
    private val batteryInfoReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                // When the system sends an update, process it and refresh the screen
                val newDetails = batteryProvider.getDetailsFromIntent(it)
                batteryDetailsList.clear()
                batteryDetailsList.addAll(newDetails)
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_battery_detail)
        supportActionBar?.title = "Battery Details"

        val recyclerView: RecyclerView = findViewById(R.id.batteryDetailRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        adapter = DeviceInfoAdapter(batteryDetailsList)
        recyclerView.adapter = adapter
        
        batteryProvider = BatteryInfoProvider(this)
    }

    override fun onResume() {
        super.onResume()
        // Start listening for battery updates when the screen is visible
        registerReceiver(batteryInfoReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    override fun onPause() {
        super.onPause()
        // Stop listening when the screen is hidden to save resources
        unregisterReceiver(batteryInfoReceiver)
    }
}

package com.deviceinfo.deviceinfoapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deviceinfo.deviceinfoapp.adapter.DeviceInfoAdapter
import com.deviceinfo.deviceinfoapp.model.DeviceInfo
import com.deviceinfo.deviceinfoapp.utils.BatteryDetailProvider

class BatteryDetailActivity : AppCompatActivity() {

    private val batteryDetailsList = mutableListOf<DeviceInfo>()
    private lateinit var adapter: DeviceInfoAdapter
    private lateinit var batteryProvider: BatteryDetailProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_battery_detail)
        supportActionBar?.title = "Battery Details"

        val recyclerView: RecyclerView = findViewById(R.id.batteryDetailRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        // Set up the adapter with an empty list initially
        adapter = DeviceInfoAdapter(batteryDetailsList)
        recyclerView.adapter = adapter
        
        // Create our new provider
        batteryProvider = BatteryDetailProvider(this)

        // Set the listener to update our UI whenever the provider has new data
        batteryProvider.setListener { newDetails ->
            batteryDetailsList.clear()
            batteryDetailsList.addAll(newDetails)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        // Start listening for battery updates when the screen is visible
        batteryProvider.startListening()
    }

    override fun onPause() {
        super.onPause()
        // Stop listening when the screen is hidden to save resources
        batteryProvider.stopListening()
    }
}

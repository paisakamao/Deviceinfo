package com.deviceinfo.deviceinfoapp

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

    // A Handler is the modern Android way to create a timer
    private val handler = Handler(Looper.getMainLooper())
    // A Runnable is the task that the Handler will execute
    private lateinit var updateRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_battery_detail)
        supportActionBar?.title = "Battery Details"

        recyclerView = findViewById(R.id.batteryDetailRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        batteryInfoHelper = BatteryInfoHelper(this)
        
        adapter = DeviceInfoAdapter(batteryDetailsList)
        recyclerView.adapter = adapter

        // Define the task that will run repeatedly
        updateRunnable = Runnable {
            updateBatteryInfo() // Refresh the data
            // Schedule this same task to run again after a 2-second delay
            handler.postDelayed(updateRunnable, 2000) // 2000 milliseconds = 2 seconds
        }
    }

    override fun onResume() {
        super.onResume()
        // When the screen becomes visible, start the real-time updates
        handler.post(updateRunnable)
    }

    override fun onPause() {
        super.onPause()
        // When the screen is hidden, stop the updates to save battery life. This is critical.
        handler.removeCallbacks(updateRunnable)
    }

    private fun updateBatteryInfo() {
        // Get the fresh, complete list of battery details from our powerful helper
        val newDetails = batteryInfoHelper.getBatteryDetailsList()
        
        // Update the list data and notify the adapter to refresh the screen
        batteryDetailsList.clear()
        batteryDetailsList.addAll(newDetails)
        adapter.notifyDataSetChanged()
    }
}

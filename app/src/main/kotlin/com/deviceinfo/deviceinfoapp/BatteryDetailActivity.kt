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
import com.deviceinfo.deviceinfoapp.utils.BatteryRealtimeHelper
import com.deviceinfo.deviceinfoapp.utils.BatteryStaticHelper

class BatteryDetailActivity : AppCompatActivity() {

    private val batteryDetailsList = mutableListOf<DeviceInfo>()
    private lateinit var adapter: DeviceInfoAdapter
    private lateinit var staticHelper: BatteryStaticHelper
    private lateinit var realtimeHelper: BatteryRealtimeHelper
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var realTimeRunnable: Runnable

    private val batteryEventReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateStaticInfo()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_battery_detail)
        supportActionBar?.title = "Battery Details"

        val recyclerView: RecyclerView = findViewById(R.id.batteryDetailRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        staticHelper = BatteryStaticHelper(this)
        realtimeHelper = BatteryRealtimeHelper(this)
        
        adapter = DeviceInfoAdapter(batteryDetailsList)
        recyclerView.adapter = adapter

        realTimeRunnable = Runnable {
            updateRealtimeInfo()
            handler.postDelayed(realTimeRunnable, 1000)
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(batteryEventReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        handler.post(realTimeRunnable)
        updateStaticInfo() // Initial load
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(batteryEventReceiver)
        handler.removeCallbacks(realTimeRunnable)
    }

    private fun updateStaticInfo() {
        val staticDetails = staticHelper.getStaticBatteryDetails()
        if (batteryDetailsList.isEmpty()) {
            batteryDetailsList.addAll(staticDetails)
            batteryDetailsList.add(DeviceInfo("Current (Real-time)", "Loading..."))
            batteryDetailsList.add(DeviceInfo("Power (Real-time)", "Loading..."))
            adapter.notifyDataSetChanged()
        } else {
            staticDetails.forEach { newItem ->
                val index = batteryDetailsList.indexOfFirst { it.label == newItem.label }
                if (index != -1) {
                    batteryDetailsList[index] = newItem
                    adapter.notifyItemChanged(index)
                }
            }
        }
    }

    private fun updateRealtimeInfo() {
        if (batteryDetailsList.isEmpty()) return

        val current = realtimeHelper.getBatteryCurrentNow()
        val power = realtimeHelper.getBatteryPowerNow()

        val currentIdx = batteryDetailsList.indexOfFirst { it.label == "Current (Real-time)" }
        if (currentIdx != -1) {
            batteryDetailsList[currentIdx] = DeviceInfo("Current (Real-time)", current)
            adapter.notifyItemChanged(currentIdx)
        }

        val powerIdx = batteryDetailsList.indexOfFirst { it.label == "Power (Real-time)" }
        if (powerIdx != -1) {
            batteryDetailsList[powerIdx] = DeviceInfo("Power (Real-time)", power)
            adapter.notifyItemChanged(powerIdx)
        }
    }
}
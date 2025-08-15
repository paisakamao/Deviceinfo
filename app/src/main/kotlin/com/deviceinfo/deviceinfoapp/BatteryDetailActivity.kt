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
    private lateinit var realTimeUpdateRunnable: Runnable

    private val batteryEventReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // Updates for slow changes
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

        adapter = DeviceInfoAdapter(batteryDetailsList)
        recyclerView.adapter = adapter

        // Real-time updates every 2 seconds
        realTimeUpdateRunnable = Runnable {
            updateBatteryInfo()
            handler.postDelayed(realTimeUpdateRunnable, 2000)
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(batteryEventReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        handler.post(realTimeUpdateRunnable)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(batteryEventReceiver)
        handler.removeCallbacks(realTimeUpdateRunnable)
    }

    private fun updateBatteryInfo() {
        val newDetails = batteryInfoHelper.getBatteryDetailsList()
        batteryDetailsList.clear()
        batteryDetailsList.addAll(newDetails)
        adapter.notifyDataSetChanged()
    }
}

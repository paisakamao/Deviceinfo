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
    private val updateIntervalMs = 1000L // 1 second

    private val batteryEventReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateBatteryInfo(fullRefresh = true)
        }
    }

    private val realTimeRunnable = object : Runnable {
        override fun run() {
            updateBatteryInfo(fullRefresh = false) // only update the real-time rows
            handler.postDelayed(this, updateIntervalMs)
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
        updateBatteryInfo(fullRefresh = true)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(batteryEventReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        handler.post(realTimeRunnable)
    }

    override fun onPause() {
        super.onPause()
        try {
            unregisterReceiver(batteryEventReceiver)
        } catch (_: IllegalArgumentException) { /* ignore */ }
        handler.removeCallbacks(realTimeRunnable)
    }

    private fun updateBatteryInfo(fullRefresh: Boolean) {
        if (fullRefresh || batteryDetailsList.isEmpty()) {
            val newDetails = batteryInfoHelper.getBatteryDetailsList()
            batteryDetailsList.clear()
            batteryDetailsList.addAll(newDetails)
            adapter.notifyDataSetChanged()
        } else {
            val currentIdx = batteryDetailsList.indexOfFirst { it.label == "Current (Real-time)" }
            val powerIdx   = batteryDetailsList.indexOfFirst { it.label == "Power (Real-time)" }
            val intent = batteryInfoHelper.getBatteryStatusIntent()

            if (currentIdx != -1) {
                val newCurrent = batteryInfoHelper.getCurrentNowMilliAmps()?.let { mA ->
                    val sign = if (mA > 0) "+" else ""
                    "$sign$mA mA"
                } ?: "N/A"
                batteryDetailsList[currentIdx] = DeviceInfo("Current (Real-time)", newCurrent)
                adapter.notifyItemChanged(currentIdx)
            }

            if (powerIdx != -1 && intent != null) {
                val mA = batteryInfoHelper.getCurrentNowMilliAmps()
                val mV = intent.getIntExtra(android.os.BatteryManager.EXTRA_VOLTAGE, -1)
                val newPower = if (mA != null && mV > 0) {
                    val watts = (mA / 1000.0) * (mV / 1000.0)
                    if (kotlin.math.abs(watts) < 0.005) "0.00 W" else String.format("%.2f W", watts)
                } else { "N/A" }
                batteryDetailsList[powerIdx] = DeviceInfo("Power (Real-time)", newPower)
                adapter.notifyItemChanged(powerIdx)
            }
        }
    }
}

package com.deviceinfo.deviceinfoapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deviceinfo.deviceinfoapp.adapter.DeviceInfoAdapter
import com.deviceinfo.deviceinfoapp.model.DeviceInfo
import com.deviceinfo.deviceinfoapp.utils.*

class MainActivity : AppCompatActivity() {
    private var batteryReceiver: BroadcastReceiver? = null
    private var currentBatteryLevel = "N/A"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Create instances of all the necessary helpers
        val deviceInfoHelper = DeviceInfoHelper(this)
        val cpuInfoHelper = CpuInfoHelper()
        val displayInfoHelper = DisplayInfoHelper(this)
        val sensorInfoHelper = SensorInfoHelper(this)
        val appInfoHelper = AppInfoHelper(this)
        val systemInfoHelper = SystemInfoHelper()

        val deviceInfoList = mutableListOf<DeviceInfo>()

        // --- Populate the full dashboard list ---
        deviceInfoList.add(DeviceInfo("Total RAM", deviceInfoHelper.getTotalRam()))
        deviceInfoList.add(DeviceInfo("Internal Storage Used", deviceInfoHelper.getInternalStorageUsagePercentage()))
        deviceInfoList.add(DeviceInfo("CPU Model", cpuInfoHelper.getCpuModel()))
        deviceInfoList.add(DeviceInfo("Root Status", systemInfoHelper.getRootStatus()))
        deviceInfoList.add(DeviceInfo("Kernel Version", systemInfoHelper.getKernelVersion()))

        // Initialize battery level before setting up the list
        setupBatteryMonitoring()

        // --- Clickable Summary Items ---
        deviceInfoList.add(DeviceInfo("Battery Details", currentBatteryLevel))
        deviceInfoList.add(DeviceInfo("Sensor Details", "${sensorInfoHelper.getSensorDetailsList().size} Sensors"))
        deviceInfoList.add(DeviceInfo("Application Details", "${appInfoHelper.getAllAppsDetails().size} Apps"))

        val adapter = DeviceInfoAdapter(deviceInfoList)

        adapter.onItemClick = { deviceInfo ->
            when (deviceInfo.label) {
                "Sensor Details" -> startActivity(Intent(this, SensorListActivity::class.java))
                "Application Details" -> startActivity(Intent(this, AppListActivity::class.java))
                "Battery Details" -> startActivity(Intent(this, BatteryDetailActivity::class.java))
            }
        }

        recyclerView.adapter = adapter
    }

    private fun setupBatteryMonitoring() {
        // Get initial battery status
        updateBatteryStatus()

        // Register for ongoing battery updates
        batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let { updateBatteryStatus(it) }
            }
        }

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_CHANGED)
            addAction(Intent.ACTION_BATTERY_LOW)
            addAction(Intent.ACTION_BATTERY_OKAY)
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
        }

        registerReceiver(batteryReceiver, filter)
    }

    private fun updateBatteryStatus(intent: Intent? = null) {
        val batteryIntent = intent ?: registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )

        batteryIntent?.let {
            val level = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val status = it.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            
            if (level != -1 && scale != -1) {
                val percentage = (level * 100.0f / scale).toInt()
                val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || 
                                status == BatteryManager.BATTERY_STATUS_FULL
                
                currentBatteryLevel = if (isCharging) {
                    "$percentage% (Charging)"
                } else {
                    "$percentage% (Discharging)"
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        batteryReceiver?.let {
            try {
                unregisterReceiver(it)
            } catch (e: Exception) {
                // Receiver was not registered
            }
        }
    }
}
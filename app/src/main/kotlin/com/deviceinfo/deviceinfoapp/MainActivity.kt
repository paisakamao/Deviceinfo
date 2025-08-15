package com.deviceinfo.deviceinfoapp

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // All helpers are correctly instantiated
        val deviceInfoHelper = DeviceInfoHelper(this)
        val cpuInfoHelper = CpuInfoHelper()
        val displayInfoHelper = DisplayInfoHelper(this)
        val sensorInfoHelper = SensorInfoHelper(this)
        val appInfoHelper = AppInfoHelper(this)
        val systemInfoHelper = SystemInfoHelper()
        
        val deviceInfoList = mutableListOf<DeviceInfo>()

        // --- Populate the full dashboard list ---
        deviceInfoList.add(DeviceInfo("Total RAM", deviceInfoHelper.getTotalRam()))
        deviceInfoList.add(DeviceInfo("Used RAM", deviceInfoHelper.getUsedRam()))
        deviceInfoList.add(DeviceInfo("Internal Storage Used", deviceInfoHelper.getInternalStorageUsagePercentage()))
        deviceInfoList.add(DeviceInfo("CPU Model", cpuInfoHelper.getCpuModel()))
        deviceInfoList.add(DeviceInfo("Number of Cores", cpuInfoHelper.getNumberOfCores()))
        deviceInfoList.add(DeviceInfo("Screen Resolution", displayInfoHelper.getScreenResolution()))
        deviceInfoList.add(DeviceInfo("Refresh Rate", displayInfoHelper.getRefreshRate()))
        deviceInfoList.add(DeviceInfo("Root Status", systemInfoHelper.getRootStatus()))
        deviceInfoList.add(DeviceInfo("Kernel Version", systemInfoHelper.getKernelVersion()))
        deviceInfoList.add(DeviceInfo("Device Model", deviceInfoHelper.getDeviceModel()))
        deviceInfoList.add(DeviceInfo("Android Version", deviceInfoHelper.getAndroidVersion()))

        // --- Clickable Summary Items ---
        deviceInfoList.add(DeviceInfo("Battery Details", getBatteryPercentageForDashboard()))
        deviceInfoList.add(DeviceInfo("Sensor Details", sensorInfoHelper.getSensorDetailsList().size.toString() + " Sensors"))
        deviceInfoList.add(DeviceInfo("Application Details", appInfoHelper.getAllAppsDetails().size.toString() + " Apps"))
        
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

    private fun getBatteryPercentageForDashboard(): String {
        val intent = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        if (level == -1 || scale == -1) return "N/A"
        return "${(level * 100.0f / scale).toInt()}%"
    }
}
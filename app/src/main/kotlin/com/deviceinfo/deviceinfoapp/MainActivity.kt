package com.deviceinfo.deviceinfoapp

import android.content.Intent
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

        val deviceInfoHelper = DeviceInfoHelper(this)
        val batteryInfoHelper = BatteryInfoHelper(this)
        val cpuInfoHelper = CpuInfoHelper()
        val displayInfoHelper = DisplayInfoHelper(this)
        val sensorInfoHelper = SensorInfoHelper(this)
        val appInfoHelper = AppInfoHelper(this)
        
        val deviceInfoList = mutableListOf<DeviceInfo>()

        // Populate the full summary list
        deviceInfoList.add(DeviceInfo("Total RAM", deviceInfoHelper.getTotalRam()))
        deviceInfoList.add(DeviceInfo("Used RAM", deviceInfoHelper.getUsedRam()))
        deviceInfoList.add(DeviceInfo("Free RAM", deviceInfoHelper.getAvailableRam()))
        deviceInfoList.add(DeviceInfo("Total Internal Storage", deviceInfoHelper.getTotalInternalStorage()))
        deviceInfoList.add(DeviceInfo("Available Internal Storage", deviceInfoHelper.getAvailableInternalStorage()))
        deviceInfoList.add(DeviceInfo("Internal Storage Used", deviceInfoHelper.getInternalStorageUsagePercentage()))
        deviceInfoList.add(DeviceInfo("Battery Level", batteryInfoHelper.getBatteryPercentage()))
        deviceInfoList.add(DeviceInfo("CPU Model", cpuInfoHelper.getCpuModel()))
        deviceInfoList.add(DeviceInfo("Number of Cores", cpuInfoHelper.getNumberOfCores()))
        deviceInfoList.add(DeviceInfo("Refresh Rate", displayInfoHelper.getRefreshRate()))
        deviceInfoList.add(DeviceInfo("Device Model", deviceInfoHelper.getDeviceModel()))
        
        // --- THIS SECTION IS NOW CORRECTED ---
        // Get the app counts for display
        val userAppCount = appInfoHelper.getUserAppsDetails().size
        val systemAppCount = appInfoHelper.getSystemAppsDetails().size
        val totalAppCount = userAppCount + systemAppCount
        
        // Add the clickable summary items
        deviceInfoList.add(DeviceInfo("Total Sensors", sensorInfoHelper.getSensorDetailsList().size.toString()))
        // Display the total count, matching the reference app
        deviceInfoList.add(DeviceInfo("All Apps", totalAppCount.toString()))

        val adapter = DeviceInfoAdapter(deviceInfoList)
        
        adapter.onItemClick = { deviceInfo ->
            when (deviceInfo.label) {
                "Total Sensors" -> {
                    val intent = Intent(this, SensorListActivity::class.java)
                    startActivity(intent)
                }
                "All Apps" -> { // Changed from "User Installed Apps" to "All Apps"
                    val intent = Intent(this, AppListActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        
        recyclerView.adapter = adapter
    }
}
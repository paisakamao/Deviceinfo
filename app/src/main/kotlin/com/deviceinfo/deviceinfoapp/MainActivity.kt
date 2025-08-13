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

        // Create instances of all our helpers
        val deviceInfoHelper = DeviceInfoHelper(this)
        val batteryInfoHelper = BatteryInfoHelper(this)
        val cpuInfoHelper = CpuInfoHelper()
        val displayInfoHelper = DisplayInfoHelper(this)
        val sensorInfoHelper = SensorInfoHelper(this)
        val appInfoHelper = AppInfoHelper(this)
        val systemInfoHelper = SystemInfoHelper() // New helper instance
        
        val deviceInfoList = mutableListOf<DeviceInfo>()

        // --- RAM Section ---
        deviceInfoList.add(DeviceInfo("Total RAM", deviceInfoHelper.getTotalRam()))
        deviceInfoList.add(DeviceInfo("Used RAM", deviceInfoHelper.getUsedRam()))
        
        // --- Storage Section ---
        deviceInfoList.add(DeviceInfo("Internal Storage Used", deviceInfoHelper.getInternalStorageUsagePercentage()))
        
        // --- Battery Section ---
        deviceInfoList.add(DeviceInfo("Battery Level", batteryInfoHelper.getBatteryPercentage()))
        deviceInfoList.add(DeviceInfo("Battery Temperature", batteryInfoHelper.getBatteryTemperature()))
        
        // --- CPU Section ---
        deviceInfoList.add(DeviceInfo("CPU Model", cpuInfoHelper.getCpuModel()))
        
        // --- Display Section ---
        deviceInfoList.add(DeviceInfo("Screen Resolution", displayInfoHelper.getScreenResolution()))
        deviceInfoList.add(DeviceInfo("Refresh Rate", displayInfoHelper.getRefreshRate()))

        // --- System Section ---
        deviceInfoList.add(DeviceInfo("Root Status", systemInfoHelper.getRootStatus()))
        deviceInfoList.add(DeviceInfo("Kernel Version", systemInfoHelper.getKernelVersion()))
        deviceInfoList.add(DeviceInfo("Build ID", systemInfoHelper.getBuildId()))
        deviceInfoList.add(DeviceInfo("Java VM Version", systemInfoHelper.getJavaVmVersion()))

        // --- Device & OS Section ---
        deviceInfoList.add(DeviceInfo("Device Model", deviceInfoHelper.getDeviceModel()))
        deviceInfoList.add(DeviceInfo("Android Version", deviceInfoHelper.getAndroidVersion()))

        // --- Clickable Summary Items ---
        deviceInfoList.add(DeviceInfo("Total Sensors", sensorInfoHelper.getSensorDetailsList().size.toString()))
        deviceInfoList.add(DeviceInfo("All Apps", appInfoHelper.getAllAppsDetails().size.toString()))
        
        val adapter = DeviceInfoAdapter(deviceInfoList)
        
        adapter.onItemClick = { deviceInfo ->
            when (deviceInfo.label) {
                "Total Sensors" -> {
                    val intent = Intent(this, SensorListActivity::class.java)
                    startActivity(intent)
                }
                "All Apps" -> {
                    val intent = Intent(this, AppListActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        
        recyclerView.adapter = adapter
    }
}
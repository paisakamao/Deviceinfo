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
        val batterySummaryHelper = BatterySummaryHelper(this) // Use the new summary helper
        val cpuInfoHelper = CpuInfoHelper()
        val displayInfoHelper = DisplayInfoHelper(this)
        val sensorInfoHelper = SensorInfoHelper(this)
        val appInfoHelper = AppInfoHelper(this)
        val systemInfoHelper = SystemInfoHelper()
        
        val deviceInfoList = mutableListOf<DeviceInfo>()

        // Populate the full summary list
        deviceInfoList.add(DeviceInfo("Total RAM", deviceInfoHelper.getTotalRam()))
        deviceInfoList.add(DeviceInfo("Internal Storage Used", deviceInfoHelper.getInternalStorageUsagePercentage()))
        deviceInfoList.add(DeviceInfo("Battery Level", batterySummaryHelper.getBatteryPercentage())) // Call the new helper
        deviceInfoList.add(DeviceInfo("CPU Model", cpuInfoHelper.getCpuModel()))
        deviceInfoList.add(DeviceInfo("Screen Resolution", displayInfoHelper.getScreenResolution()))
        deviceInfoList.add(DeviceInfo("Root Status", systemInfoHelper.getRootStatus()))
        deviceInfoList.add(DeviceInfo("Total Sensors", sensorInfoHelper.getSensorDetailsList().size.toString()))
        deviceInfoList.add(DeviceInfo("All Apps", appInfoHelper.getAllAppsDetails().size.toString()))
        
        val adapter = DeviceInfoAdapter(deviceInfoList)
        
        adapter.onItemClick = { deviceInfo ->
            when (deviceInfo.label) {
                "Total Sensors" -> {
                    startActivity(Intent(this, SensorListActivity::class.java))
                }
                "All Apps" -> {
                    startActivity(Intent(this, AppListActivity::class.java))
                }
                // Update this to launch the BatteryDetailActivity
                "Battery Level" -> {
                    startActivity(Intent(this, BatteryDetailActivity::class.java))
                }
            }
        }
        
        recyclerView.adapter = adapter
    }
}

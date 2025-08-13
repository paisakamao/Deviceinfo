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

        // --- Populate the summary list ---
        deviceInfoList.add(DeviceInfo("Total RAM", deviceInfoHelper.getTotalRam()))
        deviceInfoList.add(DeviceInfo("Battery Level", batteryInfoHelper.getBatteryPercentage()))
        deviceInfoList.add(DeviceInfo("CPU Model", cpuInfoHelper.getCpuModel()))
        deviceInfoList.add(DeviceInfo("Refresh Rate", displayInfoHelper.getRefreshRate()))
        // This is the important item for navigation
        deviceInfoList.add(DeviceInfo("Total Sensors", sensorInfoHelper.getSensorDetailsList().size.toString()))
        deviceInfoList.add(DeviceInfo("User Installed Apps", appInfoHelper.getUserAppCount()))
        
        // Create the simple adapter
        val adapter = DeviceInfoAdapter(deviceInfoList)
        
        // --- Set up the click listener ---
        adapter.onItemClick = { deviceInfo ->
            // Check if the user clicked on the "Total Sensors" item
            if (deviceInfo.label == "Total Sensors") {
                // Create an Intent to start our new SensorListActivity
                val intent = Intent(this, SensorListActivity::class.java)
                startActivity(intent)
            }
        }
        
        recyclerView.adapter = adapter
    }
}

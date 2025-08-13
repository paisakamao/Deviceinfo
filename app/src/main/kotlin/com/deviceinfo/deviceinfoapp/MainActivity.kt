package com.deviceinfo.deviceinfoapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deviceinfo.deviceinfoapp.adapter.MasterAdapter
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
        
        val masterList = mutableListOf<Any>()

        // Get the detailed sensor list once
        val sensorList = sensorInfoHelper.getSensorDetailsList()

        // --- Add all the general device info ---
        masterList.add(DeviceInfo("Total RAM", deviceInfoHelper.getTotalRam()))
        masterList.add(DeviceInfo("Used RAM", deviceInfoHelper.getUsedRam()))
        masterList.add(DeviceInfo("Free RAM", deviceInfoHelper.getAvailableRam()))
        masterList.add(DeviceInfo("Total Internal Storage", deviceInfoHelper.getTotalInternalStorage()))
        masterList.add(DeviceInfo("Available Internal Storage", deviceInfoHelper.getAvailableInternalStorage()))
        // This line is now corrected: deviceInfoHelper, not deviceInfoInfoHelper
        masterList.add(DeviceInfo("Internal Storage Used", deviceInfoHelper.getInternalStorageUsagePercentage())) 
        masterList.add(DeviceInfo("Battery Level", batteryInfoHelper.getBatteryPercentage()))
        masterList.add(DeviceInfo("CPU Model", cpuInfoHelper.getCpuModel()))
        masterList.add(DeviceInfo("Number of Cores", cpuInfoHelper.getNumberOfCores()))
        masterList.add(DeviceInfo("Refresh Rate", displayInfoHelper.getRefreshRate()))
        // This line is now corrected: get the size from the list we already fetched
        masterList.add(DeviceInfo("Total Sensors", sensorList.size.toString())) 
        masterList.add(DeviceInfo("User Installed Apps", appInfoHelper.getUserAppCount()))
        masterList.add(DeviceInfo("Device Model", deviceInfoHelper.getDeviceModel()))

        // --- Add a header for the sensors section ---
        masterList.add(DeviceInfo("--- ALL SENSORS ---", "")) 

        // --- Add all the detailed sensor info ---
        // We already have the list, so just add it
        masterList.addAll(sensorList)

        val adapter = MasterAdapter(masterList)
        recyclerView.adapter = adapter
    }
}

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
        
        // Create a master list that can hold ANY type of data
        val masterList = mutableListOf<Any>()

        // --- Add all the general device info ---
        masterList.add(DeviceInfo("Total RAM", deviceInfoHelper.getTotalRam()))
        masterList.add(DeviceInfo("Used RAM", deviceInfoHelper.getUsedRam()))
        masterList.add(DeviceInfo("Free RAM", deviceInfoHelper.getAvailableRam()))
        masterList.add(DeviceInfo("Total Internal Storage", deviceInfoHelper.getTotalInternalStorage()))
        masterList.add(DeviceInfo("Available Internal Storage", deviceInfoHelper.getAvailableInternalStorage()))
        masterList.add(DeviceInfo("Internal Storage Used", deviceInfoHelper.getInternalStorageUsagePercentage()))
        masterList.add(DeviceInfo("Battery Level", batteryInfoHelper.getBatteryPercentage()))
        masterList.add(DeviceInfo("CPU Model", cpuInfoHelper.getCpuModel()))
        masterList.add(DeviceInfo("Number of Cores", cpuInfoHelper.getNumberOfCores()))
        masterList.add(DeviceInfo("Screen Resolution", displayInfoHelper.getScreenResolution()))
        masterList.add(DeviceInfo("Refresh Rate", displayInfoHelper.getRefreshRate()))
        masterList.add(DeviceInfo("Device Model", deviceInfoHelper.getDeviceModel()))

        // --- Add a header for the sensors section ---
        // We can reuse the DeviceInfo model for a simple title
        masterList.add(DeviceInfo("--- ALL SENSORS ---", "")) 

        // --- Add all the detailed sensor info ---
        val sensorList = sensorInfoHelper.getSensorDetailsList()
        masterList.addAll(sensorList)

        // Create and set our new MasterAdapter
        val adapter = MasterAdapter(masterList)
        recyclerView.adapter = adapter
    }
}

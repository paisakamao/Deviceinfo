package com.deviceinfo.deviceinfoapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deviceinfo.deviceinfoapp.adapter.DeviceInfoAdapter
import com.deviceinfo.deviceinfoapp.model.DeviceInfo
// Import BOTH helpers
import com.deviceinfo.deviceinfoapp.utils.BatteryInfoHelper
import com.deviceinfo.deviceinfoapp.utils.DeviceInfoHelper 

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Create an instance of each helper
        val deviceInfoHelper = DeviceInfoHelper(this)
        val batteryInfoHelper = BatteryInfoHelper(this)
        
        val deviceInfoList = mutableListOf<DeviceInfo>()

        // --- Get info from deviceInfoHelper ---
        deviceInfoList.add(DeviceInfo("Total RAM", deviceInfoHelper.getTotalRam()))
        deviceInfoList.add(DeviceInfo("Used RAM", deviceInfoHelper.getUsedRam()))
        deviceInfoList.add(DeviceInfo("Free RAM", deviceInfoHelper.getAvailableRam()))
        deviceInfoList.add(DeviceInfo("Total Internal Storage", deviceInfoHelper.getTotalInternalStorage()))
        deviceInfoList.add(DeviceInfo("Available Internal Storage", deviceInfoHelper.getAvailableInternalStorage()))
        deviceInfoList.add(DeviceInfo("Internal Storage Used", deviceInfoHelper.getInternalStorageUsagePercentage()))
        deviceInfoList.add(DeviceInfo("Device Model", deviceInfoHelper.getDeviceModel()))
        // ...and so on for the rest of the methods in DeviceInfoHelper.kt

        // --- Get info from batteryInfoHelper ---
        deviceInfoList.add(DeviceInfo("Battery Level", batteryInfoHelper.getBatteryPercentage()))
        deviceInfoList.add(DeviceInfo("Battery Temperature", batteryInfoHelper.getBatteryTemperature()))
        deviceInfoList.add(DeviceInfo("Battery Voltage", batteryInfoHelper.getBatteryVoltage()))

        // You can add the rest of the device info calls here
        deviceInfoList.add(DeviceInfo("Manufacturer", deviceInfoHelper.getManufacturer()))
        deviceInfoList.add(DeviceInfo("Android Version", deviceInfoHelper.getAndroidVersion()))
        deviceInfoList.add(DeviceInfo("SDK Version", deviceInfoHelper.getSDKVersion()))
        deviceInfoList.add(DeviceInfo("CPU Info", deviceInfoHelper.getCpuInfo()))
        deviceInfoList.add(DeviceInfo("Screen Resolution", deviceInfoHelper.getScreenResolution()))
        deviceInfoList.add(DeviceInfo("Screen Density", deviceInfoHelper.getScreenDensity()))
        
        val adapter = DeviceInfoAdapter(deviceInfoList)
        recyclerView.adapter = adapter
    }
}

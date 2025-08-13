package com.deviceinfo.deviceinfoapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deviceinfo.deviceinfoapp.adapter.DeviceInfoAdapter
import com.deviceinfo.deviceinfoapp.model.DeviceInfo
// Import the new helper
import com.deviceinfo.deviceinfoapp.utils.BatteryInfoHelper
import com.deviceinfo.deviceinfoapp.utils.DeviceInfoHelper 

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Create instances of our helpers
        val deviceInfoHelper = DeviceInfoHelper(this)
        val batteryInfoHelper = BatteryInfoHelper(this) // Create an instance of the battery helper
        
        val deviceInfoList = mutableListOf<DeviceInfo>()

        // --- RAM Section ---
        deviceInfoList.add(DeviceInfo("Total RAM", deviceInfoHelper.getTotalRam()))
        deviceInfoList.add(DeviceInfo("Used RAM", deviceInfoHelper.getUsedRam()))
        deviceInfoList.add(DeviceInfo("Free RAM", deviceInfoHelper.getAvailableRam()))

        // --- Storage Section ---
        deviceInfoList.add(DeviceInfo("Total Internal Storage", deviceInfoHelper.getTotalInternalStorage()))
        deviceInfoList.add(DeviceInfo("Available Internal Storage", deviceInfoHelper.getAvailableInternalStorage()))
        deviceInfoList.add(DeviceInfo("Internal Storage Used", deviceInfoHelper.getInternalStorageUsagePercentage()))
        
        // --- Battery Section (using the correct helper) ---
        deviceInfoList.add(DeviceInfo("Battery Level", batteryInfoHelper.getBatteryPercentage()))
        deviceInfoList.add(DeviceInfo("Battery Temperature", batteryInfoHelper.getBatteryTemperature()))
        deviceInfoList.add(DeviceInfo("Battery Voltage", batteryInfoHelper.getBatteryVoltage()))

        // --- Device & OS Section ---
        deviceInfoList.add(DeviceInfo("Device Model", deviceInfoHelper.getDeviceModel()))
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

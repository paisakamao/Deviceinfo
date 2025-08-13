package com.deviceinfo.deviceinfoapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deviceinfo.deviceinfoapp.adapter.DeviceInfoAdapter
import com.deviceinfo.deviceinfoapp.model.DeviceInfo
import com.deviceinfo.deviceinfoapp.utils.BatteryInfoHelper
import com.deviceinfo.deviceinfoapp.utils.DeviceInfoHelper
import com.deviceinfo.deviceinfoapp.utils.RamInfoHelper
import com.deviceinfo.deviceinfoapp.utils.StorageInfoHelper

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Create instances of all our helpers
        val deviceInfoHelper = DeviceInfoHelper(this)
        val ramInfoHelper = RamInfoHelper(this)
        val storageInfoHelper = StorageInfoHelper(this)
        val batteryInfoHelper = BatteryInfoHelper(this)
        
        val deviceInfoList = mutableListOf<DeviceInfo>()

        // --- RAM Section ---
        deviceInfoList.add(DeviceInfo("Total RAM", ramInfoHelper.getTotalRam()))
        deviceInfoList.add(DeviceInfo("Used RAM", ramInfoHelper.getUsedRam()))
        deviceInfoList.add(DeviceInfo("Free RAM", ramInfoHelper.getAvailableRam()))

        // --- Storage Section ---
        deviceInfoList.add(DeviceInfo("Total Internal Storage", storageInfoHelper.getTotalInternalStorage()))
        deviceInfoList.add(DeviceInfo("Available Internal Storage", storageInfoHelper.getAvailableInternalStorage()))
        deviceInfoList.add(DeviceInfo("Internal Storage Used", storageInfoHelper.getInternalStorageUsagePercentage()))
        
        // --- Battery Section ---
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

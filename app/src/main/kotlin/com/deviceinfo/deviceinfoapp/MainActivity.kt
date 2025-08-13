package com.deviceinfo.deviceinfoapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deviceinfo.deviceinfoapp.adapter.DeviceInfoAdapter
import com.deviceinfo.deviceinfoapp.model.DeviceInfo
import com.deviceinfo.deviceinfoapp.utils.DeviceInfoHelper

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Assuming your activity_main.xml has a RecyclerView with the id 'recyclerView'
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val deviceInfoHelper = DeviceInfoHelper(this)
        val deviceInfoList = mutableListOf<DeviceInfo>()

        // Get Device Information
        deviceInfoList.add(DeviceInfo("Device Model", deviceInfoHelper.getDeviceModel()))
        deviceInfoList.add(DeviceInfo("Manufacturer", deviceInfoHelper.getManufacturer()))
        deviceInfoList.add(DeviceInfo("Android Version", deviceInfoHelper.getAndroidVersion()))
        deviceInfoList.add(DeviceInfo("SDK Version", deviceInfoHelper.getSDKVersion()))
        deviceInfoList.add(DeviceInfo("RAM", deviceInfoHelper.getTotalRam()))
        deviceInfoList.add(DeviceInfo("CPU", deviceInfoHelper.getCpuInfo()))
        deviceInfoList.add(DeviceInfo("Screen Resolution", deviceInfoHelper.getScreenResolution()))
        deviceInfoList.add(DeviceInfo("Screen Density", deviceInfoHelper.getScreenDensity()))
        deviceInfoList.add(DeviceInfo("Total Internal Storage", deviceInfoHelper.getTotalInternalStorage()))
        deviceInfoList.add(DeviceInfo("Available Internal Storage", deviceInfoHelper.getAvailableInternalStorage()))

        val adapter = DeviceInfoAdapter(deviceInfoList)
        recyclerView.adapter = adapter
    }
}

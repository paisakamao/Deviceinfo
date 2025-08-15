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
        val batteryInfoHelper = BatteryInfoHelper(this) // The one, definitive battery helper
        val cpuInfoHelper = CpuInfoHelper()
        val displayInfoHelper = DisplayInfoHelper(this)
        val sensorInfoHelper = SensorInfoHelper(this)
        val appInfoHelper = AppInfoHelper(this)
        val systemInfoHelper = SystemInfoHelper()
        
        val deviceInfoList = mutableListOf<DeviceInfo>()

        deviceInfoList.add(DeviceInfo("Battery Level", batteryInfoHelper.getBatteryPercentageForDashboard()))
        // Add other dashboard items as you see fit...
        
        val adapter = DeviceInfoAdapter(deviceInfoList)
        
        adapter.onItemClick = { deviceInfo ->
            if (deviceInfo.label == "Battery Level") {
                startActivity(Intent(this, BatteryDetailActivity::class.java))
            }
            // Add other navigation clicks here
        }
        
        recyclerView.adapter = adapter
    }
}

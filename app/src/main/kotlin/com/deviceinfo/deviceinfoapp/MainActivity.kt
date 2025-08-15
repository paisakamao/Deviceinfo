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
        val cpuInfoHelper = CpuInfoHelper()
        val displayInfoHelper = DisplayInfoHelper(this)
        val sensorInfoHelper = SensorInfoHelper(this)
        val appInfoHelper = AppInfoHelper(this)
        val systemInfoHelper = SystemInfoHelper()
        val dashboardHelper = DashboardHelper(this)
        
        val deviceInfoList = mutableListOf<DeviceInfo>()
        deviceInfoList.add(DeviceInfo("Battery Details", dashboardHelper.getBatteryPercentage()))
        // Add other dashboard items...
        
        val adapter = DeviceInfoAdapter(deviceInfoList)
        adapter.onItemClick = { deviceInfo ->
            if (deviceInfo.label == "Battery Details") {
                startActivity(Intent(this, BatteryDetailActivity::class.java))
            }
        }
        recyclerView.adapter = adapter
    }
}
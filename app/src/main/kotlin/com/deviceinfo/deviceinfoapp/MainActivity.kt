package com.deviceinfo.deviceinfoapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deviceinfo.deviceinfoapp.adapter.DeviceInfoAdapter
import com.deviceinfo.deviceinfoapp.model.DeviceInfo
import com.deviceinfo.deviceinfoapp.utils.*

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: DeviceInfoAdapter
    private lateinit var deviceInfoList: MutableList<DeviceInfo>
    private lateinit var batteryCurrentHelper: BatteryCurrentHelper
    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval = 1000L // 1 second

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Helpers
        val deviceInfoHelper = DeviceInfoHelper(this)
        val batteryInfoHelper = BatteryInfoHelper(this)
        val cpuInfoHelper = CpuInfoHelper()
        val displayInfoHelper = DisplayInfoHelper(this)
        val sensorInfoHelper = SensorInfoHelper(this)
        val appInfoHelper = AppInfoHelper(this)
        val systemInfoHelper = SystemInfoHelper()
        batteryCurrentHelper = BatteryCurrentHelper(this)

        deviceInfoList = mutableListOf(
            DeviceInfo("Total RAM", deviceInfoHelper.getTotalRam()),
            DeviceInfo("Internal Storage Used", deviceInfoHelper.getInternalStorageUsagePercentage()),
            DeviceInfo("Battery Level", batteryInfoHelper.getBatteryPercentageForDashboard()),
            DeviceInfo("Battery Current (mA)", "Loading..."),
            DeviceInfo("Battery Power (W)", "Loading..."),
            DeviceInfo("CPU Model", cpuInfoHelper.getCpuModel()),
            DeviceInfo("Screen Resolution", displayInfoHelper.getScreenResolution()),
            DeviceInfo("Root Status", systemInfoHelper.getRootStatus()),
            DeviceInfo("Total Sensors", sensorInfoHelper.getSensorDetailsList().size.toString()),
            DeviceInfo("All Apps", appInfoHelper.getAllAppsDetails().size.toString())
        )

        adapter = DeviceInfoAdapter(deviceInfoList)
        recyclerView.adapter = adapter

        adapter.onItemClick = { deviceInfo ->
            when (deviceInfo.label) {
                "Total Sensors" -> startActivity(Intent(this, SensorListActivity::class.java))
                "All Apps" -> startActivity(Intent(this, AppListActivity::class.java))
                "Battery Level" -> startActivity(Intent(this, BatteryDetailActivity::class.java))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        handler.post(updateRunnable)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(updateRunnable)
    }

    private val updateRunnable = object : Runnable {
        override fun run() {
            val currentMa = batteryCurrentHelper.getBatteryCurrentMa()
            val powerW = batteryCurrentHelper.getBatteryPowerW()

            // Update the list
            updateDeviceInfo("Battery Current (mA)", "${String.format("%.0f", currentMa)} mA")
            updateDeviceInfo("Battery Power (W)", "${String.format("%.2f", powerW)} W")

            handler.postDelayed(this, updateInterval)
        }
    }

    private fun updateDeviceInfo(label: String, value: String) {
        val index = deviceInfoList.indexOfFirst { it.label == label }
        if (index != -1) {
            deviceInfoList[index] = DeviceInfo(label, value)
            adapter.notifyItemChanged(index)
        }
    }
}

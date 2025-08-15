package com.deviceinfo.deviceinfoapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deviceinfo.deviceinfoapp.adapter.DeviceInfoAdapter
import com.deviceinfo.deviceinfoapp.utils.BatteryInfoHelper

class BatteryDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_battery_detail)

        supportActionBar?.title = "Battery Details"

        val recyclerView: RecyclerView = findViewById(R.id.batteryDetailRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val batteryInfoHelper = BatteryInfoHelper(this)
        // We will create this new function in the helper
        val batteryDetailsList = batteryInfoHelper.getBatteryDetailsList()

        // We can reuse our simple DeviceInfoAdapter for this key-value list
        val adapter = DeviceInfoAdapter(batteryDetailsList)
        recyclerView.adapter = adapter
    }
}

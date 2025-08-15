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
        supportActionBar?.title = "Battery Diagnostics"

        val recyclerView: RecyclerView = findViewById(R.id.batteryDetailRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val batteryInfoHelper = BatteryInfoHelper(this)
        
        // Get the raw diagnostic data from our new helper
        val diagnosticData = batteryInfoHelper.getDiagnosticBatteryDetails()
        
        // Display the data using our simple adapter
        val adapter = DeviceInfoAdapter(diagnosticData)
        recyclerView.adapter = adapter
    }
}

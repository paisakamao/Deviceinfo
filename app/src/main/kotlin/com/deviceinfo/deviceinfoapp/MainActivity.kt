package com.deviceinfo.deviceinfoapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deviceinfo.deviceinfoapp.adapter.SensorListAdapter
import com.deviceinfo.deviceinfoapp.utils.SensorInfoHelper

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 1. Create the helper
        val sensorInfoHelper = SensorInfoHelper(this)

        // 2. Get the detailed list of sensors
        val sensorList = sensorInfoHelper.getSensorDetailsList()

        // 3. Create and set the new SensorListAdapter
        val adapter = SensorListAdapter(sensorList)
        recyclerView.adapter = adapter
    }
}

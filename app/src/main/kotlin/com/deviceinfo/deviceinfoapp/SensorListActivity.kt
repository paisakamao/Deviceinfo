package com.deviceinfo.deviceinfoapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deviceinfo.deviceinfoapp.adapter.SensorListAdapter
import com.deviceinfo.deviceinfoapp.utils.SensorInfoHelper

class SensorListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // We will create this layout file in the next step
        setContentView(R.layout.activity_sensor_list) 

        // Set the title of the action bar for this screen
        supportActionBar?.title = "All Sensors"

        val recyclerView: RecyclerView = findViewById(R.id.sensorRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val sensorInfoHelper = SensorInfoHelper(this)
        val sensorList = sensorInfoHelper.getSensorDetailsList()

        // We will create this adapter in a later step
        val adapter = SensorListAdapter(sensorList)
        recyclerView.adapter = adapter
    }
}

package com.deviceinfo.deviceinfoapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.deviceinfo.deviceinfoapp.R
import com.deviceinfo.deviceinfoapp.model.SensorInfo

class SensorListAdapter(private val sensorList: List<SensorInfo>) :
    RecyclerView.Adapter<SensorListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.sensorNameTextView)
        val vendorTextView: TextView = view.findViewById(R.id.sensorVendorTextView)
        val typeTextView: TextView = view.findViewById(R.id.sensorTypeTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sensor_info, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sensor = sensorList[position]
        holder.nameTextView.text = sensor.name
        holder.vendorTextView.text = "Vendor: ${sensor.vendor}"
        holder.typeTextView.text = "Type: ${sensor.type}"
    }

    override fun getItemCount() = sensorList.size
}

package com.deviceinfo.deviceinfoapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.deviceinfo.deviceinfoapp.R
import com.deviceinfo.deviceinfoapp.model.DeviceInfo
import com.deviceinfo.deviceinfoapp.model.SensorInfo

class MasterAdapter(private val items: List<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Define integer constants for our two view types
    companion object {
        private const val TYPE_DEVICE_INFO = 0
        private const val TYPE_SENSOR_INFO = 1
    }

    // ViewHolder for the simple key-value layout
    inner class DeviceInfoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val labelTextView: TextView = view.findViewById(R.id.labelTextView)
        val valueTextView: TextView = view.findViewById(R.id.valueTextView)
    }

    // ViewHolder for the detailed sensor layout
    inner class SensorInfoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.sensorNameTextView)
        val vendorTextView: TextView = view.findViewById(R.id.sensorVendorTextView)
        val typeTextView: TextView = view.findViewById(R.id.sensorTypeTextView)
    }

    /**
     * This is the most important method. It checks the type of the item
     * at a given position and returns the corresponding view type constant.
     */
    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is DeviceInfo -> TYPE_DEVICE_INFO
            is SensorInfo -> TYPE_SENSOR_INFO
            else -> throw IllegalArgumentException("Invalid type of data at position $position")
        }
    }

    /**
     * This method uses the viewType to inflate the correct layout file.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_DEVICE_INFO -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_device_info, parent, false)
                DeviceInfoViewHolder(view)
            }
            TYPE_SENSOR_INFO -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_sensor_info, parent, false)
                SensorInfoViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    /**
     * This method binds the data to the correct ViewHolder.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DeviceInfoViewHolder -> {
                val deviceInfo = items[position] as DeviceInfo
                holder.labelTextView.text = deviceInfo.label
                holder.valueTextView.text = deviceInfo.value
            }
            is SensorInfoViewHolder -> {
                val sensorInfo = items[position] as SensorInfo
                holder.nameTextView.text = sensorInfo.name
                holder.vendorTextView.text = "Vendor: ${sensorInfo.vendor}"
                holder.typeTextView.text = "Type: ${sensorInfo.type}"
            }
        }
    }

    override fun getItemCount() = items.size
}

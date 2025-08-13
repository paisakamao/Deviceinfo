package com.deviceinfo.deviceinfoapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.deviceinfo.deviceinfoapp.R
import com.deviceinfo.deviceinfoapp.model.DeviceInfo

class DeviceInfoAdapter(private val deviceInfoList: List<DeviceInfo>) :
    RecyclerView.Adapter<DeviceInfoAdapter.ViewHolder>() {

    var onItemClick: ((DeviceInfo) -> Unit)? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val labelTextView: TextView = view.findViewById(R.id.labelTextView)
        val valueTextView: TextView = view.findViewById(R.id.valueTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device_info, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val deviceInfo = deviceInfoList[position]
        holder.labelTextView.text = deviceInfo.label
        holder.valueTextView.text = deviceInfo.value
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(deviceInfo)
        }
    }

    override fun getItemCount() = deviceInfoList.size
}

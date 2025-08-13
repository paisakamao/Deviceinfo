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

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val labelTextView: TextView = view.findViewById(R.id.labelTextView)
        val valueTextView: TextView = view.findViewById(R.id.valueTextView)
    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent an item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device_info, parent, false)
        return ViewHolder(view)
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * This method updates the contents of the ViewHolder's view to reflect the item at the given position.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val deviceInfo = deviceInfoList[position]
        holder.labelTextView.text = deviceInfo.label
        holder.valueTextView.text = deviceInfo.value
    }

    /**
     * Return the size of your dataset (invoked by the layout manager)
     */
    override fun getItemCount() = deviceInfoList.size
}

package com.deviceinfo.deviceinfoapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.deviceinfo.deviceinfoapp.R
import com.deviceinfo.deviceinfoapp.model.AppInfo

class AppListAdapter(private val appList: List<AppInfo>) :
    RecyclerView.Adapter<AppListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iconImageView: ImageView = view.findViewById(R.id.appIconImageView)
        val nameTextView: TextView = view.findViewById(R.id.appNameTextView)
        val packageTextView: TextView = view.findViewById(R.id.appPackageTextView)
        // Find the new TextViews
        val versionAndSizeTextView: TextView = view.findViewById(R.id.appVersionAndSizeTextView)
        val sourceTextView: TextView = view.findViewById(R.id.appSourceTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_app_info, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appInfo = appList[position]
        holder.iconImageView.setImageDrawable(appInfo.icon)
        holder.nameTextView.text = appInfo.appName
        holder.packageTextView.text = appInfo.packageName
        // Set the text for the new TextViews
        holder.versionAndSizeTextView.text = "Version: ${appInfo.versionName} | Size: ${appInfo.appSize}"
        holder.sourceTextView.text = "Source: ${appInfo.installerSource}"
    }

    override fun getItemCount() = appList.size
}
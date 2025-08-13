package com.deviceinfo.deviceinfoapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deviceinfo.deviceinfoapp.adapter.AppListAdapter
import com.deviceinfo.deviceinfoapp.utils.AppInfoHelper

class AppListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_list)

        supportActionBar?.title = "User Installed Apps"

        val recyclerView: RecyclerView = findViewById(R.id.appRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val appInfoHelper = AppInfoHelper(this)
        val appList = appInfoHelper.getInstalledAppsDetails()

        val adapter = AppListAdapter(appList)
        recyclerView.adapter = adapter
    }
}

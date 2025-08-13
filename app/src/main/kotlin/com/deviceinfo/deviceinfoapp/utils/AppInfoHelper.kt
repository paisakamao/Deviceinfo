package com.deviceinfo.deviceinfoapp.utils

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.deviceinfo.deviceinfoapp.model.AppInfo

class AppInfoHelper(private val context: Context) {

    // Get all applications once and store them.
    // THIS IS THE ONE-LINE FIX: We now use getInstalledPackages for a complete list.
    private val allInstalledApps: List<ApplicationInfo> by lazy {
        context.packageManager.getInstalledPackages(0).mapNotNull { it.applicationInfo }
    }

    // A function to get the full, detailed list based on a filter
    private fun getApps(filter: (ApplicationInfo) -> Boolean): List<AppInfo> {
        return allInstalledApps
            .filter(filter)
            .map { appInfo ->
                AppInfo(
                    appName = context.packageManager.getApplicationLabel(appInfo).toString(),
                    packageName = appInfo.packageName,
                    icon = context.packageManager.getApplicationIcon(appInfo)
                )
            }.sortedBy { it.appName.lowercase() }
    }
    
    fun getAllAppsDetails(): List<AppInfo> {
        // A simple filter that accepts everything
        return getApps { true }
    }

    fun getUserAppsDetails(): List<AppInfo> {
        // Filter for non-system apps that are enabled
        return getApps { (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 && it.enabled }
    }

    fun getSystemAppsDetails(): List<AppInfo> {
        // Filter for system apps that are enabled
        return getApps { (it.flags and ApplicationInfo.FLAG_SYSTEM) != 0 && it.enabled }
    }

    fun getDisabledAppsDetails(): List<AppInfo> {
        // Filter for any app that is disabled
        return getApps { !it.enabled }
    }
}
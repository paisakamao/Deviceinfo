package com.deviceinfo.deviceinfoapp.utils

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.deviceinfo.deviceinfoapp.model.AppInfo

class AppInfoHelper(private val context: Context) {

    /**
     * Gets a detailed list of all user-installed applications, sorted alphabetically.
     */
    fun getInstalledAppsDetails(): List<AppInfo> {
        val packageManager = context.packageManager
        // Get a list of all installed packages from the system
        val allApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        
        // Filter this list to include only non-system apps
        val userApps = allApps.filter { (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 }

        // Convert the filtered list into our own AppInfo data model
        return userApps.map { appInfo ->
            AppInfo(
                appName = packageManager.getApplicationLabel(appInfo).toString(),
                packageName = appInfo.packageName,
                icon = packageManager.getApplicationIcon(appInfo)
            )
        }.sortedBy { it.appName.lowercase() } // Sort the final list alphabetically
    }

    /**
     * Returns the total number of user-installed applications as a string.
     */
    fun getUserAppCount(): String {
        val packageManager = context.packageManager
        val allApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        val userApps = allApps.filter { (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 }
        return userApps.size.toString()
    }
}
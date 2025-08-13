package com.deviceinfo.deviceinfoapp.utils

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager

class AppInfoHelper(private val context: Context) {

    /**
     * Gets a list of all installed applications.
     * We are filtering out system apps to get a count similar to what users expect.
     */
    private fun getInstalledApps(): List<ApplicationInfo> {
        val packageManager = context.packageManager
        // Get a list of all installed packages
        val allApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

        // Filter out system apps, which are not typically shown in user-facing lists
        return allApps.filter { (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 }
    }

    /**
     * Returns the total number of user-installed applications.
     */
    fun getUserAppCount(): String {
        return getInstalledApps().size.toString()
    }

    // Later, we could create a function to return the full list with names and icons.
}

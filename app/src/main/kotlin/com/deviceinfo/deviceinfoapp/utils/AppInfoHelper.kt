package com.deviceinfo.deviceinfoapp.utils

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.deviceinfo.deviceinfoapp.model.AppInfo
import java.io.File

class AppInfoHelper(private val context: Context) {

    private val allInstalledApps: List<ApplicationInfo> by lazy {
        context.packageManager.getInstalledPackages(0).mapNotNull { it.applicationInfo }
    }

    // A private helper to get the readable name of the installer
    private fun getInstallerSourceName(packageName: String): String {
        val installerPackage = try {
            context.packageManager.getInstallerPackageName(packageName)
        } catch (e: Exception) {
            return "Unknown"
        }
        return when (installerPackage) {
            "com.android.vending" -> "Google Play Store"
            "com.amazon.venezia" -> "Amazon Appstore"
            null -> "Sideloaded / System"
            else -> installerPackage // Show the package name for other stores
        }
    }
    
    // A private helper to get the size of the app's APK file
    private fun getAppSize(appInfo: ApplicationInfo): String {
        return try {
            val file = File(appInfo.sourceDir)
            FormattingUtils.formatSize(file.length())
        } catch (e: Exception) {
            "N/A"
        }
    }

    private fun getApps(filter: (ApplicationInfo) -> Boolean): List<AppInfo> {
        return allInstalledApps
            .filter(filter)
            .mapNotNull { appInfo ->
                try {
                    val packageInfo = context.packageManager.getPackageInfo(appInfo.packageName, 0)
                    AppInfo(
                        appName = context.packageManager.getApplicationLabel(appInfo).toString(),
                        packageName = appInfo.packageName,
                        icon = context.packageManager.getApplicationIcon(appInfo),
                        versionName = packageInfo.versionName ?: "N/A",
                        appSize = getAppSize(appInfo),
                        installerSource = getInstallerSourceName(appInfo.packageName)
                    )
                } catch (e: PackageManager.NameNotFoundException) {
                    null // Skip if the package is somehow uninstalled during the process
                }
            }.sortedBy { it.appName.lowercase() }
    }
    
    fun getAllAppsDetails(): List<AppInfo> = getApps { true }

    fun getUserAppsDetails(): List<AppInfo> = getApps { (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 && it.enabled }

    fun getSystemAppsDetails(): List<AppInfo> = getApps { (it.flags and ApplicationInfo.FLAG_SYSTEM) != 0 && it.enabled }

    fun getDisabledAppsDetails(): List<AppInfo> = getApps { !it.enabled }
}
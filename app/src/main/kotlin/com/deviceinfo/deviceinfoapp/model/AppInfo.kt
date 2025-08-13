package com.deviceinfo.deviceinfoapp.model

import android.graphics.drawable.Drawable

/**
 * An updated data class to hold detailed info for a single installed application.
 */
data class AppInfo(
    val appName: String,
    val packageName: String,
    val icon: Drawable,
    val versionName: String,
    val appSize: String,
    val installerSource: String
)
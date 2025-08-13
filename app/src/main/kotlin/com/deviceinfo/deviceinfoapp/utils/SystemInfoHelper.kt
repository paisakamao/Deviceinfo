package com.deviceinfo.deviceinfoapp.utils

import java.io.File

class SystemInfoHelper {

    /**
     * Gets the Linux kernel version from the system.
     */
    fun getKernelVersion(): String {
        return System.getProperty("os.version") ?: "N/A"
    }

    /**
     * Gets the Android Build ID, which represents the specific system build.
     */
    fun getBuildId(): String {
        return android.os.Build.DISPLAY
    }

    /**
     * Checks for root access by looking for common superuser binaries.
     * Note: This is a basic check and can sometimes be fooled, but it's a good starting point.
     */
    fun getRootStatus(): String {
        val paths = arrayOf(
            "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su",
            "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
            "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su"
        )
        for (path in paths) {
            if (File(path).exists()) {
                return "Rooted"
            }
        }
        return "Not Rooted"
    }

    /**
     * Gets the version of the Java Virtual Machine (ART on modern Android).
     */
    fun getJavaVmVersion(): String {
        return System.getProperty("java.vm.version") ?: "N/A"
    }
}
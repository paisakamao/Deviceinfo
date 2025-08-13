package com.deviceinfo.deviceinfoapp.utils

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.util.DisplayMetrics
import android.view.WindowManager
import java.io.File
import java.io.IOException
import java.text.DecimalFormat

class DeviceInfoHelper(private val context: Context) {

    // --- NEW: A single place to get memory info to avoid repeating code ---
    private fun getMemoryInfo(): ActivityManager.MemoryInfo {
        val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return ActivityManager.MemoryInfo().also { actManager.getMemoryInfo(it) }
    }

    /**
     * Gets the total RAM of the device.
     */
    fun getTotalRam(): String {
        return formatSize(getMemoryInfo().totalMem)
    }

    /**
     * NEW: Gets the available ("free") RAM.
     */
    fun getAvailableRam(): String {
        return formatSize(getMemoryInfo().availMem)
    }

    /**
     * NEW: Calculates and returns the used RAM.
     */
    fun getUsedRam(): String {
        val memInfo = getMemoryInfo()
        val usedMem = memInfo.totalMem - memInfo.availMem
        return formatSize(usedMem)
    }

    /**
     * Gets the device model.
     */
    fun getDeviceModel(): String = Build.MODEL

    /**
     * Gets the device manufacturer.
     */
    fun getManufacturer(): String = Build.MANUFACTURER

    /**
     * Gets the Android version name (e.g., "12").
     */
    fun getAndroidVersion(): String = Build.VERSION.RELEASE

    /**
     * Gets the Android SDK version code (e.g., 31).
     */
    fun getSDKVersion(): String = Build.VERSION.SDK_INT.toString()

    /**
     * Gets CPU information by reading /proc/cpuinfo.
     */
    fun getCpuInfo(): String {
        return try {
            val file = File("/proc/cpuinfo")
            val text = file.readText()
            val modelNameLine = text.lines().find { it.startsWith("model name") }
            val hardwareLine = text.lines().find { it.startsWith("Hardware") }
            modelNameLine?.substringAfter(":")?.trim() ?: hardwareLine?.substringAfter(":")?.trim() ?: "N/A"
        } catch (e: IOException) {
            e.printStackTrace()
            "N/A"
        }
    }

    /**
     * Gets the screen resolution in pixels. This is now safe for all API levels.
     */
    fun getScreenResolution(): String {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val metrics = windowManager.currentWindowMetrics
            val width = metrics.bounds.width()
            val height = metrics.bounds.height()
            return "${height} x ${width}"
        } else {
            @Suppress("DEPRECATION")
            val display = windowManager.defaultDisplay
            @Suppress("DEPRECATION")
            val metrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            display.getMetrics(metrics)
            return "${metrics.heightPixels} x ${metrics.widthPixels}"
        }
    }

    /**
     * Gets the screen density in DPI.
     */
    fun getScreenDensity(): String {
        val metrics = context.resources.displayMetrics
        return "${metrics.densityDpi} dpi"
    }

    /**
     * Gets the total internal storage size.
     */
    fun getTotalInternalStorage(): String {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSizeLong
        val totalBlocks = stat.blockCountLong
        return formatSize(totalBlocks * blockSize)
    }

    /**
     * Gets the available internal storage size.
     */
    fun getAvailableInternalStorage(): String {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSizeLong
        val availableBlocks = stat.availableBlocksLong
        return formatSize(availableBlocks * blockSize)
    }

    /**
     * Helper function to format size in bytes to KB, MB, GB, etc.
     */
    private fun formatSize(size: Long): String {
        if (size <= 0) return "0"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
        return DecimalFormat("#,##0.#").format(size / Math.pow(1024.0, digitGroups.toDouble())) + " " + units[digitGroups]
    }
}

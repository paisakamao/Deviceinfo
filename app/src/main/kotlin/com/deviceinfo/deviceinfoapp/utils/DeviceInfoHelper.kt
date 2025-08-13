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
import java.io.RandomAccessFile
import java.text.DecimalFormat
import java.util.regex.Pattern

class DeviceInfoHelper(private val context: Context) {

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
     * Gets the total RAM of the device.
     */
    fun getTotalRam(): String {
        val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        actManager.getMemoryInfo(memInfo)
        return formatSize(memInfo.totalMem)
    }

    /**
     * Gets CPU information by reading /proc/cpuinfo.
     */
    fun getCpuInfo(): String {
        return try {
            val file = File("/proc/cpuinfo")
            val text = file.readText()
            // Extract the "model name" or "Hardware" line
            val modelNameLine = text.lines().find { it.startsWith("model name") }
            val hardwareLine = text.lines().find { it.startsWith("Hardware") }
            modelNameLine?.substringAfter(":")?.trim() ?: hardwareLine?.substringAfter(":")?.trim() ?: "N/A"
        } catch (e: IOException) {
            e.printStackTrace()
            "N/A"
        }
    }

    /**
     * Gets the screen resolution in pixels.
     */
    fun getScreenResolution(): String {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        return "${metrics.heightPixels} x ${metrics.widthPixels}"
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

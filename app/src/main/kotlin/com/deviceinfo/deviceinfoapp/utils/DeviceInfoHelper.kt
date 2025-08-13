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
import kotlin.math.log10
import kotlin.math.pow

class DeviceInfoHelper(private val context: Context) {

    private fun getMemoryInfo(): ActivityManager.MemoryInfo {
        val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return ActivityManager.MemoryInfo().also { actManager.getMemoryInfo(it) }
    }

    fun getTotalRam(): String {
        return formatSize(getMemoryInfo().totalMem)
    }

    fun getAvailableRam(): String {
        return formatSize(getMemoryInfo().availMem)
    }

    fun getUsedRam(): String {
        val memInfo = getMemoryInfo()
        val usedMem = memInfo.totalMem - memInfo.availMem
        return formatSize(usedMem)
    }

    fun getDeviceModel(): String = Build.MODEL

    fun getManufacturer(): String = Build.MANUFACTURER

    fun getAndroidVersion(): String = Build.VERSION.RELEASE

    fun getSDKVersion(): String = Build.VERSION.SDK_INT.toString()

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

    fun getScreenDensity(): String {
        val metrics = context.resources.displayMetrics
        return "${metrics.densityDpi} dpi"
    }
    
    // --- NEW HELPER FOR STORAGE ---
    private fun getInternalStorageStatFs(): StatFs {
        val path = Environment.getDataDirectory()
        return StatFs(path.path)
    }

    fun getTotalInternalStorage(): String {
        val stat = getInternalStorageStatFs()
        val totalBytes = stat.blockCountLong * stat.blockSizeLong
        return formatSize(totalBytes)
    }

    fun getAvailableInternalStorage(): String {
        val stat = getInternalStorageStatFs()
        val availableBytes = stat.availableBlocksLong * stat.blockSizeLong
        return formatSize(availableBytes)
    }

    /**
     * NEW: Calculates the internal storage usage percentage.
     */
    fun getInternalStorageUsagePercentage(): String {
        val stat = getInternalStorageStatFs()
        val totalBytes = stat.blockCountLong * stat.blockSizeLong
        val availableBytes = stat.availableBlocksLong * stat.blockSizeLong
        val usedBytes = totalBytes - availableBytes
        
        if (totalBytes <= 0) return "0%"
        
        val percentage = (usedBytes * 100.0 / totalBytes).toInt()
        return "$percentage%"
    }

    private fun formatSize(size: Long): String {
        if (size <= 0) return "0"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
        return DecimalFormat("#,##0.#").format(size / 1024.0.pow(digitGroups.toDouble())) + " " + units[digitGroups]
    }
}

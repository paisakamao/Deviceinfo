package com.deviceinfo.deviceinfoapp.utils

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
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

    fun getUsedRam(): String {
        val memInfo = getMemoryInfo()
        val usedMem = memInfo.totalMem - memInfo.availMem
        return formatSize(usedMem)
    }
    
    fun getInternalStorageUsagePercentage(): String {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val totalBytes = stat.blockCountLong * stat.blockSizeLong
        val availableBytes = stat.availableBlocksLong * stat.blockSizeLong
        val usedBytes = totalBytes - availableBytes
        if (totalBytes <= 0) return "0%"
        return "${(usedBytes * 100.0 / totalBytes).toInt()}%"
    }

    fun getDeviceModel(): String = Build.MODEL

    fun getAndroidVersion(): String = Build.VERSION.RELEASE

    private fun formatSize(size: Long): String {
        if (size <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
        return DecimalFormat("#,##0.#").format(size / 1024.0.pow(digitGroups.toDouble())) + " " + units[digitGroups]
    }
}
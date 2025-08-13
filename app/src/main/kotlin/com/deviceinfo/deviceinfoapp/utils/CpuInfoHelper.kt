package com.deviceinfo.deviceinfoapp.utils

import java.io.File
import java.io.IOException

class CpuInfoHelper {

    /**
     * Gets the CPU model name by reading /proc/cpuinfo.
     */
    fun getCpuModel(): String {
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
     * Gets the number of available processor cores.
     */
    fun getNumberOfCores(): String {
        return Runtime.getRuntime().availableProcessors().toString()
    }
}

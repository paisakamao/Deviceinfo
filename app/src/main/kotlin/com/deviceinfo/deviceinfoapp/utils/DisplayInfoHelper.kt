package com.deviceinfo.deviceinfoapp.utils

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager

class DisplayInfoHelper(private val context: Context) {

    /**
     * Gets the screen resolution in pixels. This is safe for all API levels.
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
     * Gets the screen refresh rate in Hz.
     */
    fun getRefreshRate(): String {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val refreshRate = display.refreshRate
        // Format to a whole number
        return "%.0f Hz".format(refreshRate)
    }
}

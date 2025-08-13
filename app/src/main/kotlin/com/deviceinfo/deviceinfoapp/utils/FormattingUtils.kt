package com.deviceinfo.deviceinfoapp.utils

import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow

/**
 * An object to hold common utility functions, like formatting sizes.
 * Using an 'object' makes its functions act like static methods.
 */
object FormattingUtils {

    /**
     * Helper function to format size in bytes to a human-readable string (KB, MB, GB, etc.).
     */
    fun formatSize(size: Long): String {
        if (size <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
        return DecimalFormat("#,##0.#").format(size / 1024.0.pow(digitGroups.toDouble())) + " " + units[digitGroups]
    }
}
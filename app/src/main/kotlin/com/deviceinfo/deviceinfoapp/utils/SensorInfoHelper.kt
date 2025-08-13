package com.deviceinfo.deviceinfoapp.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import com.deviceinfo.deviceinfoapp.model.SensorInfo

class SensorInfoHelper(private val context: Context) {

    fun getSensorDetailsList(): List<SensorInfo> {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val deviceSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)

        return deviceSensors.map { sensor ->
            SensorInfo(
                name = sensor.name,
                vendor = sensor.vendor,
                type = getSensorTypeString(sensor)
            )
        }
    }

    private fun getSensorTypeString(sensor: Sensor): String {
        // For modern Android versions (API 20+), use the official string type first.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            val officialType = sensor.stringType
            // If the official type is not null or empty, use it. It's the most accurate.
            if (!officialType.isNullOrEmpty()) {
                return officialType
            }
            // If the officialType is null or empty, it's likely a vendor-specific private sensor.
            // This handles the blank type issue.
            else {
                return "PRIVATE SENSOR (Type: ${sensor.type})"
            }
        }

        // For very old devices without stringType, fall back to our manual lookup table.
        return when (sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> "ACCELEROMETER"
            Sensor.TYPE_AMBIENT_TEMPERATURE -> "AMBIENT TEMPERATURE"
            Sensor.TYPE_GAME_ROTATION_VECTOR -> "GAME ROTATION VECTOR"
            Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR -> "GEOMAGNETIC ROTATION"
            Sensor.TYPE_GRAVITY -> "GRAVITY"
            Sensor.TYPE_GYROSCOPE -> "GYROSCOPE"
            Sensor.TYPE_GYROSCOPE_UNCALIBRATED -> "GYROSCOPE (Uncalibrated)"
            Sensor.TYPE_HEART_RATE -> "HEART RATE"
            Sensor.TYPE_LIGHT -> "LIGHT"
            Sensor.TYPE_LINEAR_ACCELERATION -> "LINEAR ACCELERATION"
            Sensor.TYPE_MAGNETIC_FIELD -> "MAGNETIC FIELD"
            Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED -> "MAGNETIC FIELD (Uncalibrated)"
            Sensor.TYPE_PRESSURE -> "PRESSURE"
            Sensor.TYPE_PROXIMITY -> "PROXIMITY"
            Sensor.TYPE_RELATIVE_HUMIDITY -> "RELATIVE HUMIDITY"
            Sensor.TYPE_ROTATION_VECTOR -> "ROTATION VECTOR"
            Sensor.TYPE_SIGNIFICANT_MOTION -> "SIGNIFICANT MOTION"
            Sensor.TYPE_STEP_COUNTER -> "STEP COUNTER"
            Sensor.TYPE_STEP_DETECTOR -> "STEP DETECTOR"
            @Suppress("DEPRECATION")
            Sensor.TYPE_ORIENTATION -> "ORIENTATION (Deprecated)"
            @Suppress("DEPRECATION")
            Sensor.TYPE_TEMPERATURE -> "TEMPERATURE (Deprecated)"
            else -> "CUSTOM / UNKNOWN (Type: ${sensor.type})"
        }
    }
}

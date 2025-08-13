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
        // For modern Android versions, there's a built-in method.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            // This returns a string like "com.google.sensor.some_private_sensor"
            val officialType = sensor.stringType
            // Let's make it look nicer by taking the last part and making it uppercase.
            return officialType.substringAfterLast('.').uppercase().replace("_", " ")
        }

        // For older versions, or to provide cleaner names, use our manual lookup table.
        // This list is now much more complete.
        return when (sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> "ACCELEROMETER"
            Sensor.TYPE_AMBIENT_TEMPERATURE -> "AMBIENT TEMPERATURE"
            Sensor.TYPE_GAME_ROTATION_VECTOR -> "GAME ROTATION VECTOR"
            Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR -> "GEOMAGNETIC ROTATION"
            Sensor.TYPE_GRAVITY -> "GRAVITY"
            Sensor.TYPE_GYROSCOPE -> "GYROSCOPE"
            Sensor.TYPE_GYROSCOPE_UNCALIBRATED -> "GYROSCOPE UNCALIBRATED"
            Sensor.TYPE_HEART_RATE -> "HEART RATE"
            Sensor.TYPE_LIGHT -> "LIGHT"
            Sensor.TYPE_LINEAR_ACCELERATION -> "LINEAR ACCELERATION"
            Sensor.TYPE_MAGNETIC_FIELD -> "MAGNETIC FIELD"
            Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED -> "MAGNETIC FIELD UNCALIBRATED"
            Sensor.TYPE_PRESSURE -> "PRESSURE"
            Sensor.TYPE_PROXIMITY -> "PROXIMITY"
            Sensor.TYPE_RELATIVE_HUMIDITY -> "RELATIVE HUMIDITY"
            Sensor.TYPE_ROTATION_VECTOR -> "ROTATION VECTOR"
            Sensor.TYPE_SIGNIFICANT_MOTION -> "SIGNIFICANT MOTION"
            Sensor.TYPE_STEP_COUNTER -> "STEP COUNTER"
            Sensor.TYPE_STEP_DETECTOR -> "STEP DETECTOR"
            // These types were deprecated but might still appear on older devices
            @Suppress("DEPRECATION")
            Sensor.TYPE_ORIENTATION -> "ORIENTATION"
            @Suppress("DEPRECATION")
            Sensor.TYPE_TEMPERATURE -> "TEMPERATURE (Deprecated)"
            // Add other specific integer constants for vendor types if you find them
            // For example, some Samsung devices use specific integer codes for their private sensors.
            // 65537 might be a "TILT DETECTOR" on some phones.
            // if (sensor.vendor.contains("Samsung") && sensor.type == 65537) return "TILT DETECTOR"
            else -> "UNKNOWN (${sensor.type})"
        }
    }
}

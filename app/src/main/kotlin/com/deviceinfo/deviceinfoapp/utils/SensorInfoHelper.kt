package com.deviceinfo.deviceinfoapp.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import com.deviceinfo.deviceinfoapp.model.SensorInfo

class SensorInfoHelper(private val context: Context) {

    /**
     * Gets a list of all available sensors on the device.
     */
    fun getSensorDetailsList(): List<SensorInfo> {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val deviceSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)

        // Transform the raw Android Sensor object into our own simple SensorInfo data class
        return deviceSensors.map { sensor ->
            SensorInfo(
                name = sensor.name,
                vendor = sensor.vendor,
                type = getSensorTypeString(sensor.type) // Use a helper for a readable type
            )
        }
    }

    /**
     * Converts the integer sensor type into a readable string.
     */
    private fun getSensorTypeString(type: Int): String {
        return when (type) {
            Sensor.TYPE_ACCELEROMETER -> "ACCELEROMETER"
            Sensor.TYPE_GYROSCOPE -> "GYROSCOPE"
            Sensor.TYPE_LIGHT -> "LIGHT"
            Sensor.TYPE_MAGNETIC_FIELD -> "MAGNETIC FIELD"
            Sensor.TYPE_PROXIMITY -> "PROXIMITY"
            Sensor.TYPE_GRAVITY -> "GRAVITY"
            Sensor.TYPE_ROTATION_VECTOR -> "ROTATION VECTOR"
            Sensor.TYPE_STEP_COUNTER -> "STEP COUNTER"
            Sensor.TYPE_STEP_DETECTOR -> "STEP DETECTOR"
            Sensor.TYPE_SIGNIFICANT_MOTION -> "SIGNIFICANT MOTION"
            // Add any other types you care about here
            else -> "UNKNOWN SENSOR"
        }
    }
}

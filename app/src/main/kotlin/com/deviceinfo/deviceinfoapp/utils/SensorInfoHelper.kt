package com.deviceinfo.deviceinfoapp.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import com.deviceinfo.deviceinfoapp.model.SensorInfo

class SensorInfoHelper(private val context: Context) {

    /**
     * Gets a list of all available sensors on the device.
     */
    fun getSensorDetailsList(): List<SensorInfo> {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val deviceSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)

        return deviceSensors.map { sensor ->
            SensorInfo(
                name = sensor.name,
                vendor = sensor.vendor,
                type = getSensorTypeString(sensor) // Pass the whole sensor object now
            )
        }
    }

    /**
     * Converts the sensor's integer type into a readable string.
     * This is now much more comprehensive.
     */
    private fun getSensorTypeString(sensor: Sensor): String {
        // For modern Android versions, there's a built-in method.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            // This returns a string like "android.sensor.accelerometer"
            val officialType = sensor.stringType
            // Let's make it look nicer by taking the last part and making it uppercase.
            return officialType.substringAfterLast('.').uppercase()
        }

        // For older versions, we fall back to our manual lookup table.
        return when (sensor.type) {
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
            Sensor.TYPE_AMBIENT_TEMPERATURE -> "AMBIENT TEMPERATURE"
            Sensor.TYPE_GAME_ROTATION_VECTOR -> "GAME ROTATION VECTOR"
            Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR -> "GEOMAGNETIC ROTATION VECTOR"
            Sensor.TYPE_HEART_RATE -> "HEART RATE"
            Sensor.TYPE_LINEAR_ACCELERATION -> "LINEAR ACCELERATION"
            Sensor.TYPE_PRESSURE -> "PRESSURE"
            Sensor.TYPE_RELATIVE_HUMIDITY -> "RELATIVE HUMIDITY"
            // There are many more, but this covers most of them.
            // Any sensor not in this list will be "UNKNOWN" on very old devices.
            else -> "UNKNOWN (${sensor.type})"
        }
    }
}

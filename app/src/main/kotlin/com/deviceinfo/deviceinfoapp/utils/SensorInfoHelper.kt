package com.deviceinfo.deviceinfoapp.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager

class SensorInfoHelper(private val context: Context) {

    /**
     * Gets a list of all available sensors on the device.
     */
    private fun getAllSensors(): List<Sensor> {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        return sensorManager.getSensorList(Sensor.TYPE_ALL)
    }

    /**
     * Returns the total number of available sensors.
     */
    fun getSensorCount(): String {
        return getAllSensors().size.toString()
    }

    // You could add more functions here later, like getting the name of each sensor.
}

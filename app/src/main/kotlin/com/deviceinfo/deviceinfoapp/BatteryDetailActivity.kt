package com.deviceinfo.deviceinfoapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.deviceinfo.deviceinfoapp.R

class BatteryDetailActivity : AppCompatActivity() {

    private lateinit var txtBatteryLevel: TextView
    private lateinit var txtBatteryStatus: TextView
    private lateinit var txtPowerSource: TextView
    private lateinit var txtBatteryHealth: TextView
    private lateinit var txtTechnology: TextView
    private lateinit var txtTemperature: TextView
    private lateinit var txtVoltage: TextView

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null) return

            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)
            val temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10
            val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
            val health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)
            val technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "Unknown"

            txtBatteryLevel.text = "$level%"
            txtBatteryStatus.text = when (status) {
                BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
                BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
                BatteryManager.BATTERY_STATUS_FULL -> "Battery Full"
                BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Not Charging"
                else -> "Unknown"
            }

            txtPowerSource.text = when (plugged) {
                BatteryManager.BATTERY_PLUGGED_USB -> "USB Port"
                BatteryManager.BATTERY_PLUGGED_AC -> "AC"
                else -> "Battery"
            }

            txtBatteryHealth.text = when (health) {
                BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
                BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
                BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
                BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
                BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
                BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Failure"
                else -> "Unknown"
            }

            txtTechnology.text = technology
            txtTemperature.text = "$temperature °C"
            txtVoltage.text = "$voltage mV"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_battery_detail)

        txtBatteryLevel = findViewById(R.id.txtBatteryLevel)
        txtBatteryStatus = findViewById(R.id.txtBatteryStatus)
        txtPowerSource = findViewById(R.id.txtPowerSource)
        txtBatteryHealth = findViewById(R.id.txtBatteryHealth)
        txtTechnology = findViewById(R.id.txtTechnology)
        txtTemperature = findViewById(R.id.txtTemperature)
        txtVoltage = findViewById(R.id.txtVoltage)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(batteryReceiver)
    }
}

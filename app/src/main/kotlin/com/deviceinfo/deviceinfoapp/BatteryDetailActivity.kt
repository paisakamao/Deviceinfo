package com.deviceinfo.deviceinfoapp

import android.content.BroadcastReceiver
// ... other imports

class BatteryDetailActivity : AppCompatActivity() {

    // ... (class properties are correct)

    private val realTimeRunnable = object : Runnable {
        override fun run() {
            updateBatteryInfo(fullRefresh = false) // only update the real-time rows
            handler.postDelayed(this, updateIntervalMs)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // ... (onCreate is correct)
    }

    override fun onResume() {
        // ... (onResume is correct)
    }

    override fun onPause() {
        // ... (onPause is correct)
    }

    private fun updateBatteryInfo(fullRefresh: Boolean) {
        if (fullRefresh || batteryDetailsList.isEmpty()) {
            val newDetails = batteryInfoHelper.getBatteryDetailsList()
            batteryDetailsList.clear()
            batteryDetailsList.addAll(newDetails)
            adapter.notifyDataSetChanged()
        } else {
            val currentIdx = batteryDetailsList.indexOfFirst { it.label == "Current (Real-time)" }
            val powerIdx   = batteryDetailsList.indexOfFirst { it.label == "Power (Real-time)" }
            val voltageIdx = batteryDetailsList.indexOfFirst { it.label == "Voltage" } // Also update voltage
            
            // Re-fetch the fast-updating values
            val newCurrent = batteryInfoHelper.getCurrentNowMilliAmps()?.let { "$it mA" } ?: "N/A"
            val newVoltage = batteryInfoHelper.getBatteryStatusIntent()?.let { intent ->
                (batteryInfoHelper.getInstantVoltageMilliVolts() ?: intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)).let { 
                    if (it <= 0) "N/A" else "$it mV"
                }
            } ?: "N/A"
            val newPower = batteryInfoHelper.getPowerNowString()
            
            // Update each item efficiently
            if (currentIdx != -1) {
                batteryDetailsList[currentIdx] = DeviceInfo("Current (Real-time)", newCurrent)
                adapter.notifyItemChanged(currentIdx)
            }
            if (powerIdx != -1) {
                batteryDetailsList[powerIdx] = DeviceInfo("Power (Real-time)", newPower)
                adapter.notifyItemChanged(powerIdx)
            }
            if (voltageIdx != -1) {
                batteryDetailsList[voltageIdx] = DeviceInfo("Voltage", newVoltage)
                adapter.notifyItemChanged(voltageIdx)
            }
        }
    }
}

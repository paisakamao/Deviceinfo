package com.deviceinfo.deviceinfoapp.utils

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager

class BatteryCurrentHelper(private val context: Context) {

    fun getBatteryCurrentMa(): Float {
        val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val currentMicroA = bm.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
        // On some devices this is negative when discharging
        return currentMicroA / 1000f
    }

    fun getBatteryVoltageMv(): Int {
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        return intent?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) ?: -1
    }

    fun getBatteryPowerW(): Float {
        val currentMa = getBatteryCurrentMa()
        val voltageMv = getBatteryVoltageMv()
        return if (currentMa != 0f && voltageMv > 0) {
            (currentMa / 1000f) * (voltageMv / 1000f)
        } else {
            0f
        }
    }
}

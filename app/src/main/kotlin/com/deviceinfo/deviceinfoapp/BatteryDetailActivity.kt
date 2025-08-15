package com.deviceinfo.deviceinfoapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.deviceinfo.deviceinfoapp.databinding.ActivityBatteryDetailBinding

class BatteryDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBatteryDetailBinding
    private var batteryReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBatteryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupBatteryMonitoring()
    }

    private fun setupBatteryMonitoring() {
        // Initial update
        updateBatteryData()

        // Register for updates
        batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                updateBatteryData(intent)
            }
        }

        registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    private fun updateBatteryData(intent: Intent? = null) {
        val batteryIntent = intent ?: registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        ) ?: return

        val batteryManager = getSystemService(BATTERY_SERVICE) as BatteryManager
        
        try {
            // Current in mA (may be negative if charging)
            val currentNow = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW) / 1000
            val dischargeRate = if (currentNow > 0) currentNow else 0
            
            binding.currentText.text = "Discharge: $dischargeRate mA"
        } catch (e: SecurityException) {
            binding.currentText.text = "Current data unavailable"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        batteryReceiver?.let { unregisterReceiver(it) }
    }
}
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBatteryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Battery Details"
        
        setupBatteryMonitoring()
    }

    private fun setupBatteryMonitoring() {
        val batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let { updateBatteryData(it) }
            }
        }
        
        registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    private fun updateBatteryData(intent: Intent) {
        val batteryManager = getSystemService(BATTERY_SERVICE) as BatteryManager
        
        // Current in mA (negative if charging)
        val currentNow = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW) / 1000
        
        // Update UI
        binding.currentText.text = when {
            currentNow > 0 -> "Discharging: $currentNow mA"
            currentNow < 0 -> "Charging: ${-currentNow} mA"
            else -> "Current: 0 mA"
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
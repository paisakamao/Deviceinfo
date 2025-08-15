package com.deviceinfo.deviceinfoapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.deviceinfo.deviceinfoapp.databinding.ActivityBatteryDetailBinding
import com.deviceinfo.deviceinfoapp.ui.BatteryFragment

class BatteryDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBatteryDetailBinding
    private var batteryReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBatteryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupActionBar()
        setupBatteryFragment()
        setupBatteryMonitoring()
    }

    private fun setupActionBar() {
        supportActionBar?.apply {
            title = "Battery Details"
            subtitle = "Loading..."
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupBatteryFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, BatteryFragment())
            .commit()
    }

    private fun setupBatteryMonitoring() {
        // Get initial battery status
        updateBatteryStatus()

        // Register for ongoing updates
        batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let { updateBatteryStatus(it) }
            }
        }

        registerReceiver(batteryReceiver, IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_CHANGED)
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
        })
    }

    private fun updateBatteryStatus(intent: Intent? = null) {
        val batteryIntent = intent ?: registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )

        batteryIntent?.let {
            val level = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val status = it.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            
            if (level != -1 && scale != -1) {
                val percentage = (level * 100.0f / scale).toInt()
                val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || 
                                status == BatteryManager.BATTERY_STATUS_FULL
                
                supportActionBar?.subtitle = buildString {
                    append("$percentage%")
                    append(if (isCharging) " (Charging)" else " (Discharging)")
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        batteryReceiver?.let {
            try {
                unregisterReceiver(it)
            } catch (e: IllegalArgumentException) {
                // Receiver was not registered
            }
        }
    }
}
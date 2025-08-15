package com.deviceinfo.deviceinfoapp.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.deviceinfo.deviceinfoapp.databinding.FragmentBatteryBinding

class BatteryFragment : Fragment() {
    // ViewBinding properties
    private var _binding: FragmentBatteryBinding? = null
    private val binding get() = _binding!!

    private var batteryReceiver: BroadcastReceiver? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBatteryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBatteryMonitoring()
    }

    private fun setupBatteryMonitoring() {
        batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let { updateBatteryData(it) }
            }
        }

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_CHANGED)
        }

        requireContext().registerReceiver(batteryReceiver, filter)
    }

    private fun updateBatteryData(intent: Intent) {
        val batteryManager = requireContext().getSystemService(Context.BATTERY_SERVICE) as BatteryManager

        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val batteryPct = level * 100 / scale.toFloat()
        
        // This is the key: get the current in microamps (µA)
        val currentNowMicroAmps = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
        // Convert to milliamps (mA)
        val currentNowMilliAmps = currentNowMicroAmps / 1000
        
        val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) / 1000f
        val temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) / 10f
        
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
        
        binding.apply {
            batteryLevel.text = String.format("%.0f%%", batteryPct)
            batteryVoltage.text = String.format("%.2fV", voltage)
            batteryTemp.text = String.format("%.1f°C", temperature)
            chargingStatus.text = if (isCharging) "Charging" else "Discharging"

            // Show current correctly. Negative means charging, positive means discharging.
            batteryDischargeCurrent.text = "${currentNowMilliAmps} mA"
            
            val health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)
            batteryHealth.text = when (health) {
                BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
                BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheated"
                BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
                BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over voltage"
                BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Failed"
                else -> "Unknown"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        batteryReceiver?.let {
            try {
                requireContext().unregisterReceiver(it)
            } catch (e: Exception) {
                Log.e("BatteryFragment", "Error unregistering receiver", e)
            }
        }
        _binding = null // Important to prevent memory leaks
    }
}

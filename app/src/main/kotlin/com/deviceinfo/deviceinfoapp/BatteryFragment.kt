package com.deviceinfo.deviceinfoapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.deviceinfo.deviceinfoapp.databinding.FragmentBatteryBinding

class BatteryFragment : Fragment() {
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
        // Initial update
        updateBatteryData()

        // Register for continuous updates
        batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let { updateBatteryData(it) }
            }
        }

        requireContext().registerReceiver(
            batteryReceiver,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
    }

    private fun updateBatteryData(intent: Intent? = null) {
        val batteryIntent = intent ?: requireContext().registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        ) ?: return

        val batteryManager = requireContext().getSystemService(Context.BATTERY_SERVICE) as BatteryManager

        // Current in mA (negative if charging)
        val currentNow = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW) / 1000
        val dischargeRate = if (currentNow > 0) currentNow else 0

        // Voltage in V
        val voltage = batteryIntent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) / 1000f

        // Update UI
        binding.apply {
            currentValue.text = "$dischargeRate mA"
            voltageValue.text = "$voltage V"
            powerValue.text = "${voltage * dischargeRate / 1000} W" // P = V*I

            // Show charging/discharging status
            val status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            statusValue.text = when (status) {
                BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
                BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
                BatteryManager.BATTERY_STATUS_FULL -> "Full"
                else -> "Unknown"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        batteryReceiver?.let {
            try {
                requireContext().unregisterReceiver(it)
            } catch (e: IllegalArgumentException) {
                // Receiver was not registered
            }
        }
        _binding = null
    }
}
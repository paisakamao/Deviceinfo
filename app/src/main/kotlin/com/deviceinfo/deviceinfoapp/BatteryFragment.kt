package com.deviceinfo.deviceinfoapp

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
import com.paisa.mao.deviceinfo.databinding.FragmentBatteryBinding

class BatteryFragment : Fragment() {
    private var _binding: FragmentBatteryBinding? = null
    private val binding get() = _binding!!
    private var batteryReceiver: BroadcastReceiver? = null
    private var lastUpdateTime: Long = 0
    private var lastBatteryLevel: Int = 0

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
            addAction(Intent.ACTION_BATTERY_LOW)
            addAction(Intent.ACTION_BATTERY_OKAY)
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
        }

        requireContext().registerReceiver(batteryReceiver, filter)

        // Get initial battery status
        val batteryIntent = requireContext().registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
        batteryIntent?.let { updateBatteryData(it) }
    }

    private fun updateBatteryData(intent: Intent) {
        val batteryManager = requireContext().getSystemService(Context.BATTERY_SERVICE) as BatteryManager

        // Basic battery info
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val batteryPct = level * 100 / scale.toFloat()

        // Current flow information
        val currentNow = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW) / 1000
        val dischargeRate = if (currentNow > 0) currentNow else 0

        // Voltage and temperature
        val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) / 1000f
        val temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) / 10f

        // Charging status
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || 
                        status == BatteryManager.BATTERY_STATUS_FULL
        val chargeSource = when (intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)) {
            BatteryManager.BATTERY_PLUGGED_USB -> "USB"
            BatteryManager.BATTERY_PLUGGED_AC -> "AC"
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless"
            else -> "Not charging"
        }

        // Battery health
        val health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)
        val healthStatus = when (health) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheated"
            BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over voltage"
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Failed"
            else -> "Unknown"
        }

        // Technology
        val technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "Unknown"

        // Capacity estimation (using common values as fallback)
        val capacity = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        val designCapacity = 4141 // mAh (from your screenshot, adjust as needed)

        // Time calculations
        val remainingTime = when {
            isCharging -> {
                val chargeTime = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_TIME_REMAINING)
                if (chargeTime != Long.MAX_VALUE) formatTime(chargeTime) else "Calculating..."
            }
            dischargeRate > 0 -> {
                val remainingMah = (batteryPct / 100f) * designCapacity
                val secondsRemaining = (remainingMah / dischargeRate) * 3600
                formatTime((secondsRemaining * 1000).toLong())
            }
            else -> "N/A"
        }

        // Update UI
        binding.apply {
            // Level section
            batteryLevelValue.text = "${batteryPct.toInt()}%"
            batteryStatusValue.text = if (isCharging) "Charging ($chargeSource)" else "Discharging"

            // Health section
            batteryHealthValue.text = healthStatus
            batteryChargingValue.text = chargeSource

            // Technology section
            batteryTechnologyValue.text = technology
            batteryTempValue.text = "${temperature}°C"

            // Voltage section
            batteryVoltageValue.text = "${voltage}V"
            batteryCurrentValue.text = if (dischargeRate > 0) "${dischargeRate}mA" else "N/A"

            // Power section
            batteryPowerValue.text = "${voltage * currentNow / 1000}W"
            batteryTimeFullValue.text = remainingTime

            // Capacity section
            batteryCurrentCapacityValue.text = "${(batteryPct / 100 * designCapacity).toInt()} mAh"
            batteryTotalCapacityValue.text = "$designCapacity mAh"
        }
    }

    private fun formatTime(millis: Long): String {
        val seconds = millis / 1000
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        return "${hours}h ${minutes}m"
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
        _binding = null
    }
}
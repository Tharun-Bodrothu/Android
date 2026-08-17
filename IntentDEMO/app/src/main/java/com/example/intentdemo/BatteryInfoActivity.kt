package com.example.intentdemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class BatteryInfoActivity : AppCompatActivity() {

    private lateinit var tvBatteryDetails: TextView
    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
                updateBatteryInfo(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_battery_info)

        tvBatteryDetails = findViewById(R.id.tvBatteryDetails)
        
        findViewById<Button>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val stickyIntent = ContextCompat.registerReceiver(this, batteryReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
        if (stickyIntent != null) {
            updateBatteryInfo(stickyIntent)
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(batteryReceiver)
    }

    private fun updateBatteryInfo(intent: Intent) {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val batteryPct = (level * 100 / scale.toFloat()).toInt()

        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        val health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)
        val healthString = when (health) {
            BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
            BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
            BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
            BatteryManager.BATTERY_HEALTH_UNKNOWN -> "Unknown"
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Unspecified Failure"
            else -> "Unknown"
        }

        val technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)
        val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
        val temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) / 10.0
        
        val plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
        val pluggedString = when (plugged) {
            BatteryManager.BATTERY_PLUGGED_AC -> "AC Charger"
            BatteryManager.BATTERY_PLUGGED_USB -> "USB Port"
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless"
            0 -> "Unplugged"
            else -> "Unknown"
        }

        var cycles = "N/A"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val extraCycles = intent.getIntExtra(BatteryManager.EXTRA_CYCLE_COUNT, -1)
            if (extraCycles != -1) cycles = extraCycles.toString()
        }

        val details = """
            Battery Level: $batteryPct%
            Charging Status: $isCharging
            Battery Health: $healthString
            Voltage: $voltage mV
            Temperature: $temperature °C
            Technology: $technology
            Plugged Source: $pluggedString
            Battery Cycles: $cycles
        """.trimIndent()

        tvBatteryDetails.text = details
    }
}

package com.example.intentdemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.BatteryManager
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.activity.enableEdgeToEdge
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat

class DashboardActivity : AppCompatActivity() {

    private lateinit var tvBatteryLevelMain: TextView
    private lateinit var tvChargingStatusMain: TextView

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                val batteryPct = (level * 100 / scale.toFloat()).toInt()
                tvBatteryLevelMain.text = getString(R.string.battery_level, batteryPct)

                val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL
                tvChargingStatusMain.text = getString(R.string.charging_status, isCharging.toString())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        tvBatteryLevelMain = findViewById(R.id.tvBatteryLevelMain)
        tvChargingStatusMain = findViewById(R.id.tvChargingStatusMain)

        val dashboardRoot = findViewById<android.view.View>(R.id.dashboard_root)
        ViewCompat.setOnApplyWindowInsetsListener(dashboardRoot) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.btnGoogle).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"))
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnCamera).setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnContacts).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("content://contacts/people/"))
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnDialer).setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:9676508475"))
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnContactDetail).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("content://contacts/people/1"))
            startActivity(intent)
        }

        findViewById<Button>(R.id.button).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        findViewById<Button>(R.id.btnStartService).setOnClickListener {
            val intent = Intent(this, SimpleService::class.java).apply {
                action = "ACTION_START_COUNTER"
            }
            startService(intent)
            Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnStopService).setOnClickListener {
            val intent = Intent(this, SimpleService::class.java).apply {
                action = "ACTION_STOP_COUNTER"
            }
            startService(intent)
            Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnStartMusic).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
                } else {
                    startMusicService()
                }
            } else {
                startMusicService()
            }
        }

        findViewById<Button>(R.id.btnStopMusic).setOnClickListener {
            val intent = Intent(this, SimpleService::class.java).apply {
                action = "ACTION_STOP_MUSIC"
            }
            startService(intent)
            Toast.makeText(this, "Music Stopped", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnDownload).setOnClickListener {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED) // Only Wi-Fi
                .setRequiresBatteryNotLow(true)               // Don't run if battery is low
                .build()

            val downloadWorkRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(this).enqueue(downloadWorkRequest)
            Toast.makeText(this, "Download queued (Requires Wi-Fi)", Toast.LENGTH_SHORT).show()

            // Observing status (Section 6 in the infographic)
            WorkManager.getInstance(this).getWorkInfoByIdLiveData(downloadWorkRequest.id)
                .observe(this) { workInfo ->
                    if (workInfo != null && workInfo.state.isFinished) {
                        Toast.makeText(this, "Background Download Finished!", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        findViewById<Button>(R.id.btnBatteryInfo).setOnClickListener {
            val intent = Intent(this, BatteryInfoActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val stickyIntent = ContextCompat.registerReceiver(this, batteryReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
        if (stickyIntent != null) {
            // Trigger update immediately
            batteryReceiver.onReceive(this, stickyIntent)
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(batteryReceiver)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startMusicService()
        }
    }

    private fun startMusicService() {
        val intent = Intent(this, SimpleService::class.java).apply {
            action = "ACTION_PLAY"
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }
}

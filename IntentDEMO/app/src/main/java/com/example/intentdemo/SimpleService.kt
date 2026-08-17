package com.example.intentdemo

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import java.util.Timer
import java.util.TimerTask

class SimpleService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private var timerObject: Timer? = null
    private var counter = 0
    private var isCounterRunning = false

    companion object {
        const val CHANNEL_ID = "MusicServiceChannel"
        const val NOTIFICATION_ID = 1
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        
        when (action) {
            "ACTION_PLAY" -> {
                val uri = intent.data ?: Settings.System.DEFAULT_RINGTONE_URI
                startForeground(NOTIFICATION_ID, createNotification())
                playTrack(uri)
            }
            "ACTION_STOP_MUSIC" -> {
                stopMusic()
                stopForeground(true)
                stopSelf()
            }
            "ACTION_START_COUNTER" -> {
                startCounter()
            }
            "ACTION_STOP_COUNTER" -> {
                stopCounter()
            }
        }
        
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Music Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Music Service")
            .setContentText("Playing music in loop...")
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
    }

    private fun startCounter() {
        if (isCounterRunning) return
        
        isCounterRunning = true
        counter = 0
        timerObject = Timer()
        timerObject?.schedule(object : TimerTask() {
            override fun run() {
                counter++
                Log.d("Service Log", "Counter Value: $counter")
            }
        }, 0, 1000)
        Log.d("Service Log", "Service Started")
    }

    private fun stopCounter() {
        timerObject?.cancel()
        timerObject = null
        isCounterRunning = false
        Log.d("Service Log", "Service Stopped")
    }

    private fun playTrack(uri: Uri) {
        stopMusic() // Stop any existing music before playing new
        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(this@SimpleService, uri)
                isLooping = true
                prepareAsync()
                setOnPreparedListener { 
                    start()
                    Toast.makeText(this@SimpleService, "Music Playing in Loop", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Log.e("SimpleService", "Error playing music", e)
        }
    }

    private fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        Log.d("Service Log", "Music Stopped")
    }

    override fun onDestroy() {
        super.onDestroy()
        stopCounter()
        stopMusic()
    }
}

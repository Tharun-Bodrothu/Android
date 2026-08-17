package com.example.intentdemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class MyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_AIRPLANE_MODE_CHANGED) {
            val isAirplaneModeOn = intent.getBooleanExtra("state", false)
            if (isAirplaneModeOn) {
                Toast.makeText(context, "Airplane mode is on", Toast.LENGTH_SHORT).show()
                // Handle airplane mode being turned on
            } else {
                Toast.makeText(context, "Airplane mode is off", Toast.LENGTH_SHORT).show()
                // Handle airplane mode being turned off
            }
        }
    }
}

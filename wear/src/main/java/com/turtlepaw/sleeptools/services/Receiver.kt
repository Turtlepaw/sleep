package com.turtlepaw.sleeptools.services

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.annotation.Keep
import androidx.core.content.edit
import com.turtlepaw.sleeptools.utils.Settings

@Keep
class Receiver(private val sharedPreferences: SharedPreferences): BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if(intent.action == NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED){
            BedtimeModeReceiver().onReceive(context, intent)
        } else if(intent.action == Intent.ACTION_POWER_CONNECTED){
            ChargingReceiver().onReceive(context, intent)
        } else if(intent.action == "com.turtlepaw.sunlight.SUNLIGHT_CHANGED"){
            val value = intent.getIntExtra("value", 0)
            sharedPreferences.edit {
                putInt(Settings.SUNLIGHT.getKey(), value)
                commit()
            }
        }
    }
}
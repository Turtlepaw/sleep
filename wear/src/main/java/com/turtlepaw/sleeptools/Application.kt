package com.turtlepaw.sleeptools

import android.app.Application
import android.app.NotificationManager
import android.content.Intent
import android.content.IntentFilter
import com.turtlepaw.sleeptools.services.Receiver
import com.turtlepaw.sleeptools.utils.SettingsBasics

open class SleepApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // The following code is from home assistant:
        // https://github.com/home-assistant/android/
        val sensorReceiver = Receiver(
            getSharedPreferences(
                SettingsBasics.SHARED_PREFERENCES.getKey(),
                SettingsBasics.SHARED_PREFERENCES.getMode()
            )
        )
        // This will cause the sensor to be updated every time the OS broadcasts that a cable was plugged/unplugged.
        // This should be nearly instantaneous allowing automations to fire immediately when a phone is plugged
        // in or unplugged. Updates will also be triggered when the system reports low battery and when it recovers.
        registerReceiver(
            sensorReceiver,
            IntentFilter(Intent.ACTION_POWER_CONNECTED)
        )

        // This will trigger for DND changes, including bedtime and theater mode
        registerReceiver(
            sensorReceiver,
            IntentFilter(NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED)
        )

        // Register for sunlight
        registerReceiver(
            sensorReceiver,
            IntentFilter("com.turtlepaw.sunlight.SUNLIGHT_CHANGED")
        )
    }
}
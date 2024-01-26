package com.turtlepaw.sleeptools.utils

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.provider.Settings
import android.util.Log
import java.time.LocalTime
import java.time.format.DateTimeParseException

/*
    This file includes code derived from home-assistant/android (https://github.com/home-assistant/android/blob/master/LICENSE.md)
    The original code is licensed under the Apache License Version 2.0
 */

class BedtimeModeManager {
    companion object {
        private const val TAG = "BEDTIME_MODE"
        const val STORAGE_ID = "LAST_BEDTIME"
    }

    fun getLastBedtime(sharedPreferences: SharedPreferences): LocalTime? {
        val lastBedtime = sharedPreferences.getString(STORAGE_ID, null);

        return try {
            LocalTime.parse(lastBedtime)
        } catch (e: DateTimeParseException) {
            null
        }
    }
    fun isBedtimeModeEnabled(context: Context, sharedPreferences: SharedPreferences?): Boolean {
        // The following code is from home assistant:
        // https://github.com/home-assistant/android/blob/c6ddca8fdc34d2e7741ec82c04b7d8b8d01d3995/wear/src/main/java/io/homeassistant/companion/android/sensors/BedtimeModeSensorManager.kt#L52
        val state = try {
            Settings.Global.getInt(context.contentResolver, if (Build.MANUFACTURER == "samsung") "setting_bedtime_mode_running_state" else "bedtime_mode") == 1
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update bedtime mode sensor", e)
            false
        }

        if(sharedPreferences != null && state){
            val editor = sharedPreferences.edit()
            editor.putString(STORAGE_ID, LocalTime.now().toString());
            editor.apply()
        }

        Log.e(TAG, "Bedtime is currently $state")

        return state
    }
}
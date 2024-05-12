package com.turtlepaw.sleeptools.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.health.services.client.HealthServices
import androidx.health.services.client.PassiveListenerCallback
import androidx.health.services.client.data.PassiveListenerConfig
import androidx.health.services.client.data.UserActivityInfo
import androidx.health.services.client.data.UserActivityState
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.turtlepaw.sleeptools.utils.Settings
import com.turtlepaw.sleeptools.utils.SettingsBasics
import kotlinx.coroutines.runBlocking
import java.time.Instant
import java.time.LocalDate

val passiveListenerConfig = PassiveListenerConfig.builder()
    .setShouldUserActivityInfoBeRequested(true)
    .build()

fun getPassiveListenerCallback(sharedPreferences: SharedPreferences): PassiveListenerCallback {
    val passiveListenerCallback: PassiveListenerCallback = object : PassiveListenerCallback {
        override fun onUserActivityInfoReceived(info: UserActivityInfo) {
            val stateChangeTime: Instant = info.stateChangeTime // may be in the past!
            val userActivityState: UserActivityState = info.userActivityState
            if (userActivityState == UserActivityState.USER_ACTIVITY_ASLEEP) {
                sharedPreferences.edit {
                    putString(Settings.LAST_BEDTIME.getKey(), LocalDate.from(stateChangeTime).toString())
                }
            }
        }
    }

    return passiveListenerCallback
}

class HealthStartupReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        // TODO: Check permissions first
        WorkManager.getInstance(context).enqueue(
            OneTimeWorkRequestBuilder<RegisterForPassiveDataWorker>().build()
        )
    }
}

class RegisterForPassiveDataWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        runBlocking {
            HealthServices.getClient(appContext)
                .passiveMonitoringClient
                .setPassiveListenerCallback(
                    passiveListenerConfig,
                    getPassiveListenerCallback(
                        appContext.getSharedPreferences(
                            SettingsBasics.SHARED_PREFERENCES.getKey(),
                            SettingsBasics.SHARED_PREFERENCES.getMode()
                        )
                    )
                )
        }
        return Result.success()
    }
}
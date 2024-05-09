package com.turtlepaw.sleeptools.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.health.services.client.data.PassiveListenerConfig
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters

val passiveListenerConfig = PassiveListenerConfig.builder()
    .setShouldUserActivityInfoBeRequested(true)
    .build()

class HealthStartupReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return


        // TODO: Check permissions first
//        WorkManager.getInstance(context).enqueue(
//            OneTimeWorkRequestBuilder<RegisterForPassiveDataWorker>().build()
//        )
    }
}

class RegisterForPassiveDataWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
//        runBlocking {
//            HealthServices.getClient(appContext)
//                .passiveMonitoringClient
//                .setPassiveListenerCallback(
//                    passiveListenerConfig,
//
//                )
//        }
        return Result.success()
    }
}
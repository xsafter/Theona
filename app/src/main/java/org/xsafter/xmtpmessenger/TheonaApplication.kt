package org.xsafter.xmtpmessenger

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TheonaApplication : Application() {

    companion object {
        lateinit var instance: TheonaApplication
            private set
        var isAppRunning = false
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()

        instance = this
        appContext = this

        val channel = NotificationChannel("location", "location", NotificationManager.IMPORTANCE_DEFAULT)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
    
}
package org.xsafter.xmtpmessenger

import android.app.Application
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
    }
    
}
package org.xsafter.xmtpmessenger

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TheonaApplication : Application() {
    init {
        instance = this
    }
    companion object {
        private var instance: TheonaApplication? = null
        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        val context: Context = TheonaApplication.applicationContext()
    }
}
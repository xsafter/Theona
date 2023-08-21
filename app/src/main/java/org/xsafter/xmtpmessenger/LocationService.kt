package org.xsafter.xmtpmessenger

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.xsafter.xmtpmessenger.data.LocationRepository
import org.xsafter.xmtpmessenger.data.datastore.LocationPreferences
import javax.inject.Inject

@AndroidEntryPoint
class ForegroundLocationService : LifecycleService() {

    @Inject
    lateinit var locationRepository: LocationRepository

    @Inject
    lateinit var locationPreferences: LocationPreferences

    private val localBinder = LocalBinder()
    private var bindCount = 0

    private var started = false
    private var isForeground = false

    private fun isBound() = bindCount > 0

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        // This action comes from our ongoing notification. The user requested to stop updates.
        if (intent?.action == ACTION_STOP_UPDATES) {
            stopLocationUpdates()
            lifecycleScope.launch {
                locationPreferences.setLocationTurnedOn(false)
            }
        }

        // Startup tasks only happen once.
        if (!started) {
            started = true
            // Check if we should turn on location updates.
            lifecycleScope.launch {
                if (locationPreferences.isLocationTurnedOn.first()) {
                    // If the service is restarted for any reason, we may have lost permission to
                    // access location since last time. In that case we won't turn updates on here,
                    // and the service will stop when we manage its lifetime below. Then the user
                    // will have to open the app to turn updates on again.
                    if (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) ||
                        hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    ) {
                        locationRepository.startLocationUpdates()
                    }
                }
            }
            // Update any foreground notification when we receive location updates.
            lifecycleScope.launch {
                locationRepository.lastLocation.collect(::showNotification)
            }
        }

        // Decide whether to remain in the background, promote to the foreground, or stop.
        manageLifetime()

        // In case we are stopped by the system, have the system restart this service so we can
        // manage our lifetime appropriately.
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        handleBind()
        return localBinder
    }

    override fun onRebind(intent: Intent?) {
        handleBind()
    }

    private fun handleBind() {
        bindCount++
        // Start ourself. This will let us manage our lifetime separately from bound clients.
        startService(Intent(this, this::class.java))
    }

    override fun onUnbind(intent: Intent?): Boolean {
        bindCount--
        lifecycleScope.launch {
            // UI client can unbind because it went through a configuration change, in which case it
            // will be recreated and bind again shortly. Wait a few seconds, and if still not bound,
            // manage our lifetime accordingly.
            delay(UNBIND_DELAY_MILLIS)
            manageLifetime()
        }
        // Allow clients to rebind, in which case onRebind will be called.
        return true
    }

    private fun manageLifetime() {
        when {
            // We should not be in the foreground while UI clients are bound.
            isBound() -> exitForeground()

            // Location updates were started.
            locationRepository.isReceivingLocationUpdates.value -> enterForeground()

            // Nothing to do, so we can stop.
            else -> stopSelf()
        }
    }

    private fun exitForeground() {
        if (isForeground) {
            isForeground = false
            stopForeground(true)
        }
    }

    private fun enterForeground() {
        if (!isForeground) {
            isForeground = true

            // Show notification with the latest location.
            showNotification(locationRepository.lastLocation.value)
        }
    }

    private fun showNotification(location: Location?) {
        if (!isForeground) {
            return
        }

        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification(location))
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Location updates",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(notificationChannel)
        }
    }

    private fun buildNotification(location: Location?) : Notification {
        // Tapping the notification opens the app.
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            packageManager.getLaunchIntentForPackage(this.packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        // Include an action to stop location updates without going through the app UI.
        val stopIntent = PendingIntent.getService(
            this,
            0,
            Intent(this, this::class.java).setAction(ACTION_STOP_UPDATES),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Theona")
            .setContentText("Sending location")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.baseline_location_on_24)
            .addAction(R.drawable.baseline_pause_24, "Stop", stopIntent)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .build()
    }

    // Methods for clients.

    fun startLocationUpdates() {
        locationRepository.startLocationUpdates()
    }

    fun stopLocationUpdates() {
        locationRepository.stopLocationUpdates()
    }

    /** Binder which provides clients access to the service. */
    internal inner class LocalBinder : Binder() {
        fun getService(): ForegroundLocationService = this@ForegroundLocationService
    }

    private companion object {
        const val UNBIND_DELAY_MILLIS = 2000.toLong() // 2 seconds
        const val NOTIFICATION_ID = 1
        const val NOTIFICATION_CHANNEL_ID = "LocationUpdates"
        const val ACTION_STOP_UPDATES = BuildConfig.APPLICATION_ID + ".ACTION_STOP_UPDATES"
    }
}

/**
 * ServiceConnection that provides access to a [ForegroundLocationService].
 */
class ForegroundLocationServiceConnection @Inject constructor() : ServiceConnection {

    var service: ForegroundLocationService? = null
        private set

    override fun onServiceConnected(name: ComponentName, binder: IBinder) {
        service = (binder as ForegroundLocationService.LocalBinder).getService()
    }

    override fun onServiceDisconnected(name: ComponentName) {
        // Note: this should never be called since the service is in the same process.
        service = null
    }
}

fun Context.hasPermission(permission: String): Boolean =
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
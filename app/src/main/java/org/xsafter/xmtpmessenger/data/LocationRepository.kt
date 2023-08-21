package org.xsafter.xmtpmessenger.data

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.xmtp.android.library.Conversation
import org.xsafter.xmtpmessenger.data.datastore.database.repository.ConversationRepository
import org.xsafter.xmtpmessenger.data.model.GeoMessage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val conversationRepository: ConversationRepository
) {
    private val callback = Callback()

    private val _isReceivingUpdates = MutableStateFlow(false)
    val isReceivingLocationUpdates = _isReceivingUpdates.asStateFlow()

    private val _lastLocation = MutableStateFlow<Location?>(null)
    val lastLocation = _lastLocation.asStateFlow()

    private val gson = Gson()
    private val scope = CoroutineScope(Dispatchers.IO)
    private val geoConversations = mutableListOf<Conversation>()

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        val request = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10_000 // 10 seconds
        }

        // See https://developer.android.com/reference/android/os/HandlerThread.
        fusedLocationProviderClient.requestLocationUpdates(
            request,
            callback,
            Looper.getMainLooper()
        )
        _isReceivingUpdates.value = true
    }

    fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(callback)
        _isReceivingUpdates.value = false
        _lastLocation.value = null
    }

    private inner class Callback : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            _lastLocation.value = result.lastLocation
            val geoMessage = GeoMessage(result.lastLocation!!.longitude, result.lastLocation!!.longitude)
            val message = gson.toJson(geoMessage)
            geoConversations.forEach { conversation ->
                scope.launch {
                    conversationRepository.sendMessage(conversation, message)
                }
            }
        }
    }

    fun addGeoConversation(conversation: Conversation) {
        geoConversations.add(conversation)
    }

    // Function to remove a conversation
    fun removeGeoConversation(conversation: Conversation) {
        geoConversations.remove(conversation)
    }
}
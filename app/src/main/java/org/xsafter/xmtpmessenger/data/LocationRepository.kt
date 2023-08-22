package org.xsafter.xmtpmessenger.data

import android.location.Location
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
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
    private val _lastLocation = MutableStateFlow<Location?>(null)

    private val gson = Gson()
    private val scope = CoroutineScope(Dispatchers.IO)
    private val geoConversations = mutableListOf<Conversation>()


    fun onLocationResult(result: Location) {
        val geoMessage = GeoMessage(result.longitude, result.longitude)
        val message = gson.toJson(geoMessage)
        Log.d("LocationRepository", "GeoMessage: $message")
        geoConversations.forEach { conversation ->
            scope.launch {
                conversationRepository.sendMessage(conversation, message)
            }
        }
    }

    fun addGeoConversation(conversation: Conversation) {
        geoConversations.add(conversation)
    }

}
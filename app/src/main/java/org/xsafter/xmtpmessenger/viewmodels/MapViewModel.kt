package org.xsafter.xmtpmessenger.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.xmtp.android.library.Conversation
import org.xsafter.xmtpmessenger.data.LocationRepository
import org.xsafter.xmtpmessenger.data.datastore.database.repository.ConversationRepository
import org.xsafter.xmtpmessenger.data.model.GeoMessage
import org.xsafter.xmtpmessenger.data.model.GeoMessageWrapper
import org.xsafter.xmtpmessenger.data.model.User
import org.xsafter.xmtpmessenger.ui.components.createFromObject
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val conversationRepository: ConversationRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _geoMessages = MutableStateFlow<List<GeoMessageWrapper>>(emptyList())
    val geoMessages: StateFlow<List<GeoMessageWrapper>> = _geoMessages
    val gson = Gson()

    fun setupGeoConversations(userIds: MutableList<String>) {
        userIds.forEach { userId ->
            viewModelScope.launch {
                val geoConversation = conversationRepository.createGeoConversation(userId)
                locationRepository.addGeoConversation(geoConversation)
                listenToGeoMessages(geoConversation)
            }
        }
    }

    private fun listenToGeoMessages(geoConversation: Conversation) {
        viewModelScope.launch {
            val gson = Gson()
            val messagesFlow = conversationRepository.getMessages(geoConversation)
            messagesFlow.collect { message ->
                Log.d("MapViewModel", message.body)
                if (message.body.startsWith("{")) {
                    val geoWrapper = GeoMessageWrapper(
                        gson.fromJson(message.body, GeoMessage::class.java),
                        User(
                            message.senderAddress,
                            message.senderAddress,
                            createFromObject(message.senderAddress),
                            "",
                            "",
                            message.sent
                        )
                    )
                    _geoMessages.value = _geoMessages.value.toMutableList().apply { add(geoWrapper) }
                }
            }
        }
    }

}

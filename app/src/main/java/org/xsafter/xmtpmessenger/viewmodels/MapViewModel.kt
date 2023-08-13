package org.xsafter.xmtpmessenger.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.xmtp.android.library.Conversation
import org.xsafter.xmtpmessenger.data.datastore.ConversationRepository
import org.xsafter.xmtpmessenger.data.model.GeoMessage
import org.xsafter.xmtpmessenger.data.model.GeoMessageWrapper
import org.xsafter.xmtpmessenger.data.model.User
import org.xsafter.xmtpmessenger.ui.components.createFromObject
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val conversationRepository: ConversationRepository
) : ViewModel() {

    private val _geoMessages = MutableStateFlow<List<GeoMessageWrapper>>(emptyList())
    val geoMessages: StateFlow<List<GeoMessageWrapper>> = _geoMessages

    fun setupGeoConversations(userIds: MutableList<String>) {
        userIds.forEach { userId ->
            viewModelScope.launch {
                val geoConversation = conversationRepository.createGeoConversation(userId)
                listenToGeoMessages(geoConversation)
            }
        }
    }

    private fun listenToGeoMessages(geoConversation: Conversation) {
        viewModelScope.launch {
            val gson = Gson()
            val messagesFlow = conversationRepository.getMessages(geoConversation)
            messagesFlow.collect { message ->
                if (message.body.startsWith("{")) {
                    GeoMessageWrapper(
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
                }
            }
        }
    }
}

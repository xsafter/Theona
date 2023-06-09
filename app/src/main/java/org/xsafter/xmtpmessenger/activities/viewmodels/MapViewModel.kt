package org.xsafter.xmtpmessenger.activities.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.xmtp.android.library.Client
import org.xmtp.android.library.Conversation
import org.xsafter.xmtpmessenger.ConversationHelper
import org.xsafter.xmtpmessenger.data.GeoMessage
import org.xsafter.xmtpmessenger.data.GeoMessageWrapper
import org.xsafter.xmtpmessenger.data.User
import org.xsafter.xmtpmessenger.ui.components.createFromObject

class MapViewModel(val client: Client) : ViewModel() {

    private lateinit var geoConversations: MutableList<Conversation>

    private fun createGeoMessages(userIds: MutableList<String>): MutableStateFlow<MutableList<GeoMessageWrapper>> {
        geoConversations = mutableListOf()
        val convBuilder = ConversationHelper(client)
        for (userId in userIds) {
            val conversations = convBuilder.createConversation(userId)
            geoConversations.add(conversations[1]!!)
        }

        val geoMessages = MutableStateFlow<MutableList<GeoMessageWrapper>>(mutableListOf())
        viewModelScope.launch {
            val gson = Gson()
            geoMessages.value = geoConversations.flatMap { conversation ->
                conversation.messages().mapNotNull { message ->
                    if (message.body.startsWith("{")) {
                        GeoMessageWrapper(gson.fromJson(message.body, GeoMessage::class.java),
                            User(message.senderAddress,
                                message.senderAddress,
                                createFromObject(message.senderAddress),
                                "",
                                "",
                                message.sent
                            )
                        )
                    } else {
                        null
                    }
                }
            }.toMutableList()
        }
        return geoMessages
    }

    private lateinit var _geoMessages: MutableStateFlow<MutableList<GeoMessageWrapper>>

    val geoMessages: StateFlow<List<GeoMessageWrapper>>
        get() = _geoMessages

    fun setupGeoConversations(userIds: MutableList<String>) {
        _geoMessages = createGeoMessages(userIds)
    }
}

package org.xsafter.xmtpmessenger.activities.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.xmtp.android.library.Client
import org.xmtp.android.library.Conversation
import org.xsafter.xmtpmessenger.ConversationHelper
import org.xsafter.xmtpmessenger.ui.components.chat.Message
import org.xsafter.xmtpmessenger.ui.components.createFromObject

class ChatViewModel(
    private val userId: String,
    private val context: Context,
    val client: Client
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    public lateinit var conversation: Conversation

    init {
        setupConversations()
        fetchMessages()
    }

    fun setupConversations() {
        Log.e("userId", userId)

        val convBuilder = ConversationHelper(client)
        val conversations = convBuilder.createConversation(userId)

        conversation = conversations[0]!!
    }

    fun fetchMessages() {
        viewModelScope.launch {
            conversation.streamMessages().collect { message ->
                _messages.value = _messages.value + Message(
                    message.senderAddress,
                    message.body,
                    message.sent.time,
                    authorAvatar = createFromObject(message.senderAddress)
                )
            }
        }
    }

    fun sendMessage(message: String, image: Bitmap? = null) {
        if (image != null)
            conversation.send(image)
        conversation.send(message)
    }
}

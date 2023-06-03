package org.xsafter.xmtpmessenger.ui.components.chat

import android.graphics.Bitmap
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.toMutableStateList

class ChatUIState(
    val channelName: String,
    val channelMembers: Int,
    initialMessages: List<Message>
) {
    private val _messages: MutableList<Message> = initialMessages.toMutableStateList()
    val messages: List<Message> = _messages

    fun addMessage(msg: Message) {
        _messages.add(0, msg) // Add to the beginning of the list
    }
}

@Immutable
data class Message(
    val author: String,
    val content: String,
    val timestamp: String,
    val image: Bitmap? = null,
    val authorAvatar: Bitmap
)
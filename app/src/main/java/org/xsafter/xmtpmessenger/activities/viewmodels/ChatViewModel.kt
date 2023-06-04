package org.xsafter.xmtpmessenger.activities.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.xmtp.android.library.Client
import org.xmtp.android.library.Conversation
import org.xsafter.xmtpmessenger.ConversationHelper
import org.xsafter.xmtpmessenger.ui.components.chat.Message
import org.xsafter.xmtpmessenger.ui.components.createFromObject

class ChatViewModel(private val userId: String,
                    private val context: Context,
                    val client: Client) : ViewModel() {
    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages

    public lateinit var conversation: Conversation
    public lateinit var geoConversation: Conversation


    fun setupConversations() {
        Log.e("userId", userId)

        val convBuilder = ConversationHelper(client)
        val conversations = convBuilder.createConversation(userId)

        conversation = conversations[0]!!
        //println(conversation.messages(limit = 5))
        geoConversation = conversations[1]!!
    }

    fun fetchMessages() {
        val fetchedMessages = conversation.messages().map { message ->
            Message(
                message.senderAddress,
                message.body,
                message.sent.time,
                authorAvatar = createFromObject(message.senderAddress)
            )
        }
        _messages.postValue(fetchedMessages)
    }

    fun sendMessage(message: String, image: Bitmap? = null) {
        if (image != null)
            conversation.send(image)
        conversation.send(message)
    }
}

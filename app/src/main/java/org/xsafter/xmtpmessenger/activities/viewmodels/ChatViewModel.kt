package org.xsafter.xmtpmessenger.activities.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.xmtp.android.library.Conversation
import org.xsafter.xmtpmessenger.ClientManager.client
import org.xsafter.xmtpmessenger.ConversationHelper
import org.xsafter.xmtpmessenger.ui.components.chat.Message
import org.xsafter.xmtpmessenger.ui.components.createFromObject

class ChatViewModel(private val userId: String, val context: Context) : ViewModel() {
    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages

    public lateinit var conversation: Conversation
    public lateinit var geoConversation: Conversation


    fun setupConversations() {

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
                message.sent.time.toString(),
                authorAvatar = createFromObject(message.senderAddress)
            )
        }
        _messages.postValue(fetchedMessages)
    }
}

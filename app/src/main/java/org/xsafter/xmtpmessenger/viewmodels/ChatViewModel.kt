package org.xsafter.xmtpmessenger.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.xmtp.android.library.Client
import org.xmtp.android.library.Conversation
import org.xsafter.xmtpmessenger.data.datastore.database.repository.ConversationRepository
import org.xsafter.xmtpmessenger.ui.components.chat.Message
import org.xsafter.xmtpmessenger.ui.components.createFromObject

class ChatViewModel @AssistedInject constructor(
    @Assisted private val userId: String,
    private val conversationRepository: ConversationRepository
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(userId: String): ChatViewModel
    }
    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            userId: String,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(userId = userId) as T
            }
        }
    }

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private lateinit var conversation: Conversation

    public val client: Client = conversationRepository.client

    init {
        setupConversation()
    }

    private fun setupConversation() {
        viewModelScope.launch {
            conversation = conversationRepository.createMainConversation(userId)
            fetchMessages()
        }
    }

    private fun fetchMessages() {
        viewModelScope.launch {
            val messagesFlow = conversationRepository.getMessages(conversation)
            messagesFlow.collect { message ->
                _messages.value = _messages.value + Message(
                    message.senderAddress,
                    message.body,
                    message.sent.time,
                    authorAvatar = createFromObject(message.senderAddress)
                )
            }

            Log.d("ChatViewModel", "messages: ${_messages.value}")
        }
    }

    fun sendMessage(message: String) {
        if (conversation != null) {
            conversation.send(message)
        }
    }
}

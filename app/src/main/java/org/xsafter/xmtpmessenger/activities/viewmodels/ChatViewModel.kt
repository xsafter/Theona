package org.xsafter.xmtpmessenger.activities.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.xsafter.xmtpmessenger.ui.components.chat.Message

class ChatViewModel(private val userId: String) : ViewModel() {
    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages

    fun fetchMessages() {
        // Fetch messages from your data source (e.g., API or database) and update the _messages LiveData
    }
}

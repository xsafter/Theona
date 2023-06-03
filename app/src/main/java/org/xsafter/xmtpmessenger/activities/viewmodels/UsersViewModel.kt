package org.xsafter.xmtpmessenger.activities.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.xmtp.android.library.Client
import org.xsafter.xmtpmessenger.data.User
import org.xsafter.xmtpmessenger.ui.components.createFromObject

class UsersViewModel(client: Client) : ViewModel() {
    private val _users by lazy {
        MutableStateFlow<List<User>>(listOf()).also { usersFlow ->
            viewModelScope.launch {
                usersFlow.value = client.conversations.list().map {
                    val lastMessage = it.messages(limit = 1).lastOrNull()
                    val lastMessageText = lastMessage?.body
                    val lastMessageDate = lastMessage?.sent
                    User(it.conversationId!!,
                        it.peerAddress!!,
                        createFromObject(it.peerAddress),
                        lastMessageText!!,
                        it.messages(limit = 1).lastOrNull()?.senderAddress!!,
                        lastMessageDate!!)
                }
            }
        }
    }

    val users: StateFlow<List<User>> = _users
}

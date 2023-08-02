package org.xsafter.xmtpmessenger.activities.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.xmtp.android.library.Client
import org.xsafter.xmtpmessenger.data.User
import org.xsafter.xmtpmessenger.ui.components.createFromObject

class UsersViewModel(client: Client, context: Context) : ViewModel() {
    val client = client
    val context = context

    private val _users by lazy {
        MutableStateFlow<List<User>>(listOf()).also { usersFlow ->
            viewModelScope.launch {
                val tempList = client.conversations.list().mapNotNull {
                    val lastMessage = it.messages(limit = 1).lastOrNull()
                    val lastMessageText = lastMessage?.body
                    val lastMessageDate = lastMessage?.sent

                    if (it.conversationId != null && it.peerAddress != null && lastMessageText != null &&
                        it.messages(limit = 1).lastOrNull()?.senderAddress != null && lastMessageDate != null) {
                        User(it.conversationId!!,
                            it.peerAddress,
                            createFromObject(it.peerAddress),
                            lastMessageText,
                            it.messages(limit = 1).lastOrNull()?.senderAddress!!,
                            lastMessageDate)
                    } else {
                        null
                    }
                }

                usersFlow.value = tempList
                Log.d("USERS", tempList.joinToString(separator = "\n", transform = User::toString))
            }
        }
    }

    val users: StateFlow<List<User>> = _users

    fun addUser(user: User) {
        val currentList = _users.value.toMutableList()
        currentList.add(user)
        ChatViewModel(user.username, context, client).setupConversations()
        MapViewModel(client).setupGeoConversations(mutableListOf(user.username))
        _users.value = currentList
    }
}

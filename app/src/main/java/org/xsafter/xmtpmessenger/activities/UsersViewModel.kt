package org.xsafter.xmtpmessenger.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.xmtp.android.library.Client
import org.xsafter.xmtpmessenger.data.User
import org.xsafter.xmtpmessenger.data.me

class UsersViewModel(client: Client) : ViewModel() {

    private val _users by lazy {
        MutableStateFlow<List<User>>(listOf()).also { usersFlow ->
            viewModelScope.launch {
                usersFlow.value = client.conversations.list().map {
                    User(it.conversationId!!,
                        it.conversationId!!,
                        me.avatar)
                }
            }
        }
    }

    val users: StateFlow<List<User>> = _users
}

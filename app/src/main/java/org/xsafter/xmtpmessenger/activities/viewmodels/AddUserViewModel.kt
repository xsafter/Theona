package org.xsafter.xmtpmessenger.activities.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.xmtp.android.library.Client

class AddContactViewModel(val context: Context,  val client: Client) : ViewModel() {
    private val _username = MutableLiveData<String>("")
    val username: LiveData<String> = _username

    fun onUsernameChanged(newUsername: String) {
        _username.value = newUsername
    }

    fun addUser() {
        ChatViewModel(username.value!!, context, client).setupConversations()
        MapViewModel(client).setupGeoConversations(mutableListOf(username.value!!))
    }
}

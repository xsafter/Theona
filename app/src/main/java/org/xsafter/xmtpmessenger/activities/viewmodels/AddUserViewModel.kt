package org.xsafter.xmtpmessenger.activities.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.xmtp.android.library.Client
import org.xsafter.xmtpmessenger.data.User
import org.xsafter.xmtpmessenger.ui.components.createFromObject
import java.util.Date

class AddContactViewModel(val context: Context,  val client: Client) : ViewModel() {
    private val _username = MutableLiveData<String>("")
    val username: LiveData<String> = _username

    fun onUsernameChanged(newUsername: String) {
        _username.value = newUsername
    }

    fun addUser() {
        UsersViewModel(client, context).addUser(
            User(
                username.value!!,
                username.value!!,
                createFromObject(username.value!!),
                "",
                "",
                Date()
            )
        )
    }
}

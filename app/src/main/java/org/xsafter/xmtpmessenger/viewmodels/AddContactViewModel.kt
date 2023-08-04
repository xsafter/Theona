package org.xsafter.xmtpmessenger.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import org.xsafter.xmtpmessenger.data.model.User
import org.xsafter.xmtpmessenger.ui.components.createFromObject
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddContactViewModel @Inject constructor(
    private val sharedViewModel: SharedViewModel,
    application: Application
) : BaseViewModel(application) {

    private val _username = MutableLiveData<String>("")
    val username: LiveData<String> = _username

    fun onUsernameChanged(newUsername: String) {
        _username.value = newUsername
    }

    fun addUser() {
        val user =
            User(
                username.value!!,
                username.value!!,
                createFromObject(username.value!!),
                "",
                "",
                Date()
            )
        sharedViewModel.addUser(user)
    }
}

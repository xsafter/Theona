package org.xsafter.xmtpmessenger.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.xmtp.android.library.Client
import org.xsafter.xmtpmessenger.data.datastore.database.repository.UserRepository
import org.xsafter.xmtpmessenger.data.model.User
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val userRepository: UserRepository,
    application: Application
) : ViewModel() {

    lateinit var client: Client

    fun addUser(user: User) {
        viewModelScope.launch {
            userRepository.insertUser(user)
        }
    }
}

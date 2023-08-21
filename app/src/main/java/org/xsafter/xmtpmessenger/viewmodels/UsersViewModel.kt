package org.xsafter.xmtpmessenger.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.xsafter.xmtpmessenger.data.datastore.database.repository.UserRepository
import org.xsafter.xmtpmessenger.data.model.User
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val userRepository: UserRepository,
    application: Application
) : BaseViewModel(application) {

    init {
        viewModelScope.launch {
            userRepository.getAndSaveRemoteUsers()
            userRepository.listenForNewConversations()
        }
    }

    val users = userRepository.getLocalUsers().cachedIn(viewModelScope)

    val usersList: LiveData<List<User>> = liveData {
        emit(userRepository.getLocalUsersAsList())
    }


    fun refreshUsers() {
        viewModelScope.launch {
            userRepository.refreshUsers()
        }
    }
}

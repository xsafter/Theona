package org.xsafter.xmtpmessenger.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
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

    val users = Pager(PagingConfig(pageSize = 20)) {
        userRepository.getLocalUsers()
    }.flow.cachedIn(viewModelScope)

    val usersList = userRepository.getLocalUsersAsList()

    fun refreshUsers() {
        viewModelScope.launch {
            userRepository.refreshUsers()
        }
    }

    fun addUser(user: User) {
        viewModelScope.launch {
            userRepository.insertUser(user)
        }
    }
}

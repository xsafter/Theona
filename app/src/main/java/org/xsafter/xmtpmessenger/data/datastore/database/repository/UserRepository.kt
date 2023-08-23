package org.xsafter.xmtpmessenger.data.datastore.database.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.xmtp.android.library.Client
import org.xmtp.android.library.XMTPException
import org.xsafter.xmtpmessenger.data.datastore.database.dao.UserDao
import org.xsafter.xmtpmessenger.data.model.User
import org.xsafter.xmtpmessenger.ui.components.createFromObject
import java.util.Date
import javax.inject.Inject

class UserRepository @Inject constructor(private val userDao: UserDao, val client: Client) {

    fun getLocalUsers(): Flow<PagingData<User>> {
        return Pager(PagingConfig(pageSize = 20)) {
            userDao.getAllUsers()
        }.flow
    }

    suspend fun getLocalUsersAsList(): List<User> = withContext(Dispatchers.IO) {
        userDao.getAllUsersList()
    }


    // Remote query to get users
    suspend fun getAndSaveRemoteUsers() {
        // fetch data from remote
        val remoteUsers = client.conversations.list().map {
            Log.d("UserRepository1", it.peerAddress)
            User(
                it.peerAddress,
                it.peerAddress,
                createFromObject(it.peerAddress),
                it.messages(limit = 1).firstOrNull()?.body?:"",
                it.messages(limit = 1).firstOrNull()?.senderAddress?:"",
                it.messages(limit = 1).firstOrNull()?.sent?: Date()
            )
        }

        userDao.insertUsers(remoteUsers)
    }

    suspend fun listenForNewConversations() = withContext(Dispatchers.IO) {
        client.conversations.stream().collect { conversation ->
            val user = User(
                conversation.peerAddress,
                conversation.peerAddress,
                createFromObject(conversation.peerAddress),
                conversation.messages(limit = 1).firstOrNull()?.body?:"",
                conversation.messages(limit = 1).firstOrNull()?.senderAddress?:"",
                conversation.messages(limit = 1).firstOrNull()?.sent?: Date()
            )

            Log.d("UserRepository2", user.toString())
            userDao.insertUser(user)
        }
    }

    // Use this function to refresh all the data on a pull to refresh
    suspend fun refreshUsers() {
        userDao.clearUsers()
        getAndSaveRemoteUsers()
    }

    suspend fun insertUser(user: User) = withContext(Dispatchers.IO) {
        if (!client.canMessage(user.id))
            throw XMTPException("User is not on the network")
        client.conversations.newConversation(user.id)
        userDao.insertUser(user)
    }
}

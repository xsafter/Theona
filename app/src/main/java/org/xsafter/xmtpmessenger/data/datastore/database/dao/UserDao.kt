package org.xsafter.xmtpmessenger.data.datastore.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.xsafter.xmtpmessenger.data.model.User

@Dao
interface UserDao {

    @Query("SELECT * FROM users")
    fun getAllUsers(): PagingSource<Int, User>

    @Query("SELECT * FROM users")
    fun getAllUsersList(): List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(userEntity: User)

    @Query("DELETE FROM users")
    suspend fun clearUsers()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<User>)
}
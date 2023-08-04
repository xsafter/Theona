package org.xsafter.xmtpmessenger.data.datastore.database

import androidx.room.Database
import androidx.room.RoomDatabase
import org.xsafter.xmtpmessenger.data.datastore.database.dao.UserDao
import org.xsafter.xmtpmessenger.data.model.User

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
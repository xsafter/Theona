package org.xsafter.xmtpmessenger

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.xsafter.xmtpmessenger.data.datastore.database.Converters
import org.xsafter.xmtpmessenger.data.datastore.database.dao.UserDao
import org.xsafter.xmtpmessenger.data.model.User

@Database(entities = [User::class], version = 1)
@TypeConverters(Converters::class)
abstract class TestAppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        fun createTestDB(context: Context): TestAppDatabase {
            return Room.inMemoryDatabaseBuilder(context, TestAppDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        }
    }
}

package org.xsafter.xmtpmessenger.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.xmtp.android.library.Client
import org.xsafter.xmtpmessenger.data.datastore.database.repository.ConversationRepository
import org.xsafter.xmtpmessenger.data.datastore.database.AppDatabase
import org.xsafter.xmtpmessenger.data.datastore.database.dao.UserDao
import org.xsafter.xmtpmessenger.data.datastore.database.repository.UserRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideYourDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        AppDatabase::class.java,
        "user_db"
    ).build()

    @Singleton
    @Provides
    fun provideUserRepository(
        userDao: UserDao,
        client: Client
    ): UserRepository {
        return UserRepository(userDao, client)
    }

    @Singleton
    @Provides
    fun provideUserDao(
        database: AppDatabase
        ): UserDao =  database.userDao()


    @Provides
    fun provideConversationRepository(client: Client): ConversationRepository {
        return ConversationRepository(client)
    }


}

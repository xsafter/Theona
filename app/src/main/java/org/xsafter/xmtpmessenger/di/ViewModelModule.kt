package org.xsafter.xmtpmessenger.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import org.xmtp.android.library.Client
import org.xsafter.xmtpmessenger.data.datastore.database.repository.UserRepository
import org.xsafter.xmtpmessenger.viewmodels.MapViewModel
import org.xsafter.xmtpmessenger.viewmodels.UsersViewModel

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {
    @Provides
    fun provideUsersViewModel(
        userRepository: UserRepository,
        application: Application
    ): UsersViewModel {
        return UsersViewModel(userRepository, application)
    }

    @Provides
    fun provideMapViewModel(client: Client): MapViewModel {
        return MapViewModel(client)
    }
}

package org.xsafter.xmtpmessenger.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import org.xmtp.android.library.Client
import org.xsafter.xmtpmessenger.viewmodels.MapViewModel
import org.xsafter.xmtpmessenger.viewmodels.SharedViewModel
import org.xsafter.xmtpmessenger.viewmodels.UsersViewModel

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {
    @Provides
    fun provideUsersViewModel(
        sharedViewModel: SharedViewModel,
        application: Application
    ): UsersViewModel {
        return UsersViewModel(sharedViewModel, application)
    }

    @Provides
    fun provideMapViewModel(client: Client): MapViewModel {
        return MapViewModel(client)
    }
}

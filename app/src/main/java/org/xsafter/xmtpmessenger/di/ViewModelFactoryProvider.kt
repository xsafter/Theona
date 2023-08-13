package org.xsafter.xmtpmessenger.di

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import org.xsafter.xmtpmessenger.viewmodels.ChatViewModel

@EntryPoint
@InstallIn(ActivityComponent::class)
interface ViewModelFactoryProvider {
    fun chatViewModelFactory(): ChatViewModel.Factory
}